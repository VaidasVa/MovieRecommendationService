package com.act.movierecommendationservice.mapper;

import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.repository.MovieDAO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    Movie movieDAOtoMovie(MovieDAO movieDAO);

    MovieDAO movieToMovieDAO(Movie movie);
}
