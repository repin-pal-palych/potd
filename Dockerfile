FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test -x check

CMD ["java", "-jar", "build/libs/potdb-1.0-SNAPSHOT.jar"]
