package com.example.libraryapp;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Version;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
  name= "books",
  indexes = {
    // Keep these single
     // Fast O(log n) searches by author alone
    @Index(name="idx_author", columnList="author"),
    // Fast O(log n) searches by title alone
    @Index(name ="idx_title", columnList = "title")
  },
  uniqueConstraints = {
    // The "Bouncer": Prevents Duplicate pairs AND creates a Composite Index automatically
    @UniqueConstraint(columnNames={"title", "author"})
})
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  @NotBlank(message = "Title cannot be empty")
  @Size(min = 2, max = 200, message = "Title must be between 2  and 200 characters")
  @Column(nullable = false)
  private String title;


  @NotBlank(message= "Author cannot be empty")
  @Column(nullable = false)
  private String author;

  // The "Git" Commit "Hash" for Optimistic Locking (Concurrency control)
  @Version
  private Integer version;



  //-----------------------------------
  // Constructor
  //-----------------------------------


  // JPA-ony constructor (hidden form the rest of the app)
  protected Book(){}

  // The constructor your app should actually use
  public Book(String title, String author){
    this.title = title;
    this.author = author;
  }
  
  public UUID getId() { return id; }
  // We intentionally omit setId() because the DB generates it.
  public String getTitle() { return title; }
  public String getAuthor() { return author; }
  public void setTitle(String title){ this.title = title; }
  public void setAuthor(String author){ this.author = author; }

}
