package com.michal.grud.movieCategorizationSystem;

import com.michal.grud.movieCategorizationSystem.domain.movie.controller.MovieResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class MovieCategorizationSystemApplicationTests {

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
        assertThat(context.getBean(MovieResource.class)).isNotNull();
    }

    @Test
    void main_shouldStartSpringContext() {
        MovieCategorizationSystemApplication.main(new String[]{
                "--spring.profiles.active=test",
                "--spring.main.web-application-type=none"
        });
    }
}
