FROM openjdk:17-jdk-slim
LABEL authors="gamarl"
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME

#ENTRYPOINT ["./mvnw", "spring-boot:run"]