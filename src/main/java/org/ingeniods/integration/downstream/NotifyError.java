package org.ingeniods.integration.downstream;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SuppressWarnings("deprecation")
public class NotifyError extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    onException(HttpOperationFailedException.class)
        .process(exchange -> log.error(String.valueOf(exchange)));
    configureRoute();
  }

  public void configureRoute() {
    //sino se especifica la cola de mensajes se crearan de manera automatica
    from("direct:notify-error").to(ExchangePattern.InOnly,"rabbitmq:message-error?autoDelete=false&queue=message-error&requestTimeout=1000&exchangePattern=InOnly&exchangeType=direct").end();
  }

}
