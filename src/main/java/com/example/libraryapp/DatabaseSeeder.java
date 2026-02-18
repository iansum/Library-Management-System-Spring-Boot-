package com.example.libraryapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration tells Spring: "This class contains setup instructions"
@Configuration
public class DatabaseSeeder {
    // @Bean tells Spring to manage the lifecycle of this runner
    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            // The exact same logic, just in mathematically correct location
            if (repository.count() == 0){
                repository.save(new Book("The Rust Programming Language", "Steve Klabnik"));
                repository.save(new Book("Clean Code", "Robert C. Martin"));
                // System.out.println("Database seeded successfully!");
            }
        };
    }
}
