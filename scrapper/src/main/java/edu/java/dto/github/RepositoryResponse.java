package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.OffsetDateTime;

public record RepositoryResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("html_url") URI url,
    @JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
