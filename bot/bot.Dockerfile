FROM openjdk:21

COPY target/bot.jar bot.jar

ENV TOKEN=${TOKEN}

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "-Dapp.telegram-token=$TOKEN", "/bot.jar"]
