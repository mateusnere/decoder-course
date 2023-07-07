package com.ead.course.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {
    
    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lesson")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId, @RequestBody @Valid LessonDTO lessonDTO) {

        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if(moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module doesn't exist!");
        }

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDTO, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
    }

    @DeleteMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {
        
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully!");
    }

    @PutMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId, @RequestBody @Valid LessonDTO lessonDTO) {
        
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        var lessonModel = lessonModelOptional.get();
        BeanUtils.copyProperties(lessonDTO, lessonModel);

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
    }

    @GetMapping("/modules/{moduleId}/lesson")
    public ResponseEntity<Object> getAllLessons(@PathVariable(value = "moduleId") UUID moduleId) {

        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if(moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllLessonsIntoModule(moduleId));
    }

    @GetMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {
        
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
