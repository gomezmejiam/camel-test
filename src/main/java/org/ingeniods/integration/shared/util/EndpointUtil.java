package org.ingeniods.integration.shared.util;

public class EndpointUtil {

	private static final long TIMEOUT = 240000;
	private static final int CONCURRENT_CONSUMERS = 1;

	public static String getTransformationPath() {
		return "jolt:integration/downstream/jolt.json";
	}

	public static String getCamelRoute(String name) {
		return "seda:get" + name + "?exchangePattern=inOnly&waitForTaskToComplete=Never&timeout=" + TIMEOUT
				+ "&concurrentConsumers=" + CONCURRENT_CONSUMERS;
	}
	
	public static String getRestEndpoint(String name) {
		return "/" + name + "/download";
	}

}
