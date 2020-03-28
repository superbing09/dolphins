FROM openjdk:8-jre-alpine
COPY ./target/dolphins-0.0.1-SNAPSHOT.jar dolphins.jar
ENTRYPOINT ["java","-jar","/dolphins.jar"]