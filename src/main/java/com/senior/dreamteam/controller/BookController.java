package com.senior.dreamteam.controller;

import com.senior.dreamteam.model.Book;
import com.senior.dreamteam.model.ResponseMessage;
import com.senior.dreamteam.repository.BookRepository;
import com.senior.dreamteam.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;


    @SchemaMapping(typeName = "Query",value = "allBooks")
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book findById(@Argument Integer id) {
        return bookRepository.findById(id);
    }

    @MutationMapping
    public Book createBook(@Argument("title") String title, @Argument("pages") Integer pages) {
        return bookRepository.createBook(new Book(0, title, pages));
    }

    @MutationMapping
    public Book updateBook(@Argument("id") Integer id, @Argument("title") String title, @Argument("pages") Integer pages) throws Throwable {
        return bookRepository.updateBook(id, title, pages);
    }

    @MutationMapping
    public ResponseMessage deleteBook(@Argument("id") Integer id) {
        return bookRepository.deleteBook(id);
    }

    @QueryMapping
    public ResponseMessage testAnswer(@Argument("questionId") Integer questionId, @Argument("code") String code) {
        return bookService.testAnswer(questionId, code);
    }
}