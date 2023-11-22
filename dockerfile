# FROM maven:3.8.5-openjdk-17-slim as build
FROM maven:3.8.4-openjdk-17-slim as build
WORKDIR /app
COPY ./pom.xml .
COPY ./src/ ./src/

RUN mvn -Dmaven.test.skip package

FROM openjdk:17-jdk-slim
# FROM openjdk:17-jdk-alpine

ARG JAR_FILE=/app/target/*.jar

COPY --from=build ${JAR_FILE} app.jar
ENV DB_NAME=${MYSQL_DATABASE}
ENV DB_USER=${MYSQL_USER}
ENV DB_PASS=${MYSQL_PASSWORD}
ENV DB_PORT=3306
EXPOSE 8080

ENTRYPOINT exec java -jar app.jar