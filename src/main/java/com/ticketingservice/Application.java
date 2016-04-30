package com.ticketingservice;

import com.ticketingservice.helpers.ScheduledTaskExecutor;
import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

/**
 * Spring Configuration class
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setHeadless(false);
        application.run(args);
        //SpringApplication.run(Application.class, args).;
    }

    /**
     * Inmemory HSQL DB
     * @return Datasource
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .setScriptEncoding("UTF-8")
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
    public ScheduledTaskExecutor createScheduledTaskExector(@Value("${ticket.threadpool.size}") int threadPoolSize) {
        return new ScheduledTaskExecutor(threadPoolSize);
    }

    /**
     * GUI for the HSQL DB. Uncomment the @PostConstruct annotation to enable gui.
     */
    @PostConstruct
    public void getDbManager() {
        DatabaseManagerSwing.main(new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", ""});
    }
}