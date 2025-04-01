package kr.co.cs.business.xinterface;
 
import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.xdto.XcommonDto;

public interface XinterfaceSecondService {
	public void NameVerification(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void IVRStaSearch(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void ListenContiue(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void CTIMiniBoard(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void CTIUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void CTIUserMonitoring(XcommonDto dto, XcommonService xcommonservice) throws Exception ;	
	public String CTIMiniBoard2() throws Exception ;
	public void CAMSUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
	public void MSGUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception ; 
	public void custWithdrawBatch(XcommonDto dto, XcommonService xcommonservice) throws Exception ;
}
 