FROM eclipse-temurin:17-jre-apline
WORKDIR /app
COPY app/target/app-10-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]