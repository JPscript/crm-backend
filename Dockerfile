# 1. Imagen de construcción (Build)
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY . .

# Usar Maven Wrapper si tienes mvnw, si no, usa 'mvn'
RUN mvn clean package -DskipTests

# 2. Imagen final para correr la app
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia solo el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
