FROM openjdk:11-jre-slim
Expose 8081
ADD target/ratting-0.0.1-SNAPSHOT.jar ratting-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "ratting-0.0.1-SNAPSHOT.jar"]
