package com.btpn.cakra.jenkinsjobspersist.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.btpn.cakra.jenkinsjobspersist.dto.ErrorRes;

@Provider
public class ErrorHandling implements ExceptionMapper<Exception> {

	@Inject TimestampCapturer capturer;

	@Inject Logger logger;

	@Context UriInfo uriInfo;

	@Override
	public Response toResponse(Exception e) {
		final var mapFunc = mapFunc();
		final var opt = mapFunc.keySet().parallelStream().filter(f->f.apply(e)).map(mapFunc::get).findFirst();
		final var res = opt.orElse(this::internal).apply(e);
		return Response.status(res.getStatus()).entity(res).build();
	}

	private Map<Function<Exception, Boolean>, Function<Exception, ErrorRes>> mapFunc() {
		final var map = new HashMap<Function<Exception, Boolean>, Function<Exception, ErrorRes>>();
		map.put(NotFoundException.class::isInstance, this::notFound);
		map.put(BadRequestException.class::isInstance, this::badReq);
		map.put(NotAcceptableException.class::isInstance, this::notAccess);
		return map;
	}

	private ErrorRes notAccess(Exception exception) {
		final var e = (NotAcceptableException) exception;
		logger.fatal(e.getMessage(), e);
		return ErrorRes.builder()
				.timestamp(capturer.now())
				.exception(e.getClass().getName())
				.message(e.getLocalizedMessage())
				.path(uriInfo.getPath())
				.status(406)
				.build();
	}

	private ErrorRes badReq(Exception exception) {
		final var e = (BadRequestException) exception;
		logger.fatal(e.getMessage(), e);
		return ErrorRes.builder()
				.timestamp(capturer.now())
				.exception(e.getClass().getName())
				.message(e.getLocalizedMessage())
				.path(uriInfo.getPath())
				.status(400)
				.build();
	}

	private ErrorRes notFound(Exception exception) {
		final var e = (NotFoundException) exception;
		logger.fatal(e.getMessage(), e);
		return ErrorRes.builder()
				.timestamp(capturer.now())
				.exception(e.getClass().getName())
				.message(e.getLocalizedMessage())
				.path(uriInfo.getPath())
				.status(404)
				.build();
	}

	private ErrorRes internal(Exception e) {
		logger.fatal(e.getMessage(), e);
		return ErrorRes.builder()
				.timestamp(capturer.now())
				.status(500)
				.exception(e.getClass().getName())
				.message(e.getMessage())
				.path(uriInfo.getPath())
				.build();
	}

}
