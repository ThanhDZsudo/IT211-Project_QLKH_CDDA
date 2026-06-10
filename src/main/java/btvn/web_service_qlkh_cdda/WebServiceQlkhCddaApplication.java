package btvn.web_service_qlkh_cdda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy // BẮT BUỘC: Khởi động AOP cho FR-11
public class WebServiceQlkhCddaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServiceQlkhCddaApplication.class, args);
    }
}