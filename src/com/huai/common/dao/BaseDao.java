package com.huai.common.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("baseDao")
public class BaseDao {

	@Resource(name="localprint")
	public DataSource localprint;
	
	@Resource(name="jdbcTemplate")
	public JdbcTemplate jdbcTemplate;
	
	public String getNewID(String seq_name) {
		String id = (String)jdbcTemplate.queryForObject(" select nextval('"+seq_name+"') ", String.class);
		return id;
	}
	
	public List executeSql(String sql) {
		List list = jdbcTemplate.queryForList(sql);
        return list;
	}
	
}
