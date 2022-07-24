package org.ingeniods.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ingeniods.integration.exception.IngenioBaseException;
import org.ingeniods.integration.shared.domain.model.ValidationMessageList;
import org.ingeniods.integration.shared.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
@Configuration
public class ValidationMessageConfig {

  @Autowired
  private ObjectMapper mapper;

  private void validateSpecName(Resource resource, String integrationName,
      ValidationMessageList messages) {
    if (!integrationName.equals(messages.getIntegrationName())) {
      log.error("Loading resources {}: {}", integrationName, resource.getFilename());
      throw new IngenioBaseException("El archivo de la especificación no corresponde con la ruta");
    }
  }

  private ValidationMessageList loadMessagesFile(Resource resource) {
    try {

      String text = ResourceLoader.loadContent(resource);
      return mapper.readValue(text, ValidationMessageList.class);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IngenioBaseException(e);
    }
  }

  private String getIntegrationName(Resource resource) {
    return ResourceLoader.getParentName(resource);
  }

}
