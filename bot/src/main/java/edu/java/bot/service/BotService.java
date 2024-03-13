package edu.java.bot.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class BotService {

    public void sendUpdate() {
        log.info("Пришло обновление!");
    }
}