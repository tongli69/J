package com.btpn.cakra.jenkinsjobspersist.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btpn.cakra.jenkinsjobspersist.dto.DeletedDto;
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageJobDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageJobReq;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.dto.SavedDto;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageJobReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.service.JenkinsJobService;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/jenkins-job")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsJobController {

	@Inject JenkinsJobService service;

	@Inject Validator validator;

	@GET
	public List<JenkinsJobDto> all() {
		return service.all();
	}

	@GET
	@Path("/find")
	public List<JenkinsJobDto> find(JenkinsJobDto req) {
		return service.find(req);
	}

	@GET
	@Path("/page")
	public PageJobDto page(PageReq req) {
		final var vio = validator.validate(req);
		if (!vio.isEmpty()) throw new PageReqInvalidException(vio);
		final var page = service.page(req);
		return PageJobDto.builder()
				.data(page.getData())
				.page(page.getPage())
				.pageCount(page.getPageCount())
				.size(page.getSize())
				.build();
	}

	@GET
	@Path("/find-page")
	public PageJobDto findPage(PageJobReq req) {
		final var vio = validator.validate(req);
		if (!vio.isEmpty()) throw new PageJobReqInvalidException(vio);
		final var vio2 = validator.validate(req.getPaging());
		if (!vio2.isEmpty()) throw new PageReqInvalidException(vio2);
		final var page = service.findPage(req.getPaging(), req.getWhere());
		return PageJobDto.builder()
				.data(page.getData())
				.page(page.getPage())
				.pageCount(page.getPageCount())
				.size(page.getSize())
				.build();
	}

	@GET
	@Path("/find-one")
	public JenkinsJobDto findOne(JenkinsJobDto req) {
		return service.findOne(req);
	}

	@POST
	public SavedDto save(JenkinsJobDto req) {
		return SavedDto.builder().inserted(service.saving(req)).build();
	}

	@DELETE
	public DeletedDto delete(JenkinsJobDto req) {
		return DeletedDto.builder().deleted(service.deleting(req)).build();
	}

}
