package org.todeschini.libaryapi.service;

import org.todeschini.libaryapi.exception.BussinessException;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.model.repository.LoanRepository;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BussinessException("Book already loaned");
        }
        return repository.save(loan);
    }
}
