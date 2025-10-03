FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app
COPY src /app/src
RUN mvn -f /app/pom.xml clean package -DskipTests

# Запуск приложения
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/application.jar
EXPOSE 9099
ENTRYPOINT ["java", "-jar", "application.jar"]