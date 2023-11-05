package com.bmo.moviemicroservice.exception;

public class MovieReviewNotFoundException extends RuntimeException {
    public MovieReviewNotFoundException(String message) {
        super(message);
    }
}
