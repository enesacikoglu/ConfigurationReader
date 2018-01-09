package com.cengenes.configuration.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;

import com.cengenes.configuration.client.model.ConfigurationDto;

@PrepareForTest(ConfigurationReader.class)
public class ConfigurationReaderTest extends PowerMockTestCase {

	@Test
	public void should_find_active_configuration() throws Exception {

		List<ConfigurationDto> list = new ArrayList<>();
		list.add(new ConfigurationDto());

		ConfigurationReader configurationReader = new ConfigurationReader("TestApp", "testUrl", 0L);
		ConfigurationReader configurationReaderSpy = PowerMockito.spy(configurationReader);

		PowerMockito.doReturn(list).when(configurationReaderSpy, "getApplicationConfigurationsByName", "TestApp");

		String value = configurationReaderSpy.getValue("TestApp", String.class);

		Assert.assertEquals(value, "Mock private method example: Test");

	}

}
