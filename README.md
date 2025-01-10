# デプロイメモ
アプリのビルド  
　mvn clean package -DskipTests

コンテナイメージの作成  
　docker build -t myapp-backend:latest .  

コンテナ起動  
　docker run -e AWS_REGION=ap-northeast-1 -e AWS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=YOUR_SECRET_ACCESS_KEY -p 8080:8080 myapp-backend:latest

