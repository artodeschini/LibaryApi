package org.todeschini.libaryapi.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.todeschini.libaryapi.api.dto.BookDTO;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
//        BookDTO dto = new BookDTO();
        dto.setId(1L);
//        dto.setAuthor("Author");
//        dto.setIsbn("0123456");
//        dto.setTitle("My Book");

        return dto;
    }
}
