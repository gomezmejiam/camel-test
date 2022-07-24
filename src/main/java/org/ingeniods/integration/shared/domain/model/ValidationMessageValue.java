package org.ingeniods.integration.shared.domain.model;

import java.util.HashMap;
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
public class ValidationMessageValue {
  
  private String path;
  private String  defaultValue;
  private HashMap<String,String>  description;
  
  public String getMessageDescription(String type) {
    return description.getOrDefault(type, defaultValue);
  }
  
}
