package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.component.script.HeapResearchScript;
import com.evgenltd.hnhtool.harvester.core.component.script.TestScript;
import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.core.record.TaskRecord;
import com.evgenltd.hnhtool.harvester.core.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public PageData<TaskRecord> listPageable(
            @RequestParam final int page,
            @RequestParam final int size
    ) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        return taskService.listPageable(pageRequest);
    }

    @GetMapping("/{id}/log")
    public Response<String> log(@PathVariable final Long id) {
        final Response<String> response = new Response<>();
        response.setValue(taskService.loadLog(id));
        response.setSuccess(true);
        return response;
    }

    @GetMapping("/script")
    public List<String> script() {
        return Arrays.asList(
                TestScript.class.getName(),
                HeapResearchScript.class.getName()
        );
    }

    @PostMapping("/script")
    public void script(@RequestBody final String script) {
        taskService.schedule(script);
    }

    @PostMapping("/handle")
    public void handle() {
        taskService.taskHandler();
    }

    @PostMapping("/flush")
    public void flushLogs() {
        taskService.flushLogs();
    }

}
