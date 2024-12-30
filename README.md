# デプロイメモ
アプリのビルド  
　mvn clean package -DskipTests

コンテナイメージの作成  
　docker build -t myapp-backend:latest .

