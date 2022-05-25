package org.todeschini.libaryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.todeschini.libaryapi.api.dto.BookDTO;
import org.todeschini.libaryapi.model.entity.Book;
import org.todeschini.libaryapi.service.BookService;

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
    public BookDTO create(@RequestBody BookDTO dto) {
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
}
