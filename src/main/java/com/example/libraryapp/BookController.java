package com.example.libraryapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;


@RestController
public class BookController{
  private final BookRepository repository;

  public BookController(BookRepository repository){
    this.repository = repository;

    if (repository.count() == 0) {
      repository.save(new Book("The Rust Programminng Language", "Steve Klabnik"));
      repository.save(new Book("Clean Code", "Robert C. Martin"));
    }
  }

  @GetMapping("/books")
  public List<Book> getAllBooks() {
    return repository.findAll();
  }


  @GetMapping("/books/titles")
  public Page<BookTitleOnly> getBookTitles(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    // Just give them what they want: anything that matches the strig
    return repository.findTitlesByAuthorContainingIgnoreCase(author, pageable);
  }


  // Strict Search
  @GetMapping("/books/search/exact")
  public Page<Book> searchByAuthorExact(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByAuthorIgnoreCase(author, pageable);
  }

  // Flexible Search
  @GetMapping("/books/search/contains")
  public Page<Book> searchByAuthorContains(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    return repository.findByAuthorContainingIgnoreCase(author, pageable);
  }


  @PostMapping("/books")
  public Book addBook(@Valid @RequestBody Book newBook) {
    // This sends a tiny "Ask" to the DB, not a request for the whole list
    if (repository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(newBook.getTitle(), newBook.getAuthor())) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "Strict Unique Violation: Book already exists."
    );
    }
    return repository.save(newBook);
  }

  @PutMapping("/books/{id}")
  public Book updateBook(@PathVariable UUID id, @RequestBody Book bookDetails) {
    return repository.findById(id).map(book -> {
      book.setTitle(bookDetails.getTitle());
      book.setAuthor(bookDetails.getAuthor());
      return repository.save(book);
    }).orElseThrow(() -> new RuntimeException("Book not found with id " + id));
  }

  @DeleteMapping("/books/{id}")
  public String deleteBook(@PathVariable UUID id){
    if(repository.existsById(id)){
      repository.deleteById(id);
      return "Book with ID " + id + " has been successfully deleted.";
    } else {
      return "Error: Book with ID "  +  id + " not found.";
    }
  }
}



