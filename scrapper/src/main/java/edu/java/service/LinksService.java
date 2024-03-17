package edu.java.service;

import edu.java.dto.request.AddLinkRequest;
import edu.java.dto.request.RemoveLinkRequest;
import edu.java.dto.response.LinkResponse;
import edu.java.dto.response.ListLinksResponse;

public interface LinksService {

    LinkResponse add(long tgChatId, AddLinkRequest request);

    LinkResponse remove(long tgChatId, RemoveLinkRequest request);

    ListLinksResponse listAll(long tgChatId);
}
