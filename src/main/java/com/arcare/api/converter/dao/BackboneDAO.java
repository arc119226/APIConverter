package com.arcare.api.converter.dao;

import java.util.Optional;

import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.domain.App;
import com.arcare.api.converter.domain.Task;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public interface BackboneDAO {

	public Optional<App> queryAppByUUID(String uuid);
	
	public Optional<Task> queryTaskByUUID(String uuid);

	public Optional<Api> queryApiByUUID(String uuid);

}
