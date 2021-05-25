package com.arcare.api.converter.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcare.api.converter.config.Constant;
import com.arcare.api.converter.dao.RemoteDAO;
import com.arcare.api.converter.domain.Api;
import com.arcare.api.converter.util.ResultSetUtil;
import com.google.gson.JsonObject;
/**
 * 遠程DAO 查詢遠程DB
 * @author FUHSIANG_LIU
 *
 */
public class RemoteDAOImpl implements RemoteDAO{

	private final Logger logger = LoggerFactory.getLogger(RemoteDAOImpl.class);
	
	private Api config;
	
	public RemoteDAOImpl(Api config) {
		this.config=config;
	}
	
	private Connection getConnection() {
		try {
			String url = (String) this.config.getValueByPropertiesName(Constant.PROP_JDBC_URL);
			String username = (String) this.config.getValueByPropertiesName(Constant.PROP_JDBC_USER);
			String password = (String) this.config.getValueByPropertiesName(Constant.PROP_JDBC_PASSWORD);
			String driverName =(String) this.config.getValueByPropertiesName(Constant.PROP_JDBC_DRIVER);
			Class.forName(driverName);
			return DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("",e);
		}
		return null;
	}
	
	@Override
	public Integer executeUpdate(String sql) {
		try (Connection con = this.getConnection(); 
				 PreparedStatement pst = con.prepareStatement(sql)) {
				return pst.executeUpdate();
			} catch (Exception e) {
				logger.error("", e);
				return 0;
			}
	}

	@Override
	public Boolean execute(String sql) {
		try (Connection con = this.getConnection(); 
			 PreparedStatement pst = con.prepareStatement(sql)) {
				return pst.execute();
			} catch (Exception e) {
				logger.error("", e);
				return false;
			}
	}

	@Override
	public Optional<JsonObject> query(String sql, String dbName) {
		try (Connection con = this.getConnection(); 
				 PreparedStatement pst = con.prepareStatement(sql);) {
				if (dbName != null) {
					con.setCatalog(dbName);
				}
				try (ResultSet rs = pst.executeQuery()) {
					JsonObject obj = ResultSetUtil.resultSetToJsonObject(rs);
					if (obj == null) {
						return Optional.empty();
					} else {
						return Optional.of(obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("", e);
					return Optional.empty();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("", e);
				return Optional.empty();
			}
	}

}
