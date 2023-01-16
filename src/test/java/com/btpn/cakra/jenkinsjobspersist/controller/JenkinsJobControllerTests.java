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
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageJobReq;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.dto.SavedDto;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageJobReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.models.JobStage;
import com.btpn.cakra.jenkinsjobspersist.models.JobStatus;
import com.btpn.cakra.jenkinsjobspersist.models.LastSuccessfulBuild;
import com.btpn.cakra.jenkinsjobspersist.models.Link;
import com.btpn.cakra.jenkinsjobspersist.models.Self;
import com.btpn.cakra.jenkinsjobspersist.models.Stage;
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
class JenkinsJobControllerTests {

	@InjectMock TimestampCapturer capturer;

	@InjectMock MongoClient mongoClient;

	@ConfigProperty(name = "quarkus.mongodb.database") String dbName;

	@Inject ObjectMapper mapper;

	@Test
	void testGet_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		RestAssured.given().when().get(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(List.of(jenkinsJobDto))));
	}

	@Test
	void testGet_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(false);
		RestAssured.given().when().get(path).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(404)
				.exception(NotFoundException.class.getName())
				.message("All Jenkins job not found")
				.build())));
	}

	@Test
	void testFind_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().get(pathFind).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(List.of(jenkinsJobDto))));
	}

	@Test
	void testFind_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsJobDto()))
		.when().get(pathFind).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFind).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins job properties must be not null at least once")
				.build())));
	}

	@Test
	void testFind_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().get(pathFind).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFind).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins job finds not found")
				.build())));
	}

	@Test
	void testFindOne_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().get(pathFindOne).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(jenkinsJobDto)));
	}

	@Test
	void testFindOne_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsJobDto()))
		.when().get(pathFindOne).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindOne).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins job properties must be not null at least once")
				.build())));
	}

	@Test
	void testFindOne_Empty() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().get(pathFindOne).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindOne).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins job not found")
				.build())));
	}

	@Test
	void testPage_Success() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage())
				.data(List.of(jenkinsJobDto))
				.build())));
	}

	@Test
	void testPage_Max() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage())
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList()))
				.build())));
	}

	@Test
	void testPage_Over() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage() + 1)
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList()))
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
		.body(mapper.writeValueAsString(PageReq.builder().page(0L).size(pageReq.getSize()).build()))
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
		.body(mapper.writeValueAsString(PageReq.builder().size(0L).page(pageReq.getPage()).build()))
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
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageReq))
		.when().get(pathPage).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathPage).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins job pages not found")
				.build())));
	}

	@Test
	void testFindPage_Success() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageJobReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage())
				.data(List.of(jenkinsJobDto))
				.build())));
	}

	@Test
	void testFindPage_Max() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageJobReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage())
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList()))
				.build())));
	}

	@Test
	void testFindPage_Over() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageJobReq))
		.when().get(pathFindPage).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(PageJobDto.builder()
				.page(pageReq.getPage()).size(pageReq.getSize()).pageCount(pageReq.getPage() + 1)
				.data(LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList()))
				.build())));
	}

	@Test
	void testFindPage_PagingNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageJobReq.builder().where(jenkinsJobDto).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageJobReqInvalidException.class.getName())
				.message("paging must be not null")
				.build())));
	}

	@Test
	void testFindPage_PageNull() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageJobReq.builder().where(jenkinsJobDto)
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
		.body(mapper.writeValueAsString(PageJobReq.builder().where(jenkinsJobDto)
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
		.body(mapper.writeValueAsString(PageJobReq.builder().where(jenkinsJobDto)
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
		.body(mapper.writeValueAsString(PageJobReq.builder().where(jenkinsJobDto)
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
		.body(mapper.writeValueAsString(PageJobReq.builder().paging(pageReq).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(PageJobReqInvalidException.class.getName())
				.message("where must be not null")
				.build())));
	}

	@Test
	void testFindPage_WhereEmpty() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(PageJobReq.builder().paging(pageReq).where(new JenkinsJobDto()).build()))
		.when().get(pathFindPage).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins job properties must be not null at least once")
				.build())));
	}

	@Test
	void testFindPage_Empty() throws JsonProcessingException {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(pageJobReq))
		.when().get(pathFindPage).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(pathFindPage).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins job find pages not found")
				.build())));
	}

	@Test
	void testSaving_InsertWithId() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(true).build())));
	}

	@Test
	void testSaving_InsertWithoutId() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob2))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob2))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(DtoConverterUtil.toDtoJenkinsJob(jenkinsJob2)))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(true).build())));
	}

	@Test
	void testSaving_Update() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().post(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(SavedDto.builder().inserted(false).build())));
	}

	@Test
	void testSaving_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(new JenkinsJobDto()))
		.when().post(path).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(400)
				.exception(BadRequestException.class.getName())
				.message("Jenkins job properties must be not null at least once")
				.build())));
	}

	@Test
	void testSaving_NotSaved() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().post(path).then().statusCode(406).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(406)
				.exception(NotAcceptableException.class.getName())
				.message("Jenkins job cannot saved")
				.build())));
	}

	@Test
	void testDeleting_Success() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().delete(path).then().statusCode(200).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(DeletedDto.builder().deleted(true).build())));
	}

	@Test
	void testDeleting_Invalid() throws JsonProcessingException {
		RestAssured.given().headers(headers)
		.body(mapper.writeValueAsString(DtoConverterUtil.toDtoJenkinsJob(jenkinsJob2)))
		.when().delete(path).then().statusCode(400).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(400)
				.exception(BadRequestException.class.getName())
				.message("ID must be not null")
				.build())));
	}

	@Test
	void testDeleting_NotFound() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().delete(path).then().statusCode(404).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(404)
				.exception(NotFoundException.class.getName())
				.message("Jenkins job not found")
				.build())));
	}

	@Test
	void testDeleting_NotDeleted() throws JsonProcessingException {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		RestAssured.given().headers(headers).body(mapper.writeValueAsString(jenkinsJobDto))
		.when().delete(path).then().statusCode(406).and()
		.body(CoreMatchers.is(mapper.writeValueAsString(ErrorRes.builder()
				.timestamp(1L).path(path).status(406)
				.exception(NotAcceptableException.class.getName())
				.message("Jenkins job cannot deleted")
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
		when(col.find(DocumentUtil.documentJenkinsJob(jenkinsJob))).thenReturn(iter1);
		final var cur1 = mock(MongoCursor.class);
		when(iter1.cursor()).thenReturn(cur1);
		final var iter2 = mock(FindIterable.class);
		when(col.find(DocumentUtil.documentJenkinsJob(jenkinsJob1))).thenReturn(iter2);
		final var cur2 = mock(MongoCursor.class);
		when(iter2.cursor()).thenReturn(cur2);
		final var iter3 = mock(FindIterable.class);
		when(col.find(DocumentUtil.documentJenkinsJob(jenkinsJob2))).thenReturn(iter3);
		final var cur3 = mock(MongoCursor.class);
		when(iter3.cursor()).thenReturn(cur3);
		when(capturer.now()).thenReturn(1L);
	}

	private final String colName = "jenkins_job";

	private final String path = "/jenkins-job";

	private final String pathFind = String.format("%s/find", path);

	private final String pathPage = String.format("%s/page", path);

	private final String pathFindPage = String.format("%s/find-page", path);

	private final String pathFindOne = String.format("%s/find-one", path);

	private final PageReq pageReq = PageReq.builder()
			.page(2L)
			.size(5L)
			.build();

	private final JenkinsJobDto jenkinsJobDto = JenkinsJobDto.builder()
			.id("5b3dc82faddc2f1671c40630")
			.jobName("spam-service-deploy-stg")
			.jobStatus(JobStatus.builder()
					.color("blue")
					.lastSuccessfulBuild(LastSuccessfulBuild.builder()
							.displayName("#1, 0.2.2-SNAPSHOT")
							.className("org.jenkinsci.plugins.workflow.job.WorkflowRun")
							.timestamp(1516795105851L)
							.build())
					.name("spam-service-deploy-stg")
					.className("org.jenkinsci.plugins.workflow.job.WorkflowJob")
					.build())
			.pushEvent(false)
			.groovyScript("deploy-stg.groovy")
			.specificBranchEvent("master")
			.groupId(17L)
			.jobHashcode(-1701012592L)
			.tagEvent(false)
			.mraEvent(false)
			.groupName("fes")
			.jobStage(JobStage.builder()
					.endTimeMillis(1516795146855L)
					.pauseDurationMillis(0L)
					.queueDurationMillis(16L)
					.startTimeMillis(1516795105867L)
					.link(Link.builder()
							.self(Self.builder()
									.href("/job/fes/job/spam-service-deploy-stg/lastBuild/wfapi/describe")
									.build())
							.build())
					.name("#1, 0.2.2-SNAPSHOT")
					.stages(List.of(Stage.builder()
							.pauseDurationMillis(0L)
							.startTimeMillis(1516795115399L)
							.link(Link.builder()
									.self(Self.builder()
											.href("/job/fes/job/spam-service-deploy-stg/lastBuild/execution"
													+ "/node/7/wfapi/describe")
											.build())
									.build())
							.execNode("")
							.name("tag & push")
							.durationMillis(17428L)
							.id("7")
							.status("SUCCESS")
							.build(), Stage.builder()
							.pauseDurationMillis(0L)
							.startTimeMillis(1516795132916L)
							.link(Link.builder()
									.self(Self.builder()
											.href("/job/fes/job/spam-service-deploy-stg/lastBuild/execution"
													+ "/node/21/wfapi/describe")
											.build())
									.build())
							.execNode("")
							.name("deploy")
							.durationMillis(13713L)
							.id("21")
							.status("SUCCESS")
							.build()))
					.description("Triggered by Irvan Febrianto Halim1")
					.durationMillis(40988L)
					.id("1")
					.estimatedDuration(40988L)
					.status("SUCCESS")
					.build())
			.targetBranch("master")
			.cronTrigger("0 0 0 *")
			.allBranchEvent(true)
			.selectedTrigger("promotion")
			.projectName("spam-service")
			.stageHashcode(2103688778L)
			.gitUrl("https://git.ecommchannels.com/fes/spam-service.git")
			.projectId(623L)
			.stageLabel("fes-stg")
			.build();

	private final PageJobReq pageJobReq = PageJobReq.builder()
			.paging(pageReq)
			.where(jenkinsJobDto)
			.build();

	private final JenkinsJob jenkinsJob = DtoConverterUtil.toModelJenkinsJob(jenkinsJobDto);

	private final JenkinsJob jenkinsJob1 = JenkinsJob.builder().id(jenkinsJob.getId()).build();

	private final JenkinsJob jenkinsJob2 = JenkinsJob.builder().jobName("spam-service-deploy-stg")
			.jobStatus(JobStatus.builder()
					.color("blue")
					.lastSuccessfulBuild(LastSuccessfulBuild.builder()
							.displayName("#1, 0.2.2-SNAPSHOT")
							.className("org.jenkinsci.plugins.workflow.job.WorkflowRun")
							.timestamp(1516795105851L)
							.build())
					.name("spam-service-deploy-stg")
					.className("org.jenkinsci.plugins.workflow.job.WorkflowJob")
					.build())
			.pushEvent(false)
			.groovyScript("deploy-stg.groovy").build();

	private final Map<String, Object> headers = Map.of("Content-Type", "application/json");

}
