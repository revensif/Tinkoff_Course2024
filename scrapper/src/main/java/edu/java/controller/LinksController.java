package edu.java.controller;

import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import edu.java.service.LinksService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinksController {

    private final LinksService linksService;

    public LinksController(LinksService linksService) {
        this.linksService = linksService;
    }

    private static final String HEADER = "Tg-Chat-Id";

    @GetMapping
    public ListLinksResponse getAllLinks(@RequestHeader(HEADER) Long id) {
        return linksService.listAll(id);
    }

    @PostMapping
    public LinkResponse addLink(@RequestHeader(HEADER) Long id, @RequestBody @Valid AddLinkRequest request) {
        return linksService.add(id, request);
    }

    @DeleteMapping
    public LinkResponse deleteLink(@RequestHeader(HEADER) Long id, @RequestBody @Valid RemoveLinkRequest request) {
        return linksService.remove(id, request);
    }
}
