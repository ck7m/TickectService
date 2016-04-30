package com.ticketingservice;

import com.ticketingservice.helpers.ScheduledTaskExecutor;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

@SpringBootApplication
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .setScriptEncoding("UTF-8")
                // .addScript("db/sql/schema.sql")
                .build();
    }

    private Properties getJpaProperties() {
        Properties prop = new Properties();
        prop.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        prop.setProperty("hibernate.show_sql", "false");
        return prop;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getEntityManager() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("com.ticketingservice.model");
        entityManagerFactory.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
        entityManagerFactory.setJpaProperties(getJpaProperties());
        return entityManagerFactory;
    }

    @Bean(destroyMethod = "destroy")
    public ScheduledTaskExecutor createScheduledTaskExector() {
        return new ScheduledTaskExecutor(5);
    }

    // @PostConstruct
    public void getDbManager() {
        DatabaseManagerSwing.main(
                new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
    }

}