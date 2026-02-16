# Сборка WAR (multi-stage)
# Этап 1: Maven
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Копируем родительский pom и модули
COPY pom.xml .
COPY autoservice-core/pom.xml autoservice-core/
COPY autoservice-app/pom.xml autoservice-app/
COPY autoservice-web/pom.xml autoservice-web/

# Загружаем зависимости (кэш слоя)
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B -pl autoservice-web -am || true

COPY autoservice-core/src autoservice-core/src
COPY autoservice-app/src autoservice-app/src
COPY autoservice-web/src autoservice-web/src
COPY checkstyle.xml suppressions.xml ./
COPY autoservice-web/suppressions.xml autoservice-web/

RUN mvn clean package -pl autoservice-web -am -DskipTests -B

# Этап 2: Tomcat + WAR
FROM tomcat:10.1-jdk17-temurin-jammy
# Удаляем дефолтные приложения
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем собранный WAR как ROOT (контекст /)
COPY --from=builder /app/autoservice-web/target/autoservice-web.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
