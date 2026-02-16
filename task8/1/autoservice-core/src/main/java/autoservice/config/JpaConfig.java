package autoservice.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Конфигурация JPA и транзакций Spring.
 * EntityManager и транзакции управляются через @PersistenceContext и @Transactional.
 */
@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Value("${db.driver:org.h2.Driver}")
    private String driverClassName;

    @Value("${db.url:jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE}")
    private String url;

    @Value("${db.username:sa}")
    private String username;

    @Value("${db.password:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        String jdbcUrl = url == null ? null : url.replace("\\:", ":").replace("\\=", "=");
        ds.setUrl(jdbcUrl != null && !jdbcUrl.isEmpty() ? jdbcUrl
            : "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE");
        ds.setUsername(username != null ? username : "sa");
        ds.setPassword(password != null ? password : "");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPersistenceUnitName("autoservice-pu");
        emf.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProps = new Properties();
        jpaProps.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProps.put("hibernate.hbm2ddl.auto", "update");
        jpaProps.put("hibernate.show_sql", "false");
        jpaProps.put("hibernate.format_sql", "true");
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
