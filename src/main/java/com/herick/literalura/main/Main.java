package com.herick.literalura.main;

import com.herick.literalura.model.Book;
import com.herick.literalura.model.Info;
import com.herick.literalura.repository.BookRepository;
import com.herick.literalura.service.DataConvert;
import com.herick.literalura.service.RequestAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private RequestAPI requestAPI = new RequestAPI();
    private DataConvert dataConvert = new DataConvert();
    private List<Book> books = new ArrayList<>();
    private BookRepository repository;
    private final String URL = "https://gutendex.com/books";

    public Main(BookRepository repository) {
        this.repository = repository;
    }

    public void menu() {
        boolean exit = false;
        int option;

        while (!exit) {
            var menu = """
                    1. Buscar Livro por título
                    2. Listar livros registrados
                    3. Listar autores registrados
                    4. Listar autores vivos em determinado ano
                    5. Listar livros por idiomas
                    0. Sair
                    """;
            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.println("Digite o título do livro:");
                    searchBookByTitle(scanner.nextLine());
                    break;
                case 2:
                    listBooks();
                    break;
                case 3:
                    listAuthors();
                    break;
                case 4:
                    listAuthorsAliveInYear();
                    break;
                case 5:
                    listBooksByLanguages();
                    break;
                case 0:
                    exit = true;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private Info getBooksFromAPI(String title) {
        var json = requestAPI.getData(URL + "?search=%10" + title.replace(" ", "+"));
        return dataConvert.getData(json, Info.class);
    }

    private Optional<Book> getBookData(Info info, String title) {
        return info.results().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .map(b -> new Book(
                        b.getTitle(),
                        b.getAuthor(),
                        b.getDownloads(),
                        b.getLanguage()
                ))
                .findFirst();
    }

    private Optional<Book> searchBookByTitle(String title) {
        Info infoBook = getBooksFromAPI(title);
        Optional<Book> book = getBookData(infoBook, title);

        if (book.isPresent()) {
            var result = repository.save(book.get());
            System.out.println(result);
        } else {
            System.out.println("Livro não encontrado");
        }

        return book;
    }

    private void listBooks() {
        books = repository.findAll();
        books.forEach(System.out::println);
    }

    private void listAuthors() {
        books = repository.findAll();
        books.stream()
                .map(Book::getAuthor)
                .distinct()
                .forEach(System.out::println);
    }

    private void listAuthorsAliveInYear() {
        System.out.println("Digite o ano:");
        int year = scanner.nextInt();
        scanner.nextLine();

        books = repository.findAll();
        books.stream()
                .filter(b -> b.getAuthor().contains("(" + year))
                .map(Book::getAuthor)
                .distinct()
                .forEach(System.out::println);
    }

    private void listBooksByLanguages() {
        books = repository.findAll();
        books.stream()
                .map(Book::getLanguage)
                .distinct()
                .forEach(System.out::println);
    }

}
