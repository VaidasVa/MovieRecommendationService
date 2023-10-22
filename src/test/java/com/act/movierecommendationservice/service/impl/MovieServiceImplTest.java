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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieServiceImplTest {

    @Mock
    private MovieRepository repository;
    @Mock
    private MovieMapper mapper;
    @InjectMocks
    private MovieServiceImpl service;

    Movie movie1;
    Movie movie2;
    Movie movie3;
    MovieDAO movieDAO1;
    MovieDAO movieDAO2;
    MovieDAO movieDAO3;
    List<Movie> movieList;
    List<MovieDAO> movieDAOS;
    String id = "edd05833-bd90-4b1a-833a-ad7f14348d11";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movie1 = buildMovie("edd05833-bd90-4b1a-833a-ad7f14348d11", "movieTitle1", 2000, 3.5, 200, "");
        movie2 = buildMovie("edd05833-bd90-4b1a-833a-ad7f14348d12", "movieTitle2", 2002, 4.5, 200, "");
        movie3 = buildMovie("edd05833-bd90-4b1a-833a-ad7f14348d13", "movieTitle3", 2000, 3.5, 200, "");

        movieDAO1 = buildMovieDAO("edd05833-bd90-4b1a-833a-ad7f14348d11", "daoTitle1", 2000, 3.5, 200, "");
        movieDAO2 = buildMovieDAO("edd05833-bd90-4b1a-833a-ad7f14348d12", "daoTitle2", 2000, 3.5, 200, "");
        movieDAO3 = buildMovieDAO("edd05833-bd90-4b1a-833a-ad7f14348d13", "daoTitle3", 2000, 3.5, 200, "");

        movieList = List.of(movie1, movie2, movie3);
        movieDAOS = List.of(movieDAO1, movieDAO2, movieDAO3);
    }

    @Test
    @SneakyThrows
    void getAllMovies_positive() {
        when(repository.findAll()).thenReturn(movieDAOS);
        when(mapper.movieDAOtoMovie(movieDAO1)).thenReturn(movie1);
        when(mapper.movieDAOtoMovie(movieDAO2)).thenReturn(movie2);
        when(mapper.movieDAOtoMovie(movieDAO3)).thenReturn(movie3);

        Optional<List<Movie>> expected = Optional.of(movieList);
        Optional<List<Movie>> actual = service.getAllMovies().get();

        assertTrue(actual.isPresent());
        assertEquals(3, actual.get().size());
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getAllMovies_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        Optional<List<Movie>> expected = Optional.of(new ArrayList<>());
        Optional<List<Movie>> actual = service.getAllMovies().get();

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getMovieById_positive() {
        when(repository.findById(any())).thenReturn(Optional.of(movieDAO1));
        when(mapper.movieDAOtoMovie(movieDAO1)).thenReturn(movie1);

        Optional<Movie> expected = Optional.of(movie1);
        Optional<Movie> actual = service.getMovieById("edd05833-bd90-4b1a-833a-ad7f14348d11").get();

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getMovieById_notFound() {
        String movieId = "edd05833-bd90-4b1a-833a-ad7f14348d11";
        when(repository.findById(movieId))
                .thenReturn(Optional.empty());

        CompletionException completionException = assertThrows(CompletionException.class, () -> {
            CompletableFuture<Optional<Movie>> result = service.getMovieById(movieId);
            result.join();
        });

        Throwable cause = completionException.getCause();
        assertEquals(ResponseStatusException.class, cause.getClass());


        ResponseStatusException responseStatusException = (ResponseStatusException) cause;
        assertEquals(404, responseStatusException.getBody().getStatus());
        assertEquals("Movie not found", responseStatusException.getReason());
    }

    @ParameterizedTest
    @SneakyThrows
    @ValueSource(strings = {"daoTitle1", "daoTitle2", "daoTitle3"})
    void getMovieByTitle(String title) {
        when(repository.findAllByTitleIsLikeIgnoreCase(any())).thenReturn(movieDAOS);
        when(mapper.movieDAOtoMovie(movieDAO1)).thenReturn(movie1);
        when(mapper.movieDAOtoMovie(movieDAO2)).thenReturn(movie2);
        when(mapper.movieDAOtoMovie(movieDAO3)).thenReturn(movie3);

        Optional<List<Movie>> expected = Optional.of(movieList);
        Optional<List<Movie>> actual = service.getMovieByTitle(title).get();

        assertEquals(expected, actual);
        assertTrue(actual.isPresent());
        assertEquals(3, actual.get().size());
    }

    @ParameterizedTest
    @CsvSource({"daoTitle1, movieTitle1, true",
            "daoTitle2, movieTitle2, true",
            "daoTitle3, movieTitle3, true"})
    void postMovie(String movieDAO, String movie, boolean expected) {
        MovieDAO dao = movieDAOS.stream().filter(item -> item.getTitle().equals(movieDAO)).findFirst().get();
        Movie mov = movieList.stream().filter(item -> item.getTitle().equals(movie)).findFirst().get();

        when(repository.save(any())).thenReturn(dao);
        when(mapper.movieToMovieDAO(any())).thenReturn(dao);
        when(mapper.movieDAOtoMovie(any())).thenReturn(mov);

        boolean actual = service.postMovie(mov);

        assertEquals(expected, actual);
    }

    @Test
    void updateMovie_Positive() {
        Movie movieToUpdate = movie1;
        MovieDAO movieDAOToUpdate = movieDAO1;
        Movie updatedMovie = movie2;
        MovieDAO updatedMovieDAO = movieDAO2;

        when(repository.findById(movieToUpdate.getId())).thenReturn(Optional.of(movieDAOToUpdate));
        when(repository.save(movieDAOToUpdate)).thenReturn(updatedMovieDAO);
        when(mapper.movieDAOtoMovie(updatedMovieDAO)).thenReturn(updatedMovie);

        service.updateMovie(movie1);

        assertEquals("movieTitle2", updatedMovie.getTitle());
        assertEquals(4.5, updatedMovie.getAvgUserRating());
        assertEquals(2002, updatedMovie.getReleaseYear());
    }

    @Test
    void updateMovie_NotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.updateMovie(movie1));

        assertEquals(404, exception.getBody().getStatus());
        assertTrue(exception.getMessage().contains("Object not found for update"));

    }

    @Test
    void rateMovie() {
        Movie rateThisMovie = buildMovie(id, "movieTitle1", 2000, 0.0, 0, "");
        Movie ratedMovie = buildMovie(id, "movieTitle1", 2000, 5.0, 1, "");
        MovieDAO rateThisMovieDAO = buildMovieDAO(id, "movieTitle1", 2000, 0.0, 0, "");
        MovieDAO ratedMovieDAO = buildMovieDAO(id, "movieTitle1", 2000, 5.0, 1, "");

        when(repository.findById(id)).thenReturn(Optional.of(rateThisMovieDAO));
        when(repository.save(rateThisMovieDAO)).thenReturn(ratedMovieDAO);
        when(mapper.movieDAOtoMovie(rateThisMovieDAO)).thenReturn(ratedMovie);
        when(mapper.movieToMovieDAO(ratedMovie)).thenReturn(ratedMovieDAO);

        service.rateMovie(rateThisMovie.getId(), 5);

        assertEquals(ratedMovie.getAvgUserRating(), 5);
        assertEquals(ratedMovie.getRatingsTotalAmount(), 1);
    }

    @Test
    void getRating_Positive() {
        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(movieDAO1));
        when(mapper.movieDAOtoMovie(movieDAO1)).thenReturn(movie1);

        double rating = service.getRating(id);

        assertEquals(3.5, rating);
    }

    @Test
    void getRating_NotFound() {
        when(repository.existsById(id)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.deleteMovie(id));

        assertThrows(ResponseStatusException.class, () -> service.getRating(id));

        assertEquals(404, exception.getBody().getStatus());
    }

    @Test
    void deleteMove_Positive() {
        when(repository.existsById(id)).thenReturn(true);
        service.deleteMovie(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deleteMove_NotFound() {
        when(repository.existsById(id)).thenReturn(false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deleteMovie(id));

        assertEquals(404, exception.getBody().getStatus());
    }

    public Movie buildMovie(String id, String title, int releaseYear, double avgUserRating, int ratingsTotalAmount, String genre) {
        return Movie.builder()
                .id(id)
                .title(title)
                .releaseYear(releaseYear)
                .avgUserRating(avgUserRating)
                .ratingsTotalAmount(ratingsTotalAmount)
                .genre(genre)
                .build();
    }

    public MovieDAO buildMovieDAO(String id, String title, int releaseYear, double avgUserRating, int ratingsTotalAmount, String genre) {
        return MovieDAO.builder()
                .id(id)
                .title(title)
                .releaseYear(releaseYear)
                .avgUserRating(avgUserRating)
                .ratingsTotalAmount(ratingsTotalAmount)
                .genre(genre)
                .build();
    }
}