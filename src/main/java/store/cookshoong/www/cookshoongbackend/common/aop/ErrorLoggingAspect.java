package store.cookshoong.www.cookshoongbackend.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 서버에서 처리된 예외들을 로깅하기 위한 Aspect.
 *
 * @author koesnam (추만석)
 * @since 2023.08.22
 */
@Slf4j
@Aspect
@Component
public class ErrorLoggingAspect {

    /**
     * 에러를 로깅하기 위한 어드바이스.
     *
     * @param proceedingJoinPoint the proceeding join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("@within(org.springframework.web.bind.annotation.RestControllerAdvice)")
    public Object logError(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (proceedingJoinPoint.getArgs()[0] instanceof Exception) {
            Exception exception = (Exception) proceedingJoinPoint.getArgs()[0];
            log.error("발생한 예외 :", exception.getCause());
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}
