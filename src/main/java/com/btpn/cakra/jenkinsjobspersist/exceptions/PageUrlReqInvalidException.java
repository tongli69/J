package com.btpn.cakra.jenkinsjobspersist.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.BadRequestException;

import com.btpn.cakra.jenkinsjobspersist.dto.PageUrlReq;

import lombok.Getter;

@Getter
public class PageUrlReqInvalidException extends BadRequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7863802596392863522L;

	private final transient Set<ConstraintViolation<PageUrlReq>> vio;

	public PageUrlReqInvalidException(Set<ConstraintViolation<PageUrlReq>> vio) {
		super(vio.parallelStream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
		this.vio = vio;
	}

}
