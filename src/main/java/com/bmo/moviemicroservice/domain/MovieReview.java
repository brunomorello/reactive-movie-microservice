package com.bmo.moviemicroservice.domain;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReview {
    private String id;
    @NotNull(message = "rating.move: value must not be null")
    private String moveInfoId;
    private String comment;
    @Min(value = 0l, message = "rating.negative: rating is negative, pls provide a positive value")
    private double rating;
}
