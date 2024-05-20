FROM openjdk:17-jdk-slim
LABEL authors="gamarl"
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY target/messages-0.0.1-SNAPSHOT.jar application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]