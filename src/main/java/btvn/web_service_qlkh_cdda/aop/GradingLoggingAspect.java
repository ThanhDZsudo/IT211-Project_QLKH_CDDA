package btvn.web_service_qlkh_cdda.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GradingLoggingAspect {

    /**
     * FR-11: Ghi log thời gian thực hiện cho TẤT CẢ các chức năng trong tất cả Controller.
     * Sử dụng @Around để đo thời gian thực thi của từng method.
     */
    @Around("execution(* btvn.web_service_qlkh_cdda.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        log.info("AOP LOG [START] >> Bắt đầu thực thi: {}", methodName);

        Object result;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("AOP LOG [END]   >> Kết thúc thực thi: {} | Thời gian: {} ms | Trạng thái: THÀNH CÔNG",
                    methodName, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("AOP LOG [ERROR] >> Kết thúc thực thi: {} | Thời gian: {} ms | Trạng thái: LỖI | Nguyên nhân: {}",
                    methodName, duration, e.getMessage());
            throw e;
        }

        return result;
    }

    /**
     * FR-11 (bổ sung): Log riêng sau khi chấm điểm thành công (giữ lại theo yêu cầu ban đầu).
     */
    @AfterReturning(
            pointcut = "execution(* btvn.web_service_qlkh_cdda.controller.LecturerController.gradeSubmission(..))",
            returning = "result"
    )
    public void logAfterGrading(Object result) {
        log.info("AOP LOG [GRADING]: Một đồ án vừa được Giảng viên chấm điểm và cập nhật thành công vào CSDL. Kết quả: {}",
                result.toString());
    }
}