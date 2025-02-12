package com.ashok.explore.controllers;

import com.ashok.explore.config.Properties;
import com.ashok.explore.to.ResponseTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final Properties properties;

    private final List<String> PROJECT_DATA = new ArrayList<>(Arrays.asList("Project 1", "Project 2"));

    @GetMapping("/fetch-projects")
    public ResponseEntity<?> getProjects() {
        log.info("Test message: {}", properties.getMessage());
        return ResponseEntity.ok(new ResponseTO<>("success", getMetadata(), PROJECT_DATA));
    }

    @PostMapping("/save-project")
    public ResponseEntity<?> saveProject(@RequestBody Map<String, Object> project) {

        if (PROJECT_DATA.contains((String) project.get("name"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseTO<>("fail", null, project));
        }
        PROJECT_DATA.add((String) project.get("name"));

        return ResponseEntity.ok(new ResponseTO<>("success", null, project));
    }

    private Map<String, Object> getMetadata() {
        return Map.of("count", PROJECT_DATA.size());
    }
}
