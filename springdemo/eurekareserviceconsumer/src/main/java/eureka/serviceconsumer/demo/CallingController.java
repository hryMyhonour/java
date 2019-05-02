package eureka.serviceconsumer.demo;

import grpc.demo.EchoRequest;
import grpc.demo.EchoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CallingController {
    private static final String SERVICE_NAME = "servicesupplier";
    private final DiscoveryClient client;

    @Autowired
    public CallingController(DiscoveryClient client) {
        this.client = client;
    }

    @GetMapping("grpc/call/{param}")
    public String grpcCall(@PathVariable String param) {
        log.info("all services: {}", client.getServices());
        ServiceInstance instance = client.getInstances(SERVICE_NAME).get(0);
        ManagedChannel channel = NettyChannelBuilder.forAddress(instance.getHost(), instance.getPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        EchoRequest request = EchoRequest.newBuilder().setSay(param).build();
        return EchoServiceGrpc.newBlockingStub(channel).send(request).getBack();
    }

}
