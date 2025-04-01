/**
 * Module Name : mMessageSubscriber for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.12.23
 */

define(["./mMessageHandler", "./mStatistics", "./mXhr" ,"./mLogger" ,"./mConfig", "./mErrorHandler"],
	function(mMessageHandler, mStatistics, mXhr, mLogger, mConfig, mErrorHandler){
	var _moduleName = "mMessageSubscriber";
	var default_reqPath = "/messaging";
	
	var messagePool;

	var addSubscription_Callback = function(_UICallback){

		return function(status, data){

			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Message Subscription Added");
				if(_UICallback) _UICallback(data);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Message Subscription Add Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "subscription", "PUT", error.message);
				errHandler.show();				
			}
		};
	};

	var addSubscription = function(_reqPath, _payload, _UICallback){
		if(_reqPath.substring(0,1) != "/") _reqPath = "/" + _reqPath;

		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
 		var reqPath = default_reqPath+"/subscriptions"+_reqPath;
		var	payload = _payload;
		var externalCallback = addSubscription_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var removeSubscription_Callback = function(_UICallback){

		return function(status, data){

			if(status == "204"){
				mLogger.ConsoleLog(_moduleName, "Message Subscription Removed");			

				if(_UICallback) _UICallback();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Message Subscription Remove Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, null, "DELETE", error.message);
				errHandler.show();				
			}
		};
	};

	var removeSubscription = function(_reqPath, _UICallback){
		if(_reqPath.substring(0,1) != "/") _reqPath = "/" + _reqPath;

		var server = mConfig.ICWS_SERVER;
		var method = "DELETE";
 		var reqPath = default_reqPath+"/subscriptions"+_reqPath;
		var	payload = {};
		var externalCallback = removeSubscription_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	var SessionSubscription = (function(){
		var reqPath = "/session/sessions-users";
		
		return {
			add : function(_userIds, _UICallback){			
				var payload = {userIds : _userIds};
				addSubscription(reqPath, payload, _UICallback);
			},
			remove : function(_UICallback){
				removeSubscription(reqPath, _UICallback);
			}
		};
	})();

	var UserStatusSubscription = (function(){
		var reqPath = "/status/user-statuses";
		
		return {
			add : function(_userIds, _UICallback){			
				var payload = {
					userIds : _userIds,
					userStatusProperties : ["userId", "statusId", "onPhone", "loggedIn"]					
				};
				addSubscription(reqPath, payload, _UICallback);
			},
			remove : function(_UICallback){
				removeSubscription(reqPath, _UICallback);
			}
		};
	})();

	var QueueContentSubscription = (function(){
		var reqPath = "/queues"+"/interactionStates";
		
		return {
			add : function(_queueIds, _UICallback){			
				var payload = {
					queueIds : _queueIds,
					attributeNames : ["Eic_CallId", "Eic_CallIdKey", "Eic_CallState", "Eic_CallStateString",
					                      "Eic_Capabilities", "Eic_CallType", "Eic_CallDirection", "Eic_ConnectDurationTime",
					                      "Eic_ConsultCallId", "Eic_ConsultingCallId", "Eic_ConsultTransferCallId",
					                	  "Eic_ConferenceId", "Eic_ConferenceMembers", "Eic_RemoteAddress", "Eic_UserName",
					                	  "Eic_UserQueueTimestamp", "Eic_UserSpecificQueueName", "Eic_WorkgroupQueueTimestamp", "Eic_InitialConnectTime",
					                	  "IsTransferred","ConsultSeqNo","Attr_QWaitDuration", "Eic_RecordFileName", "Eic_RecordedObjectId", "attrIVRCode" ,"attStationGroup1","attLocalTN",
					                	  "attr_service_gubun", "attr_user_pw1", "attr_bday1", "attr_sex_cd1", "attr_com_co_div_cd1",
					                	  "attr_authn_sbjt_cd1", "attr_cphn_head_tphn_no1", "attr_cphn_min_tphn_no1","attr_cphn_fnl_tphn_no1","attr_sms_authn_dtime", 
					                	  "attr_cust_no","attr_authn_no","attr_agrm_1_yn1","attr_agrm_2_yn1","attr_agrm_3_yn1","attr_agrm_4_yn1","attr_agrm_5_yn1",
					                	  "attr_cust_id","attr_key_gubun","attr_TRAN_GRP_CDNM","attr_servicecode1","attr_tr_phone1"
					                	  ]
				}; //20250225 약관 추가
				addSubscription(reqPath, payload, _UICallback);
			},
			remove : function(_UICallback){
				removeSubscription(reqPath, _UICallback);
			}
		};
	})();
	
	var StatisticsValueSubscription = (function(){
		var reqPath = "/statistics/statistic-values";
		
		return {
			add : function(_workGroupArray, _UICallback){				
				var _statisticsKeys = mStatistics.getStatisticsKeys(_workGroupArray);
				
				var payload = {
						statisticKeys : _statisticsKeys
				};
				addSubscription(reqPath, payload, _UICallback);
			},
			remove : function(_UICallback){
				removeSubscription(reqPath, _UICallback);
			}
		};
	})();
	

	var messagesPolling_Callback = function(status, data){

			if(status == "200"){
				for (var idx in data){
					mLogger.EventLog(data[idx].__type);
					if(data[idx].__type in mMessageHandler.messageCallbacks)  
						mMessageHandler.messageCallbacks[data[idx].__type](data[idx]);
				}
				mMessageHandler.clientCustomStatusChange();
				
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, null, "GET", error.message);
				errHandler.show();
				
				if(errHandler.status == "503")
				{
					mConfig.ICWS_SERVER = mConfig.getSwitchOverServer(mConfig.ICWS_SERVER);					
					jQuery("#CTI_server").val( mConfig.ICWS_SERVER );
					
					alert("현재 CTI서버와의 연결이 원활하지 않습니다. 30초후에 다시CTI 로그인 해주십시요");
					/*
					if(confirm("현재, CTI서버와의 연결이 원활하지 않습니다. 다시 연결하시겠습니까? "))
					{
						jQuery("#cti_error_popup_list .close").click();
						setTimeout('jQuery("#BTN_CTI_Login").click()',10000);
						//jQuery("#"+mConfig.$btns.connection["connect"].UIID).click();
					}	
					*/				
				}			
			}
	};
	

	var messagesPolling = function(){		

		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/messages";
		var	payload = {};
		var externalCallback = messagesPolling_Callback;
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);		
	};
	
	window.messageLoopId = null;

	var startMessagesPolling = function(){
		function messageLoopInstance(){
			//mLogger.EventLog("messsage polling");
			messagesPolling();
			window.messageLoopId = setTimeout(messageLoopInstance, mConfig.ICWS_MESSAGE_RETRIEVAL_INTERVAL_MS);
		}
		if(!window.messageLoopId){
			messageLoopInstance();
		}
	};

	var stopMessagesPolling = function(){
		if(!!(window.messageLoopId)){
			clearTimeout(window.messageLoopId);
			window.messageLoopId = null;
		}
	};

	mErrorHandler.ICWS_ERROR_Callbacks["stopMessagesPolling"] = stopMessagesPolling;
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{		
		addSubscription				: addSubscription,
		removeSubscription			: removeSubscription,
		startMessagesPolling			: startMessagesPolling,
		stopMessagesPolling			: stopMessagesPolling,
		SessionSubscription			: SessionSubscription,
		UserStatusSubscription		: UserStatusSubscription,
		QueueContentSubscription	: QueueContentSubscription,
		StatisticsValueSubscription	: StatisticsValueSubscription
	};
});