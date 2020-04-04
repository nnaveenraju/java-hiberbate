package com.example.demo.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {
	
	Logger LOGGER = LogManager.getRootLogger();
	
	@Around("@annotation(Logging) && execution(* *(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			LOGGER.info(String.format("Calling Method \" %s \"", joinPoint.getSignature()));
			long start = System.nanoTime();
			Object result = joinPoint.proceed();
			long end = System.nanoTime();
			LOGGER.info(String.format("%s took %d ns", joinPoint.getSignature(), (end - start)));
			return result;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
