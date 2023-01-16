package com.btpn.cakra.jenkinsjobspersist.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.btpn.cakra.jenkinsjobspersist.constant.JenkinsJobFields;
import com.btpn.cakra.jenkinsjobspersist.constant.JenkinsUrlFields;
import com.btpn.cakra.jenkinsjobspersist.dto.PageReq;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsJob;
import com.btpn.cakra.jenkinsjobspersist.models.JenkinsUrl;
import com.btpn.cakra.jenkinsjobspersist.models.JobStage;
import com.btpn.cakra.jenkinsjobspersist.models.JobStatus;
import com.btpn.cakra.jenkinsjobspersist.models.LastSuccessfulBuild;
import com.btpn.cakra.jenkinsjobspersist.models.Link;
import com.btpn.cakra.jenkinsjobspersist.models.Self;
import com.btpn.cakra.jenkinsjobspersist.models.Stage;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class DocumentUtil {

	private DocumentUtil() {
		super();
	}

	public static Document documentJenkinsUrl(JenkinsUrl model) {
		final var doc = new Document();
		if (Objects.nonNull(model.getId())) doc.put(JenkinsUrlFields.ID, model.getId());
		if (Objects.nonNull(model.getGroupId())) doc.put(JenkinsUrlFields.GROUP_ID, model.getGroupId());
		if (Objects.nonNull(model.getJenkins())) doc.put(JenkinsUrlFields.JENKINS, model.getJenkins());
		if (Objects.nonNull(model.getJenkinsToken())) doc.put(JenkinsUrlFields.JENKINS_TOKEN, model.getJenkinsToken());
		if (Objects.nonNull(model.getJenkinsUname())) doc.put(JenkinsUrlFields.JENKINS_UNAME, model.getJenkinsUname());
		if (Objects.nonNull(model.getJenkinsUrl())) doc.put(JenkinsUrlFields.JENKINS_URL, model.getJenkinsUrl());
		if (Objects.nonNull(model.getName())) doc.put(JenkinsUrlFields.NAME, model.getName());
		if (doc.isEmpty()) 
			throw new BadRequestException("Jenkins url properties must be not null at least once");
		return doc;
	}

	public static Document documentJenkinsJob(JenkinsJob model) {
		final var doc = new Document();
		if (Objects.nonNull(model.getId())) doc.put(JenkinsJobFields.ID, model.getId());
		if (Objects.nonNull(model.getAllBranchEvent())) doc.put(JenkinsJobFields.ALL_BRANCH_EVENT, model.getAllBranchEvent());
		if (Objects.nonNull(model.getCronTrigger())) doc.put(JenkinsJobFields.CRON_TRIGGER, model.getCronTrigger());
		if (Objects.nonNull(model.getGitUrl())) doc.put(JenkinsJobFields.GIT_URL, model.getGitUrl());
		if (Objects.nonNull(model.getGroovyScript())) doc.put(JenkinsJobFields.GROOVY_SCRIPT, model.getGroovyScript());
		if (Objects.nonNull(model.getGroupId())) doc.put(JenkinsJobFields.GROUP_ID, model.getGroupId());
		if (Objects.nonNull(model.getGroupName())) doc.put(JenkinsJobFields.GROUP_NAME, model.getGroupName());
		if (Objects.nonNull(model.getJobHashcode())) doc.put(JenkinsJobFields.JOB_HASHCODE, model.getJobHashcode());
		if (Objects.nonNull(model.getJobName())) doc.put(JenkinsJobFields.JOB_NAME, model.getJobName());
		if (Objects.nonNull(model.getJobStage())) doc.put(JenkinsJobFields.JOB_STAGE, docJobStage(model.getJobStage()));
		if (Objects.nonNull(model.getJobStatus())) doc.put(JenkinsJobFields.JOB_STATUS, docJobStatus(model.getJobStatus()));
		if (Objects.nonNull(model.getMraEvent())) doc.put(JenkinsJobFields.MRA_EVENT, model.getMraEvent());
		if (Objects.nonNull(model.getProjectId())) doc.put(JenkinsJobFields.PROJECT_ID, model.getProjectId());
		if (Objects.nonNull(model.getProjectName())) doc.put(JenkinsJobFields.PROJECT_NAME, model.getProjectName());
		if (Objects.nonNull(model.getPushEvent())) doc.put(JenkinsJobFields.PUSH_EVENT, model.getPushEvent());
		if (Objects.nonNull(model.getSelectedTrigger())) 
			doc.put(JenkinsJobFields.SELECTED_TRIGGER, model.getSelectedTrigger());
		if (Objects.nonNull(model.getSpecificBranchEvent())) 
			doc.put(JenkinsJobFields.SPECIFIC_BRANCH_EVENT, model.getSpecificBranchEvent());
		if (Objects.nonNull(model.getStageHashcode())) doc.put(JenkinsJobFields.STAGE_HASHCODE, model.getStageHashcode());
		if (Objects.nonNull(model.getStageLabel())) doc.put(JenkinsJobFields.STAGE_LABEL, model.getStageLabel());
		if (Objects.nonNull(model.getTagEvent())) doc.put(JenkinsJobFields.TAG_EVENT, model.getTagEvent());
		if (Objects.nonNull(model.getTargetBranch())) doc.put(JenkinsJobFields.TARGET_BRANCH, model.getTargetBranch());
		if (doc.isEmpty()) 
			throw new BadRequestException("Jenkins job properties must be not null at least once");
		return doc;
	}

	private static Document docJobStatus(JobStatus model) {
		final var doc = new Document();
		if (Objects.nonNull(model.getLastSuccessfulBuild())) {
			final var doc2 = new Document();
			final var last = model.getLastSuccessfulBuild();
			if (Objects.nonNull(last.getClassName())) doc2.put(JenkinsJobFields.CLASS_NAME, last.getClassName());
			if (Objects.nonNull(last.getDisplayName())) doc2.put(JenkinsJobFields.DISPLAY_NAME, last.getDisplayName());
			if (Objects.nonNull(last.getTimestamp())) doc2.put(JenkinsJobFields.TIMESTAMP, last.getTimestamp());
			doc.put(JenkinsJobFields.LAST_SUCCESSFUL_BUILD, doc2);
		}
		if (Objects.nonNull(model.getClassName())) doc.put(JenkinsJobFields.CLASS_NAME, model.getClassName());
		if (Objects.nonNull(model.getColor())) doc.put(JenkinsJobFields.COLOR, model.getColor());
		if (Objects.nonNull(model.getName())) doc.put(JenkinsJobFields.NAME, model.getName());
		return doc;
	}

	private static Document docJobStage(JobStage model) {
		final var doc = new Document();
		if (Objects.nonNull(model.getDescription())) doc.put(JenkinsJobFields.DESCRIPTION, model.getDescription());
		if (Objects.nonNull(model.getDurationMillis())) doc.put(JenkinsJobFields.DURATION_MILLIS, model.getDurationMillis());
		if (Objects.nonNull(model.getEndTimeMillis())) doc.put(JenkinsJobFields.END_TIME_MILLIS, model.getEndTimeMillis());
		if (Objects.nonNull(model.getEstimatedDuration())) 
			doc.put(JenkinsJobFields.ESTIMATED_DURATION, model.getEstimatedDuration());
		if (Objects.nonNull(model.getId())) doc.put(JenkinsJobFields.ID2, model.getId());
		if (Objects.nonNull(model.getLink())) doc.put(JenkinsJobFields.LINKS, docLink(model.getLink()));
		if (Objects.nonNull(model.getName())) doc.put(JenkinsJobFields.NAME, model.getName());
		if (Objects.nonNull(model.getPauseDurationMillis())) 
			doc.put(JenkinsJobFields.PAUSE_DURATION_MILLIS, model.getPauseDurationMillis());
		if (Objects.nonNull(model.getQueueDurationMillis())) 
			doc.put(JenkinsJobFields.QUEUE_DURATION_MILLIS, model.getQueueDurationMillis());
		if (Objects.nonNull(model.getStartTimeMillis())) doc.put(JenkinsJobFields.START_TIME_MILLIS, model.getStartTimeMillis());
		if (Objects.nonNull(model.getStatus())) doc.put(JenkinsJobFields.STATUS, model.getStatus());
		if (Objects.nonNull(model.getStages())) doc.put(JenkinsJobFields.STAGES, new BsonArray(model.getStages()
				.stream().map(DocumentUtil::docStage).collect(Collectors.toList())));
		return doc;
	}

	private static BsonDocument docStage(Stage s) {
		final var doc = new BsonDocument();
		if (Objects.nonNull(s.getDurationMillis())) 
			doc.put(JenkinsJobFields.DURATION_MILLIS, new BsonInt64(s.getDurationMillis()));
		if (Objects.nonNull(s.getExecNode())) 
			doc.put(JenkinsJobFields.EXEC_NODE, new BsonString(s.getExecNode()));
		if (Objects.nonNull(s.getId())) 
			doc.put(JenkinsJobFields.ID2, new BsonString(s.getId()));
		if (Objects.nonNull(s.getLink())) 
			doc.put(JenkinsJobFields.LINKS, docLink(s.getLink()).toBsonDocument());
		if (Objects.nonNull(s.getName())) 
			doc.put(JenkinsJobFields.NAME, new BsonString(s.getName()));
		if (Objects.nonNull(s.getPauseDurationMillis())) 
			doc.put(JenkinsJobFields.PAUSE_DURATION_MILLIS, new BsonInt64(s.getPauseDurationMillis()));
		if (Objects.nonNull(s.getStartTimeMillis())) 
			doc.put(JenkinsJobFields.START_TIME_MILLIS, new BsonInt64(s.getStartTimeMillis()));
		if (Objects.nonNull(s.getStatus())) 
			doc.put(JenkinsJobFields.STATUS, new BsonString(s.getStatus()));
		return doc;
	}

	private static Document docLink(Link link) {
		final var doc = new Document();
		if (Objects.nonNull(link.getSelf())) {
			final var doc2 = new Document();
			final var self = link.getSelf();
			if (Objects.nonNull(self.getHref())) doc2.put(JenkinsJobFields.HREF, self.getHref());
			doc.put(JenkinsJobFields.SELF, doc2);
		}
		return doc;
	}

	public static <T> List<T> cursorToList(MongoCursor<Document> cursor, Predicate<Document> filter, Function<Document, T> map) {
		return Objects.nonNull(cursor) ? Stream.iterate(cursor, MongoCursor::hasNext, c->c)
				.filter(Objects::nonNull)
				.map(MongoCursor::next)
				.filter(Objects::nonNull)
				.filter(filter)
				.map(map)
				.collect(Collectors.toList()) : List.of();
	}

	public static <T> Optional<T> cursorToOpt(MongoCursor<Document> cursor, Predicate<Document> filter, 
			Function<Document, T> map) {
		return Objects.nonNull(cursor) ? Stream.iterate(cursor, MongoCursor::hasNext, c->c)
				.filter(Objects::nonNull)
				.map(MongoCursor::next)
				.filter(Objects::nonNull)
				.filter(filter)
				.limit(1L)
				.map(map)
				.findFirst() : Optional.empty();
	}

	public static <T> List<T> cursorToPage(MongoCursor<Document> cursor, PageReq req, Predicate<Document> filter, 
			Function<Document, T> map) {
		return Objects.nonNull(cursor) ? Stream.iterate(cursor, MongoCursor::hasNext, c->c)
				.filter(Objects::nonNull)
				.map(MongoCursor::next)
				.filter(Objects::nonNull)
				.filter(filter)
				.skip(req.getSize() * (req.getPage() - 1))
				.limit(req.getSize())
				.map(map)
				.collect(Collectors.toList()) : List.of();
	}

	public static JenkinsJob jenkinsJobDocument(Document doc) {
		final var model = new JenkinsJob();
		if (doc.containsKey(JenkinsJobFields.ID)) model.setId(doc.getObjectId(JenkinsJobFields.ID));
		if (doc.containsKey(JenkinsJobFields.ALL_BRANCH_EVENT)) 
			model.setAllBranchEvent(doc.getBoolean(JenkinsJobFields.ALL_BRANCH_EVENT));
		if (doc.containsKey(JenkinsJobFields.CRON_TRIGGER)) model.setCronTrigger(doc.getString(JenkinsJobFields.CRON_TRIGGER));
		if (doc.containsKey(JenkinsJobFields.GIT_URL)) model.setGitUrl(doc.getString(JenkinsJobFields.GIT_URL));
		if (doc.containsKey(JenkinsJobFields.GROOVY_SCRIPT)) model.setGroovyScript(doc.getString(JenkinsJobFields.GROOVY_SCRIPT));
		if (doc.containsKey(JenkinsJobFields.GROUP_ID)) {
			final var bson = doc.toBsonDocument();
			final var key = JenkinsJobFields.GROUP_ID;
			model.setGroupId(bson.get(key).isInt32() ? Long.parseLong(doc.getInteger(key).toString()) :
						doc.getLong(key));
		}
		if (doc.containsKey(JenkinsJobFields.GROUP_NAME)) model.setGroupName(doc.getString(JenkinsJobFields.GROUP_NAME));
		if (doc.containsKey(JenkinsJobFields.JOB_HASHCODE)) {
			var bson = doc.toBsonDocument();
			model.setJobHashcode(bson.get(JenkinsJobFields.JOB_HASHCODE).isInt32() ?
					Long.parseLong(doc.getInteger(JenkinsJobFields.JOB_HASHCODE).toString()) :
						doc.getLong(JenkinsJobFields.JOB_HASHCODE));
		}
		if (doc.containsKey(JenkinsJobFields.JOB_NAME)) model.setJobName(doc.getString(JenkinsJobFields.JOB_NAME));
		if (doc.containsKey(JenkinsJobFields.JOB_STAGE)) 
			model.setJobStage(toJobStage(doc.toBsonDocument().getDocument(JenkinsJobFields.JOB_STAGE)));
		if (doc.containsKey(JenkinsJobFields.JOB_STATUS)) 
			model.setJobStatus(toJobStatus(doc.toBsonDocument().getDocument(JenkinsJobFields.JOB_STATUS)));
		if (doc.containsKey(JenkinsJobFields.MRA_EVENT)) model.setMraEvent(doc.getBoolean(JenkinsJobFields.MRA_EVENT));
		if (doc.containsKey(JenkinsJobFields.PROJECT_ID)) {
			final var bson = doc.toBsonDocument();
			final var key = JenkinsJobFields.PROJECT_ID;
			model.setProjectId(bson.get(key).isInt32() ? Long.parseLong(doc.getInteger(key).toString()) :
				doc.getLong(key));
		}
		if (doc.containsKey(JenkinsJobFields.PROJECT_NAME)) model.setProjectName(doc.getString(JenkinsJobFields.PROJECT_NAME));
		if (doc.containsKey(JenkinsJobFields.PUSH_EVENT)) model.setPushEvent(doc.getBoolean(JenkinsJobFields.PUSH_EVENT));
		if (doc.containsKey(JenkinsJobFields.SELECTED_TRIGGER)) 
			model.setSelectedTrigger(doc.getString(JenkinsJobFields.SELECTED_TRIGGER));
		if (doc.containsKey(JenkinsJobFields.SPECIFIC_BRANCH_EVENT)) 
			model.setSpecificBranchEvent(doc.getString(JenkinsJobFields.SPECIFIC_BRANCH_EVENT));
		if (doc.containsKey(JenkinsJobFields.STAGE_HASHCODE)) {
			final var bson = doc.toBsonDocument();
			model.setStageHashcode(bson.get(JenkinsJobFields.STAGE_HASHCODE).isInt32() ?
					Long.parseLong(doc.getInteger(JenkinsJobFields.STAGE_HASHCODE).toString()) :
						doc.getLong(JenkinsJobFields.STAGE_HASHCODE));
		}
		if (doc.containsKey(JenkinsJobFields.STAGE_LABEL)) model.setStageLabel(doc.getString(JenkinsJobFields.STAGE_LABEL));
		if (doc.containsKey(JenkinsJobFields.TAG_EVENT)) model.setTagEvent(doc.getBoolean(JenkinsJobFields.TAG_EVENT));
		if (doc.containsKey(JenkinsJobFields.TARGET_BRANCH)) model.setTargetBranch(doc.getString(JenkinsJobFields.TARGET_BRANCH));
		return model;
	}

	private static JobStatus toJobStatus(BsonDocument doc) {
		final var model = new JobStatus();
		if (doc.containsKey(JenkinsJobFields.LAST_SUCCESSFUL_BUILD) && !doc.get(JenkinsJobFields.LAST_SUCCESSFUL_BUILD).isNull()) {
			final var doc2 = doc.getDocument(JenkinsJobFields.LAST_SUCCESSFUL_BUILD);
			final var last = new LastSuccessfulBuild();
			if (doc2.containsKey(JenkinsJobFields.CLASS_NAME)) 
				last.setClassName(doc2.getString(JenkinsJobFields.CLASS_NAME).getValue());
			if (doc2.containsKey(JenkinsJobFields.DISPLAY_NAME)) 
				last.setDisplayName(doc2.getString(JenkinsJobFields.DISPLAY_NAME).getValue());
			if (doc2.containsKey(JenkinsJobFields.TIMESTAMP)) 
				last.setTimestamp(doc2.getInt64(JenkinsJobFields.TIMESTAMP).getValue());
			model.setLastSuccessfulBuild(last);
		}
		if (doc.containsKey(JenkinsJobFields.CLASS_NAME)) 
			model.setClassName(doc.getString(JenkinsJobFields.CLASS_NAME).getValue());
		if (doc.containsKey(JenkinsJobFields.COLOR)) model.setColor(doc.getString(JenkinsJobFields.COLOR).getValue());
		if (doc.containsKey(JenkinsJobFields.NAME)) model.setName(doc.getString(JenkinsJobFields.NAME).getValue());
		return model;
	}

	private static JobStage toJobStage(BsonDocument doc) {
		final var model = new JobStage();
		if (doc.containsKey(JenkinsJobFields.DESCRIPTION) && !doc.get(JenkinsJobFields.DESCRIPTION).isNull()) 
			model.setDescription(doc.getString(JenkinsJobFields.DESCRIPTION).getValue());
		if (doc.containsKey(JenkinsJobFields.DURATION_MILLIS)) {
			final var key = JenkinsJobFields.DURATION_MILLIS;
			model.setDurationMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
						Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.END_TIME_MILLIS)) {
			final var key = JenkinsJobFields.END_TIME_MILLIS;
			model.setEndTimeMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.ESTIMATED_DURATION) && !doc.get(JenkinsJobFields.ESTIMATED_DURATION).isNull()) {
			final var key = JenkinsJobFields.ESTIMATED_DURATION;
			model.setEstimatedDuration(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.ID2)) model.setId(doc.getString(JenkinsJobFields.ID2).getValue());
		if (doc.containsKey(JenkinsJobFields.LINKS)) model.setLink(toLink(doc.getDocument(JenkinsJobFields.LINKS)));
		if (doc.containsKey(JenkinsJobFields.NAME)) model.setName(doc.getString(JenkinsJobFields.NAME).getValue());
		if (doc.containsKey(JenkinsJobFields.PAUSE_DURATION_MILLIS)) {
			final var key = JenkinsJobFields.PAUSE_DURATION_MILLIS;
			model.setPauseDurationMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.QUEUE_DURATION_MILLIS)) {
			final var key = JenkinsJobFields.QUEUE_DURATION_MILLIS;
			model.setQueueDurationMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.START_TIME_MILLIS)) {
			final var key = JenkinsJobFields.START_TIME_MILLIS;
			model.setStartTimeMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.STATUS)) model.setStatus(doc.getString(JenkinsJobFields.STATUS).getValue());
		if (doc.containsKey(JenkinsJobFields.STAGES)) model.setStages(doc.getArray(JenkinsJobFields.STAGES).stream()
				.filter(Objects::nonNull).filter(BsonValue::isDocument).map(BsonValue::asDocument)
				.map(DocumentUtil::toStage).collect(Collectors.toList()));
		return model;
	}

	private static Stage toStage(BsonDocument doc) {
		final var s = new Stage();
		if (doc.containsKey(JenkinsJobFields.DURATION_MILLIS)) {
			final var key = JenkinsJobFields.DURATION_MILLIS;
			s.setDurationMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.EXEC_NODE)) 
			s.setExecNode(doc.getString(JenkinsJobFields.EXEC_NODE).getValue());
		if (doc.containsKey(JenkinsJobFields.ID2)) 
			s.setId(doc.getString(JenkinsJobFields.ID2).getValue());
		if (doc.containsKey(JenkinsJobFields.LINKS)) s.setLink(toLink(doc.getDocument(JenkinsJobFields.LINKS)));
		if (doc.containsKey(JenkinsJobFields.NAME)) s.setName(doc.getString(JenkinsJobFields.NAME).getValue());
		if (doc.containsKey(JenkinsJobFields.PAUSE_DURATION_MILLIS)) {
			final var key = JenkinsJobFields.PAUSE_DURATION_MILLIS;
			s.setPauseDurationMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.START_TIME_MILLIS)) {
			final var key = JenkinsJobFields.START_TIME_MILLIS;
			s.setStartTimeMillis(doc.get(key).isInt64() ? doc.getInt64(key).getValue() :
				Math.round(doc.getDouble(key).getValue()));
		}
		if (doc.containsKey(JenkinsJobFields.STATUS)) 
			s.setStatus(doc.getString(JenkinsJobFields.STATUS).getValue());
		return s;
	}

	private static Link toLink(BsonDocument doc) {
		final var link = new Link();
		if (doc.containsKey(JenkinsJobFields.SELF)) {
			final var doc2 = doc.getDocument(JenkinsJobFields.SELF);
			final var self = new Self();
			if (doc2.containsKey(JenkinsJobFields.HREF)) self.setHref(doc2.getString(JenkinsJobFields.HREF).getValue());
			link.setSelf(self);
		}
		return link;
	}

	public static boolean isDocumentJenkinsJob(Document doc) {
		return Stream.of(JenkinsJobFields.ALL_BRANCH_EVENT, JenkinsJobFields.CRON_TRIGGER,
				JenkinsJobFields.GIT_URL, JenkinsJobFields.GROOVY_SCRIPT, JenkinsJobFields.GROUP_ID,
				JenkinsJobFields.GROUP_NAME, JenkinsJobFields.JOB_HASHCODE, JenkinsJobFields.JOB_NAME,
				JenkinsJobFields.JOB_STAGE, JenkinsJobFields.JOB_STATUS, JenkinsJobFields.MRA_EVENT,
				JenkinsJobFields.PROJECT_ID, JenkinsJobFields.PROJECT_NAME, JenkinsJobFields.PUSH_EVENT,
				JenkinsJobFields.SELECTED_TRIGGER, JenkinsJobFields.SPECIFIC_BRANCH_EVENT, JenkinsJobFields.STAGE_HASHCODE,
				JenkinsJobFields.STAGE_LABEL, JenkinsJobFields.TAG_EVENT, JenkinsJobFields.TARGET_BRANCH)
				.parallel().anyMatch(doc::containsKey);
	}

	public static Bson updateJenkinsJob(JenkinsJob model) {
		final var change = new ArrayList<Bson>();
		if (Objects.nonNull(model.getId())) change.add(Updates.set(JenkinsJobFields.ID, model.getId()));
		if (Objects.nonNull(model.getAllBranchEvent())) 
			change.add(Updates.set(JenkinsJobFields.ALL_BRANCH_EVENT, model.getAllBranchEvent()));
		if (Objects.nonNull(model.getCronTrigger())) 
			change.add(Updates.set(JenkinsJobFields.CRON_TRIGGER, model.getCronTrigger()));
		if (Objects.nonNull(model.getGitUrl())) change.add(Updates.set(JenkinsJobFields.GIT_URL, model.getGitUrl()));
		if (Objects.nonNull(model.getGroovyScript())) 
			change.add(Updates.set(JenkinsJobFields.GROOVY_SCRIPT, model.getGroovyScript()));
		if (Objects.nonNull(model.getGroupId())) change.add(Updates.set(JenkinsJobFields.GROUP_ID, model.getGroupId()));
		if (Objects.nonNull(model.getGroupName())) change.add(Updates.set(JenkinsJobFields.GROUP_NAME, model.getGroupName()));
		if (Objects.nonNull(model.getJobHashcode())) 
			change.add(Updates.set(JenkinsJobFields.JOB_HASHCODE, model.getJobHashcode()));
		if (Objects.nonNull(model.getJobName())) change.add(Updates.set(JenkinsJobFields.JOB_NAME, model.getJobName()));
		if (Objects.nonNull(model.getJobStage())) 
			change.add(Updates.set(JenkinsJobFields.JOB_STAGE, docJobStage(model.getJobStage())));
		if (Objects.nonNull(model.getJobStatus())) 
			change.add(Updates.set(JenkinsJobFields.JOB_STATUS, docJobStatus(model.getJobStatus())));
		if (Objects.nonNull(model.getMraEvent())) change.add(Updates.set(JenkinsJobFields.MRA_EVENT, model.getMraEvent()));
		if (Objects.nonNull(model.getProjectId())) change.add(Updates.set(JenkinsJobFields.PROJECT_ID, model.getProjectId()));
		if (Objects.nonNull(model.getProjectName())) 
			change.add(Updates.set(JenkinsJobFields.PROJECT_NAME, model.getProjectName()));
		if (Objects.nonNull(model.getPushEvent())) change.add(Updates.set(JenkinsJobFields.PUSH_EVENT, model.getPushEvent()));
		if (Objects.nonNull(model.getSelectedTrigger())) 
			change.add(Updates.set(JenkinsJobFields.SELECTED_TRIGGER, model.getSelectedTrigger()));
		if (Objects.nonNull(model.getSpecificBranchEvent())) 
			change.add(Updates.set(JenkinsJobFields.SPECIFIC_BRANCH_EVENT, model.getSpecificBranchEvent()));
		if (Objects.nonNull(model.getStageHashcode())) 
			change.add(Updates.set(JenkinsJobFields.STAGE_HASHCODE, model.getStageHashcode()));
		if (Objects.nonNull(model.getStageLabel())) change.add(Updates.set(JenkinsJobFields.STAGE_LABEL, model.getStageLabel()));
		if (Objects.nonNull(model.getTagEvent())) change.add(Updates.set(JenkinsJobFields.TAG_EVENT, model.getTagEvent()));
		if (Objects.nonNull(model.getTargetBranch())) 
			change.add(Updates.set(JenkinsJobFields.TARGET_BRANCH, model.getTargetBranch()));
		if (change.isEmpty()) throw new BadRequestException("Jenkins job properties must be not null at least once");
		return Updates.combine(change);
	}

	public static Bson deleteJenkinsJob(JenkinsJob model) {
		final var change = new ArrayList<Bson>();
		if (Objects.nonNull(model.getId())) change.add(Filters.eq(JenkinsJobFields.ID, model.getId()));
		if (Objects.nonNull(model.getAllBranchEvent())) 
			change.add(Filters.eq(JenkinsJobFields.ALL_BRANCH_EVENT, model.getAllBranchEvent()));
		if (Objects.nonNull(model.getCronTrigger())) 
			change.add(Filters.eq(JenkinsJobFields.CRON_TRIGGER, model.getCronTrigger()));
		if (Objects.nonNull(model.getGitUrl())) change.add(Filters.eq(JenkinsJobFields.GIT_URL, model.getGitUrl()));
		if (Objects.nonNull(model.getGroovyScript())) 
			change.add(Filters.eq(JenkinsJobFields.GROOVY_SCRIPT, model.getGroovyScript()));
		if (Objects.nonNull(model.getGroupId())) change.add(Filters.eq(JenkinsJobFields.GROUP_ID, model.getGroupId()));
		if (Objects.nonNull(model.getGroupName())) change.add(Filters.eq(JenkinsJobFields.GROUP_NAME, model.getGroupName()));
		if (Objects.nonNull(model.getJobHashcode())) 
			change.add(Filters.eq(JenkinsJobFields.JOB_HASHCODE, model.getJobHashcode()));
		if (Objects.nonNull(model.getJobName())) change.add(Filters.eq(JenkinsJobFields.JOB_NAME, model.getJobName()));
		if (Objects.nonNull(model.getJobStage())) 
			change.add(Filters.eq(JenkinsJobFields.JOB_STAGE, docJobStage(model.getJobStage())));
		if (Objects.nonNull(model.getJobStatus())) 
			change.add(Filters.eq(JenkinsJobFields.JOB_STATUS, docJobStatus(model.getJobStatus())));
		if (Objects.nonNull(model.getMraEvent())) change.add(Filters.eq(JenkinsJobFields.MRA_EVENT, model.getMraEvent()));
		if (Objects.nonNull(model.getProjectId())) change.add(Filters.eq(JenkinsJobFields.PROJECT_ID, model.getProjectId()));
		if (Objects.nonNull(model.getProjectName())) 
			change.add(Filters.eq(JenkinsJobFields.PROJECT_NAME, model.getProjectName()));
		if (Objects.nonNull(model.getPushEvent())) change.add(Filters.eq(JenkinsJobFields.PUSH_EVENT, model.getPushEvent()));
		if (Objects.nonNull(model.getSelectedTrigger())) 
			change.add(Filters.eq(JenkinsJobFields.SELECTED_TRIGGER, model.getSelectedTrigger()));
		if (Objects.nonNull(model.getSpecificBranchEvent())) 
			change.add(Filters.eq(JenkinsJobFields.SPECIFIC_BRANCH_EVENT, model.getSpecificBranchEvent()));
		if (Objects.nonNull(model.getStageHashcode())) 
			change.add(Filters.eq(JenkinsJobFields.STAGE_HASHCODE, model.getStageHashcode()));
		if (Objects.nonNull(model.getStageLabel())) change.add(Filters.eq(JenkinsJobFields.STAGE_LABEL, model.getStageLabel()));
		if (Objects.nonNull(model.getTagEvent())) change.add(Filters.eq(JenkinsJobFields.TAG_EVENT, model.getTagEvent()));
		if (Objects.nonNull(model.getTargetBranch())) 
			change.add(Filters.eq(JenkinsJobFields.TARGET_BRANCH, model.getTargetBranch()));
		if (change.isEmpty()) throw new BadRequestException("Jenkins job properties must be not null at least once");
		return Filters.and(change);
	}

	public static boolean isDocumentJenkinsUrl(Document doc) {
		return Stream.of(JenkinsUrlFields.GROUP_ID, JenkinsUrlFields.ID, JenkinsUrlFields.JENKINS,
				JenkinsUrlFields.JENKINS_TOKEN, JenkinsUrlFields.JENKINS_UNAME, JenkinsUrlFields.JENKINS_URL,
				JenkinsUrlFields.NAME).parallel()
				.anyMatch(doc::containsKey);
	}

	public static JenkinsUrl jenkinsUrlDocument(Document doc) {
		final var model = new JenkinsUrl();
		if (doc.containsKey(JenkinsUrlFields.ID)) model.setId(doc.getObjectId(JenkinsUrlFields.ID));
		if (doc.containsKey(JenkinsUrlFields.GROUP_ID)) {
			var bson=doc.toBsonDocument();
			model.setGroupId(bson.get(JenkinsUrlFields.GROUP_ID).isInt64() ?
					doc.getLong(JenkinsUrlFields.GROUP_ID).intValue() :
						doc.getInteger(JenkinsUrlFields.GROUP_ID));
		}
		if (doc.containsKey(JenkinsUrlFields.JENKINS)) model.setJenkins(doc.getBoolean(JenkinsUrlFields.JENKINS));
		if (doc.containsKey(JenkinsUrlFields.JENKINS_TOKEN)) model.setJenkinsToken(doc.getString(JenkinsUrlFields.JENKINS_TOKEN));
		if (doc.containsKey(JenkinsUrlFields.JENKINS_UNAME)) model.setJenkinsUname(doc.getString(JenkinsUrlFields.JENKINS_UNAME));
		if (doc.containsKey(JenkinsUrlFields.JENKINS_URL)) model.setJenkinsUrl(doc.getString(JenkinsUrlFields.JENKINS_URL));
		if (doc.containsKey(JenkinsUrlFields.NAME)) model.setName(doc.getString(JenkinsUrlFields.NAME));
		return model;
	}

	public static Bson updateJenkinsUrl(JenkinsUrl model) {
		final var change = new ArrayList<Bson>();
		if (Objects.nonNull(model.getId())) change.add(Updates.set(JenkinsUrlFields.ID, model.getId()));
		if (Objects.nonNull(model.getGroupId())) change.add(Updates.set(JenkinsUrlFields.GROUP_ID, model.getGroupId()));
		if (Objects.nonNull(model.getJenkins())) change.add(Updates.set(JenkinsUrlFields.JENKINS, model.getJenkins()));
		if (Objects.nonNull(model.getJenkinsToken())) 
			change.add(Updates.set(JenkinsUrlFields.JENKINS_TOKEN, model.getJenkinsToken()));
		if (Objects.nonNull(model.getJenkinsUname())) 
			change.add(Updates.set(JenkinsUrlFields.JENKINS_UNAME, model.getJenkinsUname()));
		if (Objects.nonNull(model.getJenkinsUrl())) change.add(Updates.set(JenkinsUrlFields.JENKINS_URL, model.getJenkinsUrl()));
		if (Objects.nonNull(model.getName())) change.add(Updates.set(JenkinsUrlFields.NAME, model.getName()));
		if (change.isEmpty()) throw new BadRequestException("Jenkins url properties must be not null at least once");
		return Updates.combine(change);
	}

	public static Bson deleteJenkinsUrl(JenkinsUrl model) {
		final var change = new ArrayList<Bson>();
		if (Objects.nonNull(model.getId())) change.add(Filters.eq(JenkinsUrlFields.ID, model.getId()));
		if (Objects.nonNull(model.getGroupId())) change.add(Filters.eq(JenkinsUrlFields.GROUP_ID, model.getGroupId()));
		if (Objects.nonNull(model.getJenkins())) change.add(Filters.eq(JenkinsUrlFields.JENKINS, model.getJenkins()));
		if (Objects.nonNull(model.getJenkinsToken())) 
			change.add(Filters.eq(JenkinsUrlFields.JENKINS_TOKEN, model.getJenkinsToken()));
		if (Objects.nonNull(model.getJenkinsUname())) 
			change.add(Filters.eq(JenkinsUrlFields.JENKINS_UNAME, model.getJenkinsUname()));
		if (Objects.nonNull(model.getJenkinsUrl())) change.add(Filters.eq(JenkinsUrlFields.JENKINS_URL, model.getJenkinsUrl()));
		if (Objects.nonNull(model.getName())) change.add(Filters.eq(JenkinsUrlFields.NAME, model.getName()));
		if (change.isEmpty()) throw new BadRequestException("Jenkins url properties must be not null at least once");
		return Filters.and(change);
	}

}
