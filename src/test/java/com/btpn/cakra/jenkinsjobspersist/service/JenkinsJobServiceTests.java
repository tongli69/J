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

import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.models.JobStage;
import com.btpn.cakra.jenkinsjobspersist.models.JobStatus;
import com.btpn.cakra.jenkinsjobspersist.models.LastSuccessfulBuild;
import com.btpn.cakra.jenkinsjobspersist.models.Link;
import com.btpn.cakra.jenkinsjobspersist.models.Self;
import com.btpn.cakra.jenkinsjobspersist.models.Stage;
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
class JenkinsJobServiceTests {

	@Inject JenkinsJobService service;

	@InjectMock MongoClient mongoClient;

	@ConfigProperty(name = "quarkus.mongodb.database") String dbName;

	@Test
	void testAll_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		assertEquals(List.of(jenkinsJobDto), service.all());
	}

	@Test
	void testAll_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, service::all);
	}

	@Test
	void testFind_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertEquals(List.of(jenkinsJobDto), service.find(jenkinsJobDto));
	}

	@Test
	void testFind_Invalid() {
		assertThrows(BadRequestException.class, ()->service.find(new JenkinsJobDto()));
	}

	@Test
	void testFind_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.find(jenkinsJobDto));
	}

	@Test
	void testFindOne_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertEquals(jenkinsJobDto, service.findOne(jenkinsJobDto));
	}

	@Test
	void testFindOne_Invalid() {
		assertThrows(BadRequestException.class, ()->service.findOne(new JenkinsJobDto()));
	}

	@Test
	void testFindOne_Empty() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.findOne(jenkinsJobDto));
	}

	@Test
	void testPage_Success() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage(),
				List.of(jenkinsJobDto)));
	}

	@Test
	void testPage_Max() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage(),
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList())));
	}

	@Test
	void testPage_Over() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertEquals(service.page(pageReq), new PageDto<>(pageReq.getPage(), pageReq.getSize(), pageReq.getPage() + 1,
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList())));
	}

	@Test
	void testPage_Empty() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find().cursor().next()).thenReturn(DocumentUtil
				.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments()).thenReturn(sizePage);
		assertThrows(NotFoundException.class, ()->service.page(pageReq));
	}

	@Test
	void testFindPage_Success() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1)) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsJobDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage(),
				List.of(jenkinsJobDto)));
	}

	@Test
	void testFindPage_Max() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage()));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsJobDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage(),
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList())));
	}

	@Test
	void testFindPage_Over() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage())) + 1;
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		assertEquals(service.findPage(pageReq, jenkinsJobDto), new PageDto<>(pageReq.getPage(), pageReq.getSize(), 
				pageReq.getPage() + 1,
				LongStream.range(0, pageReq.getSize()).boxed().map(l->jenkinsJobDto).collect(Collectors.toList())));
	}

	@Test
	void testFindPage_Invalid() {
		assertThrows(BadRequestException.class, ()->service.findPage(pageReq, new JenkinsJobDto()));
	}

	@Test
	void testFindPage_Empty() {
		final var sizePage = (pageReq.getSize() * (pageReq.getPage() - 1));
		final var allows = LongStream.range(0, sizePage).boxed().map(l->l < sizePage - 1).collect(Collectors.toList())
				.toArray(new Boolean[0]);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, allows);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).countDocuments(DocumentUtil
				.documentJenkinsJob(jenkinsJob))).thenReturn(sizePage);
		assertThrows(NotFoundException.class, ()->service.findPage(pageReq, jenkinsJobDto));
	}

	@Test
	void testSaving_InsertWithId() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertTrue(service.saving(jenkinsJobDto));
	}

	@Test
	void testSaving_InsertWithoutId() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob2))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob2))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertTrue(service.saving(DtoConverterUtil.toDtoJenkinsJob(jenkinsJob2)));
	}

	@Test
	void testSaving_Update() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertFalse(service.saving(jenkinsJobDto));
	}

	@Test
	void testSaving_Invalid() {
		assertThrows(BadRequestException.class, ()->service.saving(new JenkinsJobDto()));
	}

	@Test
	void testSaving_NotSaved() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotAcceptableException.class, ()->service.saving(jenkinsJobDto));
	}

	@Test
	void testDeleting_Success() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(false);
		assertTrue(service.deleting(jenkinsJobDto));
	}

	@Test
	void testDeleting_Invalid() {
		assertThrows(BadRequestException.class, ()->service.deleting(DtoConverterUtil.toDtoJenkinsJob(jenkinsJob2)));
	}

	@Test
	void testDeleting_NotFound() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(false);
		assertThrows(NotFoundException.class, ()->service.deleting(jenkinsJobDto));
	}

	@Test
	void testDeleting_NotDeleted() {
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().hasNext()).thenReturn(true, false);
		when(mongoClient.getDatabase(dbName).getCollection(colName).find(DocumentUtil.documentJenkinsJob(jenkinsJob1))
				.cursor().next()).thenReturn(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertThrows(NotAcceptableException.class, ()->service.deleting(jenkinsJobDto));
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
	}

	private final String colName = "jenkins_job";

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

}
