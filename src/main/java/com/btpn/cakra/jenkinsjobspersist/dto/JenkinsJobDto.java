package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;

import com.btpn.cakra.jenkinsjobspersist.models.JobStage;
import com.btpn.cakra.jenkinsjobspersist.models.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class JenkinsJobDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8976116258776309487L;

	private String id;
	private String jobName;
	private JobStatus jobStatus;
	private Boolean pushEvent;
	private String groovyScript;
	private String specificBranchEvent;
	private Long groupId;
	private Long jobHashcode;
	private Boolean tagEvent;
	private Boolean mraEvent;
	private String groupName;
	private JobStage jobStage;
	private String targetBranch;
	private String cronTrigger;
	private Boolean allBranchEvent;
	private String selectedTrigger;
	private String projectName;
	private Long stageHashcode;
	private String gitUrl;
	private Long projectId;
	private String stageLabel;

}
