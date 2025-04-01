package kr.co.cs.business.xinterface;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.xdto.XcommonDto;

public interface XinterfaceService {
	public void CommonInterface(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void MultiRowTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
}
