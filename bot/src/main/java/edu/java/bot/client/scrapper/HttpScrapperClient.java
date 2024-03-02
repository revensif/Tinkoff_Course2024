package edu.java.bot.client.scrapper;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import reactor.core.publisher.Mono;

public interface HttpScrapperClient {

    Mono<String> registerChat(Long id);

    Mono<String> deleteChat(Long id);

    Mono<ListLinksResponse> getAllLinks(Long id);

    Mono<LinkResponse> addLink(Long id, AddLinkRequest request);

    Mono<LinkResponse> deleteLink(Long id, RemoveLinkRequest request);

}
