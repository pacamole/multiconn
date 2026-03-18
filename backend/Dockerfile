# 🎨 PADRÃO: Multi-stage Build
# ETAPA 1: Construção (Build)
# 💡 CORREÇÃO: Usando a imagem oficial do Maven com Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# ==========================================
# ETAPA 2: Execução (Run)
# 💡 CORREÇÃO: Usando o ambiente de execução (JRE) ultraleve do Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]