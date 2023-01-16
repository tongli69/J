package com.btpn.cakra.jenkinsjobspersist.models;

import java.io.Serializable;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Stage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2790456997288601503L;

	private Long pauseDurationMillis;
	private Long startTimeMillis;
	private String execNode;
	private String name;
	private Long durationMillis;
	private String id;
	private String status;

	@BsonProperty("_links")
	private Link link;

}
