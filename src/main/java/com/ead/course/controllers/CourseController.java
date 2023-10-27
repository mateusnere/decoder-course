package com.ead.course.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;

@Log4j2
@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {
    
    @Autowired
    CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseDTO courseDTO) {

        log.debug("[POST saveCourse] CourseDTO received! {}", courseDTO.toString());
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseService.save(courseModel);
        log.debug("[POST saveCourse] Course saved successfully. CourseModel {}", courseModel.toString());
        log.info("[POST saveCourse] Course saved successfully. CourseID {}", courseModel.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModel);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId) {

        log.debug("[DELETE deleteCourse] CourseID {} received!", courseId);
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()) {
            log.warn("[DELETE deleteCourse] CourseID {} doesn't exist!", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course doesn't exist!");
        }

        courseService.delete(courseModelOptional.get());
        log.info("[DELETE deleteCourse] Course deleted successfully! CourseID {}", courseId);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully!");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID courseId, @RequestBody @Valid CourseDTO courseDTO) {

        log.debug("[PUT updateCourse] CourseID {} received!", courseId);
        log.debug("[PUT updateCourse] CourseDTO received! {}", courseDTO.toString());
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()) {
            log.warn("[PUT updateCourse] CourseID {} doesn't exist!", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course doesn't exist!");
        }

        var courseModel = courseModelOptional.get();
        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseService.save(courseModel);
        log.info("[PUT updateCourse] Course updated successfully! CourseID {}", courseId);

        return ResponseEntity.status(HttpStatus.OK).body(courseModel);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec, 
    @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll(spec, pageable));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> findOneCourse(@PathVariable(value = "courseId") UUID courseId) {

        log.debug("[GET findOneCourse] CourseID {} received!", courseId);
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()) {
            log.warn("[GET findOneCourse] CourseID {} doesn't exist!", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
