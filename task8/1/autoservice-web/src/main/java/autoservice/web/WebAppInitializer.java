package autoservice.web;

import java.util.EnumSet;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import autoservice.config.AppConfig;
import autoservice.database.FlywayMigrator;
import autoservice.web.security.SecurityConfig;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * Регистрация DispatcherServlet и корневого контекста Spring (без web.xml).
 * Регистрирует фильтр Spring Security (springSecurityFilterChain).
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        FlywayMigrator.run();
        super.onStartup(servletContext);
        servletContext.addFilter(
                AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME))
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{AppConfig.class, SecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
