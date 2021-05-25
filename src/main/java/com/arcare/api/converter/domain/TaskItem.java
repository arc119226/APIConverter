package com.arcare.api.converter.domain;

import javax.persistence.Id;

public class TaskItem {
	@Id
	private Integer id;
	
	private String uuid;
	
	private String taskItemName;
	
	private String description;
	
	private String taskItemInputMeta;//input formate convert
	
	private String taskItemOutputMeta;//validate script need return true/false
	
	private Boolean taskItemIsMainItem;
	
	private Integer taskItemIfPresentItemId;
	
	private Integer taskItemOrElseItemId;
	
	private Integer apiId;
	
	private Api api;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTaskItemName() {
		return taskItemName;
	}
	public void setTaskItemName(String taskItemName) {
		this.taskItemName = taskItemName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTaskItemInputMeta() {
		return taskItemInputMeta;
	}
	public void setTaskItemInputMeta(String taskItemInputMeta) {
		this.taskItemInputMeta = taskItemInputMeta;
	}
	public String getTaskItemOutputMeta() {
		return taskItemOutputMeta;
	}
	public void setTaskItemOutputMeta(String taskItemOutputMeta) {
		this.taskItemOutputMeta = taskItemOutputMeta;
	}
	public Boolean getTaskItemIsMainItem() {
		return taskItemIsMainItem;
	}
	public void setTaskItemIsMainItem(Boolean taskItemIsMainItem) {
		this.taskItemIsMainItem = taskItemIsMainItem;
	}
	public Integer getTaskItemIfPresentItemId() {
		return taskItemIfPresentItemId;
	}
	public void setTaskItemIfPresentItemId(Integer taskItemIfPresentItemId) {
		this.taskItemIfPresentItemId = taskItemIfPresentItemId;
	}
	public Integer getTaskItemOrElseItemId() {
		return taskItemOrElseItemId;
	}
	public void setTaskItemOrElseItemId(Integer taskItemOrElseItemId) {
		this.taskItemOrElseItemId = taskItemOrElseItemId;
	}
	public Integer getApiId() {
		return apiId;
	}
	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}
	@Override
	public String toString() {
		return "TaskItem [id=" + id + ", uuid=" + uuid + ", taskItemName=" + taskItemName + ", description="
				+ description + ", taskItemInputMeta=" + taskItemInputMeta + ", taskItemOutputMeta="
				+ taskItemOutputMeta + ", taskItemIsMainItem=" + taskItemIsMainItem + ", taskItemIfPresentItemId="
				+ taskItemIfPresentItemId + ", taskItemOrElseItemId=" + taskItemOrElseItemId + ", apiId=" + apiId + "]";
	}
	public Api getApi() {
		return api;
	}
	public void setApi(Api api) {
		this.api = api;
	}
}
