package com.arcare.api.converter.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;

public class App {
	@Id
	private Integer id;
	
	private String uuid;
	
	private String appName;
	
	private String description;
	
	Set<Task> tasks=new HashSet<>();
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<Task> getTasks() {
		return tasks;
	}
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	@Override
	public String toString() {
		return "App [id=" + id + ", uuid=" + uuid + ", appName=" + appName + ", description=" + description + ", tasks="
				+ tasks + "]";
	}
	
}
