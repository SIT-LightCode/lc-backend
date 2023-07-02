package com.senior.dreamteam.repository;

import com.senior.dreamteam.model.Book;
import com.senior.dreamteam.model.ResponseMessage;
import graphql.GraphQLException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

    private final ResponseMessage responseMessage = new ResponseMessage("");
    private final ArrayList<Book> books = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public List<Book> findAll() {
        return books.stream().collect(Collectors.toList());
    }

    public Book findById(Integer id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst().orElse(null);
    }

    public Book createBook(Book book) {
        book.setId(nextId.getAndIncrement());
        books.add(book);
        return book;
    }

    public Book updateBook(Integer id, String title, Integer pages) {
        Book book = findById(id);
        if (book != null) {
            book.setTitle(title);
            book.setPages(pages);
            return book;
        } else {
            throw new GraphQLException("This Booking Id is not found");
        }
    }

    public ResponseMessage deleteBook(Integer id) {
        if (books.removeIf(book -> book.getId().equals(id))) {
            responseMessage.setMessage("Deleted");
            return responseMessage;
        } else {
            throw new GraphQLException("Something went wrong");
        }
    }

    @PostConstruct
    private void init() {
        books.add(new Book(1, "Reactive Spring", 484));
        books.add(new Book(2, "Spring Boot Up & Running", 328));
        books.add(new Book(3, "Hacking with Spring Boot 2.3", 392));
        nextId.set(4);
    }
}