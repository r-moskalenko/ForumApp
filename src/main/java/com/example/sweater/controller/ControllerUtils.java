//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.sweater.controller;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ControllerUtils {
    public ControllerUtils() {
    }

    static Map<String, String> getErros(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap((fieldError) -> {
            return fieldError.getField() + "Error";
        }, DefaultMessageSourceResolvable::getDefaultMessage);
        return (Map)bindingResult.getFieldErrors().stream().collect(collector);
    }
}