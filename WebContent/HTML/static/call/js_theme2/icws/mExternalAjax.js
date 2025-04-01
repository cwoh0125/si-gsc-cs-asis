/**
 * Module Name : mExternalAjax for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.07.29
 * Last Updated : 2015.11.10
 */

define(["./mLogger", "./mConfig"], function(mLogger, mConfig){
	var _moduleName = "mExternalAjax";
	
	var sendToIVR = function(interactionId){		
		var _url = '/call/softphone/action/softphone.jspx?cmd=sendToIVR&interactionId='+interactionId;
		
		var http = jQuery.ajax( {
	   		url: _url,	   		
	   		type: "GET",
			data : '0=0',
			dataType: "json",
	   		async : false,
	   		error : function(xhr)
	   		{
				alert(xhr.status);
			},
			success:function(json) 
	   		{
				
			}
	   	});
	};	
	
	var mergeAgentInfo = function(agentArr){
		var _url = '/call/softphone/action/softphone.jspx?cmd=getAgentList';
		
		var http = jQuery.ajax( {
	   		url: _url,	   		
	   		type: "GET",
			data : '0=0',
			dataType: "json",
	   		async : false,
	   		error : function(xhr)
	   		{
				alert(xhr.status);
			},
			success:function(json) 
	   		{
				var code = json.result.code;
				var msg = json.result.msg;
				var item = json.result.data;
				
				//console.log(item);
				if(code == '200')
				{	
					var length = item.agent_list.length;

					for(var i =0; i<length; i++)
					{
						var phone_user_id	=	item.agent_list[i].phone_user_id;	//CTI 사용자 아이디
						var phone_user_tel	=	item.agent_list[i].phone_user_tel;	//CTI STATION
						var emp_nm			=  item.agent_list[i].emp_nm;
						var ext_tel_no		=	item.agent_list[i].ext_tel_no;		//CTI 사용자 내선
												
						if(agentArr[phone_user_id])
						{
							agentArr[phone_user_id].name		=  emp_nm;
							agentArr[phone_user_id].extension	=  ext_tel_no || phone_user_tel;	
						}
					}
					
				}else{
					msgStart(msg_com_code_010+"("+msg+")", 'danger');
				}
			}
	   	});	
	};

	var getLoginInfo = function(_cti_login, _UICallbacks){
		
		var userid = jQuery("#CTI_userid").val();
		var password = jQuery("#CTI_password").val();
		var stationId = jQuery("#CTI_station").val();;
		var call_pre_no = "";
		//var cmpny_no =  1;
		//setCmpnyNo(cmpny_no);
		_cti_login(userid, password, stationId, _UICallbacks);
		/*
		jQuery("#CTI_userid").val(userid);
		jQuery("#CTI_password").val(password);
		jQuery("#CTI_station").val(stationId);
		jQuery("#CTI_call_pre_no").val(call_pre_no);
		*/
		
//		var _url = '/call/softphone/action/softphone.jspx?cmd=getPhoneInfo';		
		
//		var http = jQuery.ajax( {
//	   		url: _url,	   		
//	   		type: "GET",
//			data : '0=0',
//			dataType: "json",
//	   		async : false,
//	   		error : function(xhr)
//	   		{
//				alert(xhr.status);
//			},
//			success:function(json) 
//	   		{
//				var code = json.result.code;
//				var msg = json.result.msg;
//				var item = json.result.data;
//				
//				if(code == '200')
//				{
//					var userid = item.phone_info[0].phone_user_id;
//					var password = "1234";
//					var stationId = item.phone_info[0].phone_user_tel;
//					var call_pre_no = "";
//					var cmpny_no = "-1";
//					try
//					{
//						cmpny_no =  item.phone_info[0].cmpny_no;
//					}catch(e){}
//					
//					try
//					{
//						call_pre_no =  item.phone_info[0].call_pre_no;
//					}catch(e){}
//					
//					setCmpnyNo(cmpny_no);
//					
//					_cti_login(userid, password, stationId, _UICallbacks);
//					
//					jQuery("#CTI_userid").val(userid);
//					jQuery("#CTI_password").val(password);
//					jQuery("#CTI_station").val(stationId);
//					jQuery("#CTI_call_pre_no").val(call_pre_no);
//
//				}else{
//					msgStart(msg_com_code_010+"("+msg+")", 'danger');
//				}
//	   		}
//		});
	};		
	
	var savePartRecording = function(interactionId, recordingFileName, _UICallbacks){
		mLogger.ConsoleLog(_moduleName, "Save Recording File Information of "+interactionId+" : "+recordingFileName);
		var _url = '';
		/*
		var http = jQuery.ajax( {
	   		url: _url,	   		
	   		type: "POST",
			data : "recordingFileName="+recordingFileName,
			dataType: "json",
	   		async : false,
	   		error : function(xhr)
	   		{
				alert(xhr.status);
			},
			success:function(json) 
	   		{
				var code = json.result.code;
				var msg = json.result.msg;
				var item = json.result.data;
				
				if(code == '200')
				{
					
				}else{
					msgStart(msg_com_code_010+"("+msg+")", 'danger');
				}
	   		}
		});
		*/
	};

	mLogger.ConsoleLog(_moduleName,"Loaded");
	return {
		sendToIVR			:	sendToIVR,
		mergeAgentInfo		:	mergeAgentInfo,
		getLoginInfo			:	getLoginInfo,
		savePartRecording	: 	savePartRecording
	};
});