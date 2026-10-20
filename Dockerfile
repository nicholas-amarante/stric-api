# ==============================================================================
# Multi-stage Dockerfile para stric-api (Spring Boot 3 + Java 21)
# ==============================================================================

# Stage 1: Build da aplicação
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copia arquivos do Maven Wrapper e POM para cache de dependências
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Baixa dependências
RUN ./mvnw dependency:go-offline -B || true

# Copia código-fonte e compila gerando o pacote JAR executável
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime leve com suporte nativo a Tesseract OCR
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Instala Tesseract OCR com dicionários em Português e Inglês
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-eng && \
    rm -rf /var/lib/apt/lists/*

# Copia o JAR compilado do builder
COPY --from=builder /app/target/*.jar app.jar

# Garante pasta de armazenamento para arquivos
RUN mkdir -p /app/storage/uploads/curriculos && chmod -R 777 /app/storage

EXPOSE 8080

# Inicia a aplicação usando a porta fornecida pela nuvem ($PORT) ou 8080 por padrão
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
