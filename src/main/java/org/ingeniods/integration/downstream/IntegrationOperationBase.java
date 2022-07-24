package org.ingeniods.integration.downstream;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.ingeniods.integration.shared.util.EndpointUtil;

import java.io.File;
import java.util.Map;

@SuppressWarnings("deprecation")
public class IntegrationOperationBase extends RouteBuilder {
	
	private static final String RESTCOUNTRIES = "https://restcountries.com/v3.1/name/${header.country}?bridgeEndpoint=true";
	private static final String RESTUNIVERSITIES = "http://universities.hipolabs.com/search?bridgeEndpoint=true";
	private static final String UNIVERSITIES = "universities";
	private static final String APPLICATION_JSON = "application/json";
	private static final String COUNTRY = "country";

	@Override
	public void configure() throws Exception {
		onException(HttpOperationFailedException.class).to("direct:notify-error").end();
		onException(ExchangeTimedOutException.class).to("direct:notify-error").end();
		configureRoute();
	}

	public void configureRoute() {
		requestCountry();
		readCountryResponse();
		saveOnDataStore();
		requestUniversities();
		guardarArchivo();
		sendToMQ();
	}

	private void requestCountry() {
		from(EndpointUtil.getCamelRoute(UNIVERSITIES)).setHeader("Content-Type", constant(APPLICATION_JSON))
				.setHeader("Accept", constant(APPLICATION_JSON)).setHeader(Exchange.HTTP_METHOD, constant("GET"))
				.removeHeader(Exchange.HTTP_PATH)
				.recipientList(simple(RESTCOUNTRIES))
				.unmarshal()
				.json(JsonLibrary.Jackson) // string a json
				.split(body())
				.to("direct:processcountry")
				.end();
	}


	private void readCountryResponse() {
		from("direct:processcountry").split(body())
		.parallelProcessing().timeout(1000)
		.to(EndpointUtil.getTransformationPath(COUNTRY))
		.to("direct:requestUniversities")
		.end();
	}
	
	private void requestUniversities() {
		from("direct:requestUniversities")
		.log(LoggingLevel.INFO, "Requesting for universities : ${in.body}")
		.process((exchange)-> {
			Map<?, ?> peticion = (Map<?, ?>) exchange.getMessage().getBody();
			exchange.getMessage().setHeader(Exchange.HTTP_QUERY , "country="+peticion.get("nombre"));
			exchange.getMessage().setHeader("nombre-pais", peticion.get("nombre"));
	        })
		.setHeader("Content-Type", constant(APPLICATION_JSON))
		.setHeader("Accept", constant(APPLICATION_JSON))
		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
		.removeHeader(Exchange.HTTP_PATH)
		.recipientList(simple(RESTUNIVERSITIES))
		.unmarshal().json(JsonLibrary.Jackson) // string a json
		.multicast().to("direct:saveOnMongo", "direct:guardararchivo", "direct:sendToMQ")
		.end();
	}

	private void saveOnDataStore() {
		from("direct:saveOnMongo")
		.split(body()).parallelProcessing()
		.log(LoggingLevel.INFO, "saveOnMongo : ${in.body}")
		.to("mongodb:mongoClient?database=cameltest&collection=universities&operation=insert")
		.end();
	}
	
	private void sendToMQ() {
		from("direct:sendToMQ")
		.split(body()).parallelProcessing()
		.marshal().json(JsonLibrary.Jackson) 
		.to(getValidationPath()).unmarshal().json(JsonLibrary.Jackson) 
		.log(LoggingLevel.INFO, " sendToMQ : ${in.body}")
		.convertBodyTo(String.class)
		.to(ExchangePattern.InOnly,"rabbitmq:cameltest?autoDelete=false&queue=cameltest&requestTimeout=1000&exchangePattern=InOnly&exchangeType=direct")
		.end();
	}
	
	private void guardarArchivo() {
		from("direct:guardararchivo").choice().when(this::fileNotExist)
		.marshal().json(JsonLibrary.Jackson)
				.to("file://c:/universities/?fileName=${header.nombre-pais}.json&fileExist=Ignore").end();
	}

	private boolean fileNotExist(Exchange exchange) {
		String country = String.valueOf(exchange.getMessage().getHeaders().get("nombre-pais"));
		File file = new File("c:/universities/" + country+".json");
		return !file.exists();
	}

	public String getValidationPath() {
		return "json-validator:integration/downstream/universities/schema.json?errorHandler=#bean:jsonSchemaErrorHandler&schemaLoader=#bean:mySchemaLoader";
	}

}
