package com.example.libraryapp;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class BookService {
  private final BookRepository repository;

  public BookService(BookRepository repository){
    this.repository = repository;
  }
 
  public Page<Book> getAllBooks(Pageable pageable){
    return repository.findAll(pageable);
  }

  public Page<BookTitleOnly> getBookTitles(String author, Pageable pageable){
    return repository.findTitlesByAuthorContainingIgnoreCase(author, pageable);
  }

  public Page<Book> searchByAuthorExact(String author, Pageable pageable){
    return repository.findByAuthorIgnoreCase(author, pageable);
  }

  public Page<Book> searchByAuthorContains(String author, Pageable pageable){
    return repository.findByAuthorContainingIgnoreCase(author, pageable);
  }

  public Book addBook(Book newBook){
    if (repository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(newBook.getTitle(), newBook.getAuthor())){
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Strict Unique Violation: Book already exists."
        );
    }
    Book savedBook = repository.save(newBook);
    return savedBook;
  }

  public Book updateBook(UUID id, Book bookDetails){
    return repository.findById(id).map(book -> {
      book.setTitle(bookDetails.getTitle());
      book.setAuthor(bookDetails.getAuthor());
      return repository.save(book);
    }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id));
  }

  public void deleteBook(UUID id){
    if(!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: Book with ID " + id + " not found");
    }
    repository.deleteById(id);
  }
}