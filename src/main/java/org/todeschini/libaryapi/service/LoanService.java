package org.todeschini.libaryapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.todeschini.libaryapi.dto.LoanFilterDTO;
import org.todeschini.libaryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
}
