package org.todeschini.libaryapi.model.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.model.entity.Book;

import java.util.Optional;

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
        Book book = createNewBook();
        book.setIsbn(isbn);

        entityManager.persist(book);

        //when
        boolean exist = repository.existsByIsbn(isbn);

        //then
        assertThat(exist).isTrue();
    }

    private Book createNewBook() {
        return Book.builder().author("A").title("T").isbn("007").build();
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

    @Test
    @DisplayName("deve obter um livro por id")
    public void findByIdTest() {
        // given
        Book book = createNewBook();
        entityManager.persist(book);

        // when
        Optional<Book> foundBook = repository.findById(book.getId());

        // then
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @Disabled
    @DisplayName("deve obter um livro por id")
    public void deleteBookByIdTest() {
        // given
        Book book = Book.builder().id(1l).build();

        // when
        repository.delete(book);
        // then
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
}