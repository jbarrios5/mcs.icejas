package py.com.jmbr.mcs.icejas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import py.com.jmbr.java.commons.context.RequestContextInitializer;
import py.com.jmbr.mcs.icejas.interceptor.IcejasInterceptor;

@Configuration
@EnableWebMvc
public class AppMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/","/swagger-ui.html");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Bean("icejas-interceptor")
    public IcejasInterceptor icejasInterceptor(){
        return new IcejasInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(icejasInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
