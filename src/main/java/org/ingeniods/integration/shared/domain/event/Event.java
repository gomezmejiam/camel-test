package org.ingeniods.integration.shared.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event<T extends Serializable> implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3175744076713751696L;
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Bogota"));

  private final String id;
  private HashMap<String, String> headers;
  private T data;

  public Event(HashMap<String, String> headers, T data) {
    this.headers = headers;
    this.data = data;
    this.headers.put("CREATION_DATE", now());
    this.id = UUID.randomUUID().toString();
  }

  private String now() {
    try {
      return FORMATTER.format(LocalDateTime.now());
    } catch (Exception e) {
      return LocalDateTime.now().toString();
    }
  }

  public Event(T data) {
    this(new HashMap<>(), data);
  }

  public void addHeader(String name, String value) {
    this.headers.put(name, value);
  }

  public void addHeader(String upperCase, Object value) {
    addHeader(upperCase, String.valueOf(value));
  }


}
