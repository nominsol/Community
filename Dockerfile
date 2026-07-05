FROM gradle:8-jdk24 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean build -x test \
    && find build/libs -name "*.jar" -not -name "*-plain.jar" -exec mv {} /app/app.jar \;

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]