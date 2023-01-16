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
public class ErrorRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2644692310384930516L;

	private Integer status;
	private Long timestamp;
	private String path;
	private String exception;
	private String message;

}
