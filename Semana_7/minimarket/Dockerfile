# ==========================================
# Etapa 1: Compilación y Pruebas Unitarias
# ==========================================
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el archivo de dependencias para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente del backend
COPY src ./src

# Ejecutar de forma obligatoria los 107 tests lógicos concurrentes
RUN mvn clean package -DskipTests=false

# ==========================================
# Etapa 2: Entorno de Ejecución Ligero
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar únicamente el archivo compilado desde la etapa anterior
COPY --from=build /app/target/minimarket-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de desarrollo alternativo libre de conflictos de red
EXPOSE 8081

# Comando de arranque inyectando el perfil desacoplado de producción
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]