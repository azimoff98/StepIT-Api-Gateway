package az.itstep.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ErrorFilter extends ZuulFilter {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        Throwable cause = context.getThrowable().getCause();
        context.setResponseStatusCode(401);

        HttpServletResponse response = context.getResponse();
        response.addHeader("Access-Control-Allow-Origin", "*");
        try{
            response.getWriter().write(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cause.getMessage())
            );
        }catch (Exception e){
            log.error("Error occurred : {}", e.getMessage());
        }
        log.info("Error Filter: " + String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        context.setResponse(response);
        return response;
    }

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
}
