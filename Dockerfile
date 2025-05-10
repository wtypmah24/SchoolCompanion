FROM maven:latest AS maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

FROM openjdk:latest AS app
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=maven /app/target/*.jar beobachtung_app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "beobachtung_app.jar"]