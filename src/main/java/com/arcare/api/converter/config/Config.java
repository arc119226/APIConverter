package com.arcare.api.converter.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.crazycake.jdbcTemplateTool.JdbcTemplateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
/**
 * 
 * @author FUHSIANG_LIU
 *
 */
@Configuration
@EnableWebMvc
@PropertySources({ @PropertySource("classpath:system.properties") })
public class Config extends WebMvcConfigurerAdapter {

	@Autowired 
	private Environment env;

	@Bean
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty(Constant.BACKBONE_DB_DRIVER));
		dataSource.setUrl(env.getProperty(Constant.BACKBONE_DB_JDBC_URL));
		dataSource.setUsername(env.getProperty(Constant.BACKBONE_DB_USER));
		dataSource.setPassword(env.getProperty(Constant.BACKBONE_DB_PASSWORD));
		dataSource.setTestOnBorrow(true);
		dataSource.setTestOnCreate(true);
		dataSource.setTestOnReturn(true);
		dataSource.setTestWhileIdle(true);
		//TODO connection setting...
		return dataSource;
	}

	@Bean
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(this.getDataSource());
	}

	@Bean
	public JdbcTemplateTool getJdbcTemplateTool() {
		JdbcTemplateTool jtt=new JdbcTemplateTool();
		jtt.setJdbcTemplate(this.getJdbcTemplate());
		return jtt;
	}

	@Bean
	public ViewResolver getViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

}
