package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PageDto<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5847572824336689300L;

	private Long page;
	private Long size;
	private Long pageCount;
	private List<T> data;

}
