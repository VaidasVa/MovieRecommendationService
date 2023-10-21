package com.act.movierecommendationservice.service.impl;

import com.act.movierecommendationservice.mapper.MovieMapper;
import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.repository.MovieRepository;
import com.act.movierecommendationservice.service.RecommendationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// todo add logger

/**
 * Service class for recommendations based on:
 * (1) just watched movie (by its id) and
 * (2) user history (list of ids)
 */
@Service
@RequiredArgsConstructor
public class RecommendationsServiceImpl implements RecommendationsService {

    private final MovieRepository repository;
    private final MovieMapper mapper;

    /**
     * A method to get recommendations based on just watched movie.
     * First, it gets the movie by its ID, then it obtains all movies with the same genre.
     * Then it filters out the movie itself, sorts the list by release year and returns the first 5 movies.
     * If the list is shorter than 5, it gets the best rated movies and adds them to the list.
     *
     * @param movieId - movie ID
     * @return List<Movie> - recommendation list
     */
    @Override
    public List<Movie> getRecommendationsBasedOnJustWatchedMovie(String movieId) {
        Optional<Movie> movie = repository.findById(movieId).map(mapper::movieDAOtoMovie);
        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Movie> recommendationList = new ArrayList<>(repository.findAllByGenreIs(movie.get().getGenre())
                .stream()
                .map(mapper::movieDAOtoMovie)
                .filter(mov -> !mov.equals(movie.get()))
                .sorted(Comparator.comparingInt(Movie::getReleaseYear))
                .limit(5)
                .toList());

        if (recommendationList.size() < 5) {
            int remainingItems = 5 - recommendationList.size();
            List<Movie> bestRated = repository.findAllByOrderByAvgUserRating()
                    .stream()
                    .map(mapper::movieDAOtoMovie)
                    .limit(remainingItems)
                    .toList();

            recommendationList.addAll(bestRated);
        }
        return recommendationList;
    }

    /**
     * A method to get recommendations based on user history.
     * First, it gets the list of genres from the user history, then it counts the number of occurrences of each genre.
     * Then it sorts the list by the number of occurrences and returns the most popular genre.
     * Then it gets all movies with the most popular genre and returns the first 5 movies.
     * If the list is shorter than 5, it gets the best rated movies and adds them to the list.
     * If the user history is empty, it returns the 5 best rated movies.
     *
     * @param history - list of movie IDs, to imitate user history
     * @return List<Movie> - recommendation list
     */
    @Override
    public List<Movie> getRecommendationsBasedOnUserHistory(List<String> history) {
        List<String> genres = new ArrayList<>();
        if (!history.isEmpty()) {
            for (String movieID : history) {
                genres.add(repository.findById(movieID).get().getGenre());
            }

            Map<String, Long> genreCount = genres.stream()
                    .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

            Stream<Map.Entry<String, Long>> sorted =
                    genreCount.entrySet().stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));

            String popularGenre = sorted.toList().get(0).getKey();
            System.out.println(popularGenre);

            return repository.findAllByGenreIs(popularGenre)
                    .stream().map(mapper::movieDAOtoMovie).toList();
        } else {
            return repository.findAllByOrderByAvgUserRating().stream().map(mapper::movieDAOtoMovie).toList().subList(0, 5);
        }
    }
}