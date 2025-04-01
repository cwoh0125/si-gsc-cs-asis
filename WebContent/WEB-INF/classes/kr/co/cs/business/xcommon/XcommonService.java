package kr.co.cs.business.xcommon;

import java.sql.SQLException;
import java.util.List;

import com.tobesoft.xplatform.data.DataSet;

import kr.co.cs.common.xdto.XcommonDto;
 
public interface XcommonService {
	public void XcommonTransaction(XcommonDto dto) throws SQLException, Exception;
	public void XcommonUserTransaction(XcommonDto dto) throws SQLException, Exception;
	public void XcommonNonTransaction(XcommonDto dto) throws SQLException, Exception;	
	public void XcommonServerEnv(XcommonDto dto) throws Exception ;
}
