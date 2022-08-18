package com.personal.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-23 21:03
 *
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {
    //private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP通知:
     * 1.前置通知
     * 2.后置通知
     * 3.环绕通知
     * 4.异常通知
     * 5.最终通知
     */

    /**
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表方法返回类型 *代表所有类型
     * 第二处 包名代表aop监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 * 代表类名，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
     */
    @Around("execution(public * com.personal.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("===== 开始执行 {}.{} =====", joinPoint.getTarget(), joinPoint.getSignature());

        //记录开始时间
        long beginTime = System.currentTimeMillis();

        //执行目标service
        Object result = joinPoint.proceed();

        //记录结束时间
        long endTime = System.currentTimeMillis();
        long takeTime = endTime - beginTime;

        if (takeTime > 3000) {
            log.error("===== 执行结束,耗时: {} 毫秒 =====", takeTime);
        } else if (takeTime > 2000) {
            log.warn("===== 执行结束,耗时: {} 毫秒 =====", takeTime);
        } else {
            log.info("===== 执行结束,耗时: {} 毫秒 =====", takeTime);
        }
        return result;
    }
}
