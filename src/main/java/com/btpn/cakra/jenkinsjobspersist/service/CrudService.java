package com.btpn.cakra.jenkinsjobspersist.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.btpn.cakra.jenkinsjobspersist.dto.PageDto;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.utils.DocumentUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

public abstract class CrudService<T extends Serializable, D extends Serializable> {

	@Inject MongoClient mongoClient;

	@ConfigProperty(name = "quarkus.mongodb.database") String dbName;

	public List<D> all() {
		try (final var cursor = getCollection().find().cursor()) {
			final var all = DocumentUtil.cursorToList(cursor, this::validDocument, this::mappingDocument);
			if (all.isEmpty()) throw new NotFoundException(String.format("All %s not found", modelName()));
			return all.stream().map(this::toDto).collect(Collectors.toList());
		}
	}

	public List<D> find(D req) {
		try (final var cursor = getCollection().find(documentMapping(toModel(req))).cursor()) {
			final var all = DocumentUtil.cursorToList(cursor, this::validDocument, this::mappingDocument);
			if (all.isEmpty()) throw new NotFoundException(String.format("%s finds not found", modelName()));
			return all.stream().map(this::toDto).collect(Collectors.toList());
		}
	}

	public D findOne(D req) {
		try (final var cursor = getCollection().find(documentMapping(toModel(req))).cursor()) {
			final var opt = DocumentUtil.cursorToOpt(cursor, this::validDocument, this::mappingDocument);
			return opt.map(this::toDto).orElseThrow(()->new NotFoundException(String.format("%s not found", modelName())));
		}
	}

	public PageDto<D> page(PageReq req) {
		final var res = new PageDto<D>(req.getPage(), req.getSize(), null, null);
		final var getting = CompletableFuture.runAsync(()->{
			try (final var cursor = getCollection().find().cursor()) {
				final var page = DocumentUtil.cursorToPage(cursor, req, this::validDocument, this::mappingDocument);
				res.setData(page.stream().map(this::toDto).collect(Collectors.toList()));
			}
		});
		final var counting = CompletableFuture.runAsync(()->{
			final var count = getCollection().countDocuments();
			res.setPageCount((count / req.getSize()) + (count % req.getSize() > 0 ? 1 : 0));
		});
		CompletableFuture.allOf(getting, counting).join();
		if (res.getData().isEmpty()) throw new NotFoundException(String.format("%s pages not found", modelName()));
		return res;
	}

	public PageDto<D> findPage(PageReq req, D dto) {
		final var where = documentMapping(toModel(dto));
		final var res = new PageDto<D>(req.getPage(), req.getSize(), null, null);
		final var getting = CompletableFuture.runAsync(()->{
			try (final var cursor = getCollection().find(where).cursor()) {
				final var page = DocumentUtil.cursorToPage(cursor, req, this::validDocument, this::mappingDocument);
				res.setData(page.stream().map(this::toDto).collect(Collectors.toList()));
			}
		});
		final var counting = CompletableFuture.runAsync(()->{
			final var count = getCollection().countDocuments(where);
			res.setPageCount((count / req.getSize()) + (count % req.getSize() > 0 ? 1 : 0));
		});
		CompletableFuture.allOf(getting, counting).join();
		if (res.getData().isEmpty()) throw new NotFoundException(String.format("%s find pages not found", modelName()));
		return res;
	}

	@Transactional
	public boolean saving(D dto) {
		final var model = toModel(dto);
		var inserted = true;
		if (hasId(model)) try (final var cursor = getCollection().find(idDocumentMapping(model)).cursor()) {
			final var opt = DocumentUtil.cursorToOpt(cursor, this::validDocument, this::mappingDocument);
			if (opt.isPresent()) {
				getCollection().updateOne(idDocumentMapping(model), updateMapping(model));
				inserted = false;
			} else getCollection().insertOne(documentMapping(model));
		} else getCollection().insertOne(documentMapping(model));
		try (final var cursor = getCollection().find(documentMapping(model)).cursor()) {
			final var opt = DocumentUtil.cursorToOpt(cursor, this::validDocument, this::mappingDocument);
			if (opt.isEmpty()) throw new NotAcceptableException(String.format("%s cannot saved", modelName()));
		}
		return inserted;
	}

	@Transactional
	public boolean deleting(D req) {
		final var model = toModel(req);
		if (!hasId(model)) throw new BadRequestException("ID must be not null");
		try (final var cursor = getCollection().find(documentMapping(model)).cursor()) {
			final var opt = DocumentUtil.cursorToOpt(cursor, this::validDocument, this::mappingDocument);
			getCollection().deleteOne(deleteMapping(opt
					.orElseThrow(()->new NotFoundException(String.format("%s not found", modelName())))));
		}
		try (final var cursor = getCollection().find(idDocumentMapping(model)).cursor()) {
			final var opt = DocumentUtil.cursorToOpt(cursor, this::validDocument, this::mappingDocument);
			if (opt.isPresent()) throw new NotAcceptableException(String.format("%s cannot deleted", modelName()));
		}
		return true;
	}

	private MongoCollection<Document> getCollection() {
		return mongoClient.getDatabase(dbName).getCollection(collName());
	}

	protected abstract String collName();

	protected abstract String modelName();

	protected abstract D toDto(T v);

	protected abstract T toModel(D v);

	protected abstract T mappingDocument(Document doc);

	protected abstract Document documentMapping(T v);

	protected abstract boolean validDocument(Document doc);

	protected abstract boolean hasId(T v);

	protected abstract Document idDocumentMapping(T v);

	protected abstract Bson updateMapping(T v);

	protected abstract Bson deleteMapping(T v);

}
