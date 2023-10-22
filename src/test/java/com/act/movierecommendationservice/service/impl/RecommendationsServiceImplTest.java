package com.act.movierecommendationservice.service.impl;

import com.act.movierecommendationservice.mapper.MovieMapper;
import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.repository.MovieDAO;
import com.act.movierecommendationservice.repository.MovieRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RecommendationsServiceImplTest {

    @Mock
    private MovieRepository repository;
    @Mock
    private MovieMapper mapper;
    @InjectMocks
    RecommendationsServiceImpl service;

    MovieServiceImplTest builders = new MovieServiceImplTest();

    String id = "movieId";
    Movie movie;
    MovieDAO movieDAO;
    List<Movie> movieList;
    List<MovieDAO> movieDAOList;
    List<MovieDAO> bestRatedMoviesDAO;
    List<Movie> bestRatedMovies;
    List<Movie> horrorList;
    List<MovieDAO> horrorListDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movie = builders.buildMovie("movieId", "Action Movie1", 2011, 4.0, 100, "Horror");
        movieDAO = builders.buildMovieDAO("movieId", "Action Movie1", 2011, 4.0, 100, "Horror");
        movieList = List.of(
                builders.buildMovie("movieId1", "Action Movie 1", 2011, 4.0, 100, "Horror"),
                builders.buildMovie("movieId2", "Action Movie 2", 2012, 4.5, 120, "Horror"),
                builders.buildMovie("movieId3", "Action Movie 3", 2010, 3.5, 80, "Horror")
        );
        movieDAOList = List.of(
                builders.buildMovieDAO("movieId1", "Action Movie 1", 2011, 4.0, 100, "Horror"),
                builders.buildMovieDAO("movieId2", "Action Movie 2", 2012, 4.5, 120, "Horror"),
                builders.buildMovieDAO("movieId3", "Action Movie 3", 2010, 3.5, 80, "Horror")
        );

        bestRatedMovies = List.of(
                builders.buildMovie("movieId4", "Best Rated Movie 1", 2008, 4.8, 150, "Horror"),
                builders.buildMovie("movieId5", "Best Rated Movie 2", 2015, 4.7, 200, "Horror"),
                builders.buildMovie("movieId6", "Best Rated Movie 3", 2014, 4.6, 180, "Horror"),
                builders.buildMovie("movieId7", "Best Rated Movie 4", 2013, 4.5, 170, "Horror"),
                builders.buildMovie("movieId8", "Best Rated Movie 5", 2012, 4.4, 160, "Horror"),
                builders.buildMovie("movieId9", "Best Rated Movie 6", 2011, 4.3, 150, "Horror")
        );

        bestRatedMoviesDAO = List.of(
                builders.buildMovieDAO("movieId4", "Best Rated Movie 1", 2008, 4.8, 150, "Horror"),
                builders.buildMovieDAO("movieId5", "Best Rated Movie 2", 2015, 4.7, 200, "Horror"),
                builders.buildMovieDAO("movieId6", "Best Rated Movie 3", 2014, 4.6, 180, "Horror"),
                builders.buildMovieDAO("movieId7", "Best Rated Movie 4", 2013, 4.5, 170, "Horror"),
                builders.buildMovieDAO("movieId8", "Best Rated Movie 5", 2012, 4.4, 160, "Horror"),
                builders.buildMovieDAO("movieId9", "Best Rated Movie 6", 2011, 4.3, 150, "Horror")
        );

        horrorList = List.of(
                builders.buildMovie("e4e4e4e4-e4e4-4e4e-e4e4-e4e4e4e4e4e4", "Horror Movie 1", 2011, 4.0, 100, "Horror"),
                builders.buildMovie("f4f4f4f4-f4f4-4f4f-4f4f-4f4f4f4f4f4f", "Horror Movie 2", 2012, 4.5, 120, "Horror"),
                builders.buildMovie("m2m2m2m2-m2m2-2m2m-m2m2-m2m2m2m2m2m2", "Horror Movie 3", 2010, 3.5, 80, "Horror"));

        horrorListDAO = List.of(
                builders.buildMovieDAO("e4e4e4e4-e4e4-4e4e-e4e4-e4e4e4e4e4e4", "Horror Movie 1", 2011, 4.0, 100, "Horror"),
                builders.buildMovieDAO("f4f4f4f4-f4f4-4f4f-4f4f-4f4f4f4f4f4f", "Horror Movie 2", 2012, 4.5, 120, "Horror"),
                builders.buildMovieDAO("m2m2m2m2-m2m2-2m2m-m2m2-m2m2m2m2m2m2", "Horror Movie 3", 2010, 3.5, 80, "Horror"));
    }


    @ParameterizedTest
    @CsvSource({"2010,3.5,0", "2011,4.0,1", "2012,4.5,2", "2008,4.8,3", "2015,4.7,4"})
    void getRecommendationsBasedOnJustWatchedMovie(int year, double rating, int position) {
        when(repository.findById(id)).thenReturn(Optional.ofNullable(movieDAO));
        when(mapper.movieDAOtoMovie(movieDAO)).thenReturn(movie);
        when(repository.findAllByGenreIs(movieDAO.getGenre())).thenReturn(movieDAOList);
        when(repository.findAllByOrderByAvgUserRating()).thenReturn(bestRatedMoviesDAO);

        for (MovieDAO movieDAO : movieDAOList) {
            when(mapper.movieDAOtoMovie(movieDAO)).thenReturn(movieList.get(movieDAOList.indexOf(movieDAO)));
        }
        for (MovieDAO movieDAO : bestRatedMoviesDAO) {
            when(mapper.movieDAOtoMovie(movieDAO)).thenReturn(bestRatedMovies.get(bestRatedMoviesDAO.indexOf(movieDAO)));
        }

        List<Movie> recommendations = service.getRecommendationsBasedOnJustWatchedMovie(id);

        assertNotNull(recommendations);
        assertEquals(5, recommendations.size());
        assertEquals(year, recommendations.get(position).getReleaseYear());
        assertEquals(rating, recommendations.get(position).getAvgUserRating());
    }

    @Test
    void getRecommendationsBasedOnJustWatchedMovieNotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> service.getRecommendationsBasedOnJustWatchedMovie(id));
        assertSame(ResponseStatusException.class, exception.getClass());
        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    @SneakyThrows
    void getRecommendationsBasedOnUserHistory() {
        List<String> history = List.of("movieId1", "movieId2", "movieId3");

        for (String historyItem : history) {
            when(repository.findById(historyItem)).thenReturn(Optional.ofNullable(movieDAOList.get(history.indexOf(historyItem))));
        }
        for (MovieDAO movieDAO : movieDAOList) {
            when(mapper.movieDAOtoMovie(movieDAO)).thenReturn(movieList.get(movieDAOList.indexOf(movieDAO)));
        }

        when(repository.findAllByGenreIs("Horror")).thenReturn(horrorListDAO);
        for (MovieDAO movieDao : horrorListDAO) {
            when(mapper.movieDAOtoMovie(movieDao)).thenReturn(horrorList.get(horrorListDAO.indexOf(movieDao)));
        }

        List<Movie> recommendations = service.getRecommendationsBasedOnUserHistory(history);

        assertEquals(3, recommendations.size());
    }

    @Test
    void getRecommendationsBasedOnUserHistoryEmptyHistory() {
        List<String> history = new ArrayList<>();
        when(repository.findAllByOrderByAvgUserRating()).thenReturn(bestRatedMoviesDAO);

        for (MovieDAO movieDAO : bestRatedMoviesDAO) {
            when(mapper.movieDAOtoMovie(movieDAO)).thenReturn(bestRatedMovies.get(bestRatedMoviesDAO.indexOf(movieDAO)));
        }

        List<Movie> recommendations = service.getRecommendationsBasedOnUserHistory(history);

        assertEquals(5, recommendations.size());
        assertEquals(recommendations.get(0), bestRatedMovies.get(0));
    }
}