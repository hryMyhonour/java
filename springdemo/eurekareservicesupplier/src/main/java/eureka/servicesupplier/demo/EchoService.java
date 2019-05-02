package eureka.servicesupplier.demo;

import grpc.demo.EchoRequest;
import grpc.demo.EchoRespond;
import grpc.demo.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;

@GRpcService
@Service
public class EchoService extends EchoServiceGrpc.EchoServiceImplBase {

    @Override
    public void send(EchoRequest request, StreamObserver<EchoRespond> responseObserver) {
        EchoRespond respond = EchoRespond.newBuilder().setBack(request.getSay()).build();
        responseObserver.onNext(respond);
        responseObserver.onCompleted();
    }
}
