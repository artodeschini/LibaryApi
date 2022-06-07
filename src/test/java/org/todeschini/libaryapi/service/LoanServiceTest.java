package org.todeschini.libaryapi.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.model.repository.LoanRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("deve salvar o emprestimo")
    public void saveLoanTest() {
        // given
        Book book = Book.builder().id(1l).isbn("i").build();
        Loan enviado = Loan.builder()
                .book(book)
                .customer("Artur")
                .date(LocalDate.now())
                .build();

        Loan loanMock = Loan.builder()
                .id(1l)
                .book(book)
                .customer(enviado.getCustomer())
                .date(enviado.getDate())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(enviado)).thenReturn(loanMock);

        // when
        Loan saved = service.save(enviado);

        // tip >  alt + 1 import static
        // macOS cmd + 1
        assertThat(saved.getId()).isEqualTo(loanMock.getId());
        assertThat(saved.getBook().getId()).isEqualTo(book.getId());
        assertThat(saved.getCustomer()).isEqualTo(loanMock.getCustomer());
        assertThat(saved.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("deve lancar um erro de negocio ao salvar emprestimo com livro ja emprestado")
    public void saveLoanThrowsBusinessExceptionTest() {
        // given
        Book book = Book.builder().id(1l).isbn("i").build();
        Loan loan = Loan.builder()
                .book(book)
                .customer("Artur")
                .date(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        // when
        Throwable t = catchThrowable( () -> service.save(loan));

        assertThat(t).isInstanceOf(BussinessException.class);
        assertThat(t).hasMessage("Book already loaned");

        verify(repository, never()).save(loan);
    }
}
