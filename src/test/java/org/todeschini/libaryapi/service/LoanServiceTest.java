package org.todeschini.libaryapi.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.dto.LoanFilterDTO;
import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.model.repository.LoanRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public static Book createBookValid() {
        return Book.builder().id(1l).isbn("i").build();
    }

    public static Loan createtValidLoan() {
        // given
        Book book = createBookValid();
        return Loan.builder()
                .book(book)
                .customer("Artur")
                .date(LocalDate.now())
                .build();


    }

    @Test
    @DisplayName("deve lancar um erro de negocio ao salvar emprestimo com livro ja emprestado")
    public void saveLoanThrowsBusinessExceptionTest() {
        Book book = createBookValid();
        Loan loan = createtValidLoan();
        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        // when
        Throwable t = catchThrowable( () -> service.save(loan));

        assertThat(t).isInstanceOf(BussinessException.class);
        assertThat(t).hasMessage("Book already loaned");

        verify(repository, never()).save(loan);
    }

    @Test
    @DisplayName("deve obter as informacoes de um emprestipo por id")
    public void getLoanDetaisById() {
        Long id = 1l;
        Loan loan = createtValidLoan();
        loan.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isEmpty()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getDate()).isEqualTo(loan.getDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar emprestimo")
    public void updateLoanUpdate() {
        Loan loan = createtValidLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when(repository.save(any(Loan.class))).thenReturn(loan);

        Loan updateReturnedLoan = service.update(loan);

        assertThat(updateReturnedLoan.isReturned()).isTrue();
        verify(repository.save(loan));
    }


    @Test
    @DisplayName("deve filtar emprestimos pelas propriedades")
    public void findLoansTest() {
        // given

        Loan loan = createtValidLoan();
        loan.setId(1l);

        LoanFilterDTO filter = LoanFilterDTO.builder().customer(loan.getCustomer()).isbn(loan.getBook().getIsbn()).build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> loans = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<>(loans, pageRequest, 1);
        when(repository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        // when
        Page<Loan> result = service.find(filter, pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(loans);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
