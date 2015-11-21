package com.young.example;

import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.VirtualHost;
import com.linecorp.armeria.server.VirtualHostBuilder;
import com.linecorp.armeria.server.http.file.HttpFileService;
import com.linecorp.armeria.server.http.tomcat.TomcatService;
import com.linecorp.armeria.server.logging.LoggingService;

import io.netty.util.internal.PlatformDependent;

public class App {

    private static final Object lock = new Object();

    private static Server server;
    private static int httpPort;

    static int httpPort() {
        return httpPort;
    }

    static void startServer(ServerBuilder sb) throws Exception {
        synchronized (lock) {
            if (server != null) {
                return;
            }

            final VirtualHost vh = new VirtualHostBuilder()
                    .serviceUnder("/api", TomcatService.forClassPath(App.class)
                                                       .decorate(LoggingService::new))
                    .serviceUnder("/", HttpFileService.forClassPath("/assets"))
                    .build();
            sb.defaultVirtualHost(vh);
            server = sb.build();

            try {
                server.start().sync();
            } catch (InterruptedException e) {
                PlatformDependent.throwException(e);
            }

            httpPort = server.activePorts().values().stream()
                             .filter(p -> p.protocol() == SessionProtocol.HTTP)
                             .findAny().get().localAddress().getPort();
        }
    }

    static void stopServer() throws Exception {
        synchronized (lock) {
            if (server == null) {
                return;
            }

            server.stop();
            server = null;
        }
    }

    public static void main(String[] args) throws Exception {
        startServer(new ServerBuilder().port(8091, SessionProtocol.HTTP));
    }
}
