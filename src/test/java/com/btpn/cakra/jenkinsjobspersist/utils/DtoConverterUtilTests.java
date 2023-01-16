package com.btpn.cakra.jenkinsjobspersist.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

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
class DtoConverterUtilTests {

	@Test
	void testToModelJenkinsJob() {
		final var model = DtoConverterUtil.toModelJenkinsJob(jenkinsJobDto);
		assertEquals(jenkinsJob.getId(), model.getId());
		assertEquals(jenkinsJob.getAllBranchEvent(), model.getAllBranchEvent());
		assertEquals(jenkinsJob.getCronTrigger(), model.getCronTrigger());
		assertEquals(jenkinsJob.getGitUrl(), model.getGitUrl());
		assertEquals(jenkinsJob.getGroovyScript(), model.getGroovyScript());
		assertEquals(jenkinsJob.getGroupId(), model.getGroupId());
		assertEquals(jenkinsJob.getGroupName(), model.getGroupName());
		assertEquals(jenkinsJob.getJobHashcode(), model.getJobHashcode());
		assertEquals(jenkinsJob.getJobName(), model.getJobName());
		assertEquals(jenkinsJob.getJobStage(), model.getJobStage());
		assertEquals(jenkinsJob.getJobStatus(), model.getJobStatus());
		assertEquals(jenkinsJob.getMraEvent(), model.getMraEvent());
		assertEquals(jenkinsJob.getProjectId(), model.getProjectId());
		assertEquals(jenkinsJob.getProjectName(), model.getProjectName());
		assertEquals(jenkinsJob.getPushEvent(), model.getPushEvent());
		assertEquals(jenkinsJob.getSelectedTrigger(), model.getSelectedTrigger());
		assertEquals(jenkinsJob.getSpecificBranchEvent(), model.getSpecificBranchEvent());
		assertEquals(jenkinsJob.getStageHashcode(), model.getStageHashcode());
		assertEquals(jenkinsJob.getStageLabel(), model.getStageLabel());
		assertEquals(jenkinsJob.getTagEvent(), model.getTagEvent());
		assertEquals(jenkinsJob.getTargetBranch(), model.getTargetBranch());
	}

	@Test
	void testToDtoJenkinsJob() {
		final var dto = DtoConverterUtil.toDtoJenkinsJob(jenkinsJob);
		assertEquals(jenkinsJobDto, dto);
	}

	@Test
	void testToModelJenkinsUrl() {
		final var model = DtoConverterUtil.toModelJenkinsUrl(jenkinsUrlDto);
		assertEquals(jenkinsUrl.getId(), model.getId());
		assertEquals(jenkinsUrl.getGroupId(), model.getGroupId());
		assertEquals(jenkinsUrl.getJenkins(), model.getJenkins());
		assertEquals(jenkinsUrl.getJenkinsToken(), model.getJenkinsToken());
		assertEquals(jenkinsUrl.getJenkinsUname(), model.getJenkinsUname());
		assertEquals(jenkinsUrl.getJenkinsUrl(), model.getJenkinsUrl());
		assertEquals(jenkinsUrl.getName(), model.getName());
	}

	@Test
	void testToDtoJenkinsUrl() {
		final var dto = DtoConverterUtil.toDtoJenkinsUrl(jenkinsUrl);
		assertEquals(jenkinsUrlDto, dto);
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

	private final JenkinsUrl jenkinsUrl;

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

	private final JenkinsJob jenkinsJob;

	public DtoConverterUtilTests() {
		super();
		jenkinsUrl = JenkinsUrl.builder()
				.jenkins(jenkinsUrlDto.getJenkins())
				.groupId(jenkinsUrlDto.getGroupId())
				.jenkinsToken(jenkinsUrlDto.getJenkinsToken())
				.jenkinsUname(jenkinsUrlDto.getJenkinsUname())
				.jenkinsUrl(jenkinsUrlDto.getJenkinsUrl())
				.name(jenkinsUrlDto.getName())
				.id(new ObjectId(jenkinsUrlDto.getId()))
				.build();
		jenkinsJob = JenkinsJob.builder()
				.allBranchEvent(jenkinsJobDto.getAllBranchEvent())
				.cronTrigger(jenkinsJobDto.getCronTrigger())
				.gitUrl(jenkinsJobDto.getGitUrl())
				.groovyScript(jenkinsJobDto.getGroovyScript())
				.groupId(jenkinsJobDto.getGroupId())
				.groupName(jenkinsJobDto.getGroupName())
				.jobHashcode(jenkinsJobDto.getJobHashcode())
				.jobName(jenkinsJobDto.getJobName())
				.jobStage(jenkinsJobDto.getJobStage())
				.jobStatus(jenkinsJobDto.getJobStatus())
				.mraEvent(jenkinsJobDto.getMraEvent())
				.projectId(jenkinsJobDto.getProjectId())
				.projectName(jenkinsJobDto.getProjectName())
				.pushEvent(jenkinsJobDto.getPushEvent())
				.selectedTrigger(jenkinsJobDto.getSelectedTrigger())
				.specificBranchEvent(jenkinsJobDto.getSpecificBranchEvent())
				.stageHashcode(jenkinsJobDto.getStageHashcode())
				.stageLabel(jenkinsJobDto.getStageLabel())
				.tagEvent(jenkinsJobDto.getTagEvent())
				.targetBranch(jenkinsJobDto.getTargetBranch())
				.id(new ObjectId(jenkinsJobDto.getId()))
				.build();
	}

}
