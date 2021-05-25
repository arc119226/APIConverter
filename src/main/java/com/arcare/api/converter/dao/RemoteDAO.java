package com.arcare.api.converter.dao;

import java.util.Optional;

import com.google.gson.JsonObject;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public interface RemoteDAO {

	public Integer executeUpdate(String sql);
	
	public Boolean execute(String sql);
	
	public Optional<JsonObject> query(String sql,String dbName);
	
}
