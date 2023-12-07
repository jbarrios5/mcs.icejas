package py.com.jmbr.mcs.icejas.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {
    @Bean(name = "hikariConfigPGS")
    @ConfigurationProperties(prefix = "datasource.pgs")
    public HikariConfig hikariConfigPGS(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("datasourcePGS")
    @DependsOn("hikariConfigPGS")
    @Primary
    public DataSource dataSourcePGS(HikariConfig hikariConfig){

        return new HikariDataSource(hikariConfig);
    }
}
