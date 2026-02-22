package com.example.libraryapp;


public class BookAlreadyExistsException extends RuntimeException {
  public BookAlreadyExistsException(Book bookDetails){
    super("Book with title: " + bookDetails.getTitle() + ", author: " + bookDetails.getAuthor() + " already exist.");
  }
}
