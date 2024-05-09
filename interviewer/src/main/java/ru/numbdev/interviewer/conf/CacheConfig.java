package ru.numbdev.interviewer.conf;

import com.hazelcast.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public ClientConfig clientConfig(
            @Value("${spring.hazelcast.cluster-name}") String clusterName,
            @Value("${spring.hazelcast.address}") String address
    ) {
        var clientConfig = new ClientConfig();
        clientConfig.setClusterName(clusterName);
        clientConfig.getNetworkConfig().addAddress(address);
        return clientConfig;
    }
}
