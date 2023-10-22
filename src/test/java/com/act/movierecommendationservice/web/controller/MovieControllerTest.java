package com.act.movierecommendationservice.web.controller;

import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.repository.MovieDAO;
import com.act.movierecommendationservice.repository.MovieRepository;
import com.act.movierecommendationservice.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MovieController.class)
class MovieControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;
    @MockBean
    private MovieService service;
    @Mock
    private MovieRepository repository;


    private final static String BASE_URL = "http://localhost:8081/movie";
    List<Movie> movieList;
    MovieDAO movieDAO;


    @BeforeEach
    void setUp() {
        movieList = Arrays.asList(
                new Movie("uuid1", "Movie1", 2003, "Horror", 0, 0),
                new Movie("uuid2", "Movie3", 2004, "Horror", 1, 2)
        );

        movieDAO = new MovieDAO("uuid1", "Movie1", 2003, "Horror", 0, 0);
    }

    @Test
    void getAllMovies() throws Exception {

        when(service.getAllMovies())
                        .thenReturn(CompletableFuture.completedFuture(Optional.of(movieList)));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getMovieByTitle() throws Exception {

        when(service.getMovieByTitle(any(String.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(movieList)));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL+ "/title/{title}", "Movie1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getMovieByTitle_notFound() throws Exception {

        when(service.getMovieByTitle(any(String.class)))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(new ArrayList<>())));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL+ "/title/{title}", "Movie1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    void addMovie() {
        Movie newMovie = new Movie("uuid4", "Movie4", 2004, "Horror", 1, 2);
        when(service.postMovie(newMovie)).thenReturn(true);

        mockMvc.perform(post(BASE_URL + "/addMovie")
                        .content(mapper.writeValueAsString(newMovie))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SneakyThrows
    void addMovie_alreadyExists() {
        Movie newMovie = new Movie("uuid4", "Movie4", 2004, "Horror", 1, 2);
        when(repository.findAllByTitleIsLikeIgnoreCase(any()))
                .thenReturn(List.of(new MovieDAO("uuid4", "Movie4", 2004, "Horror", 1, 2)));

        mockMvc.perform(post(BASE_URL + "/addMovie")
                        .content(mapper.writeValueAsString(newMovie))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @SneakyThrows
    void rateMovie() {
        mockMvc.perform(MockMvcRequestBuilders.post("/movie/{id}/{rating}", "uuid1", 5)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SneakyThrows
    void rateMovie_BadRating() {
        mockMvc.perform(MockMvcRequestBuilders.post("/movie/{id}/{rating}", "uuid1", 6)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateMovie() throws Exception {
        Movie movie = movieList.get(0);
        when(repository.findById(movie.getId()))
                .thenReturn(Optional.of(movieDAO));


        mockMvc.perform(put(BASE_URL)
                        .content(mapper.writeValueAsString(movie))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteMovie_Positive() {
        when(repository.findById(movieList.get(0).getId()))
                .thenReturn(Optional.of(movieDAO));
        mockMvc.perform(delete(BASE_URL + "/" + movieList.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}