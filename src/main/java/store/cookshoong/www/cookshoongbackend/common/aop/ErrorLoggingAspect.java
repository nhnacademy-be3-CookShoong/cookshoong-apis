package store.cookshoong.www.cookshoongbackend.common.aop;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import store.cookshoong.www.cookshoongbackend.common.exception.ValidationFailureException;
import store.cookshoong.www.cookshoongbackend.common.util.IpResolver;

/**
 * 서버에서 처리된 예외들을 로깅하기 위한 Aspect.
 *
 * @author koesnam (추만석)
 * @since 2023.08.22
 */
@Slf4j
@Aspect
public class ErrorLoggingAspect {
    private static final String CLIENT_IP_LOG = "Client ip : {}";

    /**
     * 에러를 로깅하기 위한 어드바이스.
     *
     * @param joinPoint the join point
     */
    @Before("@within(org.springframework.web.bind.annotation.RestControllerAdvice)")
    public void logError(JoinPoint joinPoint) {
        if (joinPoint.getArgs()[0] instanceof Exception) {
            Exception exception = (Exception) joinPoint.getArgs()[0];
            log.error("처리 완료된 예외 : {} \n --> {}", exception.getMessage(), ExceptionUtils.getStackTrace(exception));
        }

        if (joinPoint.getArgs().length >= 2 && joinPoint.getArgs()[1] instanceof HttpServletRequest) {
            log.error(CLIENT_IP_LOG, IpResolver.getClientIp((HttpServletRequest) joinPoint.getArgs()[1]));
        }
    }
}
