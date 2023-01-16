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
public class JobStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4431316708122384030L;

	private String color;
	private LastSuccessfulBuild lastSuccessfulBuild;
	private String name;

	@BsonProperty("_class")
	private String className;

}
