package org.todeschini.libaryapi.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.repository.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getBookById() {
        // given
        Long id = 1l;

        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));

        // when
        Optional<Book> foundBook = service.getBookById(id);

        // then
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id")
    public void getBookNotFoundById() {
        // given
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Book> foundBook = service.getBookById(id);

        // then
        assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("deve obter um livro por id")
    public void deleteBookByIdTest() {
        // given
        Book book = Book.builder().id(1l).build();

        // when
        // check if not throws any exception
        assertDoesNotThrow(() -> service.delete(book));

        // then
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("deve ocorrer um erro ao tentar deletar um livro por id")
    public void deleteNotFoundBookByIdTest() {
        // given
        Book book = Book.builder().build(); //no id and any others details

        // when
        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // then
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("deve ataulizar um livro por id")
    public void updateBookByIdTest() {
        // given
        long id = 1L;
        Book book = Book.builder().id(id).build(); //entity retrive
        Book change = createValidBook();
        change.setId(id);

        when(repository.save(book)).thenReturn(change);

        // when
        Book updateBook = service.save(book);

        // then
        assertThat(updateBook.getId()).isEqualTo(id);
        assertThat(updateBook.getTitle()).isEqualTo(change.getTitle());
        assertThat(updateBook.getAuthor()).isEqualTo(change.getAuthor());
        assertThat(updateBook.getIsbn()).isEqualTo(change.getIsbn());
    }

    @Test
    @DisplayName("deve ocorrer um erro ao tentar alterar um livro por id")
    public void updateNotFoundBookByIdTest() {
        // given
        Book book = Book.builder().build(); //no id and any others details

        // when
        assertThrows(IllegalArgumentException.class, () -> service.update(book));

        // then
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("deve filtar livros pelas propriedades")
    public void findBookTest() {
        // given
        Book book = createValidBook();
        book.setId(1l);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> books = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(books, pageRequest, 1);
        when(repository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        // when
        Page<Book> result = service.find(book, pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(books);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}