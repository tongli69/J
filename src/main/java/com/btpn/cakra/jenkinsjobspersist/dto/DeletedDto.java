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
public class DeletedDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -667219547769570312L;

	private Boolean deleted;

}
