package btvn.web_service_qlkh_cdda.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GradingLoggingAspect {

    // Bắt sự kiện ngay sau khi API chấm điểm của LecturerController chạy thành công
    @AfterReturning(
            pointcut = "execution(* btvn.web_service_qlkh_cdda.controller.LecturerController.gradeSubmission(..))",
            returning = "result"
    )
    public void logAfterGrading(Object result) {
        log.info("AOP LOG: Một đồ án vừa được Giảng viên chấm điểm và cập nhật thành công vào CSDL. Kết quả trả về: {}", result.toString());
    }
}