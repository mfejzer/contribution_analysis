package pl.umk.wmii.msr.contributions.config;

import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.net.UnknownHostException;

@Configuration
@ComponentScan("pl.umk.wmii.msr.contributions")
public class AppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public JdbcOperations jdbcOperations() {
        SimpleDriverDataSource dSource = new SimpleDriverDataSource();
        dSource.setDriverClass(com.mysql.jdbc.Driver.class);
        //	dSource.setUsername("root");
        //*
        //configuration working on teslabot
        dSource.setUsername("msr14");
        dSource.setPassword("msr14");
        // */
        dSource.setUrl("jdbc:mysql://localhost/msr14");
        return new JdbcTemplate(dSource);
    }

    @Bean
    public MongoOperations mongoOperations() {
        try {
            return new MongoTemplate(new Mongo(), "msr14");
        } catch (UnknownHostException e) {
            String msg = String.format(
                    "Cannot create mongo template bean: %s",
                    e.toString());
            LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }
    }
}
