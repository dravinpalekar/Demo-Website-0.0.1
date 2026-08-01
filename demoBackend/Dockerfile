# ==========================================
# STAGE 1: Build Stage (Git + Maven + Java)
# ==========================================
FROM maven:3.9-eclipse-temurin-25-alpine AS builder

RUN apk add --no-cache git

WORKDIR /build

# GitHub Repository Clone
RUN git clone https://github.com/dravinpalekar/Demo-Website-0.0.1.git .

# Sub-folder (demoBackend) me Maven Build run karein
WORKDIR /build/demoBackend/

# har baar new dependency ko add karna or purani cache se nahi
# RUN mvn dependency:go-offline -B

RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Runtime Stage (Lightweight JRE)
# ==========================================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app


COPY --from=builder /build/demoBackend/authentication/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]