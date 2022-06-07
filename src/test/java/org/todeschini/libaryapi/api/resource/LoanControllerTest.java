package org.todeschini.libaryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import org.todeschini.libaryapi.dto.LoanDTO;
import org.todeschini.libaryapi.dto.RetornedLoanDTO;
import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.service.BookService;
import org.todeschini.libaryapi.service.LoanService;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("deve criar um emprestimo")
    public void createLoadTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder().isbn("0123").customer("SomeOne").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(123L).isbn(dto.getIsbn()).title("Title").author("Author").build();

        given(bookService.findBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1l).book(book).customer("Artur").date(LocalDate.now()).build();
        given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
//                .andExpect(content().string("1"));// in case to return only id I can use content as jsonPath
    }

    @Test
    @DisplayName("deve retornar erro ao tentar criar um emprestimo de livro nao existente")
    public void invalidIsbnToCreateLoadTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder().isbn("0123").customer("SomeOne").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.findBookByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", Matchers.hasSize(1)))
                .andExpect(jsonPath("erros[0]").value("Book not found for isbn"));
    }

    @Test
    @DisplayName("deve retornar erro ao tentar criar um emprestimo de livro já emprestado")
    public void loanedBookErrorCreateLoadTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder().isbn("0123").customer("SomeOne").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(123L).isbn(dto.getIsbn()).title("Title").author("Author").build();

        given(bookService.findBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        given(loanService.save(Mockito.any(Loan.class))).willThrow(new BussinessException("Book already loanned"));

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", Matchers.hasSize(1)))
                .andExpect(jsonPath("erros[0]").value("Book already loanned"));
    }

    @Test
    @DisplayName("deve retornor um livro")
    public void returnBookTest() throws Exception {
        // given
        RetornedLoanDTO dto = RetornedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1l).build();

        given(loanService.getById(anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        // when
        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)


        ).andExpect( status().isOk());

        // then
        verify(loanService, times(1)).update(loan);
    }

    @Test
    @DisplayName("deve retornor 404 quanto tentar devolver um livro inexistente")
    public void returnInexistentBookTest() throws Exception {
        // given
        RetornedLoanDTO dto = RetornedLoanDTO.builder().returned(true).build();
        given(loanService.getById(anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        // when
        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)


        ).andExpect( status().isNotFound());

        // then
//        verify(loanService, times(1)).update(loan);
    }
}
