package com.btpn.cakra.jenkinsjobspersist.utils;

import java.util.Objects;

import org.bson.types.ObjectId;

import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;

public class DtoConverterUtil {

	private DtoConverterUtil() {
		super();
	}

	public static JenkinsJob toModelJenkinsJob(JenkinsJobDto dto) {
		final var model = new JenkinsJob();
		model.setId(Objects.nonNull(dto.getId()) ? new ObjectId(dto.getId()) : null);
		model.setAllBranchEvent(dto.getAllBranchEvent());
		model.setCronTrigger(dto.getCronTrigger());
		model.setGitUrl(dto.getGitUrl());
		model.setGroovyScript(dto.getGroovyScript());
		model.setGroupId(dto.getGroupId());
		model.setGroupName(dto.getGroupName());
		model.setJobHashcode(dto.getJobHashcode());
		model.setJobName(dto.getJobName());
		model.setJobStage(dto.getJobStage());
		model.setJobStatus(dto.getJobStatus());
		model.setMraEvent(dto.getMraEvent());
		model.setProjectId(dto.getProjectId());
		model.setProjectName(dto.getProjectName());
		model.setPushEvent(dto.getPushEvent());
		model.setSelectedTrigger(dto.getSelectedTrigger());
		model.setSpecificBranchEvent(dto.getSpecificBranchEvent());
		model.setStageHashcode(dto.getStageHashcode());
		model.setStageLabel(dto.getStageLabel());
		model.setTagEvent(dto.getTagEvent());
		model.setTargetBranch(dto.getTargetBranch());
		return model;
	}

	public static JenkinsJobDto toDtoJenkinsJob(JenkinsJob model) {
		return JenkinsJobDto.builder()
				.allBranchEvent(model.getAllBranchEvent())
				.cronTrigger(model.getCronTrigger())
				.gitUrl(model.getGitUrl())
				.groovyScript(model.getGroovyScript())
				.groupId(model.getGroupId())
				.groupName(model.getGroupName())
				.id(Objects.nonNull(model.getId()) ? model.getId().toHexString() : null)
				.jobHashcode(model.getJobHashcode())
				.jobName(model.getJobName())
				.jobStage(model.getJobStage())
				.jobStatus(model.getJobStatus())
				.mraEvent(model.getMraEvent())
				.projectId(model.getProjectId())
				.projectName(model.getProjectName())
				.pushEvent(model.getPushEvent())
				.selectedTrigger(model.getSelectedTrigger())
				.specificBranchEvent(model.getSpecificBranchEvent())
				.stageHashcode(model.getStageHashcode())
				.stageLabel(model.getStageLabel())
				.tagEvent(model.getTagEvent())
				.targetBranch(model.getTargetBranch())
				.build();
	}

	public static JenkinsUrl toModelJenkinsUrl(JenkinsUrlDto dto) {
		final var model = new JenkinsUrl();
		model.setId(Objects.nonNull(dto.getId()) ? new ObjectId(dto.getId()) : null);
		model.setGroupId(dto.getGroupId());
		model.setJenkins(dto.getJenkins());
		model.setJenkinsToken(dto.getJenkinsToken());
		model.setJenkinsUname(dto.getJenkinsUname());
		model.setJenkinsUrl(dto.getJenkinsUrl());
		model.setName(dto.getName());
		return model;
	}

	public static JenkinsUrlDto toDtoJenkinsUrl(JenkinsUrl model) {
		return JenkinsUrlDto.builder()
				.groupId(model.getGroupId())
				.id(Objects.nonNull(model.getId()) ? model.getId().toHexString() : null)
				.jenkins(model.getJenkins())
				.jenkinsToken(model.getJenkinsToken())
				.jenkinsUname(model.getJenkinsUname())
				.jenkinsUrl(model.getJenkinsUrl())
				.name(model.getName())
				.build();
	}

}
