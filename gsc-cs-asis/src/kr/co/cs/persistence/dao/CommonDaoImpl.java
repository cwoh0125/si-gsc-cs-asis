package kr.co.cs.persistence.dao;

import java.util.HashMap;
import java.util.List;
import kr.co.cs.common.ibatis.SqlMapClientDaoSupportByiBatis;
 
public class CommonDaoImpl extends SqlMapClientDaoSupportByiBatis implements CommonDao{
	/*     
	 *                                       	             	                 
	 */                     
	public List<HashMap<String,Object>> selectList(String statementname, Object parameter) throws Exception {
		return getSqlMapClientTemplate().queryForList(statementname, parameter);
 	}           
 
	public String selectString(String statementname, Object parameter) throws Exception {
		Object o = getSqlMapClientTemplate().queryForObject(statementname, parameter);
		if(o==null) 
			return "";   
		else  
			return o.toString();
	}   
 
	public void insert(String statementname, Object parameter) throws Exception {
		getSqlMapClientTemplate().insert(statementname, parameter);
	}    

	public int update(String statementname, Object parameter) throws Exception {
		return getSqlMapClientTemplate().update(statementname, parameter);		
	}

	public int delete(String statementname, Object parameter) throws Exception {
		return getSqlMapClientTemplate().delete(statementname, parameter);			
	}
}
 