package eureka.servicesupplier.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ServiceController {
    private final DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String serviceId;
    @Value("${server.port}")
    private int port;

    @Autowired
    public ServiceController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("echo/{in}")
    public String echo(@PathVariable String in) {
        List<String> serviceName = this.discoveryClient.getServices();
        log.info("all service name: {}", serviceName);
        return in;
    }

}
