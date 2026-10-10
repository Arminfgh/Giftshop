# ---------- Build stage ----------
# Uses Maven + Java 17 to compile the project into a runnable .jar
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---------- Run stage ----------
# A small Java 17 image that just runs the finished .jar
FROM eclipse-temurin:17-jre
WORKDIR /app

# Keep memory usage inside the container's limit (helps on small free plans)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

COPY --from=build /app/target/giftshop.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
