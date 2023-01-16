package com.btpn.cakra.jenkinsjobspersist.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class MongoConnTests {

	@Inject MongoClient client;

	@ConfigProperty(name = "cakra.real.test") Boolean realTest;

	private static final String DB = "cakradb-staging";

	@Test
	void testConnDb() {
		if (Objects.isNull(realTest)|| realTest) assertNotNull(client.getDatabase(DB));
		else assertNotNull(realTest);
	}

	@Test
	void testConnVault() {
		if (Objects.isNull(realTest)|| realTest) assertNotNull(client.getDatabase(DB).getCollection("jenkins_job"));
		else assertNotNull(realTest);
	}

	@Test
	void testConnVaultConfig() {
		if (Objects.isNull(realTest)|| realTest) assertNotNull(client.getDatabase(DB).getCollection("jenkins_url"));
		else assertNotNull(realTest);
	}

}
