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

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {
    
    @Autowired
    ModuleService moduleService;

    @Autowired
    CourseService courseService;

    @PostMapping("/course/{courseId}/module")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId, @RequestBody @Valid ModuleDTO moduleDTO) {

        log.debug("[POST saveModule] CourseID {} received!", courseId);
        log.debug("[POST saveModule] ModuleDTO received {}", moduleDTO.toString());
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(courseModelOptional.isEmpty()) {
            log.warn("[POST saveModule] CourseID {} doesn't exist!", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course doesn't exist!");
        }

        var module = new ModuleModel();
        BeanUtils.copyProperties(moduleDTO, module);
        module.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        module.setCourse(courseModelOptional.get());
        moduleService.save(module);
        log.debug("[POST saveModule] Module saved successfully! {}", module.toString());
        log.info("[POST saveModule] Module saved successfully! ModuleID {}", module.getModuleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(module);
    }

    @DeleteMapping("course/{courseId}/module/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId) {

        log.debug("[DELETE deleteModule] CourseID {} received!", courseId);
        log.debug("[DELETE deleteModule] ModuleID {} received!", moduleId);
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(moduleModelOptional.isEmpty()) {
            log.warn("[DELETE deleteModule] Course or Module doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course or module doesn't exist!");
        }

        moduleService.delete(moduleModelOptional.get());
        log.info("[DELETE deleteModule] Module deleted successfully! ModuleID {}", moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully!");
    }

    @PutMapping("/course/{courseId}/module/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId, @RequestBody @Valid ModuleDTO moduleDTO) {

        log.debug("[PUT updateModule] CourseID {} received!", courseId);
        log.debug("[PUT updateModule] ModuleID {} received!", moduleId);
        log.debug("[PUT updateModule] ModuleDTO received! {}", moduleDTO.toString());
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(moduleModelOptional.isEmpty()) {
            log.warn("[PUT updateModule] Course or Module doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course or module doesn't exist!");
        }

        var moduleModel = moduleModelOptional.get();
        BeanUtils.copyProperties(moduleDTO, moduleModel);
        moduleService.save(moduleModel);
        log.info("[PUT updateModule] Module updated successfully! ModuleID {}", moduleId);

        return ResponseEntity.status(HttpStatus.OK).body(moduleModel);
    }

    @GetMapping("/course/{courseId}/module")
    public ResponseEntity<Object> getAllModules(SpecificationTemplate.ModuleSpec spec, @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable, 
    @PathVariable(value = "courseId") UUID courseId) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(courseModelOptional.isEmpty()) {
            log.warn("[GET getAllModules] CourseID {} doesn't exist!", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllModulesIntoCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }

    @GetMapping("/course/{courseId}/module/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId) {

        log.debug("[GET getOneModule] CourseID {} received!", courseId);
        log.debug("[GET getOneModule] ModuleID {} received!", moduleId);
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(moduleModelOptional.isEmpty()) {
            log.warn("[GET getOneModule] Course or Module doesn't exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course or module doesn't exist!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}
