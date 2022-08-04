package org.todeschini.libaryapi.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    public void returnTrueWhenIsbnExist() {
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
    @DisplayName("deve salvar um livro")
    public void saveBookTest() {
        // given
        Book book = createNewBook();

        // when
        Book savedBook = repository.save(book);

        // then
        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("deve deletar um livro")
    public void deleteBookByIdTest() {
        // given
        Book book = createNewBook();
        entityManager.persist(book);
        Book found = entityManager.find(Book.class, book.getId());

        // when
        repository.delete(found);

        // then
        Book deleted = entityManager.find(Book.class, book.getId());
        assertThat(deleted).isNull();
    }
}