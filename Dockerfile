FROM openjdk:19-alpine
ARG JAR-FILE
COPY target/bot-1.0.0-SNAPSHOT.jar /bot-1.0.0-SNAPSHOT.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/bot-1.0.0-SNAPSHOT.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]