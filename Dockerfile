FROM openjdk:17-oracle
VOLUME /main-app
ADD docker-mongo-rmq-multimodule-api-web/build/libs/docker-mongo-rmq-multimodule-api-web-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]