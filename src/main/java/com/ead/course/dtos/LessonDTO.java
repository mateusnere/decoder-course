package com.ead.course.dtos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LessonDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String videoUrl;
}
