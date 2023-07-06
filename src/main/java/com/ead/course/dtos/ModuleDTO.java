package com.ead.course.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModuleDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

}
