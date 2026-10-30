FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/campus-claim-review-service-1.0.0.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=65.0", "-XX:+UseSerialGC", "-jar", "/app/app.jar"]
