package com.bmo.moviemicroservice.client;

import com.bmo.moviemicroservice.domain.MovieInfo;
import com.bmo.moviemicroservice.exception.MovieInfoClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                        return Mono.error(new MovieInfoClientException("Movie not found for id: " + movieId, clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new MovieInfoClientException("Error to retrieve Movie info: " + s, clientResponse.statusCode().value())));
                })
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
