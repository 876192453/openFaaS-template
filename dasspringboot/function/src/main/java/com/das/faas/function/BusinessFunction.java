package com.das.faas.function;

import com.das.faas.function.dto.RequestBodyDTO;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Function;


public class BusinessFunction implements Function<RequestBodyDTO, String> {

    @Value("${key:default}")
    private String environmentVariable;

    @Override
    public String apply(RequestBodyDTO input) {
        //编写业务代码
        return "Hello " + input.getName() + ", Your key is " + environmentVariable;
    }


}