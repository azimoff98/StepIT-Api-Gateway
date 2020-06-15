package az.itstep.apigateway.filter;

import az.itstep.apigateway.exception.TokenExpiredException;
import az.itstep.apigateway.service.TokenService;
import az.itstep.apigateway.util.Constants;
import az.itstep.apigateway.util.RequestReader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class PreFilter extends ZuulFilter {

    private final TokenService tokenService;
    private final RequestReader requestReader;

    private final String tokenPrefix = "Bearer ";

    public PreFilter(TokenService tokenService, RequestReader requestReader) {
        this.tokenService = tokenService;
        this.requestReader = requestReader;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        Collections.list(request.getHeaderNames()).forEach(
                e -> log.info("Header key: {}, value: {}", e, request.getHeader(e))
        );

        String path = request.getRequestURI().substring(request.getContextPath().length());

        log.info("Request path: {}", path);

        String header = request.getHeader("Authorization");

        if(!path.equals(Constants.SIGN_IN_PATH) && !path.equals(Constants.SIGN_UP_PATH)){
            log.info("Token Validation request with method: {} with path: {}", request.getMethod(), request.getRequestURI());
            try{
                tokenService.validateToken(header);
            }catch (NullPointerException ex){
                log.error("No token found");
            }

//            if(!valid)
//                throw new TokenExpiredException("your jwt token is expired");
        }

        log.info("Pre Filter: " + String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
}
