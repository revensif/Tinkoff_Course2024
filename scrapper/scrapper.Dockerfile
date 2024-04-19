FROM openjdk:21

COPY target/scrapper.jar scrapper.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/scrapper.jar"]
