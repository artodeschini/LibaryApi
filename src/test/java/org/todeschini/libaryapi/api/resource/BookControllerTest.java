package org.todeschini.libaryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.todeschini.libaryapi.api.exception.BussinessException;
import org.todeschini.libaryapi.dto.BookDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.service.BookService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        String author = "Artur";
        String title = "My Book";
        String isbn = "007";

        BookDTO dto = createNewBookDTO();
        Book bookSaved = Book.builder().id(1L).author(author).title(title).isbn(isbn).build();

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
        BookDTO dto = createNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        String msgException = "Isbn já cadastrado!";

        given(service.save(any(Book.class))).willThrow(new BussinessException(msgException));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(1)))
                .andExpect(jsonPath("erros[0]").value(msgException));
    }

}
