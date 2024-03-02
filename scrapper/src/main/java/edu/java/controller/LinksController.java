package edu.java.controller;

import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    private static final String HEADER = "Tg-Chat-Id";

    @GetMapping
    public ListLinksResponse getAllLinks(@RequestHeader(HEADER) Long id) {
        List<LinkResponse> links = new ArrayList<>();
        return new ListLinksResponse(links, links.size());
    }

    @PostMapping
    public LinkResponse addLink(@RequestHeader(HEADER) Long id, @RequestBody @Valid AddLinkRequest request) {
        return new LinkResponse(id, URI.create(request.link()));
    }

    @DeleteMapping
    public LinkResponse deleteLink(@RequestHeader(HEADER) Long id, @RequestBody @Valid RemoveLinkRequest request) {
        return new LinkResponse(id, URI.create(request.link()));
    }
}
