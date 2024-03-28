package org.zerock.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class recipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(recipeApplication.class, args);
    }

}
