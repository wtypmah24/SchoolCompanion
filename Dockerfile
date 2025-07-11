FROM arm32v7/maven:3.8.7-openjdk-17 AS maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

FROM arm32v7/openjdk:17 AS app
WORKDIR /app

COPY --from=maven /app/target/*.jar beobachtung_app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "beobachtung_app.jar"]