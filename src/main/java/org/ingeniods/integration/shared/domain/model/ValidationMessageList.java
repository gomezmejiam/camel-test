package org.ingeniods.integration.shared.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationMessageList {
  
  private String integrationName;
  private List<ValidationMessageValue>  messages;
  
}
