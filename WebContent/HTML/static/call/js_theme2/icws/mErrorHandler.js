/**
 * Module Name : mErrorHandler for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.04.08
 * Last Updated : 2015.11.17
 */
define(["mConfig", "./mLogger", "./mUtil"],function(mConfig, mLogger, mUtil){
	var _moduleName = "mErrorHandler";
	var error_view_ex = window.fn_softphone_error_vew_ex; 
	
	var ICWS_ERROR_Callbacks = {
			
	};
	
	var ERR_LIST = {
			400 : {
				status : "Bad Request",
				messages : {
					basic 					: "요청 정보가 올바르지 않습니다.",
					connection 			: "CTI 서버와의 연결 정보가 올바르지 않습니다.",
					interactions			: "발신정보가 올바르지 않습니다.",
					interaction			: "현재 통화가 올바르지 않습니다. 현재 통화를 종료하시겠습니까?",
					user_status			: "올바르지 않은 사용자 상태값입니다.",		
					subscription			: "CTI 시스템 이벤트를 등록하는데에 실패하였습니다.",					
					status_messges		: "CTI 사용자 상태를 불러오는 데 실패했습니다. CTI를 다시 연결해 주세요."
				},
				//decidable : true,
				decidable : false,
				callbacks : {
					basic 					: function(){},
					connection 			: function(){},
					interactions			: function(){},
					interaction			: function(interactionId){ ICWS_ERROR_Callbacks.deleteFromApplicationQueue(interactionId, ICWS_ERROR_Callbacks.setActiveInteraction); },
					user_status			: function(){},
					subscription			: function(){
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
											},
					status_messges		: function(){
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
											}
				}
			},
			401 : {
				status : "Unauthorized",
				messages : {
					basic 					: "CTI 서버와의 접속이 끊어졌습니다. CTI를 다시 연결해주십시오."
				},
				decidable : false,
				callbacks : {
					basic 					: function(){ 	
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
												//jQuery("#"+mConfig.$btns.connection.connect.UIID).click();
											}
				}
			},
			403 : {
				status : "Forbidden",
				messages : {
					basic 					: "CTI 서버에서 현재 사용자의 요청을 거부했습니다.",
					interaction 			: "현재 통화가 올바르지 않습니다. 현재 통화를 종료하시겠습니까?",					
					connection			: "현재 사용자는 해당 소프트폰에 대한 연결 권한이 없습니다. 관리자에게 문의하세요.",
					conference			: "현재 사용자는 삼자통화를 맺을 권한이 없습니다. 관리자에게 문의하세요.",
					user_status			: "사용자 상태 설정에 대한 권한이 없습니다. 관리자에게 문의하세요."
				},
				//decidable : true	,
				decidable : false,
				callbacks : {
					basic 					: function(){},
					interaction 			: function(interactionId){ ICWS_ERROR_Callbacks.deleteFromApplicationQueue(interactionId, ICWS_ERROR_Callbacks.setActiveInteraction); },
					connection			: function(){
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();							
											},
					conference			: function(){}	,
					user_status			: function(){}
				}
			},
			404 : {
				status : "Not Found",
				messages : {
					basic 					: "CTI 서버에서 해당 데이터를 찾을 수 없습니다.",
					interaction			: "현재 통화가 올바르지 않습니다. 현재 통화를 종료하시겠습니까?",
					subscription			: "CTI 시스템 이벤트를 등록하는데에 실패하였습니다.",
					conference			: "입장할 삼자통화가 존재하지 않습니다."
				},
				//decidable : true	,
				decidable : false,
				callbacks : {
					basic 					: function(){},
					interaction			: function(interactionId){ ICWS_ERROR_Callbacks.deleteFromApplicationQueue(interactionId, ICWS_ERROR_Callbacks.setActiveInteraction); },
					subscription			: function(){
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
											},
					conference			: function(){}					
				}
			},
			410 : {
				status : "Gone",
				messages : {
					basic 					: "현재 버전에서는 지원하지 않는 기능입니다."
				},
				decidable : false,
				callbacks : {
					basic 					: function(){}
				}
			},
			500 : {
				status : "Internal Server Error",
				messages : {
					basic 					: "CTI 서버에서 요청을 수행하는 중 오류가 발생하였습니다. 동일 오류 메세지가 계속 발생하면 CTI를 다시 연결해 주세요."
				},
				decidable : false,
				callbacks : {
					basic 					: function(){},
					interactions			: function(){}
				}	
			},
			503 : {
				status : "Service Unavailable",
				messages : {
					basic 					: "CTI 서버와 연결할 수 없습니다."
				},
				//decidable : true,
				decidable : false,
				callbacks : {
					basic 					: function(){ 
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
											}
				}
			},
			999 : {
				status : "Unknown Error",
				messages : {
					basic 					: "오류가 발생하였습니다."
				},
				decidable : false,
				callbacks : {
					basic 					: function(){
												ICWS_ERROR_Callbacks.stopMessagesPolling();
												ICWS_ERROR_Callbacks.logoutActivity();
											}
				}
			},
			limitCnt : 0
	};
	
	var ICWSError = function(_status, _msgof, _method, _msg){		
		if((ERR_LIST[_status]))   this.status = _status;
		else						 this.status = 999;
		
		var msgof = "basic";		
		if(!!(_msgof) && !!(ERR_LIST[this.status].messages[_msgof]) ) msgof = _msgof;
		
		this.message = ERR_LIST[this.status].messages[msgof]+"("+this.status+")"; 
		
		if(_method == 'DELETE') this.callback = null;
		else						   this.callback = ERR_LIST[this.status].callbacks[msgof];
		
		this.decidable = ERR_LIST[this.status].decidable;
		this.system_msg = _msg;
	};
	
	ICWSError.prototype.show = function(arg){
		var _callback;
		var _message = this.message;
		var _decidable = this.decidable;
		var error_div;
		
		var timeStamp = (new Date()).toLocaleString() || "-";
		if(ERR_LIST.limitCnt < 2)
		{
			if(this.callback instanceof Function)
			{
				if(_decidable)
				{
					_callback = this.callback;
				}
				else
				{
					this.callback(arg);
					_callback = null;										
				}
				ERR_LIST.limitCnt++;
			}
			else
			{
				_callback = null
			}
			error_div = mUtil.popUpFactory.create("CTI ERROR["+timeStamp+"]("+ERR_LIST.limitCnt+")", _message, _callback);
		}else if(ERR_LIST.limitCnt == 2){
			//jQuery("#cti_error_popup_list").find(".close").click();			
			var _sys_msg;
			if(this.system_msg.indexOf("A session cookie is needed") > -1)	_sys_msg = mConfig.ICWS_SERVER.replace(/^[a-zA-Z0-9]*(?=[.])/, "*")+"를 신뢰하는 사이트로 등록하여주십시오.";
			else																			_sys_msg = _message+" ("+this.system_msg+")";
			
			error_div = mUtil.popUpFactory.create("CTI ERROR["+timeStamp+"](※)", _sys_msg, null);
			ERR_LIST.limitCnt++;
		}
		if(error_div) jQuery("#cti_error_popup_list").html(error_div);				
		
		if(error_view_ex instanceof Function)
		{
			error_view_ex(_message);
		}
	};

	window.ICWSError = ICWSError;
	mLogger.ConsoleLog(_moduleName,"Loaded");
	return {
		ICWSError					:	ICWSError,
		ICWS_ERROR_LIST			:	ERR_LIST,		
		ICWS_ERROR_Callbacks	:	ICWS_ERROR_Callbacks
	};
});