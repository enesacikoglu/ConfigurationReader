package com.cengenes.configuration.client.base;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public abstract class BaseServiceClient {

	protected static final Integer DEFAULT_SERVICE_TIMEOUT_MS = 120000;

	protected final Integer serviceTimeout;

	protected final String baseUrl;

	protected final String username;

	protected final String password;

	public BaseServiceClient(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.serviceTimeout = DEFAULT_SERVICE_TIMEOUT_MS;
	}

	public BaseServiceClient(String baseUrl, String username, String password, Integer connectionTimeout) {
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.serviceTimeout = connectionTimeout;
	}

	/**
	 * Creates a rest client starting from base url, if username and password is
	 * provided during initialization {@link HttpAuthenticationFeature} header
	 * is also included.
	 * 
	 * @return {@link WebTarget} client object
	 */
	protected WebTarget getWsClient() {
		Client client = ClientBuilder.newClient();

		if (StringUtils.isNotEmpty(username) && password != null) {
			HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(username, password);
			client.register(authFeature);
		}
		client.property(ClientProperties.CONNECT_TIMEOUT, serviceTimeout);
		client.property(ClientProperties.READ_TIMEOUT, serviceTimeout);

		return client.target(baseUrl);
	}

}
