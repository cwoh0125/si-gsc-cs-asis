/**
 * Module Name : mUIController for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.07.21
 * Last Updated : 2015.12.23
 */

define(["./mLogger", "./mConfig"],
	function(mLogger, mConfig){
	var _moduleName = "mUIController";

	var $btns 			= mConfig.$btns;
	var $statusImgs		= mConfig.$statusImgs;
	var CAPA				= mConfig.CAPA;
	
	var _available_state = CAPA.DISCONNECT+CAPA.TRANSFER + CAPA.RECORD + CAPA.PAUSE + CAPA.PICKUP + CAPA.HOLD;
	var _available_consult_state = CAPA.DISCONNECT+ CAPA.CONSULT+CAPA.JOIN+CAPA.CONFERENCE;
	
	//My Status UI Controller
	var _agentStatus = 0;
	var _allowedUserStats = { "Available" : 1, "Follow Up" : 2, "Away from desk" : 4, "At a training session" : 2048, "In a meeting" : 1024, "At Lunch" : 512, "At Rest" : 4096, "Gone Home" : 8,  "On Phone" : 128, "On Phone Outbound" : 128, "Available, No ACD" : 256};
	var _allowedCallStats = {"Proceeding" : 16, "Dialing" : 16, "Offering" : 32, "Alerting" : 32, "On Hold" : 64, "Connected" : 128};	

	var _login_callbacak = window.fn_softphone_setLogin;
	var _logout_callback = window.fn_softphone_setLogout;
	
	var setAgentStatus = function(userStat, callStat){
		var agentStatus = 0;
		if(callStat)
		{
			agentStatus = _allowedCallStats[callStat];
			jQuery("#line_stat").val(callStat);
		}			
		else if(userStat)
		{
			agentStatus = _allowedUserStats[userStat];
			jQuery("#line_stat").val(userStat);
			
			
		}		
		else
		{
			agentStatus = 8;
		}
		
		if(_agentStatus != agentStatus){
			
			var isTimerReset = false;
			
			if(agentStatus == 64)
			{
				if(_agentStatus != 128) isTimerReset = true;
			}
			else if(agentStatus == 128)
			{
				if(_agentStatus != 64) isTimerReset = true;
			}
			else
			{
				isTimerReset = true;
			}
			
			_agentStatus = agentStatus;
			if(isTimerReset) window.startTimerAgent();
		}		
	};	
	
	//Button UI Controller
	
	var checkStatusUI = function(statusMessages){
		if( !(statusMessages instanceof Object) ) return false;
		for(var btn in $btns.status){
			//console.log(btn);
			if(statusMessages.containsKey($btns.status[btn].statusId)){
				jQuery("#"+$btns.status[btn].UIID).prop("disabled", false)
														 .css("background-color", "white" );
			}
		}
		return true;
	};	
	
	var offAllBtns = function(){
		for(var btn1 in $btns.interaction){
			jQuery("#"+$btns.interaction[btn1].UIID).prop("disabled", true)
			 .css("background-color", "gray" );			
		}
		for(var btn2 in $btns.status){
			jQuery("#"+$btns.status[btn2].UIID).prop("disabled", true)
			 .css("background-color", "gray" );			
		}
	};
	
	var interactionBtnChangeByCallState = function(func_capabilities, call_state){
		var marker = func_capabilities & _available_state;
		//console.log(marker.toString(2));
		for(var btn in $btns.interaction){
			if($btns.interaction[btn].controlBy != "manual")
			{
				var activity = (marker & $btns.interaction[btn].bit) == $btns.interaction[btn].bit ? true : false;
				var versus_activity = false;
				
				if($btns.interaction[btn].vs_bit) versus_activity = (marker & $btns.interaction[btn].vs_bit) == $btns.interaction[btn].vs_bit ? true : false;				
				
				$btns.interaction[btn].activity = activity && !versus_activity;
				jQuery("#"+$btns.interaction[btn].UIID).prop("disabled", !($btns.interaction[btn].activity))
																.css("background-color", $btns.interaction[btn].activity ? "white" : "gray" );
			}
			else
			{
				switch(btn)
				{
					case "pickup" :
						var pickup_maker	=	func_capabilities & CAPA.PICKUP;
						var pickup_activity = false;
						
						pickup_activity = (call_state == "Alerting" &&  ( (pickup_maker & $btns.interaction["pickup"].bit) == $btns.interaction["pickup"].bit ));
						
						$btns.interaction["pickup"].activity = pickup_activity;
						jQuery("#"+$btns.interaction["pickup"].UIID).prop("disabled", !($btns.interaction["pickup"].activity))
																		.css("background-color", $btns.interaction["pickup"].activity ? "white": "gray" );
						break;
				}
			}
		}
	};	
	
	var statusBtnChangeByUserStatus = function(myUser){
		var statusId = myUser.statusId;
		var isOnPhone = myUser.onPhone;
		if(isOnPhone || statusId == "On Phone" || statusId == "On Phone Outbound"){
			for(var btn in $btns.status){
				jQuery("#"+$btns.status[btn].UIID).css("background-color","gray");
			}				
			jQuery("#"+$btns.connection["disconnect"].UIID).css("background-color","gray");
		}else{
			for(var btn in $btns.status){
				if($btns.status[btn].statusId == statusId)	jQuery("#"+$btns.status[btn].UIID).css("background-color","#ADD8E6");				
				else												jQuery("#"+$btns.status[btn].UIID).css("background-color","white");
			}
			jQuery("#"+$btns.connection["disconnect"].UIID).css("background-color","#F4A460");			
		}
	};
	
	var transferBtnChangeByConsults = function(consultee_func_capabilities, consultant_func_capabilities){
		var consult_marker = consultee_func_capabilities & CAPA.CONSULT;
		var transfer_marker = consultee_func_capabilities & CAPA.TRANSFER;
		var consultant_marker = consultant_func_capabilities & _available_consult_state;
		
		$btns.consult["disconnect"].activity = (consultant_marker & $btns.consult["disconnect"].bit) == 0 ? false : true;
		jQuery("#"+$btns.consult["disconnect"].UIID).prop("disabled", !($btns.consult["disconnect"].activity))
														.css("background-color", $btns.consult["disconnect"].activity ? "white" : "gray");

		$btns.consult["consult"].activity = consultant_func_capabilities != 0 || consult_marker == 0 ? false : true;
		jQuery("#"+$btns.consult["consult"].UIID).prop("disabled", !($btns.consult["consult"].activity))
														.css("background-color", $btns.consult["consult"].activity ?  "white" : "gray" );
		
		$btns.consult["grp_transfer"].activity = consultant_func_capabilities != 0 || transfer_marker == 0 ? false : true;
		jQuery("#"+$btns.consult["grp_transfer"].UIID).prop("disabled", !($btns.consult["grp_transfer"].activity))
														.css("background-color", $btns.consult["grp_transfer"].activity ?  "white" : "gray" );

	};	

	var conferencableBtnChangeByCallers = function(consultee_func_capabilities, consultant_func_capabilities){
		var consultee_marker = consultee_func_capabilities & CAPA.CONFERENCE;
		var consultant_marker = consultant_func_capabilities & CAPA.CONFERENCE;
		
		var consultee_activity = (consultee_marker & CAPA.CONFERENCE) == 0 ? false : true;
		var consultant_activity = (consultant_marker & CAPA.CONFERENCE) == 0 ? false : true;		
		
		var conference_activity = consultee_activity && consultant_activity;
		
		$btns.conference["create"].activity = conference_activity;
		
		jQuery("#"+$btns.conference["create"].UIID).prop("disabled", !($btns.conference["create"].activity))
														.css("background-color",  $btns.conference["create"].activity ? "white" : "gray");		
	};

	//Agent Status List UI Controller
	var getAgentStatusImg = function(statusId, onPhone){
		var img = "";
		
		if(onPhone){
			img = $statusImgs.onPhone;
		}else{
			switch(statusId){
			case	"Available"				:
						img = $statusImgs.available;
						break;			
			case	"Away from desk"		:
						img = $statusImgs.awayfromdesk;
						break;
			case	"At Lunch"		:
						img = $statusImgs.atLunch;
						break;
			case	"In a meeting"		:
						img = $statusImgs.inMeeting;
						break;
			case	"At a training session"		:
						img = $statusImgs.atTraining;
						break;
			case	"At Rest"		:
						img = $statusImgs.atRest;
						break;
			case	"Available, No ACD"	:						
			case	"Follow Up"				:
						img = $statusImgs.followup;
						break;									
			case	"On Phone"				:
			case	"On Phone Outbound"	:
						img = $statusImgs.onPhone;
						break;								
			}
		}
		return img;
	};
	
	var initAgentStatusList = function(){
		var $agentsDiv = jQuery(".call_status_body");
		$agentsDiv.find("tr:gt(0)").remove();				  
	};

	var updateAgentStatusList = function(myId, agentList){
		var $agentsDiv = jQuery(".call_status_body");
		
		for(var agent in agentList){
			var $agentDiv = $agentsDiv.find("#"+agentList[agent].extension);
			var agentStatusImg = getAgentStatusImg(agentList[agent].statusId, agentList[agent].onPhone);
			
			if(myId != agent && (agentList[agent].loggedIn && agentList[agent].statusId != "Gone Home")){
				var $agentDiv = $agentsDiv.find("#"+agentList[agent].extension);
				if($agentDiv.size() < 1){
					window.cf_call_append_call_status(agentList[agent].id, agentList[agent].extension, agentList[agent].name, agentList[agent].statusId, agentList[agent].workgroups);
				}else{
					window.cf_call_change_call_status($agentDiv, agentList[agent].extension, agentList[agent].name, agentList[agent].statusId, agentList[agent].workgroups);
				}
			}else{
				window.cf_call_remove_call_status($agentDiv);
				
			}
		}		
	};	

	var makeAgentGroupCombo = function(agent_grp_list){
		//jQuery("#agent_grp_combo").html("<option value='' extension=''>::전체::</option>");
		
		var cnt = agent_grp_list.length;
		for(var idx=(cnt-1); idx >= 0; idx--)
		{
			jQuery("#agent_grp_combo").append("<option extension='"+agent_grp_list[idx].grp_extension+"' value='"+agent_grp_list[idx].grp_no+"'>"+agent_grp_list[idx].grp_nm+"</option>");
		}
		
		/*
		for(var idx in agent_grp_list){
			jQuery("#agent_grp_combo").append("<option extension='"+agent_grp_list[idx].grp_extension+"' value='"+agent_grp_list[idx].grp_no+"'>"+agent_grp_list[idx].grp_nm+"</option>");
		}
		*/
	};
	
	var overrideLoadingBar = function(UIID){
		//jQuery("#"+UIID).parent().find(".softphoneUILoad").css("display", "");	
	};
		
	var stripAwayLoadingBar = function(UIID){
		//jQuery("#"+UIID).parent().find(".softphoneUILoad").css("display", "none");
	};
	
	var closeAnswerPop = function(callid){
		var pickupCallid = window.getPickUpCallid();
		if(pickupCallid == callid){
			jQuery("#softphone_answer_div").css("display", "none");
		}
	};

	var setLoginAfterStatus = function(){
		jQuery("#line_stat").val("login");
		setTimeout("")
		jQuery("#"+mConfig.$btns.status["atRest"].UIID).click();
	};
	
	var updateWaitQCnt = function(cnt){
		var $wait_q_cnt = jQuery("#wait_q_cnt");
		
		//if(cnt > 4) $wait_q_cnt.parent().css("background-image", "url('./static/call/img_theme2/cti/_call_entered_box_red.png')");
		//else		 $wait_q_cnt.parent().css("background-image", "url('./static/call/img_theme2/cti/_call_entered_box_green.png')");
			
		$wait_q_cnt.html(cnt);
	};
	
	var loginActivity = function(){
		jQuery("#line_stat").val("login");
		
		jQuery("#li_Login").css("display", "none").find("#"+$btns.connection["connect"].UIID).css("background-color","gray");
		jQuery("#li_Logout").css("display", "").find("#"+$btns.connection["disconnect"].UIID).css("background-color","white");		
		
		jQuery("#"+$btns.etc["agent_list"].UIID).css("background-color", "white");

		if(_login_callbacak instanceof Function)
		{
			_login_callbacak();
		}
		//jQuery("#BTN_NotReady1").attr("src", $btns.status["awayFromDesk"].imgs.on);
		//jQuery("#BTN_MainNotReady").prop("disabled", false);
	};
	
	var logoutActivity = function(){
		offAllBtns();
		window.restartStatusTimer();
		
		jQuery("#line_stat").val("logout");
		jQuery("#li_Login").css("display", "").find("#"+$btns.connection["connect"].UIID).css("background-color","white");
		jQuery("#li_Logout").css("display", "none").find("#"+$btns.connection["disconnect"].UIID).css("background-color","gray");
		jQuery("#"+$btns.etc["agent_list"].UIID).css("background-color","gray");

		
		if(_logout_callback instanceof Function)
		{
			_logout_callback();
		}
		
		//jQuery("#BTN_NotReady1").attr("src", $btns.status["awayFromDesk"].imgs.off);
		//jQuery("#BTN_MainNotReady").prop("disabled", true);
		//jQuery("#call_status_dialog .close").click();
		
		updateWaitQCnt(0);
	};
		
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return {		
		setAgentStatus							:	setAgentStatus,
		checkStatusUI							:	checkStatusUI,		
		initBtns									:	offAllBtns,
		interactionBtnChangeByCallState		:	interactionBtnChangeByCallState,
		statusBtnChangeByUserStatus		:	statusBtnChangeByUserStatus,
		transferBtnChangeByConsults			:	transferBtnChangeByConsults,
		conferencableBtnChangeByCallers	:	conferencableBtnChangeByCallers,
		loginActivity								:	loginActivity,
		logoutActivity							:	logoutActivity,
		initAgentStatusList						:	initAgentStatusList,
		updateAgentStatusList					:	updateAgentStatusList,
		makeAgentGroupCombo				:	makeAgentGroupCombo,
		overrideLoadingBar					:	overrideLoadingBar,
		stripAwayLoadingBar					:	stripAwayLoadingBar,
		closeAnswerPop						:	closeAnswerPop,
		setLoginAfterStatus					:	setLoginAfterStatus,
		updateWaitQCnt						:	updateWaitQCnt
	};
});