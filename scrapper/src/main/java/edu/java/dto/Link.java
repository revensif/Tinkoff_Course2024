package edu.java.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(long linkId, URI url, OffsetDateTime updatedAt) {
}
