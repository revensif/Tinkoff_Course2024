package edu.java.controller;

import edu.java.service.TgChatService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
public class TgChatController {

    private final TgChatService tgChatService;

    public TgChatController(TgChatService tgChatService) {
        this.tgChatService = tgChatService;
    }

    @PostMapping("/{id}")
    public String registerChat(@PathVariable("id") Long id) {
        tgChatService.register(id);
        return "Чат зарегистрирован";
    }

    @DeleteMapping("/{id}")
    public String deleteChat(@PathVariable("id") Long id) {
        tgChatService.unregister(id);
        return "Чат успешно удален";
    }
}
