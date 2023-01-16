package com.btpn.cakra.jenkinsjobspersist.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.BadRequestException;

import com.btpn.cakra.jenkinsjobspersist.dto.PageJobReq;

import lombok.Getter;

@Getter
public class PageJobReqInvalidException extends BadRequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4234772851796279257L;

	private final transient Set<ConstraintViolation<PageJobReq>> vio;

	public PageJobReqInvalidException(Set<ConstraintViolation<PageJobReq>> vio) {
		super(vio.parallelStream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.vio = vio;
	}

}
