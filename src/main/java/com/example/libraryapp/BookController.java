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
  private final BookService bookService;

  public BookController(BookService bookService){
    this.bookService = bookService;
  }

  @GetMapping
  public ResponseEntity<Page<Book>> getAllBooks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) { 
    Pageable pageable = PageRequest.of(page,size);
    return ResponseEntity.ok(bookService.getAllBooks(pageable));
  }


  @GetMapping("/titles")
  public ResponseEntity<Page<BookTitleOnly>> getBookTitles(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(bookService.getBookTitles(author, pageable));
  }


  @GetMapping(params = "author")
  public ResponseEntity<Page<Book>> searchByAuthorExact(
    @RequestParam String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(bookService.searchByAuthorExact(author, pageable));
  }

  @GetMapping(params = "authorContains")
  public ResponseEntity<Page<Book>> searchByAuthorContains(
    @RequestParam("authorContains") String author,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ){
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(bookService.searchByAuthorContains(author, pageable));
  }


  @PostMapping
  public ResponseEntity<Book> addBook(@Valid @RequestBody Book newBook) {
    Book savedBook = bookService.addBook(newBook);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable UUID id, @Valid @RequestBody Book bookDetails) {
    return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id){
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}



    