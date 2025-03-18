FROM gradle:7.6-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

COPY src ./src

RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar bnpl-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "bnpl-app.jar"]