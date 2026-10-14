FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias primero para aprovechar la caché de Docker
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# preferIPv4Stack evita el error "Network unreachable" al conectar con Azure
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
