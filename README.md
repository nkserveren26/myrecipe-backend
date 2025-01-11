# 料理レシピ管理アプリ - バックエンド

## プロジェクト概要 (Introduction)
このプロジェクトは、料理のレシピを登録・管理するためのWebアプリケーションのバックエンドAPIです。  
ユーザーはYouTubeやブログで見つけたレシピを登録し、編集や削除を行うことができます。

主な機能：
- レシピの登録
- レシピの取得
- レシピの編集
- レシピの削除

---

## 技術スタック (Tech Stack)
- 言語: Java
- フレームワーク: Spring Boot
- データベース: MySQL
- ビルドツール: Maven

---

## API仕様 (API Documentation)
以下は主なエンドポイントの概要です。

| HTTPメソッド | エンドポイント        | 説明                |
|----------|----------------|-------------------|
| GET      | /api/recipes/by-category | 特定カテゴリのレシピのリストを取得 |
| GET      | /api/recipes/{id}  | レシピの詳細情報を取得       |
| POST     | /api/recipes       | レシピの登録            |
| PUT      | /api/recipes/{id}  | レシピの更新            |
| DELETE   | /api/recipes/{id}  | レシピの削除            |

詳細なAPI仕様は今後作成予定。

---

## フォルダ構成 (Project Structure)
```plaintext
src/
├── main/
│   ├── java/
│   │   └── com.myrecipes.backend/
│   │       ├── config/        # Configurationクラスを配置
│   │       ├── dao/           # DAOクラスを配置
│   │       ├── dto/           # DTOクラスを配置
│   │       ├── exception/     # 例外処理を定義したクラスを配置
│   │       ├── rest/          # Controllerクラスを配置
│   │       ├── service/       # ビジネスロジックを定義したServiceクラスを配置
│   └── resources/
│       ├── application.properties  # 設定ファイル
│       └── static/                 # 静的リソース
└── test/
    └── java/                       # テストコード
```

---

# デプロイメモ
アプリのビルド  
　mvn clean package -DskipTests

コンテナイメージの作成  
　docker build -t myapp-backend:latest .  

コンテナ起動  
　docker run -e AWS_REGION=ap-northeast-1 -e AWS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=YOUR_SECRET_ACCESS_KEY -p 8080:8080 myapp-backend:latest

