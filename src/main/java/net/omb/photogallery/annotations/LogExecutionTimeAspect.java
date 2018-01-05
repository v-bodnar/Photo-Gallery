package net.omb.photogallery.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by volodymyr.bodnar on 9/8/2017.
 */
@Aspect
@Component
public class LogExecutionTimeAspect {
    private static Logger log = LoggerFactory.getLogger("LogExecutionTimeAspect");
    @Pointcut("within(@LogExecutionTime *)")
    public void beanAnnotatedWithLogExecutionTime() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && beanAnnotatedWithLogExecutionTime()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Executing method: " + joinPoint.getSignature());
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.debug(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }
}
