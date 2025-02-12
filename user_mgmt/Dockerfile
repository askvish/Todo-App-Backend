FROM maven:3.9.9-amazoncorretto-8-al2023 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:25-oraclelinux9
WORKDIR /app
COPY --from=build /app/target/user_mgmt-1.0.jar app.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "app.jar"]

