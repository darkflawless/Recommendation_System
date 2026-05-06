FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/*-SNAPSHOT.jar app.jar

EXPOSE 8095
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
