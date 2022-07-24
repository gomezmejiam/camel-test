package org.ingeniods.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class CamelTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(CamelTestApplication.class, args);
  }

}
