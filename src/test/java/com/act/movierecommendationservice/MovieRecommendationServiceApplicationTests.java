package com.act.movierecommendationservice;

import com.act.movierecommendationservice.service.MovieService;
import com.act.movierecommendationservice.service.RecommendationsService;
import com.act.movierecommendationservice.web.controller.MovieController;
import com.act.movierecommendationservice.web.controller.RecommendationsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertTrue;


@SpringBootTest(properties = "spring.profiles.active=local")
class MovieRecommendationServiceApplicationTests {

    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieController movieController;
    @Autowired
    private RecommendationsService recommendationsService;
    @Autowired
    private RecommendationsController recommendationsController;

    @Test
    void contextLoads() {
        assertTrue("MovieController is not null", movieService != null);
        assertTrue("MovieController is not null", movieController != null);
        assertTrue("RecommendationsService is not null", recommendationsService != null);
        assertTrue("RecommendationsController is not null", recommendationsController != null);

    }

}
