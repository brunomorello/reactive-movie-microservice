package com.bmo.moviemicroservice.controller;

import com.bmo.moviemicroservice.client.MoviesInfoRestClient;
import com.bmo.moviemicroservice.client.MoviesReviewRestClient;
import com.bmo.moviemicroservice.domain.Movie;
import com.bmo.moviemicroservice.domain.MovieReview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/movies")
public class MovieController {

    private MoviesInfoRestClient moviesInfoRestClient;
    private MoviesReviewRestClient moviesReviewRestClient;

    public MovieController(MoviesInfoRestClient moviesInfoRestClient, MoviesReviewRestClient moviesReviewRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.moviesReviewRestClient = moviesReviewRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") final String id) {
        return moviesInfoRestClient.retrieveMovieInfo(id)
                .flatMap(movieInfo -> {
                    Mono<List<MovieReview>> movieReviewList = moviesReviewRestClient.retrieveReviews(id).collectList();
                    return movieReviewList.map(movieReviews -> new Movie(movieInfo, movieReviews));
                });
    }
}
