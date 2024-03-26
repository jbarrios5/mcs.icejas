package py.com.jmbr.mcs.icejas.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;
import py.com.jmbr.java.commons.exception.JMBRException;
import py.com.jmbr.java.commons.exception.JMBRExceptionType;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class IcejasInterceptor implements HandlerInterceptor {
    @Value("${api.key}")
    private String apiKey;

    @Value("${auth.uri}")
    private String authUri;
    private Logger logger = LoggerFactory.getLogger(IcejasInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Permitir que las solicitudes preflight CORS pasen
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }
        if (request.getRequestURI().contains("/swagger") || request.getRequestURI().contains("/v3/api-docs") || request.getRequestURI().contains("/swagger-ui/")) {
            return true;
        }

        String apiKeyHeader = request.getHeader("apiKey");

        if(StringUtils.isBlank(apiKeyHeader))
            throw new JMBRException("ApiKey is required", JMBRExceptionType.FALTAL, HttpStatus.BAD_REQUEST);

        if(!apiKeyHeader.equals(apiKey))
            throw new JMBRException("ApiKey invalid", JMBRExceptionType.FALTAL, HttpStatus.BAD_REQUEST);

        String accessToken = request.getHeader("Authorization");
        if(StringUtils.isBlank(accessToken))
            throw new JMBRException("AT is required", JMBRExceptionType.FALTAL, HttpStatus.BAD_REQUEST);

        if(isAccessTokenExpires(accessToken))
            throw new JMBRException("AT invalid", JMBRExceptionType.FALTAL, HttpStatus.BAD_REQUEST);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean isAccessTokenExpires(String accessToken){
        if(accessToken.isBlank()){
            logger.warn(RequestUtil.LOG_FORMATT,"no-log-id","accessToken is required",null);
            throw new JMBRException("Error en autenticacion", JMBRExceptionType.FALTAL, HttpStatus.BAD_REQUEST);
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add(TransactionConstant.API_KEY,apiKey);
        header.add(TransactionConstant.AUTHORIZATION,accessToken);
        ResponseEntity<Boolean> response;
        HttpEntity<?> entity = new HttpEntity<>(HttpEntity.EMPTY,header);
        String URI = UriComponentsBuilder
                .fromUriString(authUri.concat("/verify"))
                .toUriString();
        logger.debug(RequestUtil.LOG_FORMATT,"no-log-id","isAccessTokenValid:request",entity.toString());
       try {
           response = restTemplate.exchange(URI, HttpMethod.POST,entity,Boolean.class);
           logger.debug(RequestUtil.LOG_FORMATT,"no-log-id","isAccessTokenValid:response",response);
           if(response.getBody() != null)
               return response.getBody();
           else
               return false;
       }catch (Exception e){
           logger.warn(RequestUtil.LOG_FORMATT,"no-log-id","isAccessTokenValid:Error",e.getMessage());
            return true;
       }

    }
}
