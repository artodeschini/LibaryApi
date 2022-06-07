package org.todeschini.libaryapi.service;

import org.todeschini.libaryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    void update(Loan loan);
}
