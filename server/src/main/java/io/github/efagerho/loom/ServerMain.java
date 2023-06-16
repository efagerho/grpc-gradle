package io.github.efagerho.loom;

import io.github.efagerho.loom.grpc.EchoServiceGrpc;
import io.github.efagerho.loom.grpc.HelloRequest;
import io.github.efagerho.loom.grpc.HelloResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerMain {

    private Server server;

    private void start() throws IOException {
        int port = 8080;
        server = ServerBuilder.forPort(port).addService(new EchoServiceImpl()).build().start();

        Runtime
            .getRuntime()
            .addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        System.err.println("Shutting down gRPC server");
                        try {
                            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
            );
    }

    static class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

        @Override
        public void hello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            HelloResponse reply = HelloResponse
                .newBuilder()
                .setResponse("Server says " + "\"" + req.getName() + " " + req.getName() + "\"")
                .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final ServerMain greetServer = new ServerMain();
        greetServer.start();
        greetServer.server.awaitTermination();
    }
}
