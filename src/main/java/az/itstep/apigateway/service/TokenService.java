package az.itstep.apigateway.service;

import az.itstep.ts.model.JwtTokenParameter;
import az.itstep.ts.util.JwtTokenUtil;
import az.itstep.ts.util.JwtTokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService {

    private final JwtTokenValidator tokenValidator;
    private final JwtTokenUtil tokenUtil;

    private final String tokenPrefix = "Bearer ";

    public TokenService(@Value("${default.jwt.secret}") String secret, @Value("${default.jwt.expiration}") Long expiration){
        JwtTokenParameter tokenParameter = new JwtTokenParameter(secret, expiration);
        this.tokenValidator = new JwtTokenValidator(tokenParameter);
        this.tokenUtil = new JwtTokenUtil(secret);
    }


    public boolean validateToken(String requestHeader){
        String authToken = requestHeader.substring(tokenPrefix.length());
        log.info("token's expire date: {}", tokenUtil.getExpirationDateFromToken(authToken));
        return tokenValidator.validateToken(authToken);
    }

    public String getUsernameFromToken(String requestHeader){
        String authToken = requestHeader.substring(tokenPrefix.length());
        return tokenUtil.getUsernameFromToken(authToken);
    }

}
