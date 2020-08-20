package com.evgenltd.hnhtool.harvester.core.aspect;

import com.evgenltd.hnhtool.harvester.core.Agent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Aspect
@Component
public class AgentAspect {

    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();

    private static Logger getLogger(final String name) {
        return LOGGERS.computeIfAbsent(name, LoggerFactory::getLogger);
    }

    @Pointcut("within(com.evgenltd.hnhtool.harvester.core.service.AgentImpl) && @annotation(AgentCommand)")
    public void commandCall() {}

    @Around("commandCall()")
    public Object aroundAtCommandCall(final ProceedingJoinPoint call) throws Throwable {
        final StopWatch stopWatch = new StopWatch();
        boolean success = true;
        stopWatch.start();
        try {
            return call.proceed();
        } catch (final Throwable t) {
            success = false;
            throw t;
        } finally {
            stopWatch.stop();

            final Agent agent = (Agent) call.getTarget();
            final Logger logger = getLogger(agent.getCharacter().name());

            final String args = Arrays.stream(call.getArgs())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            logger.info(
                    "{}({}); {}ms; {}",
                    call.getSignature().getName(),
                    args,
                    stopWatch.getTotalTimeMillis(),
                    success ? "success" : "failed"
            );

        }
    }

}
