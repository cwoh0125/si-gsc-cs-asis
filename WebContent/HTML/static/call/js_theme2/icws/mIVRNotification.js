/**
 * Module Name : mUIHandler for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.12.01
 * Last Updated : 2016.02.22
 */


define(["./mXhr","./mLogger","./mConfig"], function(mXhr, mLogger, mConfig){
	var _moduleName = "mIVRNotification";	
	var default_reqPath = "/system/handler-notification";

	var sendNotification_Callback = function(_UICallback){

		//결과값이 오지 않고, connextion 이벤트로 떨어진다.
		return function(xmlHttp){
			var data;
			try{
				data = JSON.parse(xmlHttp.responseText);
			}catch(e){
				data = {};
			}
			if(xmlHttp.status == "202"){
				mLogger.ConsoleLog(_moduleName, "IVR Notification Accepted");				
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "IVR Notification Denied");
				mLogger.ConsoleLog(_moduleName, xmlHttp.status +" : "+ error.message);
			}
		};
	};

	var sendNotification = function(_noti_data, _UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath;
		var	payload = _noti_data;
		var externalCallback = sendNotification_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var sendMentNotification = function(_data, _UICallback){
		var noti_data = {
			objectId	: "IVR_MENT1000",
			eventId	: "IVR_MENT1000_EVT",
			data		: _data
		};

		sendNotification(noti_data, _UICallback);
	};

	var sendPwNotification = function(_data, _UICallback){
		var noti_data = {
			objectId	: "IVR_PW",
			eventId	: "IVR_PW_EVT",
			data		: _data
		};

		sendNotification(noti_data, _UICallback);
	};

	
	var sendIVR = function(_interaction_id, _svc_id, _cust_no, _cust_new_info, _agent_id, _UICallback){
		
		console.log("===> IVR요청 : interaction_id "+_interaction_id+ " / service_id : "+_svc_id	+ " / cust_no : "+_cust_no	+ " / agent_id : "+_agent_id	); 
		
		// 생년월일 | 성별 | 핸드폰번호 | 본인타인 | 통신사 | SMS인증일시 | SMS알림여부  
		//var new_cust_info = "19801002|01|01024135402|01|01|Y|20170717170302";
		
		if(_svc_id == "80" || _svc_id == "45" || _svc_id == "90")
		{
			alert(" ARS로 고객정보를 전달합니다.");
		}
		
		var arr = [_interaction_id,_svc_id,_cust_no, _cust_new_info, _agent_id];
		var noti_data = {
		objectId	: "IVR_MENT1000",
		eventId	: "IVR_MENT1000_EVT",
		data		: arr
		};
		sendNotification(noti_data, _UICallback);
	};	
	
	
	var sendTestNotification = function(_data, _UICallback){
		
		//alert("콜아이디확인"+_data);
		var svc_id = "30";
		var arr = [_data,svc_id];
		var noti_data = {
		objectId	: "IVR_MENT1000",
		eventId	: "IVR_MENT1000_EVT",
		data		: arr
};
		
//되는거		
//		var noti_data = {
//			objectId	: "IVR_MENT1",
//			eventId	: "IVR_MENT1_EVT",
//			data		: arr
//		};
		

//안되는거		
//		var noti_data = {
//				objectId	: "IVR_MENT1000",
//				eventId	: "IVR_MENT1000_EVT",
//				data		: _data
//		};

		sendNotification(noti_data, _UICallback);
		
	};	
	
	return {
		sendNotification				: sendNotification,
		sendTestNotification		: sendTestNotification,
		sendMentNotification		: sendMentNotification,
		sendPwNotification			: sendPwNotification,
		sendIVR			: sendIVR
	};
});