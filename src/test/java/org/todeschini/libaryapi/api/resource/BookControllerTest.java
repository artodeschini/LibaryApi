package org.todeschini.libaryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.dto.BookDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.service.BookService;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    //rota
    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    private BookDTO createNewBookDTO() {
        String author = "Artur";
        String title = "My Book";
        String isbn = "007";

        return BookDTO.builder().author(author).title(title).isbn(isbn).build();
    }

    private Book createBookEntity() {
        String author = "Artur";
        String title = "My Book";
        String isbn = "007";

        return Book.builder().id(1L).author(author).title(title).isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        BookDTO dto = createNewBookDTO();
        Book bookSaved = createBookEntity();

        given(service.save(Mockito.any(Book.class))).willReturn(bookSaved);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lancar erro de validacao ao criar um livro quando nao houver dados suficiente para criar o livro")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(BookDTO.builder().build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lancar erro ao tentar gravar um livro com isbn ja utilizado por outro")
    public void createBookWithDuplicatedIsbn() throws Exception {
        //ginve
        BookDTO dto = createNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        String msgException = "Isbn já cadastrado!";

        given(service.save(any(Book.class))).willThrow(new BussinessException(msgException));

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(1)))
                .andExpect(jsonPath("erros[0]").value(msgException));
    }

    @Test
    @DisplayName("Deve obter informacoes de um livro")
    public void getBookDetailsTest() throws Exception {
        //given
        Long id = 1l;
        Book book = createBookEntity();
        given(service.getBookById(id)).willReturn(Optional.of(book));

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()))
                .andExpect(jsonPath("title").value(book.getTitle()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro informado nao existir")
    public void bookNotFoundTest() throws Exception {
        //given
        given(service.getBookById(anyLong())).willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookByIdTest() throws Exception {
        //given
        Long id = 1L;
        Book book = createBookEntity();
        given(service.getBookById(anyLong())).willReturn(Optional.of(book));

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando nao encontrar o livro para deletar")
    public void deleteBookNotFoundByIdTest() throws Exception {
        //given
        Long id = 1L;
        given(service.getBookById(anyLong())).willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar  um livro")
    public void updateBookTest() throws Exception {
        //given
        Long id = 1L;

        String change = "change";

        BookDTO dto = BookDTO.builder().id(id).title(change).author(change).isbn("007").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        given(service.getBookById(anyLong())).willReturn(Optional.of(createBookEntity()));
        given(service.update(any(Book.class))).
                willReturn(Book.builder().id(id).author(change).title(change).isbn("007").build());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()))
                .andExpect(jsonPath("title").value(dto.getTitle()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro nao encontrado")
    public void updateBookNotFoundTest() throws Exception {
        //given
        Long id = 1L;
        given(service.getBookById(anyLong())).willReturn(Optional.empty());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve filtrar livros")
    public void findBooksTest() throws Exception {
        //given
        var id = 2L;
        BookDTO dto = createNewBookDTO();

        Book book = Book.builder().id(id).title(dto.getTitle()).author(dto.getAuthor()).isbn(dto.getIsbn()).build();

        given(service.find(any(Book.class), any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        //when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
