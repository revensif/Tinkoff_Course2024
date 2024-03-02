package edu.java.bot.controller;

import edu.java.bot.dto.request.LinkUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdateController {

    @PostMapping()
    public ResponseEntity<String> sendUpdate(@RequestBody @Valid LinkUpdateRequest request) {
        return ResponseEntity.ok("Обновление обработано");
    }
}
