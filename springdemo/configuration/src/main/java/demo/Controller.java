package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RefreshScope
public class Controller {

    @Value("${spring.application.name:null}")
    private String applicationName;

    @Autowired
    private ConfigurableEnvironment environment;

    @GetMapping(value = "config/app/name", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public String getConfiguration(){
        return applicationName;
    }

    @PutMapping(value = "config", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public String setConfiguration(@RequestParam String key, @RequestParam String value){
        environment.getSystemProperties().put(key, value);
        return "success";
    }
}
