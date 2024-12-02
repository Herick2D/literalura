package com.herick.literalura.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "book")
    private List<Author> author;
    private Double downloads;
    private Languages language;

    public Book() {
    }

    public Book(String title, List<Author> author, Double downloads, Languages language) {
        this.title = title;
        this.author = author;
        this.downloads = downloads;
        this.language = language;
    }
}
