package kr.co.cs.persistence.dao;

import java.sql.SQLException;
import java.util.List;


public interface CommonDao {  
  
	public List selectList(String statementname, Object parameter) throws SQLException, Exception ;
	
	public String selectString(String statementname, Object parameter) throws SQLException, Exception ;

	public void insert(String statementname, Object parameter) throws SQLException, Exception ;

	public int update(String statementname, Object parameter) throws SQLException, Exception ;

	public int delete(String statementname, Object parameter) throws SQLException, Exception ;
}


