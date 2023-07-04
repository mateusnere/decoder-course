package com.ead.course.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ead.course.models.ModuleModel;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID> {

    /*
     * @EntityGraph - anotação utilizada para quando for feito o acionamento desse método, o atributo course passará de LAZY para EAGER
     */
    // @EntityGraph(attributePaths = {"course"})
    // ModuleModel findByTitle(String title);


    /*
     * Para utilizar queries nativas para insert, update e delete, utilizar a anotação @Modifying antes de @Query
     */
    @Query(value = "select * from tb_modules where course_course_id = :courseId", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);
    
}
