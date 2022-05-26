package org.todeschini.libaryapi.service;

import org.todeschini.libaryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getBookById(Long id);
}
