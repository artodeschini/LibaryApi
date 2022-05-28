package org.todeschini.libaryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.todeschini.libaryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
