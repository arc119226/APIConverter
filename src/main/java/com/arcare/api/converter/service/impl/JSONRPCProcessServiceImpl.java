package com.arcare.api.converter.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.service.InvokeService;
import com.arcare.api.converter.service.JSONRPCProcessService;
import com.arcare.api.converter.vo.JSONRPCError;
import com.arcare.api.converter.vo.JSONRPCErrorResponse;
import com.arcare.api.converter.vo.JSONRPCRequest;
import com.arcare.api.converter.vo.JSONRPCResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Service("JSONRPCProcessServiceImpl")
public class JSONRPCProcessServiceImpl implements JSONRPCProcessService{

	private final Logger logger = LoggerFactory.getLogger(JSONRPCProcessServiceImpl.class);

	@Autowired
	@Qualifier("TaskInvokeServiceImpl")
	private InvokeService taskInvokeService;

	@Autowired
	@Qualifier("APIInvokeServiceImpl")
	private InvokeService apiInvokeService;
	
	@Override
	public ResponseEntity<?> process(String json) {
		logger.info("process service start");
		JsonElement jsonElement;
		try {
			jsonElement=new JsonParser().parse(json);
			if(jsonElement.isJsonObject()){
				//single
				ObjectMapper mapper = new ObjectMapper();
				JSONRPCRequest singleRequest = mapper.readValue(json, JSONRPCRequest.class);
				return this.processSingle(singleRequest);
			}else if(jsonElement.isJsonArray()){
				//betch
				ObjectMapper mapper = new ObjectMapper();
				List<JSONRPCRequest> requestList = mapper.readValue(json, new TypeReference<List<JSONRPCRequest>>(){});
				return this.processMulti(requestList);
			}
		} catch (Exception e) {
			JSONRPCErrorResponse result=new JSONRPCErrorResponse();
			return ResponseEntity.badRequest().body(result);
		}
		//other bad request
		JSONRPCErrorResponse result=new JSONRPCErrorResponse();
		return ResponseEntity.badRequest().body(result);
	}

	private ResponseEntity<?> processSingle(JSONRPCRequest request){
		JSONRPCResponse resp=null;
		if(Constant.METHOD_TASK.equals(request.getMethod())){
			resp = this.taskInvokeService.invoke(request);
		}else if(Constant.METHOD_SEQUENCE.equals(request.getMethod()) || Constant.METHOD_PARALLEL.equals(request.getMethod())){
			resp = this.apiInvokeService.invoke(request);
		}else {
			resp = new JSONRPCErrorResponse(request,-32008,JSONRPCError.ERR_32008_NOT_SUPPORT_THIS_METHOD);
		}
		return ResponseEntity.ok(resp);
	}

	private ResponseEntity<?> processMulti(List<JSONRPCRequest> requestList){
		List<JSONRPCResponse> respList=new CopyOnWriteArrayList<JSONRPCResponse>();		
		requestList.sort(Comparator.comparing(JSONRPCRequest::getId));

		List<Map<String, List<JSONRPCRequest>>> groupbyListMap = this.groupbyJSONRPCMethod(requestList);
		List<Map<String,List<JSONRPCResponse>>> resopnseListMap = new CopyOnWriteArrayList<>();
		
		groupbyListMap.forEach(m->{			
			if(m.keySet().stream().findFirst().isPresent()) {
				String key=m.keySet().stream().findFirst().get();
				Map<String,List<JSONRPCResponse>> respMap=new ConcurrentHashMap<>();
				respMap.put(key, new CopyOnWriteArrayList<JSONRPCResponse>());
				resopnseListMap.add(respMap);
				if(Constant.METHOD_TASK.equals(key)) {//task
					List<JSONRPCRequest> sequenceList=m.get(key);
					List<JSONRPCResponse> sequenceRespList = sequenceList.stream().sequential()
							.map(r->this.taskInvokeService.invoke(r))
							.collect(Collectors.toList());
					respMap.put(key, sequenceRespList);
					respList.addAll(sequenceRespList);
				}else if(Constant.METHOD_SEQUENCE.equals(key)) {//api
					List<JSONRPCRequest> sequenceList=m.get(key);
					List<JSONRPCResponse> sequenceRespList = sequenceList.stream().sequential()
							.map(r->this.apiInvokeService.invoke(r))
							.collect(Collectors.toList());
					respMap.put(key, sequenceRespList);
					respList.addAll(sequenceRespList);
				}else if(Constant.METHOD_PARALLEL.equals(key)) {//api
					List<JSONRPCRequest> parallelList=m.get(key);
					List<JSONRPCResponse> parallelRespList=new ArrayList<>();
					try {
						ForkJoinPool pool = new ForkJoinPool(16);
						parallelRespList = pool.submit(() ->
							parallelList.parallelStream().map(r->this.apiInvokeService.invoke(r))
							.collect(Collectors.toList())
						).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
						logger.error("",e);
					}
					respMap.put(key, parallelRespList);
					respList.addAll(parallelRespList);
				}
			}
		});
		return ResponseEntity.ok(respList);
	}
	
	private List<Map<String,List<JSONRPCRequest>>> groupbyJSONRPCMethod(List<JSONRPCRequest> requestList){
		requestList.sort(Comparator.comparing(JSONRPCRequest::getId));
		/**
		 * list Map<p/s,requestList>
		 */
		List<Map<String,List<JSONRPCRequest>>> listMap = new CopyOnWriteArrayList<>();
		Map<String,List<JSONRPCRequest>> firstMap=new ConcurrentHashMap<>();
		listMap.add(firstMap);
		for(int i=0;i<requestList.size();i++) {
			Map<String,List<JSONRPCRequest>> curentMap= listMap.get(listMap.size()-1);
			if(curentMap.keySet().size()==0) {
				JSONRPCRequest currentRequest=requestList.get(i);
				String method=currentRequest.getMethod();
				curentMap.put(method, new CopyOnWriteArrayList<JSONRPCRequest>());
				curentMap.get(method).add(currentRequest);
			}else {//have key
				JSONRPCRequest currentRequest=requestList.get(i);
				String method=currentRequest.getMethod();
				//check key equal current method if true add
				if(curentMap.keySet().stream().findFirst().filter(key->key.equals(method)).isPresent()) {
					curentMap.get(method).add(currentRequest);
				}else {//check key equal current method if false create map and add
					Map<String,List<JSONRPCRequest>> newMap=new ConcurrentHashMap<>();
					newMap.put(method, new CopyOnWriteArrayList<JSONRPCRequest>());
					listMap.add(newMap);
					newMap.get(method).add(currentRequest);
				}
			}
		}
		return listMap;
	}
}
