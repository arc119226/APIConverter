package com.arcare.api.converter.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Id;

public class Api {
	@Id
	private Integer id;
	
	private String uuid;
	
	private String apiName;
	
	private String apiType;
	
	private String apiAction;
	
	private String apiMeta;
	
	private String description; 
	private List<ApiProp> props =new ArrayList<>();
	
	public String getValueByPropertiesName(String key){
		Optional<ApiProp> optProp=props.stream()
				.filter(it->key.equals(it.getPropName()))
				.findFirst();
		if(optProp.isPresent()) {
			return optProp.get().getPropValue();
		}else {
			return null;
		}
	}
	
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
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getApiType() {
		return apiType;
	}
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	public String getApiAction() {
		return apiAction;
	}
	public void setApiAction(String apiAction) {
		this.apiAction = apiAction;
	}
	public String getApiMeta() {
		return apiMeta;
	}
	public void setApiMeta(String apiMeta) {
		this.apiMeta = apiMeta;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ApiProp> getProps() {
		return props;
	}
	public void setProps(List<ApiProp> props) {
		this.props = props;
	}

	@Override
	public String toString() {
		return "Api [id=" + id + ", uuid=" + uuid + ", apiName=" + apiName + ", apiType=" + apiType + ", apiAction="
				+ apiAction + ", apiMeta=" + apiMeta + ", description=" + description + ", props=" + props + "]";
	}
	
	
}
