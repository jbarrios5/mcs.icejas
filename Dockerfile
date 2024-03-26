FROM openjdk:11
COPY target/mcs.icejas.jar icejas-app.jar

ENTRYPOINT ["java","-jar","icejas-app.jar"]
