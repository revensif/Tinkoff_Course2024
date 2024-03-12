package edu.java.bot.dto.request;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(@NotNull(message = "The link shouldn't be null") URI url) {
}
