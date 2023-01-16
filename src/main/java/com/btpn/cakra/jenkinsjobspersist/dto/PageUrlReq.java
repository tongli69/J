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
public class PageUrlReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1075539664586499360L;

	@NotNull(message = "paging must be not null")
	private PageReq paging;

	@NotNull(message = "where must be not null")
	private JenkinsUrlDto where;

}
