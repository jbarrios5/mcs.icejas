package py.com.jmbr.mcs.icejas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {
    //configuracion general

    @Bean("icejasRestTemplate")
    public RestTemplate icejasResTemplate(){

        return new RestTemplate();
    }


}
