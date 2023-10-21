package com.act.movierecommendationservice.service.impl;

import com.act.movierecommendationservice.mapper.MovieMapper;
import com.act.movierecommendationservice.model.Movie;
import com.act.movierecommendationservice.repository.MovieRepository;
import com.act.movierecommendationservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// todo add logger

/**
 * Basic CRUD for movie object + movie rating methods
 * Wanted to try out CompletableFuture, therefore some methods are implemented with it
 */

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;
    private final MovieMapper mapper;
    private String id;

    /**
     * Returns all movies in the database
     *
     * @return CompletableFuture<Optional < List < Movie>>>
     */
    @Override
    public CompletableFuture<Optional<List<Movie>>> getAllMovies() {
        return CompletableFuture.supplyAsync(() ->
                Optional.of(repository.findAll()
                        .stream()
                        .map(mapper::movieDAOtoMovie)
                        .toList())
        );
    }

    /**
     * Method to obtain movie by ID, if found
     *
     * @param id - movie ID
     * @return CompletableFuture<Optional < Movie>>
     */
    @Override
    public CompletableFuture<Optional<Movie>> getMovieById(String id) {
        return CompletableFuture.supplyAsync(() ->
                Optional.ofNullable(repository.findById(id)
                        .map(mapper::movieDAOtoMovie)
                        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movie not found"))));
    }

    /**
     * Return movie by its title, or title fragment
     *
     * @param title - movie title/fragment
     * @return CompletableFuture<Optional < List < Movie>>>
     */
    @Override
    public CompletableFuture<Optional<List<Movie>>> getMovieByTitle(String title) {
        return CompletableFuture.supplyAsync(() ->
                Optional.of(repository.findAllByTitleIsLikeIgnoreCase("%" + title + "%")
                        .stream()
                        .map(mapper::movieDAOtoMovie)
                        .toList()));
    }

    /**
     * Method to add movie to the database. Checks if movie already exists in the database by title and release year
     *
     * @param movie validated in the controller
     * @return boolean value: true if movie was added, false if movie already exists
     */
    @Override
    public boolean postMovie(Movie movie) {
        Optional<Movie> existingMovie = repository.findAllByTitleIsLikeIgnoreCase(movie.getTitle())
                .stream()
                .filter(item -> (item.getTitle().equalsIgnoreCase(movie.getTitle()) &&
                        item.getReleaseYear() == movie.getReleaseYear()))
                .map(mapper::movieDAOtoMovie)
                .findFirst();

        System.out.println(existingMovie);

        if (existingMovie.isEmpty()) {
            movie.setId(UUID.randomUUID().toString());
            mapper.movieDAOtoMovie(repository.save(mapper.movieToMovieDAO(movie)));
            return true;
        } else return false;
    }

    /**
     * A method to update existing movie. Checks if movie exists in the database by ID and then updates it.
     *
     * @param movie validated in the controller
     */
    @Override
    public void updateMovie(Movie movie) {
        AtomicReference<Optional<Movie>> atomicReference = new AtomicReference<>();

        repository.findById(movie.getId()).ifPresentOrElse(
                found -> {
                    found.setTitle(movie.getTitle());
                    found.setGenre(movie.getGenre());
                    found.setReleaseYear(movie.getReleaseYear());
                    atomicReference.set(Optional.of(mapper.movieDAOtoMovie(repository.save(found))));
                },
                () -> {
                    throw new ResponseStatusException(NOT_FOUND, "Object not found for update");
                }
        );
    }

    /**
     * A method to rate a movie. Checks if movie exists in the database by ID and then updates its rating.
     *
     * @param id      - movie ID
     * @param newRate - new rating for the movie (1-5), validated in the controller
     */
    @Override
    public void rateMovie(String id, int newRate) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        repository.findById(id).ifPresentOrElse(
                found -> {
                    double newAvg = (found.getRatingsTotalAmount() * found.getAvgUserRating() + newRate) / (found.getRatingsTotalAmount() + 1);
                    found.setAvgUserRating(Double.parseDouble(decimalFormat.format(newAvg)));
                    found.setRatingsTotalAmount(found.getRatingsTotalAmount() + 1);
                    updateMovie(mapper.movieDAOtoMovie(found));
                }, () -> {
                    throw new ResponseStatusException(NOT_FOUND, "Movie not found");
                });
    }

    /**
     * A method to get movie rating. Checks if movie exists in the database by ID and then returns its rating.
     *
     * @param id - movie ID
     * @return
     */
    @Override
    public double getRating(String id) {
        if (repository.existsById(id)) return repository.findById(id).get().getAvgUserRating();
        else throw new ResponseStatusException(NOT_FOUND, "Movie not found");
    }

    /**
     * A method to delete movie from the database. Checks if movie exists in the database by ID and then deletes it.
     *
     * @param id - movie ID
     */
    @Override
    public void deleteMove(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else throw new ResponseStatusException(NOT_FOUND);
    }
}
