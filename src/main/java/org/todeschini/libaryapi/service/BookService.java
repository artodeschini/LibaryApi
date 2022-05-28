package org.todeschini.libaryapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.todeschini.libaryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getBookById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);

    Optional<Book> findBookByIsbn(String isbn);
}
