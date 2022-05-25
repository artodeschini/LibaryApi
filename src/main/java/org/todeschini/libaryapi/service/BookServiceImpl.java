package org.todeschini.libaryapi.service;

import org.springframework.stereotype.Service;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.repository.BookRepository;

@Service // annotation que permite que o spring injete essa classe
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
