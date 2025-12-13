FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# mvnw ko executable banao (Linux fix)
RUN chmod +x mvnw

# Build Spring Boot app
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Exact jar name (wildcard nahi chalega)
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
