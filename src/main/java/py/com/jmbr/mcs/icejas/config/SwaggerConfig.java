package py.com.jmbr.mcs.icejas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;

@Configuration
public class SwaggerConfig {
    public OpenAPI springAuthApi(ServletContext servletContext){
        return new OpenAPI().addServersItem(new Server().url(servletContext.getContextPath()))
                ;
    }
}
