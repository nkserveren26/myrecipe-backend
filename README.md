# デプロイメモ
アプリのビルド  
　mvn clean package -DskipTests

コンテナイメージの作成  
　docker build -t myapp-backend:latest .  

コンテナ起動  
　docker run -p 8080:8080 myapp-backend:latest

