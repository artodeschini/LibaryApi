package org.todeschini.libaryapi.service;

//import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean // permite simular as operacoes de JpaRepository
    BookRepository repository;

    @BeforeEach
    void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("deve salvar o livro")
    void saveBookTest() {
        //given
        Book book = Book.builder().title("Book Test").author("Any").isbn("0123").build();

        // see tBuild lombok permite criar uma novo objeto apartir de outro
        Book future = book.toBuilder().id(1L).build();

        when(repository.save(book)).thenReturn(future);

        //when
        Book saved = service.save(book);

        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthor()).isEqualTo("Any");
        assertThat(saved.getTitle()).isEqualTo("Book Test");
        assertThat(saved.getIsbn()).isEqualTo("0123");


    }
}