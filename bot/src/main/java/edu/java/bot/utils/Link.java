package edu.java.bot.utils;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class Link {
    public static final String INCORRECT_LINK = "Incorrect input, try /track https://stackoverflow.com";

    private String protocol;
    private String hostname;
    private String path;
    private String query;
    private String fragment;

    @SneakyThrows
    @Override
    public String toString() {
        return new URI(protocol, hostname, path, query, fragment).toString();
    }
}
