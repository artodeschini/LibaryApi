package org.todeschini.libaryapi.api.exception;

import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiErros {

    private List<String> erros;

    public ApiErros(BindingResult result) {
        this.erros = new ArrayList<>();
        result.getAllErrors().forEach(erro -> this.erros.add(erro.getDefaultMessage()));
    }

}
