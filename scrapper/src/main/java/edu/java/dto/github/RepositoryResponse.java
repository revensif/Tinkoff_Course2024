package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record RepositoryResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("html_url") String url,
    @JsonProperty("updated_at") OffsetDateTime updatedAt) {
}
