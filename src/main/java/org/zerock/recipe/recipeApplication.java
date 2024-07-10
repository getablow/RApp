package org.zerock.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan("org.zerock.recipe.domain")
public class recipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(recipeApplication.class, args);
    }

}
