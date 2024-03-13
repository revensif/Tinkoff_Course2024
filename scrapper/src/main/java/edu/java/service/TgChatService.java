package edu.java.service;

import org.springframework.stereotype.Service;

@Service
public class TgChatService {

    public String registerChat() {
        return "The chat has been registered";
    }

    public String unregisterChat() {
        return "The chat has been unregistered";
    }
}
