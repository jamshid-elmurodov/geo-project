package org.elec.geoproject.config;

import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.DefaultNameResolver;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .resolver(ipv4OnlyResolver());

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private AddressResolverGroup<InetSocketAddress> ipv4OnlyResolver() {
        return new AddressResolverGroup<>() {
            @Override
            protected io.netty.resolver.AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) {
                return new InetSocketAddressResolver(executor, new DefaultNameResolver(executor) {
                    @Override
                    protected void doResolveAll(String host,
                            Promise<List<InetAddress>> promise) throws Exception {

                        Promise<List<InetAddress>> all = executor.newPromise();
                        super.doResolveAll(host, all);

                        all.addListener(f -> {
                            if (!f.isSuccess()) {
                                promise.setFailure(f.cause());
                                return;
                            }
                            @SuppressWarnings("unchecked")
                            List<InetAddress> ipv4 = ((List<InetAddress>) f.getNow())
                                    .stream()
                                    .filter(a -> a instanceof Inet4Address)
                                    .collect(Collectors.toList());

                            if (ipv4.isEmpty()) {
                                promise.setFailure(
                                        new UnknownHostException("No IPv4 address found for: " + host));
                            } else {
                                promise.setSuccess(ipv4);
                            }
                        });
                    }
                });
            }
        };
    }
}
