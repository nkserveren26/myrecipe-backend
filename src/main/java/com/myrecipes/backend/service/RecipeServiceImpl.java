package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.*;
import com.myrecipes.backend.dto.*;
import com.myrecipes.backend.entity.*;
import com.myrecipes.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;
    private RecipeIngredientDAO recipeIngredientDAO;
    private RecipeStepDAO recipeStepDAO;
    private RecipePointDAO recipePointDAO;

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucketName = "testbucket-kn";

    @Autowired
    public RecipeServiceImpl(RecipeDAO theRecipeDAO, CategoryDAO theCategoryDAO,
                             RecipeIngredientDAO theRecipeIngredientDAO, RecipeStepDAO theRecipeStepDAO, RecipePointDAO theRecipePointDAO) {
        recipeDAO = theRecipeDAO;
        categoryDAO = theCategoryDAO;
        recipeIngredientDAO = theRecipeIngredientDAO;
        recipeStepDAO = theRecipeStepDAO;
        recipePointDAO = theRecipePointDAO;

        // S3クライアントの設定
        Region region = Region.AP_NORTHEAST_1;  // 適切なリージョンに変更
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    @Override
    public List<Recipe> findAll() {
        return recipeDAO.findAll();
    }

    @Override
    @Transactional
    public List<RecipeResponse> findRecipeByCategoryName(String categoryName) {
        try {
            List<RecipeResponse> recipes = recipeDAO.findByCategoryName(categoryName);

            // 各レシピの画像URLをチェックして、必要であれば再生成する
            recipes.forEach(recipe -> {
                String imageUrl = recipe.getImage();

                if (isExpired(imageUrl)) {
                    // URLが期限切れの場合、新しい署名付きURLを生成
                    String imageObjectKey = extractObjectKey(imageUrl);
                    String newSignedUrl = generatePresignedUrl(imageObjectKey);

                    // 更新対象レシピのカテゴリーインスタンスを取得
                    Category category = categoryDAO.findCategoryByName(categoryName);

                    // 更新対象レシピの既存データ取得
                    Recipe existingRecipe = recipeDAO.findById(recipe.getId());

                    // 更新後の画像URLをセット
                    existingRecipe.setImage(newSignedUrl);

                    // レシピテーブルを更新
                    recipeDAO.update(existingRecipe);
                }
            });

            return recipes;
        } catch (Exception e) {
            throw new RuntimeException("レシピ取得に失敗しました。");
        }
    }

    @Override
    public RecipeDetailsResponse getRecipeDetails(int id) {
        // 指定されたidのレシピを取得
        Recipe recipe = recipeDAO.findById(id);

        // RecipeIngredientをDTOに変換
        List<RecipeIngredientDTO> ingredients = recipe.getIngredients().stream()
                .map(ingredient -> new RecipeIngredientDTO(ingredient.getId(), ingredient.getName(), ingredient.getAmount()))
                .collect(Collectors.toList());

        // RecipeStepをDTOに変換
        List<RecipeStepDTO> steps = recipe.getSteps().stream()
                .sorted(Comparator.comparingInt(RecipeStep::getStepNumber)) // step_numberでソート
                .map(step -> new RecipeStepDTO(step.getId(), step.getStepNumber(), step.getDescription()))
                .collect(Collectors.toList());

        // 取得したレシピのコツ・ポイントを取得
        String point = recipe.getRecipePoint().getPoint();

        return new RecipeDetailsResponse(
                id, recipe.getTitle(), recipe.getServings(), recipe.getVideoUrl(), ingredients, steps, point
        );
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryDAO.findCategoryByName(categoryName);
    }

    @Override
    @Transactional
    public void addRecipe(AddRecipeRequest addRecipeRequest, MultipartFile thumbnail) {

        try {
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

            // サムネイル画像が指定されている場合にのみ画像処理を実行
            if (thumbnail != null && !thumbnail.isEmpty()) {
                // 画像をS3にアップロード
                String imageObjectKey = uploadImageToS3(thumbnail);

                // アップロードした画像の署名付きURLを生成
                String presignedUrl = generatePresignedUrl(imageObjectKey);

                // 署名付きURLをRecipeのimageフィールドにセット
                recipe.setImage(presignedUrl);
            }

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
        } catch (Exception e) {
            throw new RuntimeException("レシピ登録に失敗しました。");
        }

    }

    @Override
    @Transactional
    public void updateRecipeById(int id, UpdateRecipeRequest updateRecipeRequest, MultipartFile thumbnail) {

        Recipe updateRecipe = recipeDAO.findById(id);

        if (updateRecipe == null) {
            throw new RuntimeException("指定されたIDのレシピが見つかりません: " + id);
        }

        try {
            // Recipeインスタンスの各フィールドに値をセット
            updateRecipe.setTitle(updateRecipeRequest.getTitle());
            updateRecipe.setServings(updateRecipeRequest.getServings());
            updateRecipe.setVideoUrl(updateRecipeRequest.getVideoUrl());

            // 現在の材料データを取得
            List<RecipeIngredient> existingIngredients = recipeIngredientDAO.findByRecipeId(id);

            // 新しい材料データ
            List<RecipeIngredient> newIngredients = updateRecipeRequest.getIngredients();

            // 材料データの更新および追加
            for (RecipeIngredient newIngredient : newIngredients) {
                Optional<RecipeIngredient> existingIngredient = existingIngredients.stream()
                        .filter(i -> i.getId() == newIngredient.getId()) // IDで一致を確認
                        .findFirst();

                if (existingIngredient.isPresent()) {
                    // 既存データの更新
                    RecipeIngredient ingredient = existingIngredient.get();
                    ingredient.setName(newIngredient.getName());
                    ingredient.setAmount(newIngredient.getAmount());
                    recipeIngredientDAO.update(ingredient);
                } else {
                    // 新規データの追加
                    RecipeIngredient newEntity = new RecipeIngredient();
                    newEntity.setName(newIngredient.getName());
                    newEntity.setAmount(newIngredient.getAmount());
                    newEntity.setRecipe(updateRecipe);
                    recipeIngredientDAO.save(newEntity);
                }
            }

            // 既存のレシピステップデータを取得
            List<RecipeStep> existingRecipeSteps = recipeStepDAO.findByRecipeId(id);

            // 新しいレシピステップデータ
            List<RecipeStep> newRecipeSteps = updateRecipeRequest.getSteps();

            // レシピステップデータの更新および追加
            for (RecipeStep newRecipeStep : newRecipeSteps) {
                Optional<RecipeStep> existingRecipeStep = existingRecipeSteps.stream()
                        .filter(i -> i.getId() == newRecipeStep.getId()) // IDで一致を確認
                        .findFirst();

                if (existingRecipeStep.isPresent()) {
                    // 既存データの更新
                    RecipeStep step = existingRecipeStep.get();
                    step.setStepNumber(newRecipeStep.getStepNumber());
                    step.setDescription(newRecipeStep.getDescription());
                    recipeStepDAO.update(step);
                } else {
                    // 新規データの追加
                    RecipeStep newEntity = new RecipeStep();
                    newEntity.setStepNumber(newRecipeStep.getStepNumber());
                    newEntity.setDescription(newRecipeStep.getDescription());
                    newEntity.setRecipe(updateRecipe);
                    recipeStepDAO.save(newEntity);
                }
            }

            // 既存のレシピポイントデータ取得
            RecipePoint existingRecipePoint = recipePointDAO.findByRecipeId(id);

            // 新しいレシピポイントデータをセット
            existingRecipePoint.setPoint(updateRecipeRequest.getPoint());

            // レシピポイントの更新
            recipePointDAO.update(existingRecipePoint);

            // categoryフィールドにCategoryインスタンスをセット
            Category category = findCategoryByName(updateRecipeRequest.getCategory());
            updateRecipe.setCategory(category);

            // サムネイル画像が指定されている場合にのみ画像処理を実行
            if (thumbnail != null && !thumbnail.isEmpty()) {
                // 画像をS3にアップロード
                String imageObjectKey = uploadImageToS3(thumbnail);

                // アップロードした画像の署名付きURLを生成
                String presignedUrl = generatePresignedUrl(imageObjectKey);

                // 署名付きURLをRecipeのimageフィールドにセット
                updateRecipe.setImage(presignedUrl);
            }

            // レシピにセットされた各ステップのrecipeフィールドに対象レシピを設定
            if (updateRecipe.getSteps() != null) {
                for (RecipeStep step : updateRecipe.getSteps()) {
                    step.setRecipe(updateRecipe);
                }
            }

            recipeDAO.update(updateRecipe);
        } catch (Exception e) {
            throw new RuntimeException("レシピの更新に失敗しました: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteRecipeById(int id) {
        try {
            // レシピの削除を実行
            recipeDAO.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("レシピの削除に失敗しました: " + e.getMessage(), e);
        }
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
                    .signatureDuration(Duration.ofMinutes(10080))
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

            // X-Amz-Date パラメータをInstant.parseが想定する形式に変換
            String formattedAmzDate = amzDate.substring(0, 4) + "-" + amzDate.substring(4, 6) + "-" +
                    amzDate.substring(6, 8) + "T" + amzDate.substring(9, 11) + ":" +
                    amzDate.substring(11, 13) + ":" + amzDate.substring(13, 15) + "Z";

            // Instant に変換
            Instant requestTime = Instant.parse(formattedAmzDate);

            // 有効期限を計算 (リクエスト時刻 + X-Amz-Expires 秒)
            Instant expirationTime = requestTime.plus(Duration.ofSeconds(expiresInSeconds));

            // 現在時刻と有効期限を比較して期限切れかを判断
            return Instant.now().isAfter(expirationTime);

        } catch (Exception e) {
            // URLが不正またはパラメータ取得に失敗した場合は期限切れと見なす
            System.out.println(e);
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
