package com.bmo.moviemicroservice.util;

import com.bmo.moviemicroservice.exception.MovieInfoServerException;
import com.bmo.moviemicroservice.exception.MovieReviewServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetrySpecUtils {
    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof MovieInfoServerException || throwable instanceof MovieReviewServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }
}
