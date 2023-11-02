package com.bmo.moviemicroservice.client;

import com.bmo.moviemicroservice.domain.MovieReview;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class MoviesReviewRestClient {

    private WebClient webClient;

    @Value("${restClient.moviesReviewUrl}")
    private String moviesReviewUrl;

    public MoviesReviewRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<MovieReview> retrieveReviews(final String movieId) {
        final String url = UriComponentsBuilder.fromUriString(moviesReviewUrl)
                .queryParam("moveInfoId", movieId)
                .buildAndExpand()
                .toString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(MovieReview.class)
                .log();
    }
}
