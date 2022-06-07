package org.todeschini.libaryapi.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("deve verificar se existe em emprestimo nao devolvido para o livro ")
    public void existsByBookAndNotReturned() {
        // given
        Book book = Book.builder().id(1L).author("a").title("t").isbn("i").build();
        entityManager.persist(book);

        Loan notReturn = Loan.builder().id(1l).book(book).customer("c").date(LocalDate.now()).build();
        entityManager.persist(notReturn);

        // when
        boolean exist = repository.existsByBookAndNotReturned(book);

        // then
        assertThat(exist).isTrue();
    }
}
