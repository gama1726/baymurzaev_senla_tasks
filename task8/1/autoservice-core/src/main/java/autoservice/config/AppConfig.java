package autoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Конфигурация Spring приложения.
 * Включает сканирование компонентов и загрузку свойств из config.properties.
 */
@Configuration
@ComponentScan(basePackages = "autoservice")
@PropertySource("classpath:config.properties")
@Import(JpaConfig.class)
public class AppConfig {

    /**
     * Настраивает внедрение параметров из конфигурационных файлов в бины через @Value.
     * Обрабатывает плейсхолдеры вида ${property.name} в аннотации @Value.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
