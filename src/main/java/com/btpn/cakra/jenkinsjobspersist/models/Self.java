package com.btpn.cakra.jenkinsjobspersist.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Self implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538760350698366971L;

	private String href;

}
