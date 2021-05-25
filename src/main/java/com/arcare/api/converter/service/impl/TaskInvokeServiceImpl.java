package com.arcare.api.converter.service.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.dao.BackboneDAO;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.domain.Task;
import com.arcare.api.converter.domain.TaskItem;
import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.service.InvokeService;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.arcare.api.converter.vo.JSONRPCSuccessResponse;
import com.arcare.api.converter.vo.ProcessResultStatusVO;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Service("TaskInvokeServiceImpl")
public class TaskInvokeServiceImpl implements InvokeService {
	private final Logger logger = LoggerFactory.getLogger(TaskInvokeServiceImpl.class);

	@Autowired
	@Qualifier("BackboneDAOImpl")
	private BackboneDAO backboneDAO;

	@Autowired
	@Qualifier("APIInvokeServiceImpl")
	private InvokeService apiInvokeService;

	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;

	@SuppressWarnings("unchecked")
	@Override
	public JSONRPCResponse invoke(JSONRPCRequest request) {
		logger.info("task invoke start");
		
		if(false== (request.getParams() instanceof Map)) {
			logger.error("this app only support map params");
			return new JSONRPCErrorResponse(request);//only support map
		}
		if(null == request.readTaskId()) {
			logger.error("task is is null");
			return new JSONRPCErrorResponse(request,-32004,JSONRPCError.ERR_32004_TASK_ID_IS_NULL);
		}
		

		logger.info(String.format("process task "+Constant.STR_TASK_ID+"= %s", request.readTaskId()));
		
		Optional<Task> taskConfigOpt=this.backboneDAO.queryTaskByUUID(request.readTaskId());
		if(!taskConfigOpt.isPresent()) {
			logger.error("task config not found");
			return new JSONRPCErrorResponse(request,-32009,JSONRPCError.ERR_32009_TASK_CONFIG_NOT_EXISTS);//not support
		}

		Task taskConfigObj=taskConfigOpt.get();
		//AUTH START
		if(taskConfigObj.getIsNeedProxyAuthentication() &&
			StringUtils.isNotBlank(taskConfigObj.getProxyAuthenticationResultMeta()) &&
			taskConfigObj.getProxyAuthenticationApiId() != null) {
			logger.info("task need call proxy auth token");
			JSONRPCResponse authResp=this.processAuth(request, taskConfigObj);
			if(false == authResp instanceof JSONRPCSuccessResponse) {
				return authResp;
			}
		}
		//AUTH END
		
		List<TaskItem> taskConfigDetailList=taskConfigObj.getTaskItems();
		if(taskConfigDetailList.isEmpty()) {
			logger.error("task config detail not exists");
			return new JSONRPCErrorResponse(request,-32009,JSONRPCError.ERR_32009_TASK_CONFIG_NOT_EXISTS);//not support
		}
		
		logger.info("task config detail "+taskConfigDetailList);

		Optional<TaskItem> mainTaskOption = taskConfigDetailList.stream()
			.filter(it->true == it.getTaskItemIsMainItem())
			.findFirst();

		if(!mainTaskOption.isPresent()) {
			logger.error("main task not exists");
			return new JSONRPCErrorResponse(request,-32705,JSONRPCError.ERR_32705_NO_MAIN_TASK);//not support
		}
		
		TaskItem mainTaskDetail=mainTaskOption.get();
		if(mainTaskDetail.getApiId()==null) {
			logger.error("api_id not exists");
			return new JSONRPCErrorResponse(request,-32005,JSONRPCError.ERR_32005_API_CONFIG_NOT_EXISTS);//not support
		}

		Optional<Api> apiConfigOption = mainTaskDetail.getApi()==null?Optional.empty():Optional.of(mainTaskDetail.getApi());

		if(!apiConfigOption.isPresent()){
			logger.error("main task api not exists -> id:");
			return new JSONRPCErrorResponse(request,-32005,JSONRPCError.ERR_32005_API_CONFIG_NOT_EXISTS);//not support
		}

		Map<String,Object> strDataMap=request.readStrDataInParamByMap();

		if(StringUtils.isNotBlank(mainTaskDetail.getTaskItemInputMeta())){
			//main need execute input method

			String strDataMapJson = new Gson().toJson(request.readStrDataInParamByMap());
			ProcessResultStatusVO<Object> resultStatusObj=this.dynamicParserService.executeMethod("execute", mainTaskDetail.getTaskItemInputMeta(), strDataMapJson);
			if(resultStatusObj.getResult().isPresent()) {
				strDataMap = (Map<String, Object>) resultStatusObj.getResult().get();
			}else {
				//fail
				return new JSONRPCErrorResponse(request,-32010,JSONRPCError.ERR_32010_EXECUTE_FAIL);//not support
			}
		}else {
			//main don't need execute input method
		}
		
		JSONRPCRequest apiRequest=new JSONRPCRequest();
		Map<String, Object> apiParamMap=new HashMap<>();
		apiRequest.setParams(apiParamMap);
		apiParamMap.put(Constant.STR_API_ID, apiConfigOption.get().getUuid());
		apiParamMap.put(Constant.STR_DATA, strDataMap);
//		System.out.println("main "+strDataMap);
		JSONRPCResponse lastResponse = this.apiInvokeService.invoke(apiRequest);

		return this.executeNext(request, taskConfigDetailList, mainTaskDetail, lastResponse);
	}
	
	/**
	 * 呼叫認證API
	 * @param request
	 * @param taskConfigObj
	 * @return
	 */
	private JSONRPCResponse processAuth(JSONRPCRequest request,Task taskConfigObj) {
		if(null == request.readAccessToken()) {
			logger.error("task strAccessToken is null");
			return new JSONRPCErrorResponse(request,-32011,JSONRPCError.ERR_32011_ACCESS_TOKEN_NOT_VALID);//not support
		}
		
		logger.info("task validate "+Constant.STR_ACCESS_TOKEN+"= %s",request.readAccessToken());
		
		Optional<Api> authApiConfig=taskConfigObj.getProxyAuthenticationApi()==null?Optional.empty():Optional.of(taskConfigObj.getProxyAuthenticationApi());
		
		if(!authApiConfig.isPresent()) {
			logger.error("task validate api not exists -> id:"+taskConfigObj.getProxyAuthenticationApiId());
			return new JSONRPCErrorResponse(request,-32005,JSONRPCError.ERR_32010_AUTH_API_CONFIG_NOT_EXISTS);//not support
		}
		
		Api authApi=authApiConfig.get();
		JSONRPCRequest authRequest=new JSONRPCRequest();
		Map<String, Object> apiParamMap=new HashMap<>();
		authRequest.setParams(apiParamMap);
		apiParamMap.put(Constant.STR_API_ID, authApi.getUuid());
		
		Map<String,String> dataMap=new HashMap<>();
		dataMap.put(Constant.STR_ACCESS_TOKEN, request.readAccessToken());

		apiParamMap.put(Constant.STR_DATA, dataMap);
		
		JSONRPCResponse authResp=this.apiInvokeService.invoke(authRequest);
		
		if(authResp instanceof JSONRPCSuccessResponse) {
			//need execute output option method for option if or else
			String jsonStr= ((JSONRPCSuccessResponse) authResp).getResult().toString();
			ProcessResultStatusVO<Object> resultStatusObj=this.dynamicParserService.executeMethod("execute", taskConfigObj.getProxyAuthenticationResultMeta(),jsonStr);
			try {
				if(resultStatusObj.getResult().isPresent() == true
					&& (Boolean) resultStatusObj.getResult().get() == true) {
					logger.info("auth pass");
				}else {
					logger.info("auth fail");
					return new JSONRPCErrorResponse(request,-32011,JSONRPCError.ERR_32011_ACCESS_TOKEN_NOT_VALID);
				}
			}catch(Exception e) {
				logger.error("",e);
				return new JSONRPCErrorResponse(request,-32011,JSONRPCError.ERR_32011_ACCESS_TOKEN_NOT_VALID);
			}
		}
		return authResp;
	}

	private JSONRPCResponse executeNext(JSONRPCRequest taskRequest,List<TaskItem> taskDetailList,TaskItem beforeTaskDetail,JSONRPCResponse lastResponse){
		logger.info("executeNext start");
		if(lastResponse instanceof JSONRPCSuccessResponse) {
			if(StringUtils.isNotBlank(beforeTaskDetail.getTaskItemOutputMeta())){
				//need execute output option method for option if or else
				ProcessResultStatusVO<Object> resultStatusObj=this.dynamicParserService.executeMethod("execute", beforeTaskDetail.getTaskItemOutputMeta(), ((JSONRPCSuccessResponse) lastResponse).getResult().toString());
				if(resultStatusObj.getResult().isPresent() && 
				   (Boolean)resultStatusObj.getResult().get() == true) {
					logger.info("ifPresent");
					return this.processNormal("task_detail_if_present_task_id", taskRequest, taskDetailList, beforeTaskDetail, JSONRPCSuccessResponse.class.cast(lastResponse));
				}else {
					logger.info("orElse");
					return this.processNormal("task_detail_or_else_task_id", taskRequest, taskDetailList, beforeTaskDetail, JSONRPCSuccessResponse.class.cast(lastResponse));
				}
			}else {
				logger.info("ifPresent just go true");
				return this.processNormal("task_detail_if_present_task_id", taskRequest, taskDetailList, beforeTaskDetail, JSONRPCSuccessResponse.class.cast(lastResponse));
			}
		}
		logger.error("all other rule means exception just return last step");
		return lastResponse;
	}
	
	private JSONRPCResponse processNormal(String typeIfOrElse,JSONRPCRequest taskRequest,List<TaskItem> taskDetailList,TaskItem beforeTaskDetail,JSONRPCSuccessResponse lastResponse) {
		logger.info("processNormal start - "+typeIfOrElse);

		if("task_detail_if_present_task_id".equals(typeIfOrElse) && beforeTaskDetail.getTaskItemIfPresentItemId() == null) {			
			logger.info("no ifPresent next task return last step");
			return lastResponse;
		}else if("task_detail_or_else_task_id".equals(typeIfOrElse) && beforeTaskDetail.getTaskItemOrElseItemId() == null){
			logger.info("no orElse next task return last step");
			return lastResponse;
		}

		Optional<TaskItem> currentTaskDetailOption=null;
		if("task_detail_if_present_task_id".equals(typeIfOrElse)) {
			Integer currentTaskId = beforeTaskDetail.getTaskItemIfPresentItemId();
			currentTaskDetailOption = taskDetailList.stream()
					.filter(it->currentTaskId.equals(it.getId()))
					.findFirst();
			logger.info(typeIfOrElse+" current task step id is "+currentTaskId);
		}else if("task_detail_or_else_task_id".equals(typeIfOrElse)) {
			Integer currentTaskId = beforeTaskDetail.getTaskItemOrElseItemId();
			currentTaskDetailOption = taskDetailList.stream()
					.filter(it->currentTaskId.equals(it.getId()))
					.findFirst();
			logger.info(typeIfOrElse+" current task step id is "+currentTaskId);
		}

		if(currentTaskDetailOption==null || !currentTaskDetailOption.isPresent()) {
			logger.error("api not define jsut return last step");
			return lastResponse;
		}
		if(currentTaskDetailOption.get().getApiId() == null) {
			logger.error("api not define jsut return last step");
			return lastResponse;
		}

		Optional<Api> apiConfigJsonOption = currentTaskDetailOption.get().getApi()==null?Optional.empty():Optional.of(currentTaskDetailOption.get().getApi());
		
		if(!apiConfigJsonOption.isPresent()) {
			logger.error("api not define jsut return last step");
			return lastResponse;
		}

		String apiConfigUuid=apiConfigJsonOption.get().getUuid();

		JSONRPCRequest apiRequest=new JSONRPCRequest();
		Map<String, Object> apiMap=new HashMap<>();
		apiMap.put(Constant.STR_API_ID, apiConfigUuid);
		
		JsonElement jsonElement=lastResponse.getResult();

		if(jsonElement.isJsonArray()) {
			//if input meta not null execute method try to get map and add to Constant.strData
			apiMap.put(Constant.STR_DATA, new HashMap<>());
			if(StringUtils.isNotBlank(currentTaskDetailOption.get().getTaskItemInputMeta())){
				ProcessResultStatusVO<Object> obj=this.dynamicParserService.executeMethod("execute", currentTaskDetailOption.get().getTaskItemInputMeta(), jsonElement.toString());
				if(!obj.getResult().isPresent()) {
					logger.error("execute fail "+obj.getErrorMsgs());
					return new JSONRPCErrorResponse(taskRequest,-32010,JSONRPCError.ERR_32010_EXECUTE_FAIL);//not support
				}
				apiMap.put(Constant.STR_DATA, obj.getResult().get());
			}
		}else if(jsonElement.isJsonObject()) {
			//if input meta not null execute method try to get map and add to Constant.strData
			Type type = new TypeToken<Map<String, String>>(){}.getType();
			apiMap.put(Constant.STR_DATA, new Gson().fromJson(jsonElement.toString(), type));
			if(StringUtils.isNotBlank(currentTaskDetailOption.get().getTaskItemInputMeta())){
				ProcessResultStatusVO<Object> obj=this.dynamicParserService.executeMethod("execute", currentTaskDetailOption.get().getTaskItemInputMeta(), jsonElement.toString());
				if(!obj.getResult().isPresent()) {
					logger.error("execute fail "+obj.getErrorMsgs());
					return new JSONRPCErrorResponse(taskRequest,-32010,JSONRPCError.ERR_32010_EXECUTE_FAIL);//not support
				}
				apiMap.put(Constant.STR_DATA, obj.getResult().get());
			}
		}
		apiRequest.setParams(apiMap);
		return this.executeNext(taskRequest, taskDetailList, currentTaskDetailOption.get(), this.apiInvokeService.invoke(apiRequest));
	}

}
