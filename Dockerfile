FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew gradlew.bat build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN chmod +x gradlew
COPY src ./src
RUN --mount=type=cache,id=midterm-gradle,target=/root/.gradle ./gradlew --no-daemon clean test bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system studentapi && useradd --system --gid studentapi --home-dir /app studentapi
COPY --from=build --chown=studentapi:studentapi /workspace/build/libs/student-api.jar app.jar
LABEL org.opencontainers.image.title="Sarsembayev Eldar Student API" lab.student_id="37006"
USER studentapi
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=65.0", "-jar", "/app/app.jar"]
