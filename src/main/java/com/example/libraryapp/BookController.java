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
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/books")
public class BookController{
  private final BookRepository repository;

  public BookController(BookRepository repository){
    this.repository = repository;
  }

  @GetMapping
  public ResponseEntity<Page<Book>> getAllBooks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) { 
    Pageable pageable = PageRequest.of(page,size);
    return ResponseEntity.ok(repository.findAll(pageable));
  }


  @GetMapping("/titles")
  public ResponseEntity<Page<BookTitleOnly>> getBookTitles(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(repository.findTitlesByAuthorContainingIgnoreCase(author, pageable));
  }


  // Strict Search
  @GetMapping(params = "author")
  public ResponseEntity<Page<Book>> searchByAuthorExact(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(repository.findByAuthorIgnoreCase(author, pageable));
  }

  // Flexible Search
  @GetMapping(params = "authorContains")
  public ResponseEntity<Page<Book>> searchByAuthorContains(
    @RequestParam("authorContains") String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(repository.findByAuthorContainingIgnoreCase(author, pageable));
  }


  @PostMapping
  public ResponseEntity<Book> addBook(@Valid @RequestBody Book newBook) {
    // This sends a tiny "Ask" to the DB, not a request for the whole list
    if (repository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(newBook.getTitle(), newBook.getAuthor())) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT, "Strict Unique Violation: Book already exists."
    );
    }
    Book savedBook = repository.save(newBook);
    // Returns 201 created
    return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable UUID id, @Valid @RequestBody Book bookDetails) {
    return repository.findById(id).map(book -> {
      book.setTitle(bookDetails.getTitle());
      book.setAuthor(bookDetails.getAuthor());
      return ResponseEntity.ok(repository.save(book));
    }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteBook(@PathVariable UUID id){
    if(repository.existsById(id)){
      repository.deleteById(id);
      return ResponseEntity.noContent().build();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: Book with ID " + " not found.");
    }
  }
}



