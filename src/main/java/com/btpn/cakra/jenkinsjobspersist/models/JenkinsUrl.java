package com.btpn.cakra.jenkinsjobspersist.models;

import java.io.Serializable;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class JenkinsUrl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8666092593681132021L;

	private ObjectId id;
	private String jenkinsUname;
	private Integer groupId;
	private Boolean jenkins;
	private String jenkinsToken;
	private String name;
	private String jenkinsUrl;

}
