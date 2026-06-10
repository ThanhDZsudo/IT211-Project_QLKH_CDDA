package btvn.web_service_qlkh_cdda.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiDataResonse<T>{
    private Boolean success;
    private String message;
    private T data;
    private T errors;
    private HttpStatus httpStatus;
}
