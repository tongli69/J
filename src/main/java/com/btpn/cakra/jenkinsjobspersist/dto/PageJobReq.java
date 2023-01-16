package com.btpn.cakra.jenkinsjobspersist.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PageJobReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6597562236305923849L;

	@NotNull(message = "paging must be not null")
	private PageReq paging;

	@NotNull(message = "where must be not null")
	private JenkinsJobDto where;

}
