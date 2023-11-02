package com.bmo.moviemicroservice.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieInfo {

    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @Positive(message = "year must be a positive number")
    private Integer year;

    private List<@NotBlank(message = "cast must not be blank") String> cast;

    @NotNull(message = "releaseDate cannot be null")
    private LocalDate releaseDate;

}
