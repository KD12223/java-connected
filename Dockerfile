FROM openjdk:11
ADD target/java-connected-0.0.1-SNAPSHOT.jar java-connected-0.0.1-SNAPSHOT.jar
EXPOSE 8000
ENTRYPOINT [ "java", "-jar", "/java-connected-0.0.1-SNAPSHOT.jar"]
