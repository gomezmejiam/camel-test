package org.ingeniods.integration.downstream;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.ingeniods.integration.shared.util.Json;
import org.ingeniods.integration.shared.util.EndpointUtil;

public class RoutesApi extends RouteBuilder {

	private static final String COUNTRY = "country";
	private final String afterOperation;
	private final String path;

	public RoutesApi() {
		super();
		this.afterOperation = EndpointUtil.getCamelRoute("universities");
		this.path = EndpointUtil.getRestEndpoint("universities");
	}

	@Override
	public void configure() throws Exception {
		configureApi();
	}

	private void configureApi() {
		rest().post(path).route()
				.log(LoggingLevel.INFO, "requesting: ${in.body}")
				.process((exchange) -> {
					Map<?, ?> peticion = (Map<?, ?>) exchange.getMessage().getBody();
					exchange.getMessage().setHeader(COUNTRY, peticion.get(COUNTRY));
				})
				.to(this.afterOperation).endRest();

		rest().get(path).route()
				.log(LoggingLevel.INFO, "requesting: ${header.country}")
				.log(LoggingLevel.INFO, "requesting: ${in.body}").process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						HashMap<String, String> event = new HashMap<String, String>();
						String country = exchange.getMessage().getHeaders().get(COUNTRY).toString();
						event.put(COUNTRY, country);
						exchange.getMessage().setBody(Json.GSON.toJson(event));
					}
				}).to(this.afterOperation).endRest();
	}

}
