package com.aegis.aegis_backend.config;


import com.algorand.algosdk.v2.client.common.AlgodClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlgorandConfig {

    @Value("${algorand.algod.url}")
    private String algodUrl;

    @Value("${algorand.algod.token:}")
    private String algodToken;

    @Bean
    public AlgodClient algodClient() {
        // Public testnet node — empty token string is correct for tokenless public nodes
        return new AlgodClient(algodUrl, 443, algodToken);
    }
}
