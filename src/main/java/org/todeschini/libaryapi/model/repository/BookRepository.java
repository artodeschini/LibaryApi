package org.todeschini.libaryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.todeschini.libaryapi.model.entity.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
