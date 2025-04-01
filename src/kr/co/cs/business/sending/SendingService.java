package kr.co.cs.business.sending;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.xdto.XcommonDto;

public interface SendingService {
	public void CommonEmailTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void BuyingEmailTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void CommonFaxTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void FaxResendTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
}
