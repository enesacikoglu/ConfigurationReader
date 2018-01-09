package com.cengenes.configuration.client;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cengenes.configuration.client.base.BaseServiceClient;
import com.cengenes.configuration.client.model.ConfigurationDto;
import com.cengenes.configuration.client.model.ConfigurationTypes;

/**
 * ConfigurationReader Initialize reader with given
 * <b>applicationName</b>,<b>configurationServiceURL</b> and check configuration
 * changes with given time interval <b>refreshTimeIntervalInMs</b> in
 * miliseconnds.
 * 
 * @author enes.acikoglu
 */
public class ConfigurationReader extends BaseServiceClient {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<ConfigurationDto> cachedConfigurationList = Collections.emptyList();

	private final String CONFIGURATION_SERVICE_PATH = "api/configuration/appname/{appname}";

	/**
	 * Initialize reader with given <b>applicationName</b>,<b>connectionURL</b>
	 * and check configuration changes with given time interval
	 * <b>refreshTimeIntervalInMs</b> in miliseconnds.
	 * 
	 * @param applicationName
	 *            Name of the service application
	 * @param connectionURL
	 *            URL to check configuration changes
	 * @param refreshTimeIntervalInMs
	 *            check changes with time interval
	 */
	public ConfigurationReader(final String applicationName, final String connectionURL,
			final Long refreshTimeIntervalInMs) {

		super(connectionURL, StringUtils.EMPTY, StringUtils.EMPTY);
		this.checkConfigurationChanges(applicationName, refreshTimeIntervalInMs);
	}

	private ConfigurationReader() throws IllegalAccessException {
		super(null, null, null);
		throw new IllegalAccessException("can not Initialize!");
	}

	/**
	 * Checks Configuration records with given <b>refreshTimeIntervalInMs</b>
	 * and updates <b>cachedConfigurationList</b>
	 * 
	 * @param applicationName
	 * @param refreshTimeIntervalInMs
	 */
	private void checkConfigurationChanges(final String applicationName, final Long refreshTimeIntervalInMs) {

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		// First Run fill the Cache list.
		cachedConfigurationList = getApplicationConfigurationsByName(applicationName);

		Runnable task = () -> {

			if (isNewOrUpdatedConfiguration(applicationName))
				cachedConfigurationList = getApplicationConfigurationsByName(applicationName);
		};

		executor.scheduleAtFixedRate(task, 0, refreshTimeIntervalInMs, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @param applicationName
	 * @return true If cachedConfiguration changed with given applicationName
	 *         else return false.
	 * 
	 */
	private Boolean isNewOrUpdatedConfiguration(final String applicationName) {

		List<ConfigurationDto> newConfigurationList = getApplicationConfigurationsByName(applicationName);

		if (cachedConfigurationList.isEmpty())
			return cachedConfigurationList.isEmpty();

		else if (newConfigurationList.isEmpty())
			return !newConfigurationList.isEmpty();

		return !(cachedConfigurationList.containsAll(newConfigurationList)
				&& newConfigurationList.containsAll(cachedConfigurationList));
	}

	/**
	 * 
	 * @param key
	 *            applicationName
	 * @param type
	 *            is a class parameter that can be {String, Boolean, Integer,
	 *            Double} class
	 * @return Value of given <b>key</b> with related <b>type</b>
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(final String key, final Class<T> type) {

		return (T) getApplicationConfigurationsByKey(key).parallelStream()
				.filter(configuration -> ConfigurationTypes.getConfigurationType(configuration.getType()).equals(type))
				.filter(Objects::nonNull).map(configuration -> getInstanceForField(type, configuration.getValue()))
				.findFirst().orElse(null);

	}

	private List<ConfigurationDto> getApplicationConfigurationsByKey(String key) {
		return cachedConfigurationList.parallelStream().filter(configuration -> configuration.getName().equals(key))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private List<ConfigurationDto> getApplicationConfigurationsByName(final String applicationName) {

		List<ConfigurationDto> configurationResponse = Collections.emptyList();

		try {

			WebTarget webTarget = getWsClient().path(CONFIGURATION_SERVICE_PATH).resolveTemplate("appname",
					applicationName);

			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

			Response response = invocationBuilder.get();

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				logger.info("Invalid Response with url {} for getAllConfigurationsByAppName is {}",
						response.getLocation(), response.toString());
				return configurationResponse;
			}

			configurationResponse = response.readEntity(new GenericType<List<ConfigurationDto>>() {
			});

		} catch (Exception e) {
			logger.error("Error on calling getApplicationConfigurationsByName {}", e.getMessage(), e);
		}

		return configurationResponse;
	}
	
		/**
	 * Creates the target object for property bind operation. Not all types of
	 * objects supported. Supported types {@link String}, {@link Integer},
	 * {@link Double}, {@link Boolean}
	 * 
	 * @param clazz
	 * @param value
	 * @return Casted Object
	 */
	private static Object getInstanceForField(Class<?> clazz, String value) {
		if (clazz == String.class) {
			return value;
		} else if (clazz == Integer.class) {
			return Integer.parseInt(value);
		} else if (clazz == Double.class) {
			return Double.parseDouble(value);
		} else if (clazz == Boolean.class) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}


}