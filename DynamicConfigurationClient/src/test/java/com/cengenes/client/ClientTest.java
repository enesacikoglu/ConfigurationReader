package com.cengenes.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cengenes.configuration.client.ConfigurationReader;

public class ClientTest {

	public static void main(String[] args) {

		ConfigurationReader configurationReader = new ConfigurationReader("EnesApp", "http://localhost:8080/", 5000L);

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		Runnable task = () -> {

			System.out.println("Value:" + configurationReader.getValue("TimeOut", Integer.class));
			System.out.println("ValueStr:" + configurationReader.getValue("AppName", String.class));
		};

		executor.scheduleAtFixedRate(task, 0, 6001L, TimeUnit.MILLISECONDS);

	}

}
