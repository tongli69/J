package com.btpn.cakra.jenkinsjobspersist.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.btpn.cakra.jenkinsjobspersist.config.TimestampCapturer;
import com.btpn.cakra.jenkinsjobspersist.dto.DeletedDto;
import com.btpn.cakra.jenkinsjobspersist.dto.ErrorRes;
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.dto.PageUrlDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageUrlReq;
import com.btpn.cakra.jenkinsjobspersist.dto.SavedDto;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageUrlReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;
import com.btpn.cakra.jenkinsjobspersist.utils.DocumentUtil;
import com.btpn.cakra.jenkinsjobspersist.utils.DtoConverterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;

@QuarkusTest
class JenkinsUrlControllerTests {

	@InjectMock TimestampCapturer capturer;

	@InjectMock MongoClient mongoClient;

	@ConfigProperty(name = "quarkus.mongodb.database") String dbName;

	@Inject ObjectMapper mapper;

	@Test
	void testAll_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().when().get(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(List.of(jenkinsUrlDto))));
	}

	@Test
	void testAll_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(false);
		RestAssured.given().when().get(path).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(404)
				.exception(NotFoundException.class.getName())
				.message("All Jenkins url not found")
				.build())));
	}

	@Test
	void testFind_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().get(pathFind).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(List.of(jenkinsUrlDto))));
	}

	@Test
	void testFind_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsUrlDto()))
		.when().get(pathFind).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFind).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins url properties must be not null at least once")
				.build())));
	}

	@Test
	void testFind_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().get(pathFind).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFind).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins url finds not found")
				.build())));
	}

	@Test
	void testFindOne_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().get(pathFindOne).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(jenkinsUrlDto)));
	}

	@Test
	void testFindOne_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsUrlDto()))
		.when().get(pathFindOne).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindOne).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins url properties must be not null at least once")
				.build())));
	}

	@Test
	void testFindOne_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().get(pathFindOne).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindOne).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins url not found")
				.build())));
	}

	@Test
	void testPage_Success() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(List.of(jenkinsUrlDto))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage())
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testPage_Max() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList()))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage())
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testPage_Over() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList()))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage() + 1)
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testPage_PageNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageReq.builder().size(pageReq.getSize()).build()))
		.when().get(pathPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("page must be not null")
				.build())));
	}

	@Test
	void testPage_SizeNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageReq.builder().page(pageReq.getPage()).build()))
		.when().get(pathPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("size must be not null")
				.build())));
	}

	@Test
	void testPage_PageZero() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageReq.builder().size(pageReq.getSize()).page(0L).build()))
		.when().get(pathPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("page must be positive")
				.build())));
	}

	@Test
	void testPage_SizeZero() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageReq.builder().page(pageReq.getPage()).size(0L).build()))
		.when().get(pathPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("size must be positive")
				.build())));
	}

	@Test
	void testPage_Empty() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins url pages not found")
				.build())));
	}

	@Test
	void testFindPage_Success() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageUrlReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(List.of(jenkinsUrlDto))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage())
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testFindPage_Max() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageUrlReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList()))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage())
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testFindPage_Over() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageUrlReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageUrlDto.builder()
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList()))
				.page(pageReq.getPage())
				.pageCount(pageReq.getPage() + 1)
				.size(pageReq.getSize())
				.build())));
	}

	@Test
	void testFindPage_PagingNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().where(jenkinsUrlDto).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageUrlReqInvalidException.class.getName())
				.message("paging must be not null")
				.build())));
	}

	@Test
	void testFindPage_PageNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().where(jenkinsUrlDto)
				.paging(PageReq.builder().size(pageReq.getSize()).build()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("page must be not null")
				.build())));
	}

	@Test
	void testFindPage_SizeNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().where(jenkinsUrlDto)
				.paging(PageReq.builder().page(pageReq.getPage()).build()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("size must be not null")
				.build())));
	}

	@Test
	void testFindPage_PageZero() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().where(jenkinsUrlDto)
				.paging(PageReq.builder().page(0L).size(pageReq.getSize()).build()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("page must be positive")
				.build())));
	}

	@Test
	void testFindPage_SizeZero() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().where(jenkinsUrlDto)
				.paging(PageReq.builder().size(0L).page(pageReq.getPage()).build()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageReqInvalidException.class.getName())
				.message("size must be positive")
				.build())));
	}

	@Test
	void testFindPage_WhereNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().paging(pageReq).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageUrlReqInvalidException.class.getName())
				.message("where must be not null")
				.build())));
	}

	@Test
	void testFindPage_WhereEmpty() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageUrlReq.builder().paging(pageReq).where(new JenkinsUrlDto()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins url properties must be not null at least once")
				.build())));
	}

	@Test
	void testFindPage_Empty() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageUrlReq))
		.when().get(pathFindPage).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins url find pages not found")
				.build())));
	}

	@Test
	void testPost_InsertWithId() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(true).build())));
	}

	@Test
	void testPost_InsertWithoutId() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl2))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl2))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(DtoConverterUtil.toDtoJenkinsUrl(jenkinsUrl2)))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(true).build())));
	}

	@Test
	void testPost_Update() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(false).build())));
	}

	@Test
	void testPost_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsUrlDto()))
		.when().post(path).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins url properties must be not null at least once")
				.build())));
	}

	@Test
	void testPost_NotSaved() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().post(path).then().statusCode(406).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(406)
				.exception(NotAcceptableException.class.getName())
				.message("Jenkins url cannot saved")
				.build())));
	}

	@Test
	void testDelete_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().delete(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(DeletedDto.builder().deleted(true).build())));
	}

	@Test
	void testDelete_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(DtoConverterUtil.toDtoJenkinsUrl(jenkinsUrl2)))
		.when().delete(path).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(400)
				.exception(BadRequestException.class.getName())
				.message("ID must be not null")
				.build())));
	}

	@Test
	void testDelete_NotFound() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().delete(path).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins url not found")
				.build())));
	}

	@Test
	void testDelete_NotDeleted() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsUrlDto))
		.when().delete(path).then().statusCode(406).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(406)
				.exception(NotAcceptableException.class.getName())
				.message("Jenkins url cannot deleted")
				.build())));
	}

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void before() {
		final var db = mock(MongoDatabase.class);
		when(mongoClient.getDatabase(dbName)).thenReturn(db);
		final var col = mock(MongoCollection.class);
		when(db.getCollection(colName)).thenReturn(col);
		final var iter = mock(FindIterable.class);
		when(col.find()).thenReturn(iter);
		final var cur = mock(MongoCursor.class);
		when(iter.cursor()).thenReturn(cur);
		final var iter1 = mock(FindIterable.class);
		when(col.find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))).thenReturn(iter1);
		final var cur1 = mock(MongoCursor.class);
		when(iter1.cursor()).thenReturn(cur1);
		final var iter2 = mock(FindIterable.class);
		when(col.find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))).thenReturn(iter2);
		final var cur2 = mock(MongoCursor.class);
		when(iter2.cursor()).thenReturn(cur2);
		final var iter3 = mock(FindIterable.class);
		when(col.find(DocumentUtil.documentJenkinsUrl(jenkinsUrl2))).thenReturn(iter3);
		final var cur3 = mock(MongoCursor.class);
		when(iter3.cursor()).thenReturn(cur3);
		when(capturer.now()).thenReturn(1L);
	}

	private final String colName = "jenkins_url";

	private final String path = "/jenkins-url";

	private final String pathFind = String.format("%s/find", path);

	private final String pathPage = String.format("%s/page", path);

	private final String pathFindPage = String.format("%s/find-page", path);

	private final String pathFindOne = String.format("%s/find-one", path);

	private final PageReq pageReq = PageReq.builder()
			.page(2L)
			.size(5L)
			.build();

	private final JenkinsUrlDto jenkinsUrlDto = JenkinsUrlDto.builder()
			.id("5b3dc7eeaddc2f167034d22c")
			.jenkinsUname("jenkins.svc")
			.groupId(52)
			.jenkins(false)
			.jenkinsToken("9ae673a981e9eba0c5ef69855875672c")
			.name("jenius2")
			.jenkinsUrl("https://jenkinsmaster04.corp.bankbtpn.co.id/")
			.build();

	private final PageUrlReq pageUrlReq = PageUrlReq.builder()
			.paging(pageReq)
			.where(jenkinsUrlDto)
			.build();

	private final JenkinsUrl jenkinsUrl = DtoConverterUtil.toModelJenkinsUrl(jenkinsUrlDto);

	private final JenkinsUrl jenkinsUrl1 = JenkinsUrl.builder().id(jenkinsUrl.getId()).build();

	private final JenkinsUrl jenkinsUrl2 = JenkinsUrl.builder().jenkinsUname("jenkins.svc")
			.groupId(52)
			.jenkins(false)
			.jenkinsToken("9ae673a981e9eba0c5ef69855875672c")
			.name("jenius2")
			.jenkinsUrl("https://jenkinsmaster04.corp.bankbtpn.co.id/").build();

	private final Map<String, Object> headers = Map.of("Content-Type", "application/json");

}
