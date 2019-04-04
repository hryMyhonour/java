package eureka.serviceconsumer.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class CallingController {
    private final RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient client;

    @Autowired
    public CallingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("call/{param}")
    public String call(@PathVariable String param) {
        log.info("all services: {}", client.getServices());
        log.info(String.valueOf(client.getInstances("servicesupplier").get(0).getUri()));
        return restTemplate.getForEntity("http://servicesupplier/echo/" + param, String.class).getBody();
    }

}
