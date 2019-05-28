package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * 在一个更高优先级的切面写入配置，Test可以执行到代码
 */
public class ConfigConfigurationProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties properties = new Properties();
        properties.setProperty("spring.config.location", "file:D:/IDEA/java/springdemo/configuration/app-config/");
        environment.getPropertySources().addFirst(
                new PropertiesPropertySource("configConfiguration", properties));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
