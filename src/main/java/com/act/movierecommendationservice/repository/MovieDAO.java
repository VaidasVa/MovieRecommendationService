package com.act.movierecommendationservice.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movies")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDAO {

    @Id
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Title is a mandatory field")
    @NotNull
    private String title;

    @NotNull
    @Column(name = "release_year")
    private int releaseYear;

    @NotBlank(message = "Genre is a mandatory field")
    @NotNull
    private String genre;

    @Column(name = "user_rating")
    private double avgUserRating;

    @Column(name = "amount_of_ratings")
    private int ratingsTotalAmount;
}
