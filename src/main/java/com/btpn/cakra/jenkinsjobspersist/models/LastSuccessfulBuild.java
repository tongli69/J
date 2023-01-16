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
public class LastSuccessfulBuild implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1542230902227884081L;

	private String displayName;

	@BsonProperty("_class")
	private String className;

	private Long timestamp;

}
