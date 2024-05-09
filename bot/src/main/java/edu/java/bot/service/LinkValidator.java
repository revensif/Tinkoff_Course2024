package edu.java.bot.service;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkValidator {

    private final List<String> resources;

    public boolean validateLink(URI url) {
        return ((url.getScheme() != null)
            && (url.getHost() != null)
            && (url.getPath() != null)
            && resources.stream().anyMatch((link) -> url.getHost().matches(link)));
    }
}
