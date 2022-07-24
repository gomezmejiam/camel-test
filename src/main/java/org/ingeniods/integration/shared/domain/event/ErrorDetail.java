package org.ingeniods.integration.shared.domain.event;

import java.io.Serializable;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class ErrorDetail implements Serializable {
  private static final long serialVersionUID = -3901608789471456495L;
  private String type;
  private String description;
  private String code;
}
