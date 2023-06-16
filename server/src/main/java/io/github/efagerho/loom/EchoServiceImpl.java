package io.github.efagerho.loom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.efagerho.loom.grpc.EchoServiceGrpc;
import io.github.efagerho.loom.grpc.HelloRequest;
import io.github.efagerho.loom.grpc.HelloResponse;
import io.grpc.stub.StreamObserver;

public class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void hello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        executor.submit(() -> {
            responseObserver.onNext(helloHandler(req));
            responseObserver.onCompleted();
        });
    }

    private HelloResponse helloHandler(HelloRequest req) {
        return HelloResponse
            .newBuilder()
            .setResponse("Server says " + "\"" + req.getName() + " " + req.getName() + "\"")
            .build();
    }
}
