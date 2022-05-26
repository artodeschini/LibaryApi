package org.todeschini.libaryapi.service;

import org.springframework.stereotype.Service;
import org.todeschini.libaryapi.api.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.repository.BookRepository;

import java.util.Optional;

@Service // annotation que permite que o spring injete essa classe
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BussinessException("Isbn já cadastrado!");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return null;
    }
}
