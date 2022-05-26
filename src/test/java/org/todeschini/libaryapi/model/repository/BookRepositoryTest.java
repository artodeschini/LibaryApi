package org.todeschini.libaryapi.model.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.model.entity.Book;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    @DisplayName("Deve retornar verdadeiro quanto existir um livro com o isbn informado")
    void returnTrueWhenIsbnExist() {
        //given
        String isbn = "0123";
        entityManager.persist(Book.builder().author("A").title("T").isbn(isbn).build());

        //when
        boolean exist = repository.existsByIsbn(isbn);

        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quanto nao existir um livro com o isbn informado")
    void returnFalseWhenIsbnNotExist() {
        //given
        String isbn = "0123";

        //when
        boolean exist = repository.existsByIsbn(isbn);

        //then
        assertThat(exist).isFalse();
    }
}