package com.young.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.VirtualHost;
import com.linecorp.armeria.server.VirtualHostBuilder;
import com.linecorp.armeria.server.composition.SimpleCompositeServiceBuilder;
import com.linecorp.armeria.server.http.file.HttpFileService;
import com.linecorp.armeria.server.http.tomcat.TomcatService;
import com.linecorp.armeria.server.logging.LoggingService;

import io.netty.util.internal.PlatformDependent;

public final class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

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

            final Service service = new SimpleCompositeServiceBuilder()
                    .serviceUnder("/api", TomcatService.forClassPath(App.class))
                    .serviceUnder("/", HttpFileService.forClassPath("/assets"))
                    .build();

            final VirtualHost vh = new VirtualHostBuilder()
                    .serviceUnder("/", service.decorate(LoggingService::new)).build();
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
            logger.info("Listening on port {}...", httpPort);
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
        String port = System.getenv("PORT");
        if (port == null) {
            port = System.getProperty("PORT");
        }

        startServer(new ServerBuilder().port(port != null ? Integer.parseInt(port) : 8091,
                                             SessionProtocol.HTTP));
    }

    private App() {}
}
