package com.evgenltd.hnhtool.harvester.core.aspect;

import com.evgenltd.hnhtool.harvester.core.service.A;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class AgentAspect {

    private static final Logger log = LogManager.getLogger(AgentAspect.class);

    @Pointcut("@annotation(AgentCommand)")
    public void commandCall() {}

    @Around("commandCall()")
    public Object aroundAtCommandCall(final ProceedingJoinPoint call) throws Throwable {

        final String arguments = Arrays.stream(call.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        A.downward(String.format(
                "%s.%s(%s)",
                call.getTarget().getClass().getSimpleName(),
                call.getSignature().getName(),
                arguments
        ));

        Throwable throwable = null;
        Object result = null;
        final StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            result = call.proceed();
            stopWatch.stop();
            return result;
        } catch (final Throwable t) {
            throwable = t;
            throw t;
        } finally {
            A.upward(
                    String.format(": %s", result),
                    stopWatch.getTotalTimeMillis(),
                    throwable
            );
        }

    }

}
