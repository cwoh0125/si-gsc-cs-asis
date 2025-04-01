/**
 * Module Name : mMessageHandler for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.12.23
 */

define(["./mInteraction", "./mConference", "./mQueue",  "./mUser", "./mStatistics", "./mLogger", "./mConfig", "./mErrorHandler", "./mUIController", "./mUtil"],
	function(mInteraction, mConference, mQueue, mUser, mStatistics, mLogger, mConfig, mErrorHandler, mUIController, mUtil){
	
	var _moduleName = "mMessageHandler";

	//Softphone UI Callback
	var _alerting_callbacak = window.fn_softphone_popAnswer;
	var _connected_callback = window.fn_softphone_setAfterConnect;
	var _transferred_callback = window.fn_softphone_setTransferd;
	var _disconnected_callback = window.fn_softphone_setAfterDisConnect;
	var _dialing_callback = window.fn_softphone_setDialing;
	var _consult_dialing_callback = window.fn_softphone_setConsultDialing;
	var _hold_callback = window.fn_softphone_setHold;
	var _unhold_callback = window.fn_softphone_setUnHold;
	var _ivr_callback = window.fn_softphone_setIVR;
	
	var _cust_waiting = window.fn_miniview_setCustWait;
	var _agent_waiting = window.fn_miniview_setAgentWait;
	var _agent_break = window.fn_miniview_setAgentBreak;
	
	var _disconnected_UIcallback = function(interactionId, queueObj){
		mLogger.ConsoleLog("_pickedUp_UIcallback");
		mLogger.ConsoleLog("interactionId : "+interactionId);
		mLogger.ConsoleLog("_pickup_callid : "+window.getPickUpCallid());
		if(window.getPickUpCallid() == interactionId){
			jQuery("#softphone_answer_div").css("display", "none");
			window.setPickUpCallid(0);
		}
		//mLogger.ConsoleLog("@@ connected_duration : "+connected_duration);
		if(_disconnected_callback instanceof Function)
		{
			_disconnected_callback(interactionId, queueObj.Eic_CallIdKey, queueObj.Eic_ConnectDurationTime, queueObj.Eic_CallDirection, queueObj.Eic_CallType, queueObj.cid,  queueObj.Eic_CallStateString );
		}
		
	};
	
	var consultee_callback = function(interactionId, attributes){
		mLogger.ConsoleLog("consultee_callback : isStateChanged = "+ attributes.isStateChanged);
		
		if(attributes.isStateChanged){
			var consultSeqNo = attributes.ConsultSeqNo;
			//console.log("☆☆☆☆☆☆☆☆☆☆attributes.Eic_CallState : "+attributes.Eic_CallState)
			switch(attributes.Eic_CallState){
			case "Alerting"			:
				//mUser.setUserStatus("Ringing");
				mUser.setUserStatus(mConfig.ICWS_OCW.IB);
				if(_alerting_callbacak instanceof Function){
					var q_wait_time = attributes["Attr_QWaitDuration"];
					var dnis = attributes["attLocalTN"];
					var call_id_key = attributes["Eic_CallIdKey"];
					//console.log("@@DNIS : "+dnis);
					if(!q_wait_time){
						q_wait_time = mUtil.getTimeDifference(attributes["Eic_WorkgroupQueueTimestamp"], attributes["Eic_UserQueueTimestamp"]);
						if(q_wait_time) mInteraction.postInteraction(interactionId, { "attributes" : { "Attr_QWaitDuration" : q_wait_time, "attStationGroup1":getCmpnyNo() } });						
					}
					else
					{
						mInteraction.postInteraction(interactionId, { "attributes" : { "attStationGroup1":getCmpnyNo() } });
					}
					
					_alerting_callbacak(interactionId, call_id_key, attributes.cid, attributes.IsTransferred, attributes.whois, q_wait_time, attributes.attr_servicecode1, dnis, attributes);
				}
				attributes.IsAlerted = true;
				break;
			case "Dialing"			:
				mUser.setUserStatus(mConfig.ICWS_OCW.OB);
				attributes.IsDialed = true;
				if(_dialing_callback instanceof Function )_dialing_callback(interactionId, attributes.Eic_CallIdKey, attributes.Eic_CallDirection, attributes.Eic_CallType, attributes.cid);				
				break;
			case "Connected"		:
				mUser.setUserBusy(attributes.Eic_CallDirection);
				if(attributes.IsTransferred=="Y" && !attributes.IsTransferSent && !attributes.IsTransferReceived){
					if(_transferred_callback instanceof Function){
						_transferred_callback(attributes.cid, attributes, interactionId, attributes.Eic_CallIdKey, consultSeqNo, "T");
					}
					attributes.IsTransferReceived = true;
				}else if(_connected_callback instanceof Function && !(attributes.IsConnected)){
					if		(attributes.Eic_CallDirection == "I" && attributes.IsAlerted)	_connected_callback(interactionId, attributes.Eic_CallIdKey, attributes.Eic_CallDirection, attributes.Eic_CallType, attributes.cid);
					else if(attributes.Eic_CallDirection == "O" && attributes.IsDialed)	_connected_callback(interactionId, attributes.Eic_CallIdKey, attributes.Eic_CallDirection, attributes.Eic_CallType, attributes.cid);
					attributes.IsConnected = true;		
				}
				else if(_unhold_callback instanceof Function && (attributes.IsConnected)) 
				{
					_unhold_callback(interactionId, attributes.Eic_CallIdKey);
				}
				break;
			case "On Hold"			:
				mUser.setUserBusy(attributes.Eic_CallDirection);
				jQuery("#"+mConfig.$btns.interaction["pickup"].UIID).prop("disabled", true).attr("src", mConfig.$btns.interaction["pickup"].imgs.off);
				if(_hold_callback instanceof Function ) _hold_callback(interactionId, attributes.Eic_CallIdKey);
				break;
			}
			attributes.isStateChanged = false;
		}
    	else
		{
		    if(attributes.isRetrievable)
		    {
		        mInteraction.unholdInteraction(interactionId);
		        attributes.isRetrievable = false;
		    }
		}
		mUIController.interactionBtnChangeByCallState(attributes.Eic_Capabilities, attributes.Eic_CallState);
	};
	
	var consultant_callback = function(interactionId, attributes){
		mLogger.ConsoleLog("consultant_callback : isStateChanged = "+ attributes.isStateChanged + "/  STATUS : "+attributes.Eic_CallState);
		if(attributes.isStateChanged){
			
			switch(attributes.Eic_CallState){
			case "Alerting"			:
				mUser.setUserStatus(mConfig.ICWS_OCW.IB);
				if(_alerting_callbacak instanceof Function){										
					_alerting_callbacak(interactionId, attributes.cid, attributes.IsTransferred, attributes.whois);				
				}
				break;
			case "Dialing"			:
				mUser.setUserStatus(mConfig.ICWS_OCW.OB);
				if(_consult_dialing_callback instanceof Function){
					_consult_dialing_callback();
				}
				break;
			case "Connected"		:

			case "On Hold"			:
				mUser.setUserBusy(attributes.Eic_CallDirection);			
				break;
			}			
			attributes.isStateChanged = false;
		}			
	};
	
	var combination_callback = function(consulteeId, consulteeAttributes, consultantId, consultatnAttributes, conference){		
		
		if(!consulteeId && !consultantId){
			jQuery("#call_status_dialog .head_text").text("호상태조회");
			jQuery("#conference_div").css("display", "none");
			jQuery("#consult_div, #call_status_page").css("display", "");	
		}else if(!consultantId && !(conference.id)){ //consult X
			jQuery("#call_status_dialog .head_text").text("호상태조회");
			jQuery("#conference_div").css("display", "none");
			jQuery("#consult_div, #call_status_page").css("display", "");
		}else if(conference.id){ //conference O
			jQuery("#call_status_dialog .head_text").text("삼자통화 진행 중");
			jQuery("#conference_div").css("display", "");
			jQuery("#consult_div").css("display", "none");
			
			mConference.conferenceChangeByConference(mQueue, conference);
		}else if(!!(consultantId) && !(conference.id)){ //consult O
			jQuery("#call_status_dialog .head_text").text("호상태조회 - ["+consultatnAttributes.whois+"님과 협의호 진행 중]");
			jQuery("#conference_div, #call_status_page").css("display", "none");	
		}
		
		mUIController.transferBtnChangeByConsults(consulteeAttributes.Eic_Capabilities, consultatnAttributes.Eic_Capabilities);
		mUIController.conferencableBtnChangeByCallers(consulteeAttributes.Eic_Capabilities, consultatnAttributes.Eic_Capabilities);
	};
	
	var messageCallbacks = {
		"urn:inin.com:session:sessionsMessage" : function(data){
		//session callback
			mLogger.ObjectLog(data);
		},
		"urn:inin.com:connection:connectionStateChangeMessage" : function(data){
		//connectionState callback
			mLogger.ObjectLog(data);
		},
		"urn:inin.com:statistics:statisticValueMessage" : function(data){
			//statistics value callback
			//mLogger.ObjectLog(data);
			var statisticsValues = data.statisticValueChanges;
			mStatistics.updateStatistics(statisticsValues);
			
			var objQueue = mStatistics.getQueueStatistics();
			var objAgent = mStatistics.getAgentStatistics();
			
			var workgroup_cust_wait_cnt = {};
			var workgroup_agent_wait_cnt = {};
			var workgroup_agent_break_cnt = {};
			
			for( var workgroup in objQueue.members)
			{
				workgroup_cust_wait_cnt[workgroup] = objQueue.members[workgroup].waiting; 
			}
			
			for( var workgroup in objAgent.members)
			{
				workgroup_agent_wait_cnt[workgroup] = objAgent.members[workgroup].available; 
			}
			
			for( var workgroup in objAgent.members)
			{
				workgroup_agent_break_cnt[workgroup] = objAgent.members[workgroup].atRest; 
			}
			
			//var objCustWait = mStatistics.getQueueStatistics();			
			var wait_q_cnt = objQueue.total.waiting;
			mUIController.updateWaitQCnt(wait_q_cnt);
			
			if( _cust_waiting instanceof Function) _cust_waiting(workgroup_cust_wait_cnt);
			if( _agent_waiting instanceof Function) _agent_waiting(workgroup_agent_wait_cnt);
			if( _agent_break instanceof Function) _agent_break(workgroup_agent_break_cnt);
		},
		"urn:inin.com:status:userStatusMessage" : function(data){
		//userStatus callback
			mLogger.ObjectLog(data);
			var userStatuses = data.userStatusList;
			mUser.updateUserList(userStatuses);
			mUIController.statusBtnChangeByUserStatus(mUser.getMyUserInfo());
		},
		"urn:inin.com:queues:queueContentsMessage" : function(data){
		//queueContent callback
			mLogger.ObjectLog(data);
			var interactionInfos;
			var totalInteractionCount = 0;
			if(data.interactionsAdded && data.interactionsAdded.length > 0)
			{
				interactionInfos = data.interactionsAdded;
				for(var idx1 in interactionInfos){
					var interactionInfo1 = data.interactionsAdded[idx1];
					if(interactionInfo1.conferenceParentId)	interactionInfo1.attributes["conferenceParentId"] = interactionInfo1.conferenceParentId;
					
					mQueue.runQueueLifeCycle(interactionInfo1.interactionId, interactionInfo1.attributes, {
						disconnected : _disconnected_UIcallback
					});
					
					mLogger.EventLog("["+interactionInfo1.interactionId + "] is added.");
				}				
			}
			
			if(data.interactionsChanged && data.interactionsChanged.length > 0)
			{
				interactionInfos = data.interactionsChanged;
				for(var idx2 in interactionInfos){
					var interactionInfo2 = data.interactionsChanged[idx2];
					if(interactionInfo2.conferenceParentId)	interactionInfo2.attributes["conferenceParentId"] = interactionInfo2.conferenceParentId;

					mQueue.runQueueLifeCycle(interactionInfo2.interactionId, interactionInfo2.attributes, {
						disconnected : _disconnected_UIcallback
					});					
					
					mLogger.EventLog("["+interactionInfo2.interactionId + "] is changed.");
				}
			}
			
			if(data.interactionsRemoved && data.interactionsRemoved.length > 0)
			{
				interactionInfos = data.interactionsRemoved;
				for(var idx3 in interactionInfos){	
					var interactionInfo3 = data.interactionsRemoved[idx3];
					var attributes = {isRemoved : true}; 
					mQueue.runQueueLifeCycle(interactionInfo3, attributes, {
						disconnected : _disconnected_UIcallback
					});					
					mLogger.EventLog("["+interactionInfo3+ "] is removed.");
				}
			}			
			mQueue.setActiveInteraction(consultee_callback, consultant_callback, combination_callback);
			
			//★★★★★★★★★★★
			//mLogger.ConsoleLog(_moduleName, "The length of the consulteeStack is "+_consulteeStack.length);
			//mLogger.ConsoleLog(_moduleName, "The length of the _consultantStack is "+_consultantStack.length);
			
			/*
			var userStat = mUser.getMyUserInfo().statusId;
			var callStat = mQueue.getConsultee().Eic_CallState;
			mUIController.setAgentStatus(userStat, callStat);
			*/
		}
	};
	
	var clientCustomStatusChange = function(){
		var user = mUser.getMyUserInfo();
		var userStat = user.statusId;
		
		var consultee = mQueue.getConsultee();
		var consultant = mQueue.getConsultant();
		var callStat = consultee.Eic_CallState;
		
		if( user.isACWable && !consultee.id && !consultant.id){
			mUser.setUserStatus(mConfig.ICWS_ACW);			
			user.isACWable = false;
			mLogger.ConsoleLog(_moduleName, "There is no active interaction, so your user-status goes to ACW");
		}
		else if(userStat == "AcdAgentNotAnswering")
		{
			mUser.setUserStatus(mConfig.ICWS_ACW);
		}
		else
		{				
			mUIController.setAgentStatus(userStat, callStat);
		}
	}
	
	mErrorHandler.ICWS_ERROR_Callbacks["setActiveInteraction"] = function(){ mQueue.setActiveInteraction(consultee_callback, consultant_callback, combination_callback); };
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		messageCallbacks 				: messageCallbacks,
		clientCustomStatusChange	: clientCustomStatusChange
	};
});