package edu.java.service;

import org.springframework.stereotype.Service;

@Service
public class LinksService {

    public String getAllLinks() {
        return "All links have been requested";
    }

    public String addLink() {
        return "The link has been added";
    }

    public String deleteLink() {
        return "The link has been deleted";
    }
}
