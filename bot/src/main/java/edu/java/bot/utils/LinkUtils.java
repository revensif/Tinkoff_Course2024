package edu.java.bot.utils;

import java.net.URI;
import java.util.List;

public final class LinkUtils {

    private static final List<String> AVAILABLE_LINKS = List.of(
        "stackoverflow\\.com",
        "github\\.com"
    );

    private LinkUtils() {
    }

    public static Link parse(URI url) {
        return new Link(
            url.getScheme(),
            url.getHost(),
            url.getPath(),
            url.getQuery(),
            url.getFragment()
        );
    }

    public static boolean isCorrectLink(URI url) {
        return ((url.getScheme() != null) && (url.getHost() != null) && (url.getPath() != null)
            && AVAILABLE_LINKS.stream().anyMatch((link) -> url.getHost().matches(link)));
    }

}
