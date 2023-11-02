package com.bmo.moviemicroservice.exception;

public class MovieReviewClientException extends RuntimeException {
    public MovieReviewClientException(String message) {
        super(message);
    }
}
