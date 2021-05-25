package com.arcare.api.converter.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcare.api.converter.config.Constant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * @author FUHSIANG_LIU
 *
 */
public class ResultSetUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ResultSetUtil.class);
	
	public static JsonObject resultSetToJsonObject(ResultSet rs) {
		JsonObject element = null;
		JsonArray ja = new JsonArray();
		JsonObject jo = new JsonObject();
		ResultSetMetaData resultSetMetaData = null;
		String columnName, columnValue = null;
		boolean hasData = false;
		try {
			resultSetMetaData = rs.getMetaData();
			while (rs.next()) {
				hasData = true;
				element = new JsonObject();
				for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
					columnName = resultSetMetaData.getColumnName(i + 1);
					if (resultSetMetaData.getColumnType(i + 1) == -4 
						|| resultSetMetaData.getColumnType(i + 1) == -3
						|| resultSetMetaData.getColumnType(i + 1) == -2) {
						// skip binary
					} else {
						if(rs.getString(columnName)!=null) {
							columnValue = rs.getString(columnName).trim();
						}else {
							columnValue = rs.getString(columnName);
						}
					}
					if (columnValue == null) {
						columnValue = "";
					}else {
						element.addProperty(columnName, columnValue);
					}
				}
				ja.add(element);
			}
			jo.add(Constant.RESULT, ja);
		} catch (SQLException e) {
			logger.error("", e);
			e.printStackTrace();
		}
		return jo;
	}
}
