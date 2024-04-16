package edu.java.bot.service;

import edu.java.bot.utils.Link;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class LinkParser {

    public Link parseLink(URI url) {
        return new Link(
            url.getScheme(),
            url.getHost(),
            url.getPath(),
            url.getQuery(),
            url.getFragment()
        );
    }
}
