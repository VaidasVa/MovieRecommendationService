package com.act.movierecommendationservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private String id;
    private String title;
    private int releaseYear;
    private String genre;
    private double avgUserRating;
    private int ratingsTotalAmount;
}
