package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Job;
import com.evgenltd.hnhtool.harvester.core.entity.Task;
import com.evgenltd.hnhtool.harvester.core.record.PageData;
import com.evgenltd.hnhtool.harvester.core.record.TaskRecord;
import com.evgenltd.hnhtool.harvester.core.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LogManager.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final AgentService agentService;
    private final ObjectMapper objectMapper;
    private final BeanFactory beanFactory;

    private final ExecutorService executor;

    public TaskService(
            final TaskRepository taskRepository,
            final AgentService agentService,
            final ObjectMapper objectMapper,
            final BeanFactory beanFactory
    ) {
        this.taskRepository = taskRepository;
        this.agentService = agentService;
        this.objectMapper = objectMapper;
        this.beanFactory = beanFactory;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @PostConstruct
    public void postConstruct() {
        taskRepository.findByStatusOrderByActualDesc(Task.Status.IN_PROGRESS)
                .forEach(task -> {
                    task.setStatus(Task.Status.FAILED);
                    task.setFailReason("Marked as failed on server startup");
                    taskRepository.save(task);
                });
    }

    public PageData<TaskRecord> listPageable(final PageRequest pageRequest) {
        final Page<Task> result = taskRepository.findAll(pageRequest);
        final List<TaskRecord> data = result.get()
                .map(TaskRecord::of)
                .collect(Collectors.toList());
        return new PageData<>(data, result.getTotalElements());
    }

    @Scheduled(cron = "0 * * * * *")
    public void taskHandler() {
        final List<Task> tasks = taskRepository.findByStatusOrderByActualDesc(Task.Status.NEW);
        for (final Task task : tasks) {

            final Object script = resolveBean(task.getScript());
            if (script == null) {
                failTask(task, "Script not found");
                continue;
            }

            final Method method = resolveExecutionMethod(script);
            if (method == null) {
                failTask(task, "Execution method not found");
                continue;
            }

            final AgentContext agentContext = agentService.takeRandomAgent();
            if (agentContext == null) {
                log.info("No free agents");
                return;
            }

            updateTaskStatus(task, Task.Status.IN_PROGRESS);

            executor.submit(() -> {

                try {
                    agentService.initializeAgent(agentContext);

                    assignTask(task, agentContext.getAgent());
                    A.initialize(agentContext, task);

                    method.invoke(script);
                    updateTaskStatus(task, Task.Status.DONE);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    failTask(task, "Illegal execution method signature");
                } catch (final Throwable t) {
                    failTask(task, "Execution process failed");
                    log.error("", t);
                } finally {
                    saveLogToTask(task);
                    agentService.releaseAgent(agentContext);
                    A.cleanup();
                }

            });

        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void flushLogs() {
        A.logsForFlush(this::logAsString)
                .forEach((taskId, log) -> {
                    final Task task = taskRepository.findOne(taskId);
                    saveLogToTask(task, log);
                });
    }

    public void schedule(final String script) {
        final Task task = new Task();
        task.setActual(LocalDateTime.now());
        task.setScript(script);
        task.setStatus(Task.Status.NEW);
        taskRepository.save(task);
    }

    public String loadLog(final Long id) {
        try {
            return String.join("\n", Files.readAllLines(Paths.get(id.toString() + ".json")));
        } catch (NoSuchFileException e) {
            log.warn("File [{}.json] not found", id);
            return "{}";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Task createTask(final Job job, final String script, final Agent agent) {
        final Task task = new Task();
        task.setStatus(Task.Status.NEW);
        task.setActual(LocalDateTime.now());
        task.setJob(job);
        task.setScript(script);
        task.setAgent(agent);
        return taskRepository.save(task);
    }

    public boolean hasActualTaskForJob(final Job job) {
        return taskRepository.findByJobId(job.getId())
                .stream()
                .anyMatch(task -> !task.getStatus().isTerminated());
    }

    private String logAsString(final Object logObject) {
        try {
            return objectMapper.writeValueAsString(logObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveLogToTask(final Task task) {
        final String log = A.logForFlush(this::logAsString);
        saveLogToTask(task, log);
    }

    private void saveLogToTask(final Task task, final String log) {
        try {
            Files.writeString(Paths.get(task.getId().toString() + ".json"), log, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assignTask(final Task task, final Agent agent) {
        task.setAgent(agent);
        taskRepository.save(task);
    }

    public void failTask(final Task task, final String failReason) {
        task.setFailReason(failReason);
        updateTaskStatus(task, Task.Status.FAILED);
    }

    private void updateTaskStatus(final Task task, final Task.Status status) {
        task.setStatus(status);
        taskRepository.save(task);
    }

    private Object resolveBean(final String name) {
        try {
            final Class<?> scriptClass = Class.forName(name);
            return beanFactory.getBean(scriptClass);
        } catch (final BeansException | ClassNotFoundException e) {
            log.info("Bean [{}] not found", name);
            return null;
        }
    }

    private Method resolveExecutionMethod(final Object script) {
        final Class<?> scriptClass = script.getClass();
        try {
            return scriptClass.getDeclaredMethod("execute");
        } catch (NoSuchMethodException e) {
            log.info("Script [{}] does not have execution method", scriptClass.getName());
            return null;
        }
    }

}
