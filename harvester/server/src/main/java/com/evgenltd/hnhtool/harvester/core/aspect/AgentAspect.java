package com.evgenltd.hnhtool.harvester.core.aspect;

import com.evgenltd.hnhtool.harvester.core.entity.AgentLog;
import com.evgenltd.hnhtool.harvester.core.repository.AgentLogRepository;
import com.evgenltd.hnhtool.harvester.core.service.A;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class AgentAspect {

    private static final Logger log = LogManager.getLogger(AgentAspect.class);
    private static final ThreadLocal<Invocation> invocationContext = new ThreadLocal<>();

    private final AgentLogRepository agentLogRepository;
    private final ObjectMapper objectMapper;

    public AgentAspect(
            final AgentLogRepository agentLogRepository,
            final ObjectMapper objectMapper
    ) {
        this.agentLogRepository = agentLogRepository;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(AgentCommand)")
    public void commandCall() {}

    @Around("commandCall()")
    public Object aroundAtCommandCall(final ProceedingJoinPoint call) throws Throwable {

        final List<String> arguments = Arrays.stream(call.getArgs())
                .map(String::valueOf)
                .collect(Collectors.toList());
        startInvocation(call.getTarget().getClass().getName(), call.getSignature().getName(), arguments);

        Throwable throwable = null;
        Object result = null;

        try {
            result = call.proceed();
            return result;
        } catch (final Throwable t) {
            throwable = t;
            throw t;
        } finally {
            stopInvocation(result, throwable);
        }

    }

    private void startInvocation(final String target, final String method, final List<String> arguments) {
        final Invocation invocation = new Invocation(target, method, arguments);

        final Invocation parent = invocationContext.get();
        if (parent != null) {
            invocation.setParent(parent);
            parent.getChildren().add(invocation);
        }

        invocationContext.set(invocation);
        invocation.start();
    }

    private void stopInvocation(final Object result, final Throwable throwable) {
        final Invocation invocation = invocationContext.get();
        invocation.stop(throwable == null, throwable);
        invocation.setReturnValue(String.valueOf(result));
        if (invocation.getParent() != null) {
            invocationContext.set(invocation.getParent());
        } else {
            invocationContext.remove();
            try {
                final AgentLog agentLog = new AgentLog();
                agentLog.setDate(LocalDateTime.now());
                agentLog.setAccount(A.getAccount());
                agentLog.setLog(objectMapper.writeValueAsString(invocation));
                agentLogRepository.save(agentLog);
            } catch (JsonProcessingException e) {
                log.error("Unable to store agent log", e);
            }
        }
    }

    private static final class Invocation {

        private String target;
        private String method;
        private List<String> arguments;
        private String returnValue;
        private boolean success;
        private long time;
        private String exception;
        private final List<Invocation> children = new ArrayList<>();

        @JsonIgnore
        private Invocation parent;
        @JsonIgnore
        private final StopWatch stopWatch = new StopWatch();

        public Invocation(final String target, final String method, final List<String> arguments) {
            this.target = target;
            this.method = method;
            this.arguments = arguments;
        }

        public void start() {
            stopWatch.start();
        }

        public void stop(final boolean success, final Throwable throwable) {
            stopWatch.stop();
            this.time = stopWatch.getTotalTimeMillis();
            this.success = success;

            if (throwable == null) {
                return;
            }

            if (!children.isEmpty()) {
                final Invocation lastChild = children.get(children.size() - 1);
                if (lastChild.getException() != null) {
                    return;
                }
            }

            final StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            exception = sw.toString();
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(final String target) {
            this.target = target;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(final String method) {
            this.method = method;
        }

        public List<String> getArguments() {
            return arguments;
        }

        public void setArguments(final List<String> arguments) {
            this.arguments = arguments;
        }

        public String getReturnValue() {
            return returnValue;
        }

        public void setReturnValue(final String returnValue) {
            this.returnValue = returnValue;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(final boolean success) {
            this.success = success;
        }

        public long getTime() {
            return time;
        }

        public void setTime(final long time) {
            this.time = time;
        }

        public String getException() {
            return exception;
        }

        public void setException(final String exception) {
            this.exception = exception;
        }

        public List<Invocation> getChildren() {
            return children;
        }

        public Invocation getParent() {
            return parent;
        }

        public void setParent(final Invocation parent) {
            this.parent = parent;
        }
    }

}
