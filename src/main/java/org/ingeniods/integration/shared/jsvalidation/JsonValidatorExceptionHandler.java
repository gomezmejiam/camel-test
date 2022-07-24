package org.ingeniods.integration.shared.jsvalidation;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.component.jsonvalidator.JsonValidatorErrorHandler;
import org.ingeniods.integration.shared.domain.event.ErrorDetail;
import org.ingeniods.integration.shared.domain.event.EventError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonValidatorExceptionHandler implements JsonValidatorErrorHandler {

  private final ObjectMapper mapper;

  public JsonValidatorExceptionHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void handleErrors(Exchange exchange, JsonSchema schema, Set<ValidationMessage> errors)
      throws ValidationException {
    String mensaje = getMessageBody(exchange);
    EventError<String> eventError = generateEventError(exchange, errors, mensaje);
    notifyEvent(exchange, eventError);
    exchange.setRouteStop(Boolean.TRUE);

  }

  private String getMessageBody(Exchange exchange) {
    return new String((byte[]) exchange.getIn().getBody());
  }

  private EventError<String> generateEventError(Exchange exchange, Set<ValidationMessage> errors,
      String mensaje) {
    EventError<String> eventError =
        new EventError<>(mensaje, getErrorDetails(errors, mensaje));
    setEventErrorHeader(exchange, eventError);
    return eventError;
  }

  private void setEventErrorHeader(Exchange exchange, EventError<String> eventError) {
    exchange.getIn().getHeaders().entrySet().stream()
        .filter(e -> e.getKey().startsWith("x-ingeniods-")).forEach(e -> eventError
            .addHeader(e.getKey().replace("x-ingeniods-", "").toUpperCase(), e.getValue()));
    eventError.addHeader("ERROR_TYPE", "FORMA");
  }

  private List<ErrorDetail> getErrorDetails(Set<ValidationMessage> errors,
      String dataEvaluate) {
    return errors.stream().map(e -> getErrorMessage(e, dataEvaluate))
        .collect(Collectors.toList());
  }

  private void notifyEvent(Exchange exchange, EventError<?> eventError) {
    String message = null;
    try {
      message = mapper.writeValueAsString(eventError);
    } catch (JsonProcessingException e) {
      message = eventError.toString();
    }
    log.error(message);
    exchange.getContext().createProducerTemplate().sendBody("direct:notify-error", message);
  }

  private ErrorDetail getErrorMessage(ValidationMessage error,
      String dataEvaluate) {

    String path = error.getPath().replaceAll("\\[[0-9]\\]", "[n]");
    String fieldName = error.getPath().replaceAll("([A-Za-z\\$.]+[\\[]+[0-9]+[\\]]+.)|([\\$.]+[a-zA-Z]+[.])", "");
    String fieldValue = getFieldValue(error, dataEvaluate);
    String type = "VALIDACIONES_FORMA";
    String message = replacePathInMessage(error.getMessage(), path).concat(" ").concat(fieldName)
        .concat(": ").concat(fieldValue);
    String pathFinal = error.getPath();
    return new ErrorDetail(type, message, pathFinal);
  }

  public String getFieldValue(ValidationMessage error, String dataEvaluate) {
    val value = JsonPath.read(dataEvaluate, error.getPath());
    if (Objects.isNull(value)) {
      return "null";
    }
    return value instanceof String ? "'".concat(value.toString()).concat("'") : value.toString();
  }

  @Override
  public void handleErrors(Exchange exchange, JsonSchema schema, Exception e)
      throws ValidationException {
    log.error(String.valueOf(e));
  }

  public String replacePathInMessage(String message, String path) {
    return message.replace(path + ":", "");
  }

}
