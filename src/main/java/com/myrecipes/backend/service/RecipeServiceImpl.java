package com.myrecipes.backend.service;

import com.myrecipes.backend.dao.CategoryDAO;
import com.myrecipes.backend.dao.RecipeDAO;
import com.myrecipes.backend.dto.AddRecipeRequest;
import com.myrecipes.backend.dto.RecipeDetailsResponse;
import com.myrecipes.backend.dto.RecipeResponse;
import com.myrecipes.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService{

    private RecipeDAO recipeDAO;
    private CategoryDAO categoryDAO;

    private final S3Client s3Client;
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
    }

    @Override
    public List<Recipe> findAll() {
        return recipeDAO.findAll();
    }

    @Override
    public List<RecipeResponse> findRecipeByCategoryName(String categoryName) {
        return recipeDAO.findByCategoryName(categoryName);
    }

    @Override
    public RecipeDetailsResponse getRecipeDetails(int id) {
        // 指定されたidのレシピを取得
        Recipe recipe = recipeDAO.findById(id);

        // 取得したレシピの材料を取得
        List<RecipeIngredient> ingredients = recipe.getIngredients();

        // 取得したレシピの手順を取得
        List<RecipeStep> steps = recipe.getSteps();

        // 取得したレシピのコツ・ポイントを取得
        String point = recipe.getRecipePoint().getPoint();

        return new RecipeDetailsResponse(
                id, recipe.getTitle(), recipe.getVideoUrl(), ingredients, steps, point
        );
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryDAO.findCategoryByName(categoryName);
    }

    @Override
    public void AddRecipe(AddRecipeRequest addRecipeRequest, MultipartFile thumbnail) {

        // Recipeインスタンス生成
        Recipe recipe = new Recipe();

        // Recipeインスタンスの各フィールドに値をセット
        recipe.setTitle(addRecipeRequest.getTitle());
        recipe.setServings(addRecipeRequest.getServings());
        recipe.setVideoUrl(addRecipeRequest.getVideoUrl());
        recipe.setIngredients(addRecipeRequest.getIngredients());
        recipe.setSteps(addRecipeRequest.getSteps());

        // RecipePointインスタンスを生成し、RecipeのrecipePointフィールドにセット
        RecipePoint recipePoint = new RecipePoint(addRecipeRequest.getPoint());
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
        String imageUrl = uploadImageToS3(thumbnail);
        recipe.setImage(imageUrl);

        // 署名付きURLをRecipeのimageフィールドにセット

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
            // ユニークなファイル名を生成
            String fileName = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();

            // S3に画像ファイルをアップロード
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            System.out.println(fileName);

            byte[] bytes = imageFile.getBytes();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);) {
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, bytes.length));
            }

            // アップロードされた画像のS3 URLを返す
            return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("S3への画像アップロードに失敗しました。", e);
        }
    }
}
