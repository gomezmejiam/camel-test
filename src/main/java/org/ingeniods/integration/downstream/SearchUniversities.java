package org.ingeniods.integration.downstream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchUniversities {

    @Bean
    public IntegrationOperationBase searchUniversitiesIntegrationOperation() {
        return new IntegrationOperationBase();
    }

    @Bean
    public RoutesApi searchUniversitiesDownStreamEndpoint() {
        return new RoutesApi();
    }

}
