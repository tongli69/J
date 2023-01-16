package com.btpn.cakra.jenkinsjobspersist.models;

import java.io.Serializable;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class JobStage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3124403114697933963L;

	private Long endTimeMillis;
	private Long pauseDurationMillis;
	private Long queueDurationMillis;
	private Long startTimeMillis;
	private String name;
	private List<Stage> stages;
	private String description;
	private Long durationMillis;
	private String id;
	private Long estimatedDuration;
	private String status;

	@BsonProperty("_links")
	private Link link;

}
