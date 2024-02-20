package org.example.handler;

import org.example.handler.client.Client;
import org.example.handler.response.ApplicationStatusResponse;
import org.example.handler.response.Response;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Handler {

    private static final long TIMEOUT = 15;
    private volatile long lastRequest = 0L;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    private final Client client;

    public ClientHandler(Client client) {
        this.client = client;
    }

    @Override
    public ApplicationStatusResponse performOperation(String id) {
        try {
            CompletableFuture<Response> completableFuture1 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));
            CompletableFuture<Response> completableFuture2 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus2(id));

            CompletableFuture<Object> responseFuture = CompletableFuture.anyOf(completableFuture1, completableFuture2).thenApply(o -> o);

            Response response = (Response) responseFuture.get(TIMEOUT, TimeUnit.SECONDS);
            if (response instanceof Response.Failure
                    || response instanceof Response.RetryAfter) {
                return new ApplicationStatusResponse.Failure(Duration.ofMillis(lastRequest), retryCount.incrementAndGet());
            }
            Response.Success success = (Response.Success) response;
            retryCount.set(0);
            return new ApplicationStatusResponse.Success(success.applicationId(), success.applicationStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
