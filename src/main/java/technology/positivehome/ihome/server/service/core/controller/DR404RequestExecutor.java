package technology.positivehome.ihome.server.service.core.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public interface DR404RequestExecutor {
    <R> R performRequest(SocketExecutor<R> executor) throws IOException, InterruptedException;

    interface SocketExecutor<R> {
        R run(Socket socket) throws IOException, InterruptedException;
    }
}
