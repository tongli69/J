package com.btpn.cakra.jenkinsjobspersist.service;

import java.util.Objects;

import javax.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsUrlDto;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;
import com.btpn.cakra.jenkinsjobspersist.utils.DocumentUtil;
import com.btpn.cakra.jenkinsjobspersist.utils.DtoConverterUtil;

@Singleton
public class JenkinsUrlService extends CrudService<JenkinsUrl, JenkinsUrlDto> {

	@Override
	protected String collName() {
		return "jenkins_url";
	}

	@Override
	protected String modelName() {
		return "Jenkins url";
	}

	@Override
	protected JenkinsUrlDto toDto(JenkinsUrl v) {
		return DtoConverterUtil.toDtoJenkinsUrl(v);
	}

	@Override
	protected JenkinsUrl toModel(JenkinsUrlDto v) {
		return DtoConverterUtil.toModelJenkinsUrl(v);
	}

	@Override
	protected JenkinsUrl mappingDocument(Document doc) {
		return DocumentUtil.jenkinsUrlDocument(doc);
	}

	@Override
	protected Document documentMapping(JenkinsUrl v) {
		return DocumentUtil.documentJenkinsUrl(v);
	}

	@Override
	protected boolean validDocument(Document doc) {
		return DocumentUtil.isDocumentJenkinsUrl(doc);
	}

	@Override
	protected boolean hasId(JenkinsUrl v) {
		return Objects.nonNull(v.getId());
	}

	@Override
	protected Document idDocumentMapping(JenkinsUrl v) {
		return DocumentUtil.documentJenkinsUrl(JenkinsUrl.builder().id(v.getId()).build());
	}

	@Override
	protected Bson updateMapping(JenkinsUrl v) {
		return DocumentUtil.updateJenkinsUrl(v);
	}

	@Override
	protected Bson deleteMapping(JenkinsUrl v) {
		return DocumentUtil.deleteJenkinsUrl(v);
	}

}
