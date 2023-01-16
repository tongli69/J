package com.btpn.cakra.jenkinsjobspersist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;
import com.btpn.cakra.jenkinsjobspersist.utils.DocumentUtil;
import com.btpn.cakra.jenkinsjobspersist.utils.DtoConverterUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class JenkinsUrlServiceTests {

	@Inject JenkinsUrlService service;

	@InjectMock MongoClient mongoClient;

	@ConfigProperty(name = "quarkus.mongodb.database") String dbName;

	@Test
	void testAll_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		assertEquals(List.of(jenkinsUrlDto), service.all());
	}

	@Test
	void testAll_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, service::all);
	}

	@Test
	void testFind_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertEquals(List.of(jenkinsUrlDto), service.find(jenkinsUrlDto));
	}

	@Test
	void testFind_Invalid() {
		assertThrows(BadRequestException.class, ()->service.find(new JenkinsUrlDto()));
	}

	@Test
	void testFind_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.find(jenkinsUrlDto));
	}

	@Test
	void testFindOne_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertEquals(jenkinsUrlDto, service.findOne(jenkinsUrlDto));
	}

	@Test
	void testFindOne_Invalid() {
		assertThrows(BadRequestException.class, ()->service.findOne(new JenkinsUrlDto()));
	}

	@Test
	void testFindOne_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.findOne(jenkinsUrlDto));
	}

	@Test
	void testPage_Success() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage(),
				List.of(jenkinsUrlDto)));
	}

	@Test
	void testPage_Max() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage(),
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList())));
	}

	@Test
	void testPage_Over() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage() + 1,
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList())));
	}

	@Test
	void testPage_Empty() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertThrows(NotFoundException.class, ()->service.page(pageReq));
	}

	@Test
	void testFindPage_Success() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsUrlDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage(),
				List.of(jenkinsUrlDto)));
	}

	@Test
	void testFindPage_Max() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsUrlDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage(),
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList())));
	}

	@Test
	void testFindPage_Over() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsUrlDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage() + 1,
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsUrlDto).collect(Collectors.toList())));
	}

	@Test
	void testFindPage_Invalid() {
		assertThrows(BadRequestException.class, ()->service.findPage(pageReq, new JenkinsUrlDto()));
	}

	@Test
	void testFindPage_Empty() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsUrl(jenkinsUrl))).thenReturn(sizePage);
		assertThrows(NotFoundException.class, ()->service.findPage(pageReq, jenkinsUrlDto));
	}

	@Test
	void testSaving_InsertWithId() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertTrue(service.saving(jenkinsUrlDto));
	}

	@Test
	void testSaving_InsertWithoutId() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl2))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl2))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertTrue(service.saving(DtoConverterUtil.toDtoJenkinsUrl(jenkinsUrl2)));
	}

	@Test
	void testSaving_Update() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertFalse(service.saving(jenkinsUrlDto));
	}

	@Test
	void testSaving_Invalid() {
		assertThrows(BadRequestException.class, ()->service.saving(new JenkinsUrlDto()));
	}

	@Test
	void testSaving_NotSaved() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotAcceptableException.class, ()->service.saving(jenkinsUrlDto));
	}

	@Test
	void testDeleting_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(false);
		assertTrue(service.deleting(jenkinsUrlDto));
	}

	@Test
	void testDeleting_Invalid() {
		assertThrows(BadRequestException.class, ()->service.deleting(DtoConverterUtil.toDtoJenkinsUrl(jenkinsUrl2)));
	}

	@Test
	void testDeleting_NotFound() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.deleting(jenkinsUrlDto));
	}

	@Test
	void testDeleting_NotDeleted() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsUrl(jenkinsUrl1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertThrows(NotAcceptableException.class, ()->service.deleting(jenkinsUrlDto));
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
	}

	private final String colName = "jenkins_url";

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

	private final JenkinsUrl jenkinsUrl = DtoConverterUtil.toModelJenkinsUrl(jenkinsUrlDto);

	private final JenkinsUrl jenkinsUrl1 = JenkinsUrl.builder().id(jenkinsUrl.getId()).build();

	private final JenkinsUrl jenkinsUrl2 = JenkinsUrl.builder().jenkinsUname("jenkins.svc")
			.groupId(52)
			.jenkins(false)
			.jenkinsToken("9ae673a981e9eba0c5ef69855875672c")
			.name("jenius2")
			.jenkinsUrl("https://jenkinsmaster04.corp.bankbtpn.co.id/").build();

}
