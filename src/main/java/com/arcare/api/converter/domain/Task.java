package com.arcare.api.converter.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

public class Task {
	@Id
	private Integer id;
	
	private String uuid;
	
	private String taskName;
	
	private Integer appId;
	
	private String description;
	
	private String proxyAuthenticationResultMeta;//script need return true/false
	
	private Integer proxyAuthenticationApiId;
	
	private Api proxyAuthenticationApi;
	
	private Boolean isNeedProxyAuthentication;
	
	private List<TaskItem> taskItems = new ArrayList<>();
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProxyAuthenticationResultMeta() {
		return proxyAuthenticationResultMeta;
	}
	public void setProxyAuthenticationResultMeta(String proxyAuthenticationResultMeta) {
		this.proxyAuthenticationResultMeta = proxyAuthenticationResultMeta;
	}
	public Integer getProxyAuthenticationApiId() {
		return proxyAuthenticationApiId;
	}
	public void setProxyAuthenticationApiId(Integer proxyAuthenticationApiId) {
		this.proxyAuthenticationApiId = proxyAuthenticationApiId;
	}
	public Boolean getIsNeedProxyAuthentication() {
		return isNeedProxyAuthentication;
	}
	public void setIsNeedProxyAuthentication(Boolean isNeedProxyAuthentication) {
		this.isNeedProxyAuthentication = isNeedProxyAuthentication;
	}
	public List<TaskItem> getTaskItems() {
		return taskItems;
	}
	public void setTaskItems(List<TaskItem> taskItems) {
		this.taskItems = taskItems;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		return "Task [id=" + id + ", uuid=" + uuid + ", taskName=" + taskName + ", appId=" + appId + ", description="
				+ description + ", proxyAuthenticationResultMeta=" + proxyAuthenticationResultMeta
				+ ", proxyAuthenticationApiId=" + proxyAuthenticationApiId + ", isNeedProxyAuthentication="
				+ isNeedProxyAuthentication + ", taskItems=" + taskItems + "]";
	}
	public Api getProxyAuthenticationApi() {
		return proxyAuthenticationApi;
	}
	public void setProxyAuthenticationApi(Api proxyAuthenticationApi) {
		this.proxyAuthenticationApi = proxyAuthenticationApi;
	}
	
}
