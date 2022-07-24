package org.ingeniods.integration.shared.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceLoader {
  
  private ResourceLoader() {}

  public static Resource[] loadResources(String path) throws IOException {
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    return resourcePatternResolver.getResources(path);
  }
  
  public static Stream<String> loadContent(String path) throws IOException {
    return Arrays.stream(ResourceLoader.loadResources(path)).map(ResourceLoader::loadContent);
  }

  public static String loadContent(Resource resource) {
    try {
      return readResource(resource);
    } catch (IOException e) {
      log.error(e.getMessage());
      // TODO: remover runtime exception
      throw new RuntimeException(e);
    }
  }
  
  public static String readResource(Resource resource) throws IOException {
    return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
 
}

  public static String getParentName(Resource resource) {
    try {
      String[] path = resource.getURL().getPath().split("/");
      return  path[path.length-2];
    } catch (IOException e) {
      log.error(e.getMessage());
      // TODO: remover runtime exception
      throw new RuntimeException(e);
    }
  }

}
