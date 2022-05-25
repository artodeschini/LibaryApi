package org.todeschini.libaryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.todeschini.libaryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
