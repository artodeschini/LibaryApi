package org.todeschini.libaryapi.api.resource;

//import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.todeschini.libaryapi.dto.BookDTO;
import org.todeschini.libaryapi.dto.LoanDTO;
import org.todeschini.libaryapi.dto.LoanFilterDTO;
import org.todeschini.libaryapi.dto.RetornedLoanDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.model.entity.Loan;
import org.todeschini.libaryapi.service.BookService;
import org.todeschini.libaryapi.service.LoanService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {


//    @Autowired
    private LoanService service;
//    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    public LoanController(LoanService service, BookService bookService, ModelMapper modelMapper) {
        this.service = service;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

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

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id,
                           @RequestBody RetornedLoanDTO dto) {

        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        loan.setReturned(dto.getReturned());

        service.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO filter, Pageable pageRequest) {
        Page<Loan> result = service.find(filter, pageRequest);

        List<LoanDTO> loans = result.getContent().stream().map(
                entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO dto = modelMapper.map( entity, LoanDTO.class);
                    dto.setBook(bookDTO);

                    return dto;
                }
        ).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
}
