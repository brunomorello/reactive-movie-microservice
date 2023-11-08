package com.bmo.moviemicroservice.client;

import com.bmo.moviemicroservice.domain.Movie;
import com.bmo.moviemicroservice.domain.MovieInfo;
import com.bmo.moviemicroservice.exception.MovieInfoNotFoundException;
import com.bmo.moviemicroservice.exception.MovieInfoServerException;
import com.bmo.moviemicroservice.util.RetrySpecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Component
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesInfoRestClient.class);

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(final String movieId) {
        var url = moviesInfoUrl + "/{id}";
        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    LOGGER.error("Status Code is: {}", clientResponse.statusCode().value());
                    if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
                        return Mono.error(new MovieInfoNotFoundException("Movie not found for id: " + movieId, clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new MovieInfoServerException(s, clientResponse.statusCode().value())));
                })
                .bodyToMono(MovieInfo.class)
                .retryWhen(RetrySpecUtils.retrySpec())
                .log();
    }

    public Flux<MovieInfo> movieInfoStream() {
        var url = moviesInfoUrl + "/stream";
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    LOGGER.error("Status Code is: {}", clientResponse.statusCode().value());
                    return Mono.error(new MovieInfoNotFoundException("Movie not found", clientResponse.statusCode().value()));
                })
                .bodyToFlux(MovieInfo.class)
                .log();
    }
}
