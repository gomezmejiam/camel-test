package org.ingeniods.integration.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ingeniods.integration.shared.domain.model.ValidationMessageList;
import org.ingeniods.integration.shared.domain.model.ValidationMessageValue;
import org.ingeniods.integration.shared.jsvalidation.MessageService;
import org.ingeniods.integration.shared.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ValidationMessageConfig {

  @Autowired
  private ObjectMapper mapper;

  @Bean
  public MessageService messageService() throws IOException {
    Map<String, List<ValidationMessageValue>> map = loadMessageMap();
    return new MessageService(map);
  }


  private Map<String, List<ValidationMessageValue>> loadMessageMap() throws IOException {
    Resource[] resources = ResourceLoader.loadResources("classpath*:/integration/*/message.json");
    Map<String, List<ValidationMessageValue>> map = new HashMap<>();
    Arrays.stream(resources).forEach(resource -> {
      String integrationName = getIntegrationName(resource);
      ValidationMessageList messages = loadMessagesFile(resource);
      map.putIfAbsent(messages.getIntegrationName(), messages.getMessages());
      log.info("Loadig resources {}: {}", integrationName, resource.getFilename());
    });
    return map;
  }

  private ValidationMessageList loadMessagesFile(Resource resource) {
    try {

      String text = ResourceLoader.loadContent(resource);
      return mapper.readValue(text, ValidationMessageList.class);
    } catch (IOException e) {
      // TODO: remover runtime exception
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private String getIntegrationName(Resource resource) {
    return ResourceLoader.getParentName(resource);
  }

}
