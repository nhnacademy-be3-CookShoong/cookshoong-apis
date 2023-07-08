package store.cookshoong.www.cookshoongbackend.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestComponent;

@Aspect
@TestComponent
public class TestEntityAspect {
    @Autowired
    TestEntityManager em;

    @Around("execution(* TestEntity.*(..)))")
    public Object applyPersistence(ProceedingJoinPoint joinPoint) throws Throwable {
        return em.merge(joinPoint.proceed());
    }
}
