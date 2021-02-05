package technology.positivehome.ihome.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;

/**
 * The accounts Spring configuration.
 */
@Configuration
@ComponentScan
@PropertySource("classpath:db-config.properties") // RC This is only to remind  about including properties
public class PersistenceConfiguration {


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public GeneratedKeyHolder keyHolder() {
        return new GeneratedKeyHolder();
    }

    @Bean
    @Autowired
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}