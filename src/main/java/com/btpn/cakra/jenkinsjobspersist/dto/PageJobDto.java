package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PageJobDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7378885854736990389L;

	private Long page;
	private Long pageCount;
	private Long size;
	private List<JenkinsJobDto> data;

}
