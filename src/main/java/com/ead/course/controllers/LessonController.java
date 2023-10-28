package com.ead.course.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {
    
    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lesson")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId, @RequestBody @Valid LessonDTO lessonDTO) {

        log.debug("[POST saveLesson] ModuleID {} received!", moduleId);
        log.debug("[POST saveLesson] LessonDTO received! {}", lessonDTO.toString());
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if(moduleModelOptional.isEmpty()) {
            log.warn("[POST saveLesson] Module doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module doesn't exist!");
        }

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDTO, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        lessonService.save(lessonModel);
        log.debug("[POST saveLesson] Lesson saved successfully! {}", lessonModel.toString());
        log.info("[POST saveLesson] Lesson saved successfully! LessonID {}", lessonModel.getLessonId());
        
        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

    @DeleteMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {

        log.debug("[DELETE deleteLesson] ModuleID {} received!", moduleId);
        log.debug("[DELETE deleteLesson] LessonID {} received!", lessonId);
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            log.warn("[DELETE deleteLesson] Module or Lesson doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        lessonService.delete(lessonModelOptional.get());
        log.info("[DELETE deleteLesson] Lesson deleted successfully! LessonID {}", lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully!");
    }

    @PutMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId, @RequestBody @Valid LessonDTO lessonDTO) {

        log.debug("[PUT updateLesson] ModuleID {} received!", moduleId);
        log.debug("[PUT updateLesson] LessonID {} received!", lessonId);
        log.debug("[PUT updateLesson] LessonDTO received! {}", lessonDTO.toString());
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            log.warn("[PUT updateLesson] Module or Lesson doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        var lessonModel = lessonModelOptional.get();
        BeanUtils.copyProperties(lessonDTO, lessonModel);
        lessonService.save(lessonModel);
        log.debug("[PUT updateLesson] Lesson updated successfully! {}", lessonModel.toString());
        log.info("[PUT updateLesson] Lesson updated successfully! LessonID {}", lessonId);

        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

    @GetMapping("/modules/{moduleId}/lesson")
    public ResponseEntity<Object> getAllLessons(SpecificationTemplate.LessonSpec spec, @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable, 
    @PathVariable(value = "moduleId") UUID moduleId) {

        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if(moduleModelOptional.isEmpty()) {
            log.warn("[GET getAllLessons] Module doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllLessonsIntoModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
    }

    @GetMapping("/modules/{moduleId}/lesson/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {

        log.debug("[GET getOneLesson] ModuleID {} received!", moduleId);
        log.debug("[GET getOneLesson] LessonID {} received!", lessonId);
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if(lessonModelOptional.isEmpty()) {
            log.warn("[GET getOneLesson] Module or Lesson doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module or lesson doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
