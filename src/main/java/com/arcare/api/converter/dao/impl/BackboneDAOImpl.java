package com.arcare.api.converter.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.crazycake.jdbcTemplateTool.JdbcTemplateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.arcare.api.converter.dao.BackboneDAO;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.domain.ApiProp;
import com.arcare.api.converter.domain.App;
import com.arcare.api.converter.domain.Task;
import com.arcare.api.converter.domain.TaskItem;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Repository("BackboneDAOImpl")
public class BackboneDAOImpl implements BackboneDAO {

	private final Logger logger = LoggerFactory.getLogger(BackboneDAOImpl.class);

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JdbcTemplateTool jdbcTemplateTool;

	@Override
	public Optional<Task> queryTaskByUUID(String uuid) {
		//USE NO SQLL
		//TODO
		//USE SQL
		List<Task> taskList=this.jdbcTemplateTool.list("select * from task where uuid = ?", new Object[]{uuid}, Task.class);
		if(taskList.isEmpty()) {
			return Optional.empty();
		}else {
			Integer taskId=taskList.stream().findFirst().get().getId();
			List<TaskItem> details=this.jdbcTemplateTool.list("select * from task_item where task_id = ?", new Object[] {taskId}, TaskItem.class);
			details.forEach(item->{
				Integer apiId=item.getApiId();
				Optional<Api> optApi=this.queryApiById(apiId);
				optApi.ifPresent(api->{
					//bind api
					item.setApi(api);
				});
			});
			taskList.stream().findFirst().get().setTaskItems(details);
			Integer authApiId = taskList.stream().findFirst().get().getProxyAuthenticationApiId();
			Optional<Api> optAuthApi=this.queryApiById(authApiId);
			if(optAuthApi.isPresent()) {
				//bind auth api data
				taskList.stream().findFirst().get().setProxyAuthenticationApi(optAuthApi.get());
			}
			return taskList.stream().findFirst();
		}
	}

	private Optional<Api> queryApiById(Integer id) {
		//USE NO SQLL
		//TODO
		//USE SQL
		List<Api> apiList=this.jdbcTemplateTool.list("select * from api where id = ?", new Object[] {id}, Api.class);
		if(apiList.isEmpty()) {
			return Optional.empty();
		}else {
			List<ApiProp> properties=this.jdbcTemplateTool.list("select * from api_prop where api_id = ?", new Object[] {id}, ApiProp.class);
			apiList.stream().findFirst().get().setProps(properties);
			return apiList.stream().findFirst();
		}
	}

	@Override
	public Optional<Api> queryApiByUUID(String uuid) {
		List<Api> apiList=this.jdbcTemplateTool.list("select * from api where uuid = ?", new Object[] {uuid}, Api.class);
		if(apiList.isEmpty()) {
			return Optional.empty();
		}else {
			List<ApiProp> properties=this.jdbcTemplateTool.list("select * from api_prop where api_id = ?", new Object[] {apiList.stream().findFirst().get().getId()}, ApiProp.class);
			apiList.stream().findFirst().get().setProps(properties);
			return apiList.stream().findFirst();
		}
	}

	@Override
	public Optional<App> queryAppByUUID(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

}
