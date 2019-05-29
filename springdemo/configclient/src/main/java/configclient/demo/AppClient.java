/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package configclient.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
public class AppClient implements CommandLineRunner {

    @Value("${client.name:null}")
    private String name;

    public static void main(String[] args) {
        SpringApplication.run(AppClient.class);
    }


    @Override
    public void run(String... args) throws Exception {

    }
}

@RefreshScope
@RestController
class MessageRestController {

    @Value("${client.name:null}")
    private String name;

    @RequestMapping("/message")
    String getMessage() {
        return this.name;
    }
}
