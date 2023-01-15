FROM openjdk:17-jdk-slim
COPY ./build/libs/*.jar asd-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","asd-0.0.1-SNAPSHOT.jar"]