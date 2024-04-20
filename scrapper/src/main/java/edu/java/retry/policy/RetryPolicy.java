package edu.java.retry.policy;

import edu.java.retry.backoff.RetryBackoff;
import java.util.List;
import org.springframework.http.HttpStatusCode;

public record RetryPolicy(List<HttpStatusCode> statuses, RetryBackoff retryBackoff) {
}
