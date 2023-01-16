package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PageReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7410270203656562213L;

	@NotNull(message = "page must be not null")
	@Positive(message = "page must be positive")
	private Long page;

	@NotNull(message = "size must be not null")
	@Positive(message = "size must be positive")
	private Long size;

}
