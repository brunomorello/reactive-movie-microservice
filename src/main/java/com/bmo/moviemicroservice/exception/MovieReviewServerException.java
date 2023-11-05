package com.bmo.moviemicroservice.exception;

public class MovieReviewServerException extends RuntimeException {
    public MovieReviewServerException(String message) {
        super(message);
    }
}
