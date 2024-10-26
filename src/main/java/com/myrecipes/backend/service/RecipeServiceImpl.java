package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.CategoryDAO;
import com.myrecipes.backend.dao.RecipeDAO;
import com.myrecipes.backend.dto.*;
import com.myrecipes.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucketName = "testbucket-kn";

    @Autowired
    public RecipeServiceImpl(RecipeDAO theRecipeDAO, CategoryDAO theCategoryDAO) {
        recipeDAO = theRecipeDAO;
        categoryDAO = theCategoryDAO;

        // S3クライアントの設定
        Region region = Region.AP_NORTHEAST_1;  // 適切なリージョンに変更
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Override
    public List<Recipe> findAll() {
        return recipeDAO.findAll();
    }

    @Override
    public List<RecipeResponse> findRecipeByCategoryName(String categoryName) {
        List<Recipe> recipes = recipeDAO.findByCategoryName(categoryName);

        // 各レシピの画像URLをチェックして、必要であれば再生成する
        recipes.forEach(recipe -> {
            String imageUrl = recipe.getImage();

            if (isExpired(imageUrl)) {
                // URLが期限切れの場合、新しい署名付きURLを生成
                String imageObjectKey = extractObjectKey(imageUrl);
                String newSignedUrl = generatePresignedUrl(imageObjectKey);
                recipe.setImage(newSignedUrl);

                // レシピテーブルを更新
                recipeDAO.update(recipe);
            }
        });

        //Recipe -> RecipeResponse に変換してレスポンスを作成
        return recipes.stream()
                .map(recipe -> new RecipeResponse(
                        recipe.getId(),
                        recipe.getTitle(),
                        recipe.getImage(),      // 更新された署名付きURLがここに含まれる
                        recipe.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public RecipeDetailsResponse getRecipeDetails(int id) {
        // 指定されたidのレシピを取得
        Recipe recipe = recipeDAO.findById(id);

        // RecipeIngredientをDTOに変換
        List<RecipeIngredientDTO> ingredients = recipe.getIngredients().stream()
                .map(ingredient -> new RecipeIngredientDTO(ingredient.getName(), ingredient.getAmount()))
                .collect(Collectors.toList());

        // RecipeStepをDTOに変換
        List<RecipeStepDTO> steps = recipe.getSteps().stream()
                .map(step -> new RecipeStepDTO(step.getStepNumber(), step.getDescription()))
                .collect(Collectors.toList());

        // 取得したレシピのコツ・ポイントを取得
        String point = recipe.getRecipePoint().getPoint();

        System.out.println("Return Recipe Details");

        return new RecipeDetailsResponse(
                id, recipe.getTitle(), recipe.getVideoUrl(), ingredients, steps, point
        );
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryDAO.findCategoryByName(categoryName);
    }

    @Override
    public void addRecipe(AddRecipeRequest addRecipeRequest, MultipartFile thumbnail) {

        // Recipeインスタンス生成
        Recipe recipe = new Recipe();

        // Recipeインスタンスの各フィールドに値をセット
        recipe.setTitle(addRecipeRequest.getTitle());
        recipe.setServings(addRecipeRequest.getServings());
        recipe.setVideoUrl(addRecipeRequest.getVideoUrl());
        recipe.setIngredients(addRecipeRequest.getIngredients());
        recipe.setSteps(addRecipeRequest.getSteps());

        // RecipePointインスタンスを生成し、RecipeのrecipePointフィールドにセット
        RecipePoint recipePoint = new RecipePoint();
        recipePoint.setPoint(addRecipeRequest.getPoint());
        recipe.setRecipePoint(recipePoint);

        // 日本時間の現在時刻を取得
        ZoneId zoneId = ZoneId.of("Asia/Tokyo");
        LocalDateTime jstNow = LocalDateTime.now(zoneId);

        // createdAtフィールドに日本時間の現在時刻を設定
        recipe.setCreatedAt(jstNow);

        // categoryフィールドにCategoryインスタンスをセット
        Category category = findCategoryByName(addRecipeRequest.getCategory());
        recipe.setCategory(category);

        // 画像をS3にアップロード
        String imageObjectKey = uploadImageToS3(thumbnail);

        // アップロードした画像の署名付きURL生成
        String presignedUrl = generatePresignedUrl(imageObjectKey);

        // 署名付きURLをRecipeのimageフィールドにセット
        recipe.setImage(presignedUrl);

        // レシピにセットされた各材料のrecipeフィールドに対象レシピを設定
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe);
            }
        }

        // レシピにセットされた各ステップのrecipeフィールドに対象レシピを設定
        if (recipe.getSteps() != null) {
            for (RecipeStep step : recipe.getSteps()) {
                step.setRecipe(recipe);
            }
        }

        // レシピにセットされた、レシピのコツのrecipeフィールドに対象レシピを設定
        if (recipe.getRecipePoint() != null) {
            recipe.getRecipePoint().setRecipe(recipe);
        }

        recipeDAO.save(recipe);
    }

    private String uploadImageToS3(MultipartFile imageFile) {
        try {
            // ユニークなオブジェクトキー名を生成
            String objectKey = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();

            // S3に画像ファイルをアップロード
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            byte[] bytes = imageFile.getBytes();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);) {
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, bytes.length));
            }

            // アップロードされた画像のS3オブジェクトキー名を返す
            return objectKey;

        } catch (Exception e) {
            throw new RuntimeException("S3への画像アップロードに失敗しました。", e);
        }
    }

    private String generatePresignedUrl(String objectKey) {
        try {
            // getするオブジェクトの情報を組み立て
            GetObjectRequest objReq = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // 署名付きURLの生成
            GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(1440))
                    .getObjectRequest(objReq)
                    .build();

            // 発行
            URL url = presigner.presignGetObject(presignReq).url();

            return url.toString();

        } catch (Exception e) {
            throw new RuntimeException("署名付きURLの生成に失敗しました。", e);
        }
    }

    private boolean isExpired(String imageUrl) {
        try {
            // URLをパースしてクエリパラメータを取得
            URL url = new URL(imageUrl);
            Map<String, String> queryParams = Stream.of(url.getQuery().split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(p -> p[0], p -> p[1]));

            // X-Amz-ExpiresとX-Amz-Dateパラメータが存在するか確認
            if (!queryParams.containsKey("X-Amz-Expires") || !queryParams.containsKey("X-Amz-Date")) {
                return true;  // パラメータがない場合は期限切れと判断
            }

            // X-Amz-ExpiresとX-Amz-Dateパラメータの値を取得
            String expiresStr = queryParams.get("X-Amz-Expires");
            String amzDate = queryParams.get("X-Amz-Date");

            if (expiresStr == null || amzDate == null) {
                return true;  // パラメータの値がない場合は期限切れと判断
            }

            long expiresInSeconds = Long.parseLong(expiresStr);  // 文字列をlongに変換

            // `X-Amz-Date` パラメータをISO8601形式からInstantに変換
            Instant requestTime = Instant.parse(amzDate.substring(0, 4) + "-" + amzDate.substring(4, 6) + "-"
                    + amzDate.substring(6, 8) + "T" + amzDate.substring(9, 11) + ":" + amzDate.substring(11, 13)
                    + ":" + amzDate.substring(13) + "Z");

            // 有効期限を計算 (リクエスト時刻 + X-Amz-Expires 秒)
            Instant expirationTime = requestTime.plus(Duration.ofSeconds(expiresInSeconds));

            // 現在時刻と有効期限を比較して期限切れかを判断
            return Instant.now().isAfter(expirationTime);

        } catch (Exception e) {
            // URLが不正またはパラメータ取得に失敗した場合は期限切れと見なす
            return true;
        }
    }

    public String extractObjectKey(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();  // "/<object-key>" という形式
            return path.substring(1);  // 先頭の '/' を除いた部分がオブジェクトキー
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid image URL", e);
        }
    }
}
