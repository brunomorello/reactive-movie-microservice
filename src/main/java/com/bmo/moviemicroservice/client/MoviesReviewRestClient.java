package com.bmo.moviemicroservice.client;

import com.bmo.moviemicroservice.domain.MovieReview;
import com.bmo.moviemicroservice.exception.MovieReviewClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MoviesReviewRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesInfoRestClient.class);

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
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    LOGGER.error("Status code is: {}", clientResponse.statusCode().value());
                    if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new MovieReviewClientException("Error to Retrieve Movie Review: " + s)));
                })
                .bodyToFlux(MovieReview.class)
                .log();
    }
}
