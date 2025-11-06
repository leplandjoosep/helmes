package com.helmes.task.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FormDto {

    @NotBlank
    private String name;

    @NotEmpty
    private List<Long> sectors = new ArrayList<>();

    @AssertTrue
    private boolean agree;
}
