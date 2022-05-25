package org.todeschini.libaryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.todeschini.libaryapi.api.exception.ApiErros;
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
        System.out.println("em create");
//        BookDTO dto = new BookDTO();
//        dto.setId(1L);
//        dto.setAuthor("Author");
//        dto.setIsbn("0123456");
//        dto.setTitle("My Book");
        //Book entity = Book.builder().author(dto.getAuthor()).title(dto.getTitle()).isbn(dto.getIsbn()).build();
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);

//        return BookDTO.builder().id(entity.getId()).author(entity.getAuthor()).title(entity.getTitle()).isbn(entity.getIsbn()).build();
        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ApiErros handlerValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        return new ApiErros(result);
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public void handleException(final Exception e, final HttpServletRequest request, Writer writer) {
//        String json = new ObjectMapper().writeValueAsString(e.);
//    }
}
