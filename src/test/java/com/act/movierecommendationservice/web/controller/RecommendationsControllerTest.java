package com.act.movierecommendationservice.web.controller;

import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.service.MovieService;
import com.act.movierecommendationservice.service.RecommendationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationsController.class)
class RecommendationsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private RecommendationsService service;
    @MockBean
    private MovieService movieService;

    private final static String BASE_URL = "http://localhost:8081/recommendations";

    List<Movie> movieList;

    @BeforeEach
    void setUp() {
        movieList = Arrays.asList(
                new Movie("uuid2", "Movie1", 2003, "Horror", 0, 0),
                new Movie("uuid3", "Movie3", 2004, "Horror", 1, 2),
                new Movie("uuid4", "Movie4", 2005, "Horror", 1, 2),
                new Movie("uuid5", "Movie5", 2006, "Horror", 1, 2),
                new Movie("uuid6", "Movie6", 2007, "Horror", 1, 2),
                new Movie("uuid7", "Movie7", 2008, "Horror", 1, 2)
        );


    }

    @Test
    void getRecommendations() throws Exception {
        Movie movie = new Movie("uuid1", "Movie1", 2003, "Horror", 0, 0);

        when(movieService.getMovieById("uuid1"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(movie)));
        when(service.getRecommendationsBasedOnJustWatchedMovie("uuid1"))
                .thenReturn(movieList.stream().limit(5).toList());

        mockMvc.perform(get(BASE_URL + "/uuid1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void getRecommendationsBasedOnHistory() throws Exception {
        List<String> history = List.of("uuid1", "uuid2", "uuid3", "uuid4", "uuid5", "uuid6", "uuid7");

        when(service.getRecommendationsBasedOnUserHistory(history))
                .thenReturn(movieList.stream().limit(5).toList());

        mockMvc.perform(get(BASE_URL + "/history")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(history)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void getRecommendationsBasedOnHistory_EmptyThenReturnAnotherList() throws Exception {
        List<Movie> popMovieList = Arrays.asList(
                new Movie("uuid8", "Movie1", 2003, "Horror", 0, 0),
                new Movie("uuid9", "Movie3", 2004, "Horror", 1, 2),
                new Movie("uuid10", "Movie4", 2005, "Horror", 1, 2),
                new Movie("uuid11", "Movie5", 2006, "Horror", 1, 2),
                new Movie("uuid12", "Movie6", 2007, "Horror", 1, 2),
                new Movie("uuid13", "Movie7", 2008, "Horror", 1, 2)
        );

        List<Movie> limitedList = popMovieList.stream().limit(5).toList();

        when(service.getRecommendationsBasedOnUserHistory(List.of()))
                .thenReturn(limitedList);
        when(service.getRecommendationsBasedOnUserHistory(null))
                .thenReturn(limitedList);

        mockMvc.perform(get(BASE_URL + "/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }
}