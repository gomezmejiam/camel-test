package org.ingeniods.integration.configuration;

import org.apache.camel.component.jsonvalidator.JsonSchemaLoader;
import org.apache.camel.component.jsonvalidator.JsonValidatorErrorHandler;
import org.ingeniods.integration.shared.jsvalidation.JsonValidatorExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

@Service
public class JsonSpecsConfig {

  @Bean(name = "mySchemaLoader")
  public JsonSchemaLoader mySchemaLoader() {
    return (camelContext, schemaStream) -> JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        .getSchema(schemaStream);
  }

  @Bean(name = "jsonSchemaErrorHandler")
  public JsonValidatorErrorHandler errorHandler(@Autowired ObjectMapper mapper) {
    return new JsonValidatorExceptionHandler( mapper);
  }

}
