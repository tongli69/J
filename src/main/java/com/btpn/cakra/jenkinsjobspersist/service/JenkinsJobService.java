package com.btpn.cakra.jenkinsjobspersist.service;

import java.util.Objects;

import javax.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.btpn.cakra.jenkinsjobspersist.dto.JenkinsJobDto;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.utils.DocumentUtil;
import com.btpn.cakra.jenkinsjobspersist.utils.DtoConverterUtil;

@Singleton
public class JenkinsJobService extends CrudService<JenkinsJob, JenkinsJobDto> {

	@Override
	protected String collName() {
		return "jenkins_job";
	}

	@Override
	protected String modelName() {
		return "Jenkins job";
	}

	@Override
	protected JenkinsJobDto toDto(JenkinsJob v) {
		return DtoConverterUtil.toDtoJenkinsJob(v);
	}

	@Override
	protected JenkinsJob toModel(JenkinsJobDto v) {
		return DtoConverterUtil.toModelJenkinsJob(v);
	}

	@Override
	protected JenkinsJob mappingDocument(Document doc) {
		return DocumentUtil.jenkinsJobDocument(doc);
	}

	@Override
	protected Document documentMapping(JenkinsJob v) {
		return DocumentUtil.documentJenkinsJob(v);
	}

	@Override
	protected boolean validDocument(Document doc) {
		return DocumentUtil.isDocumentJenkinsJob(doc);
	}

	@Override
	protected boolean hasId(JenkinsJob v) {
		return Objects.nonNull(v.getId());
	}

	@Override
	protected Document idDocumentMapping(JenkinsJob v) {
		return DocumentUtil.documentJenkinsJob(JenkinsJob.builder().id(v.getId()).build());
	}

	@Override
	protected Bson updateMapping(JenkinsJob v) {
		return DocumentUtil.updateJenkinsJob(v);
	}

	@Override
	protected Bson deleteMapping(JenkinsJob v) {
		return DocumentUtil.deleteJenkinsJob(v);
	}

}
