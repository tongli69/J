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
public class SavedDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1616846038369868728L;

	private Boolean inserted;

}
