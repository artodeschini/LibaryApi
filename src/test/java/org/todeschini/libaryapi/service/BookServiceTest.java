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
import org.todeschini.libaryapi.api.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.*;
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

    private Book createValidBook() {
        return Book.builder().title("Book Test").author("Any").isbn("0123").build();
    }

    @Test
    @DisplayName("deve salvar o livro")
    void saveBookTest() {
        //given
        Book book = createValidBook();

        // see tBuild lombok permite criar uma novo objeto apartir de outro
        Book future = book.toBuilder().id(1L).build();

        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(future);

        //when
        Book saved = service.save(book);

        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthor()).isEqualTo("Any");
        assertThat(saved.getTitle()).isEqualTo("Book Test");
        assertThat(saved.getIsbn()).isEqualTo("0123");
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicatedIsbn() {
        //given
        String msgException = "Isbn já cadastrado!";

        when(repository.existsByIsbn(anyString())).thenReturn(true);

        Book book = createValidBook();

        // when
        Throwable exception = catchException(() -> service.save(book));

        // then
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage(msgException);

        //verifico se nao chamou nenhuma vez o save do repository
        verify(repository, never()).save(book);
    }
}