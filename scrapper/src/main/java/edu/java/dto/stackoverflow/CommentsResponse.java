package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CommentsResponse(List<ItemResponse> items) {
    public record ItemResponse(
        @JsonProperty("comment_id") Long commentId) {
    }
}
