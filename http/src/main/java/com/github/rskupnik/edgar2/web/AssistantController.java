package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.assistant.Assistant;
import com.github.rskupnik.edgar2.web.dto.TaskTriggerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AssistantController {

    private final Assistant assistant;

    @Autowired
    public AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping(path = "tasks/trigger", consumes = "application/json")
    public ResponseEntity<?> triggerTask(@RequestBody TaskTriggerRequest taskTriggerRequest) {
        assistant.processCommandHeadless(taskTriggerRequest.getTaskId(), taskTriggerRequest.getData());
        return ResponseEntity.ok().build();
    }
}
