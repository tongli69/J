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
public class Link implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3972266367291580423L;

	private Self self;

}
