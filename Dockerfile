FROM eclipse-temurin:17-jre-focal
Expose 8081
ADD target/ratting-0.0.1-SNAPSHOT.jar ratting-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "ratting-0.0.1-SNAPSHOT.jar"]
