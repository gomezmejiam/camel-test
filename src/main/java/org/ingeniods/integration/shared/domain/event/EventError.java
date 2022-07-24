package org.ingeniods.integration.shared.domain.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventError<T extends Serializable> extends Event<T>{
   
  private static final long serialVersionUID = -1334821630413426035L;
    private final List<ErrorDetail> details;
    
    public EventError(T data, Exception ex) {
      super(data);
      setErrorHeaders();
      this.details= new ArrayList<>();
      this.details.add(createDetail(ex));
    }
    
    public EventError(T data, ErrorDetail detail) {
      super(data);
      setErrorHeaders();
      this.details= new ArrayList<>();
      this.details.add(detail);
    }
    
    public EventError(T data, List<ErrorDetail> details) {
      super(data);
      setErrorHeaders();
      this.details=details;
    }
    
    private ErrorDetail createDetail(Exception ex) {
      return new ErrorDetail(ex.getClass().getSimpleName(), ex.getMessage(), ex.getClass().getName());
    }
    
    private void setErrorHeaders() {
      super.addHeader("ERROR", "TRUE");
      super.addHeader("EMITTED_BY", "camel-test");
    }

}
