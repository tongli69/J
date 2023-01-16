package com.btpn.cakra.jenkinsjobspersist.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.BadRequestException;

import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;

import lombok.Getter;

@Getter
public class PageReqInvalidException extends BadRequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7160826722338920668L;

	private final transient Set<ConstraintViolation<PageReq>> vio;

	public PageReqInvalidException(Set<ConstraintViolation<PageReq>> vio) {
		super(vio.parallelStream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.vio = vio;
	}

}
