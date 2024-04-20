package edu.java.bot.retry.policy;

import edu.java.bot.retry.backoff.RetryBackoff;
import java.util.List;
import org.springframework.http.HttpStatusCode;

public record RetryPolicy(List<HttpStatusCode> statuses, RetryBackoff retryBackoff) {
}
