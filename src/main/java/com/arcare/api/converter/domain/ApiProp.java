package com.arcare.api.converter.domain;

import javax.persistence.Id;

public class ApiProp {
	@Id
	private Integer id;
	
	private Integer apiId;
	
	private String propName;
	
	private String propValue;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getApiId() {
		return apiId;
	}
	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getPropValue() {
		return propValue;
	}
	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	@Override
	public String toString() {
		return "ApiProp [id=" + id + ", apiId=" + apiId + ", propName=" + propName + ", propValue=" + propValue + "]";
	}
	
	
}
