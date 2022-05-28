package org.todeschini.libaryapi.api.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.todeschini.libaryapi.dto.LoanDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.service.BookService;
import org.todeschini.libaryapi.service.LoanService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor // cria um construtor com os argumentos requeridos

public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@RequestBody LoanDTO dto) {
        Book book = bookService.findBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for isbn"));

        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .date(LocalDate.now())
                .build();

        entity = service.save(entity);

        return entity;
    }
}
