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
import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.dto.PageUrlDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageUrlReq;
import com.btpn.cakra.jenkinsjobspersist.dto.SavedDto;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.exceptions.PageUrlReqInvalidException;
import com.btpn.cakra.jenkinsjobspersist.service.JenkinsUrlService;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/jenkins-url")
@Produces(MediaType.APPLICATION_JSON)
public class JenkinsUrlController {

	@Inject JenkinsUrlService service;

	@Inject Validator validator;

	@GET
	public List<JenkinsUrlDto> all() {
		return service.all();
	}

	@GET
	@Path("/find")
	public List<JenkinsUrlDto> find(JenkinsUrlDto req) {
		return service.find(req);
	}

	@GET
	@Path("/page")
	public PageUrlDto page(PageReq req) {
		final var vio = validator.validate(req);
		if (!vio.isEmpty()) throw new PageReqInvalidException(vio);
		final var page = service.page(req);
		return PageUrlDto.builder()
				.data(page.getData())
				.page(page.getPage())
				.pageCount(page.getPageCount())
				.size(page.getSize())
				.build();
	}

	@GET
	@Path("/find-page")
	public PageUrlDto findPage(PageUrlReq req) {
		final var vio = validator.validate(req);
		if (!vio.isEmpty()) throw new PageUrlReqInvalidException(vio);
		final var vio2 = validator.validate(req.getPaging());
		if (!vio2.isEmpty()) throw new PageReqInvalidException(vio2);
		final var page = service.findPage(req.getPaging(), req.getWhere());
		return PageUrlDto.builder()
				.data(page.getData())
				.page(page.getPage())
				.pageCount(page.getPageCount())
				.size(page.getSize())
				.build();
	}

	@GET
	@Path("/find-one")
	public JenkinsUrlDto findOne(JenkinsUrlDto req) {
		return service.findOne(req);
	}

	@POST
	public SavedDto save(JenkinsUrlDto req) {
		return SavedDto.builder().inserted(service.saving(req)).build();
	}

	@DELETE
	public DeletedDto delete(JenkinsUrlDto req) {
		return DeletedDto.builder().deleted(service.deleting(req)).build();
	}

}
