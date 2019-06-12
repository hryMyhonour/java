/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package configserver.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
//@EnableConfigServer
@Slf4j
public class AppServer implements CommandLineRunner {
    @Autowired
    private AutowireCapableBeanFactory factory;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AppServer.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        ApplicationContext context = application.run(args);
        log.info("SpringApplication bean factory hashcode: {}", context.getAutowireCapableBeanFactory().hashCode());
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("autowired bean factory hashcode: {}", factory.hashCode());
    }
}
