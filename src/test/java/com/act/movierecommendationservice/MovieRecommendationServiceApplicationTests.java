package com.act.movierecommendationservice;

import com.act.movierecommendationservice.service.MovieService;
import com.act.movierecommendationservice.service.RecommendationsService;
import com.act.movierecommendationservice.web.controller.MovieController;
import com.act.movierecommendationservice.web.controller.RecommendationsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import static org.springframework.test.util.AssertionErrors.assertTrue;


@SpringBootTest
@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'local'}", loadContext = true)
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
    @EnabledIf(expression = "#{environment['spring.profiles.active'] == 'local'}", loadContext = true)
    void contextLoads() {
        assertTrue("MovieController is not null", movieService != null);
        assertTrue("MovieController is not null", movieController != null);
        assertTrue("RecommendationsService is not null", recommendationsService != null);
        assertTrue("RecommendationsController is not null", recommendationsController != null);

    }

}
