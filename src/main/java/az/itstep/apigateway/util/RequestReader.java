package az.itstep.apigateway.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestReader {

    public String readRequestHeader(HttpServletRequest request){
        return request.getHeader("Authorization");
    }
}
