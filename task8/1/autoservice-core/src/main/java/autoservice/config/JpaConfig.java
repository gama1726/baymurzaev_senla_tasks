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
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USERNAME");
        String envPassword = System.getenv("DB_PASSWORD");
        String envDriver = System.getenv("DB_DRIVER");
        String resolvedUrl = envUrl != null && !envUrl.isEmpty() ? envUrl : url;
        String resolvedUser = envUser != null ? envUser : username;
        String resolvedPassword = envPassword != null ? envPassword : (password != null ? password : "");
        String resolvedDriver = envDriver != null && !envDriver.isEmpty() ? envDriver : driverClassName;

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(resolvedDriver);
        String jdbcUrl = resolvedUrl.replace("\\:", ":").replace("\\=", "=");
        ds.setUrl(jdbcUrl.isEmpty() ? "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE" : jdbcUrl);
        ds.setUsername(resolvedUser);
        ds.setPassword(resolvedPassword);
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
        String jdbcUrl = dataSource instanceof DriverManagerDataSource
            ? ((DriverManagerDataSource) dataSource).getUrl()
            : "";
        String dialect = jdbcUrl != null && jdbcUrl.contains("postgresql")
            ? "org.hibernate.dialect.PostgreSQLDialect"
            : "org.hibernate.dialect.H2Dialect";
        jpaProps.put("hibernate.dialect", dialect);
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
