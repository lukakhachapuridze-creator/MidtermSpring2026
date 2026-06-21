FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src src
COPY resources resources

RUN mvn -q package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN mkdir -p data

COPY --from=build /app/target/midterm-uno-cli-1.0-SNAPSHOT.jar app.jar

VOLUME ["/app/data"]

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--bots", "3", "--games", "1", "--quiet"]
