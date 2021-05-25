package com.arcare.api.converter.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.dao.RemoteDAO;
import com.arcare.api.converter.dao.impl.RemoteDAOImpl;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.service.DynamicParserService;
import com.arcare.api.converter.service.RequestService;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.arcare.api.converter.vo.JSONRPCSuccessResponse;
import com.arcare.api.converter.vo.ProcessResultStatusVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("SQLRequestServiceImpl")
public class SQLRequestServiceImpl implements RequestService {
	
	private final Logger logger = LoggerFactory.getLogger(SQLRequestServiceImpl.class);
	
	@Autowired
	@Qualifier("DynamicParserServiceImpl")
	private DynamicParserService dynamicParserService;
	
	@Override
	public JSONRPCResponse request(JSONRPCRequest request, Api config) {
		logger.info("request service");
		ProcessResultStatusVO<String> bindResult = this.dynamicParserService.bindTemplate(config.getApiMeta(),request.readStrDataInParamByMap());

		if(bindResult==null||!bindResult.getResult().isPresent()) {
			return new JSONRPCErrorResponse(request,-32002,JSONRPCError.ERR_32002_BIND_ERROR);
		}
		
		if(Constant.API_ACTION_JDBC_INSERT.equals(config.getApiAction()) 
			|| Constant.API_ACTION_JDBC_UPDATE.equals(config.getApiAction()) 
			|| Constant.API_ACTION_JDBC_DELETE.equals(config.getApiAction())) {
			RemoteDAO arcareEngDAO=new RemoteDAOImpl(config);
			
			Integer rows = arcareEngDAO.executeUpdate(bindResult.getResult().get());

			JsonElement element = new GsonBuilder().create().toJsonTree(rows);

			JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
			resp.setResult(element);
			return resp;
		}else if(Constant.API_ACTION_JDBC_QUERY.equals(config.getApiAction())) {
			RemoteDAO dao=new RemoteDAOImpl(config);

			Optional<JsonObject> queryJson = dao.query(bindResult.getResult().get(), null);
			
			Type queryType = new TypeToken<List<Map<String, String>>>(){}.getType();

			List<Map<String, Object>> queryResult = new Gson().fromJson(queryJson.get().getAsJsonObject().get(Constant.RESULT).getAsJsonArray().toString(), queryType);

			JsonElement element = new GsonBuilder().create().toJsonTree(queryResult);

			JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
			resp.setResult(element);
			return resp;
		}else if(Constant.API_ACTION_JDBC_EXECUTE.equals(config.getApiAction())) {
			RemoteDAO dao=new RemoteDAOImpl(config);
			Boolean rows = dao.execute(bindResult.getResult().get());

			JsonElement element = new GsonBuilder().create().toJsonTree(rows);

			JSONRPCSuccessResponse resp=new JSONRPCSuccessResponse(request);
			resp.setResult(element);
			return resp;
		}
		return new JSONRPCErrorResponse(request,-32001,JSONRPCError.ERR_32001_NOT_SUPPORT_THIS_API_ACTION);//參數無效
	}

}
