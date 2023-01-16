package com.btpn.cakra.jenkinsjobspersist.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.btpn.cakra.jenkinsjobspersist.constant.JenkinsJobFields;
import com.btpn.cakra.jenkinsjobspersist.constant.JenkinsUrlFields;
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;
import com.btpn.cakra.jenkinsjobspersist.models.JobStage;
import com.btpn.cakra.jenkinsjobspersist.models.JobStatus;
import com.btpn.cakra.jenkinsjobspersist.models.LastSuccessfulBuild;
import com.btpn.cakra.jenkinsjobspersist.models.Link;
import com.btpn.cakra.jenkinsjobspersist.models.Self;
import com.btpn.cakra.jenkinsjobspersist.models.Stage;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DocumentUtilTests {

	@Test
	void testDocumentJenkinsUrl_Success() {
		final var doc = DocumentUtil.documentJenkinsUrl(jenkinsUrl);
		assertTrue(Stream.of(JenkinsUrlFields.GROUP_ID, JenkinsUrlFields.ID, JenkinsUrlFields.JENKINS,
				JenkinsUrlFields.JENKINS_TOKEN, JenkinsUrlFields.JENKINS_UNAME, JenkinsUrlFields.JENKINS_URL,
				JenkinsUrlFields.NAME).allMatch(doc::containsKey));
	}

	@Test
	void testDocumentJenkinsUrl_Error() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.documentJenkinsUrl(DtoConverterUtil
				.toModelJenkinsUrl(new JenkinsUrlDto())));
	}

	@Test
	void testIsDocumentJenkinsUrl_Success() {
		assertTrue(DocumentUtil.isDocumentJenkinsUrl(DocumentUtil.documentJenkinsUrl(jenkinsUrl)));
	}

	@Test
	void testIsDocumentJenkinsUrl_Empty() {
		assertFalse(DocumentUtil.isDocumentJenkinsUrl(new Document()));
	}

	@Test
	void testJenkinsUrlDocument_Success() {
		final var res = DocumentUtil.jenkinsUrlDocument(DocumentUtil.documentJenkinsUrl(jenkinsUrl));
		assertEquals(jenkinsUrl, res);
	}

	@Test
	void testJenkinsUrlDocument_Empty() {
		assertEquals(new JenkinsUrl(), DocumentUtil.jenkinsUrlDocument(new Document()));
	}

	@Test
	void testUpdateJenkinsUrl_Success() {
		final var bson = DocumentUtil.updateJenkinsUrl(jenkinsUrl);
		assertTrue(bson.toBsonDocument().keySet().parallelStream().allMatch(k->{
			final var v = bson.toBsonDocument().get(k).asDocument();
			return Stream.of(JenkinsUrlFields.GROUP_ID, JenkinsUrlFields.ID, JenkinsUrlFields.JENKINS,
					JenkinsUrlFields.JENKINS_TOKEN, JenkinsUrlFields.JENKINS_UNAME, JenkinsUrlFields.JENKINS_URL,
					JenkinsUrlFields.NAME).parallel()
					.allMatch(v::containsKey);
		}));
	}

	@Test
	void testUpdateJenkinsUrl_Empty() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.updateJenkinsUrl(new JenkinsUrl()));
	}

	@Test
	void testDeleteJenkinsUrl_Success() {
		final var bson = DocumentUtil.deleteJenkinsUrl(jenkinsUrl);
		assertTrue(bson.toBsonDocument().keySet().parallelStream().allMatch(k->{
			final var v = bson.toBsonDocument().get(k).asArray();
			return Stream.of(JenkinsUrlFields.GROUP_ID, JenkinsUrlFields.ID, JenkinsUrlFields.JENKINS,
					JenkinsUrlFields.JENKINS_TOKEN, JenkinsUrlFields.JENKINS_UNAME, JenkinsUrlFields.JENKINS_URL,
					JenkinsUrlFields.NAME).parallel()
					.allMatch(k1->v.parallelStream().anyMatch(v1->v1.asDocument().containsKey(k1)));
		}));
	}

	@Test
	void testDeleteJenkinsUrl_Empty() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.deleteJenkinsUrl(new JenkinsUrl()));
	}

	@Test
	void testDocumentJenkinsJob_Success() {
		final var doc = DocumentUtil.documentJenkinsJob(jenkinsJob);
		assertTrue(Stream.of(JenkinsJobFields.ALL_BRANCH_EVENT, JenkinsJobFields.CRON_TRIGGER,
				JenkinsJobFields.GIT_URL, JenkinsJobFields.GROOVY_SCRIPT, JenkinsJobFields.GROUP_ID,
				JenkinsJobFields.GROUP_NAME, JenkinsJobFields.JOB_HASHCODE, JenkinsJobFields.JOB_NAME,
				JenkinsJobFields.JOB_STAGE, JenkinsJobFields.JOB_STATUS, JenkinsJobFields.MRA_EVENT,
				JenkinsJobFields.PROJECT_ID, JenkinsJobFields.PROJECT_NAME, JenkinsJobFields.PUSH_EVENT,
				JenkinsJobFields.SELECTED_TRIGGER, JenkinsJobFields.SPECIFIC_BRANCH_EVENT, JenkinsJobFields.STAGE_HASHCODE,
				JenkinsJobFields.STAGE_LABEL, JenkinsJobFields.TAG_EVENT, JenkinsJobFields.TARGET_BRANCH)
				.allMatch(doc::containsKey));
	}

	@Test
	void testDocumentJenkinsJob_Error() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.documentJenkinsJob(DtoConverterUtil
				.toModelJenkinsJob(new JenkinsJobDto())));
	}

	@Test
	void testJenkinsJobDocument_Success() {
		final var model = DocumentUtil.jenkinsJobDocument(DocumentUtil.documentJenkinsJob(jenkinsJob));
		assertEquals(model, jenkinsJob);
	}

	@Test
	void testJenkinsJobDocument_Empty() {
		assertEquals(DocumentUtil.jenkinsJobDocument(new Document()), new JenkinsJob());
	}

	@Test
	void testIsDocumentJenkinsJob_Success() {
		assertTrue(DocumentUtil.isDocumentJenkinsJob(DocumentUtil.documentJenkinsJob(jenkinsJob)));
	}

	@Test
	void testIsDocumentJenkinsJob_Empty() {
		assertFalse(DocumentUtil.isDocumentJenkinsJob(new Document()));
	}

	@Test
	void testUpdateJenkinsJob_Success() {
		final var bson = DocumentUtil.updateJenkinsJob(jenkinsJob);
		assertTrue(bson.toBsonDocument().keySet().parallelStream().allMatch(k->{
			final var v = bson.toBsonDocument().get(k).asDocument();
			return Stream.of(JenkinsJobFields.ALL_BRANCH_EVENT, JenkinsJobFields.CRON_TRIGGER,
					JenkinsJobFields.GIT_URL, JenkinsJobFields.GROOVY_SCRIPT, JenkinsJobFields.GROUP_ID,
					JenkinsJobFields.GROUP_NAME, JenkinsJobFields.JOB_HASHCODE, JenkinsJobFields.JOB_NAME,
					JenkinsJobFields.JOB_STAGE, JenkinsJobFields.JOB_STATUS, JenkinsJobFields.MRA_EVENT,
					JenkinsJobFields.PROJECT_ID, JenkinsJobFields.PROJECT_NAME, JenkinsJobFields.PUSH_EVENT,
					JenkinsJobFields.SELECTED_TRIGGER, JenkinsJobFields.SPECIFIC_BRANCH_EVENT, JenkinsJobFields.STAGE_HASHCODE,
					JenkinsJobFields.STAGE_LABEL, JenkinsJobFields.TAG_EVENT, JenkinsJobFields.TARGET_BRANCH)
					.parallel()
					.allMatch(v::containsKey);
		}));
	}

	@Test
	void testUpdateJenkinsJob_Empty() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.updateJenkinsJob(new JenkinsJob()));
	}

	@Test
	void testDeleteJenkinsJob_Success() {
		final var bson = DocumentUtil.deleteJenkinsJob(jenkinsJob);
		assertTrue(bson.toBsonDocument().keySet().parallelStream().allMatch(k->{
			final var v = bson.toBsonDocument().get(k).asArray();
			return Stream.of(JenkinsJobFields.ALL_BRANCH_EVENT, JenkinsJobFields.CRON_TRIGGER,
					JenkinsJobFields.GIT_URL, JenkinsJobFields.GROOVY_SCRIPT, JenkinsJobFields.GROUP_ID,
					JenkinsJobFields.GROUP_NAME, JenkinsJobFields.JOB_HASHCODE, JenkinsJobFields.JOB_NAME,
					JenkinsJobFields.JOB_STAGE, JenkinsJobFields.JOB_STATUS, JenkinsJobFields.MRA_EVENT,
					JenkinsJobFields.PROJECT_ID, JenkinsJobFields.PROJECT_NAME, JenkinsJobFields.PUSH_EVENT,
					JenkinsJobFields.SELECTED_TRIGGER, JenkinsJobFields.SPECIFIC_BRANCH_EVENT, JenkinsJobFields.STAGE_HASHCODE,
					JenkinsJobFields.STAGE_LABEL, JenkinsJobFields.TAG_EVENT, JenkinsJobFields.TARGET_BRANCH)
					.parallel()
					.allMatch(k1->v.parallelStream().anyMatch(v1->v1.asDocument().containsKey(k1)));
		}));
	}

	@Test
	void testDeleteJenkinsJob_Empty() {
		assertThrows(BadRequestException.class, ()->DocumentUtil.deleteJenkinsJob(new JenkinsJob()));
	}

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

}
