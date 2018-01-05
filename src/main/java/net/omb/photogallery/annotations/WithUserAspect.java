package net.omb.photogallery.annotations;

import net.omb.photogallery.model.Role;
import net.omb.photogallery.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class WithUserAspect {
    private static Logger log = LoggerFactory.getLogger("WithUserAspect");
    @Pointcut("within(@WithUser *)")
    public void beanAnnotatedWithLogExecutionTime() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && beanAnnotatedWithLogExecutionTime()")
    public Object setUserToSecurityContext(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        WithUser myAnnotation = method.getAnnotation(WithUser.class);

        log.debug("Setting user: " + myAnnotation.username());
        User user = new User(myAnnotation.username(), null, Role.USER, true);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        Object proceed = joinPoint.proceed();

        SecurityContextHolder.getContext().setAuthentication(null);
        return proceed;
    }
}
