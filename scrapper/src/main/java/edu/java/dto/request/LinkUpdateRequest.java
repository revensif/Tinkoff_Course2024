package edu.java.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    Long id,
    @NotNull(message = "The link shouldn't be null") URI url,
    String description,
    @NotEmpty(message = "The chat list should not be empty") List<Long> tgChatIds
) {
}
