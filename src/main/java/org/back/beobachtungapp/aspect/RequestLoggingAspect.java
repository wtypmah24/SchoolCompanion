package org.back.beobachtungapp.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

  @Around("execution(* org.back.beobachtungapp.controller.*.*(..))")
  public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
    // Log method params
    Object[] args = joinPoint.getArgs();
    String params = Arrays.toString(args);

    // Log method's name
    String methodName = joinPoint.getSignature().getName();
    log.info("Calling method: {} with params: {}", methodName, params);

    Object result = joinPoint.proceed();

    // Log result
    log.info("Method {} returned: {}", methodName, result);

    return result;
  }
}
