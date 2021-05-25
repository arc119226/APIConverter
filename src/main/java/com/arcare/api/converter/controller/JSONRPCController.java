package com.arcare.api.converter.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arcare.api.converter.dao.BackboneDAO;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.domain.Task;
import com.arcare.api.converter.service.JSONRPCProcessService;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Controller
public class JSONRPCController {

	private final Logger logger = LoggerFactory.getLogger(JSONRPCController.class);
	
	@Autowired
	@Qualifier("JSONRPCProcessServiceImpl")
	private JSONRPCProcessService jSONRPCProcessService;
	
	@Autowired
	@Qualifier("BackboneDAOImpl")
	private BackboneDAO backboneDao;
	
	@GetMapping(value = "/api", produces="application/json")
	public @ResponseBody ResponseEntity<?> api(String uuid) {
		try {
			Optional<Api> api=backboneDao.queryApiByUUID("{"+uuid+"}");
				if(api.isPresent()) {
					HashMap<String,Object> map=new HashMap<>();
					map.put("api", api);
					return ResponseEntity.ok(map);
				}else {
					Map<String,String> result=new HashMap<>();
					result.put("error", "not found");
					return ResponseEntity.ok(result);
				}
		}catch(Exception e) {
			e.printStackTrace();
			Map<String,String> result=new HashMap<>();
			result.put("error", "other error");
			return ResponseEntity.ok(result);
		}
	}
	
	@GetMapping(value = "/task", produces="application/json")
	public @ResponseBody ResponseEntity<?> task(String uuid) {
		try {
			Optional<Task> task=backboneDao.queryTaskByUUID("{"+uuid+"}");
				if(task.isPresent()) {
					HashMap<String,Object> map=new HashMap<>();
					map.put("task", task);
					return ResponseEntity.ok(map);
				}else {
					Map<String,String> result=new HashMap<>();
					result.put("error", "not found");
					return ResponseEntity.ok(result);
				}
		}catch(Exception e) {
			e.printStackTrace();
			Map<String,String> result=new HashMap<>();
			result.put("error", "other error");
			return ResponseEntity.ok(result);
		}
	}
	
	@PostMapping(value = "/json-rpc", produces="application/json")
	public @ResponseBody ResponseEntity<?> postJsonRpc(@RequestBody String json) {
		logger.info("post /json-rpc start");
		return this.jSONRPCProcessService.process(json);
	}
}
