FROM openjdk:14
EXPOSE 8080
ARG JAR_FILE=target/forum-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]