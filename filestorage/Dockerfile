FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy only what we need for a cached build
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN chmod +x ./mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/filestorage-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
