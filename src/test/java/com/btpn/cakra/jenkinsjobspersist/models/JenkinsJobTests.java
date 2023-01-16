package com.btpn.cakra.jenkinsjobspersist.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.service.JenkinsJobService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class JenkinsJobTests {

	@Inject Logger logger;

	@ConfigProperty(name = "cakra.real.test") Boolean realTest;

	@Inject JenkinsJobService service;

	@Test
	void testAll() {
		if (Objects.isNull(realTest)|| realTest) {
			final var all = service.all();
			logger.info(all);
			assertNotNull(all);
		} else assertNotNull(realTest);
	}

	@Test
	void testPage() {
		if (Objects.isNull(realTest)|| realTest) {
			final var all = service.page(PageReq.builder().page(1L).size(5L).build());
			logger.info(all);
			assertNotNull(all);
		} else assertNotNull(realTest);
	}

}
