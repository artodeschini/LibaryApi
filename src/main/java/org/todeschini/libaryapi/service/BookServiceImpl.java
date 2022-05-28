package org.todeschini.libaryapi.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.todeschini.libaryapi.exception.BussinessException;
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
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id can be null");
        } else {
            repository.delete(book);
        }
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id can be null");
        } else {
            return repository.save(book);
        }
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // is same is like in start and end
                        .withStringMatcher(ExampleMatcher.StringMatcher.ENDING) // is same is like in end
        );
        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return null;
    }
}
