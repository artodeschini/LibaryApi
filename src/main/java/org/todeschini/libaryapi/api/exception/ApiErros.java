package org.todeschini.libaryapi.api.exception;

import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.todeschini.libaryapi.exception.BussinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class ApiErros {

    private List<String> erros;

    public ApiErros(BindingResult result) {
        this.erros = new ArrayList<>();
        result.getAllErrors().forEach(erro -> this.erros.add(erro.getDefaultMessage()));
    }

    public ApiErros(BussinessException e) {
        this.erros = Arrays.asList(e.getMessage());
    }

    public ApiErros(ResponseStatusException e) {
        this.erros = Arrays.asList(e.getReason());
    }
}
