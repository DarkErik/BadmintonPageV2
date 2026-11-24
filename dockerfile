FROM eclipse-temurin:25-jre@sha256:4ed132f6832e9804b123e89a849a7b4502756ae64a4ee08ebab939d9d5d0a9fa

WORKDIR /app

COPY target/*.jar app.jar


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]