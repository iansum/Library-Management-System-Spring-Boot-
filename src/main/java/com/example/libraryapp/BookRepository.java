package com.example.libraryapp;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BookRepository extends JpaRepository<Book, UUID>{
  // This looks long, but it's a specific "Language" Spring Understands
  // Foe oue "Strict Unique" check
  boolean existsByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

  // This now supports Pagination automatically!
  Page<Book> findAll(Pageable pageable);

  // // Optimized Search Exact Match
  // Page<Book> findByAuthorIgnoreCase(String author, Pageable pageable);

  // You can even paginate your searches
  Page<Book> findByAuthorIgnoreCase(String author, Pageable pageable);

  Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

  // The "Uttimate Optimization"
  // Spring translates this to: SELECT title FROM book WHERE author = ...
  // For title-only-projection -- different method name:
  Page<BookTitleOnly> findTitlesByAuthorContainingIgnoreCase(String author, Pageable pageable);
}
