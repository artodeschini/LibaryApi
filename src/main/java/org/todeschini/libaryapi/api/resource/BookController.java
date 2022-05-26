package org.todeschini.libaryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.todeschini.libaryapi.api.exception.ApiErros;
import org.todeschini.libaryapi.api.exception.BussinessException;
import org.todeschini.libaryapi.dto.BookDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.service.BookService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Writer;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper; // need to put in class Application @Bean to init ModelMapper

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ApiErros handlerValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        return new ApiErros(result);
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ApiErros handlerBusinessExceptions(BussinessException e) {
        return new ApiErros(e);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
//        Book book = service.getBookById(id).get();
//        return modelMapper.map(book, BookDTO.class);
        return service.getBookById(id).map(book -> modelMapper.map(book, BookDTO.class) ).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
