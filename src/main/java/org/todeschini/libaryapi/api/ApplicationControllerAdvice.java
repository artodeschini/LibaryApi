package org.todeschini.libaryapi.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.todeschini.libaryapi.api.exception.ApiErros;
import org.todeschini.libaryapi.exception.BussinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    //this class has global confi to thy throws exception

    // erros for book
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ApiErros handlerValidationExceptions(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        return new ApiErros(result);
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ApiErros handlerBusinessExceptions(BussinessException e) {
        return new ApiErros(e);
    }

    // erros for loans
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public
    ResponseEntity handlerResponseStatusException(ResponseStatusException e) {
        return new ResponseEntity(new ApiErros(e), e.getStatus());
    }

}
