package com.arcare.api.converter.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * 
 * @author FUHSIANG_LIU
 *
 * @param <T>
 */
public class ProcessResultStatusVO<T>{

	public static <T> ProcessResultStatusVO<T> newInstance(){
		return new ProcessResultStatusVO<>();
	}
	
	/**
	 * 回傳資料集合
	 */
	private T result;

	/**
	 * 錯誤集合
	 */
	private List<String> errorMsgs = new ArrayList<>();
	/**
	 * 警告集合
	 */
	private List<String> warniMsgs = new ArrayList<>();
	/**
	 * 寫入警告
	 * @param warniMsgs
	 * @return
	 */
	public ProcessResultStatusVO<?> addWarniMsgs(String warniMsgs) {
		this.warniMsgs.add(warniMsgs);
		return this; 
	}
	/**
	 * 寫入ERROR
	 * @param errormg
	 * @return
	 */
	public ProcessResultStatusVO<?> addErrorMsg(String errormg) {
		this.errorMsgs.add(errormg);
		return this; 
	}
	/**
	 * 收集自己的信息 導入 目的物件 回傳自己
	 * @param processResultStatus
	 * @return
	 */
	public ProcessResultStatusVO<T> collectTo(ProcessResultStatusVO<?> processResultStatus){
		if(!this.getErrorMsgs().isEmpty()) {
			processResultStatus.getErrorMsgs().addAll(this.getErrorMsgs().stream().collect(Collectors.toList()));
		}
		if(!this.getWarniMsgs().isEmpty()) {
			processResultStatus.getWarniMsgs().addAll(this.getWarniMsgs().stream().collect(Collectors.toList()));
		}
		return this;
	}

	public List<String> getErrorMsgs() {
		return errorMsgs;
	}
	public void setErrorMsgs(List<String> errorMsgs) {
		this.errorMsgs = errorMsgs;
	}
	
	public void setResult(T result) {
		this.result = result;
	}
	
	public Optional<T> getResult() {
		if(this.errorMsgs.isEmpty()) {
			return Optional.of(result);
		}else {
			return Optional.empty();
		}
	}

	public List<String> getWarniMsgs() {
		return warniMsgs;
	}

	public void setWarniMsgs(List<String> warniMsgs) {
		this.warniMsgs = warniMsgs;
	}
}
