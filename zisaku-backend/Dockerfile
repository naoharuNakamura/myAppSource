# --- Stage 1: ビルド環境 ---
FROM gradle:8-jdk17 AS build-stage
WORKDIR /app

# ソースコードとGradleの設定ファイルをコピー
COPY build.gradle .
# ※もし build.gradle.kts の場合は、上の行を COPY build.gradle.kts . に書き換えてください
COPY settings.gradle .
COPY src ./src

# 本番用にJARファイルをビルド（テストはスキップして高速化）
RUN gradle bootJar -x test

# --- Stage 2: 実行環境 ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# コンテナ内にSQLiteのデータベースファイルを置くディレクトリを作成
RUN mkdir /data

COPY database.db /data/app.db

# Stage 1 で作成された JAR ファイルをコピー
COPY --from=build-stage /app/build/libs/*.jar app.jar

# 環境変数で、Spring BootにSQLiteの保存先（/data の中）を教えてあげる設定
ENV SQLITE_DB_PATH=/data/app.db

EXPOSE 8080

# アプリケーションの実行
ENTRYPOINT ["java", "-jar", "app.jar"]