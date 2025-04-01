package kr.co.cs.business.arsinterface;


import java.sql.SQLException;

import kr.co.cs.common.xdto.XcommonDto;

public interface ARSinterfaceService {
	public void ARSTransaction(XcommonDto dto) throws SQLException, Exception;
	public void ARSUserTransaction(XcommonDto dto) throws SQLException, Exception;
	public void ARSNonTransaction(XcommonDto dto) throws SQLException, Exception;	
}
 