package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class JenkinsUrlDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7106012076581111338L;

	private String id;
	private String jenkinsUname;
	private Integer groupId;
	private Boolean jenkins;
	private String jenkinsToken;
	private String name;
	private String jenkinsUrl;

}
