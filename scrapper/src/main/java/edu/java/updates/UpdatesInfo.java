package edu.java.updates;

import java.time.OffsetDateTime;

public record UpdatesInfo(boolean isSomethingUpdated, OffsetDateTime updatedAt, String message) {
}
