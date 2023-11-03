package com.bmo.moviemicroservice.controller;

import com.bmo.moviemicroservice.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/moviesInfo",
        "restClient.moviesReviewUrl=http://localhost:8084/v1/reviews"
})
class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void when_retrieveMovieById_then_return_it() {
        // Given
        var movieId = "123Abc";

        stubFor(get(urlEqualTo("/v1/moviesInfo/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviereview.json")));

        // When
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {

                    // Then
                    Movie responseBody = movieEntityExchangeResult.getResponseBody();

                    assertNotNull(responseBody);
                    assertEquals(2, responseBody.getMovieReview().size());
                });
    }

    @Test
    void when_retrieveMovieById_and_moviesInfo_is_404_then_return_404() {
        // Given
        var movieId = "123Abc";

        stubFor(get(urlEqualTo("/v1/moviesInfo/" + movieId))
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("moviereview.json")));

        // When
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String responseBody = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals("Movie not found for id: 123Abc", responseBody);
                });
    }

    @Test
    void when_retrieveMovieById_and_moviesReview_is_404_then_return_ok_with_empty_reviews() {
        // Given
        var movieId = "123Abc";

        stubFor(get(urlEqualTo("/v1/moviesInfo/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

        // When
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    // Then
                    final Movie responseBody = movieEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertTrue(responseBody.getMovieReview().isEmpty());
                });
    }

    @Test
    void when_retrieveMovieById_and_moviesInfo_is_5xx_then_return_5xx() {
        // Given
        var movieId = "123Abc";

        stubFor(get(urlEqualTo("/v1/moviesInfo/" + movieId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withBody("Error to retrieve Movie info")));

        // When
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    String responseBody = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals("Error to retrieve Movie info: Error to retrieve Movie info", responseBody);
                });
    }
}