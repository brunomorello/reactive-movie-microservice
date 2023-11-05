package com.bmo.moviemicroservice.exception;

public class MovieInfoServerException extends RuntimeException {
    private String message;
    private int statusCode;

    public MovieInfoServerException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
