package com.bmo.moviemicroservice.exception;

public class MovieInfoClientException extends RuntimeException {
    private String message;
    private int statusCode;

    public MovieInfoClientException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
