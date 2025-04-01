/**
 * Module Name : mConfig for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2016.04.18
 */

define(function(){

	var CAPA = {
			NONE : 0,
			CONSULT : 1,
			DISCONNECT : 2,
			HOLD : 4,
			LISTEN : 8,
			MESSAGING : 16,
			MUTE : 32,
			PARK : 64,
			PAUSE : 128,
			PICKUP : 256,
			UNHOLD : 260,
			PRIVATE : 512,
			RECORD : 1024,
			REQUESTHELP : 2048,
			TRANSFER : 4096,
			JOIN : 8192,
			OBJECTWINDOW : 16384,
			CONFERENCE : 32768,
			COACH : 65536,
			SUSPENDED : 131072,
			SECURERECORDINGPAUSE : 262144
		};		
	
	var $btns = {
			connection : {
				connect 		:	{UIID:"BTN_CTI_Login", activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_CTI01_on.gif", off : "./static/call/img_theme2/cti/i_CTI01_off.gif", active : "./static/call/img_theme2/cti/i_CTI01_active.gif"  }
									},
				disconnect	:	{UIID:"BTN_CTI_Logout", activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_CTI02_on.gif", off : "./static/call/img_theme2/cti/i_CTI02_off.gif", active : "./static/call/img_theme2/cti/i_CTI02_active.gif"  }
									}
			},
			interaction : {

				disconnect	:	{UIID:"BTN_Release", UIType:"", bit:CAPA.DISCONNECT, activity:false, controlBy : "auto",
									imgs: { on : "./static/call/img_theme2/cti/i_CTI04_on.gif", off : "./static/call/img_theme2/cti/i_CTI04_off.gif", active : "./static/call/img_theme2/cti/i_CTI04_active.gif" 	}
									},
				hold			:	{UIID:"BTN_Hold", UIType:"", bit:CAPA.HOLD, activity:false, controlBy : "auto",  
									imgs: { on : "./static/call/img_theme2/cti/i_CTI05_on.gif", off : "./static/call/img_theme2/cti/i_CTI05_off.gif", active : "./static/call/img_theme2/cti/i_CTI05_active.gif" 	},
									vs_bit : CAPA.PICKUP
									},
				unhold		:	{UIID:"BTN_UnHold", UIType:"", bit:(CAPA.HOLD+CAPA.PICKUP), activity:false, controlBy : "auto",  
									imgs: { on : "./static/call/img_theme2/cti/i_CTI06_on.gif", off : "./static/call/img_theme2/cti/i_CTI06_off.gif", active : "./static/call/img_theme2/cti/i_CTI06_active.gif" 	}
									},									
				pickup		:	{UIID:"BTN_PickUp", UIType:"", bit:CAPA.PICKUP, activity:false, controlBy : "",
									imgs: { on : "./static/call/img_theme2/cti/i_CTI03_on.gif", off : "./static/call/img_theme2/cti/i_CTI03_off.gif", active : "./static/call/img_theme2/cti/i_CTI03_active.gif" 	}
									},
				transfer		:	{UIID:"BTN_Transfer", UIType:"", bit:CAPA.TRANSFER, activity:false, controlBy : "auto",
									imgs: { on : "./static/call/img_theme2/cti/i_cti12_on.gif", off : "./static/call/img_theme2/cti/i_cti12_off.gif", active : "./static/call/img_theme2/cti/i_cti12_active.gif" 	}
									},
				record		:	{UIID:"BTN_Record", UIType:"", bit:CAPA.RECORD, activity:false, controlBy : "auto",
									imgs: { on : "./static/call/img_theme2/cti/i_CTI23_on.gif", off : "./static/call/img_theme2/cti/i_CTI23_off.gif", active : "./static/call/img_theme2/cti/i_CTI23_active.gif" 	},
									vs_bit : CAPA.PAUSE
									},
				unrecord		:	{UIID:"BTN_Unrecord", UIType:"", bit:(CAPA.RECORD+CAPA.PAUSE), activity:false, controlBy : "auto", 
									imgs: { on : "./static/call/img_theme2/cti/i_CTI24_on.gif", off : "./static/call/img_theme2/cti/i_CTI24_off.gif", active : "./static/call/img_theme2/cti/i_CTI24_active.gif" 	}
									}
			},
			consult : {
				consult		:	{UIID:"BTN_Consult", UIType:"", bit:CAPA.CONSULT, activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_cti13_on.gif", off : "./static/call/img_theme2/cti/i_cti13_off.gif", active : "./static/call/img_theme2/cti/i_cti13_active.gif" 	}
									},
				disconnect	:	{UIID:"BTN_CancelConsult", UIType:"", bit:CAPA.DISCONNECT, activity:false, 
									imgs: { on : "./static/call/img_theme2/cti/i_cti14_on.gif", off : "./static/call/img_theme2/cti/i_cti14_off.gif", active : "./static/call/img_theme2/cti/i_cti4_active.gif" 	}
									},
				grp_transfer	:	{UIID:"BTN_Group_Transfer", UIType:"", bit:CAPA.TRANSFER, activity:false, controlBy : "auto",
										imgs: { on : "./static/call/img_theme2/cti/i_cti22_on.gif", off : "./static/call/img_theme2/cti/i_cti22_off.gif", active : "./static/call/img_theme2/cti/i_cti22_active.gif" 	}
									}
			},
			conference : {
				create 		:	{UIID:"BTN_Conference", UIType:"", bit:CAPA.CONFERENCE, activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_cti17_on.gif", off : "./static/call/img_theme2/cti/i_cti17_off.gif", active : "./static/call/img_theme2/cti/i_cti17_active.gif" 	}
									}
			},
			status : {
				available			:	{UIID:"BTN_Ready", statusId:"Available" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI07_on.gif", off : "./static/call/img_theme2/cti/i_CTI07_off.gif", active : "./static/call/img_theme2/cti/i_CTI07_active.gif"  }
										},
				followUp			:	{UIID:"BTN_Work", statusId:"Follow Up" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI08_on.gif", off : "./static/call/img_theme2/cti/i_CTI08_off.gif", active : "./static/call/img_theme2/cti/i_CTI08_active.gif"  }
										},										
				awayFromDesk	:	{UIID:"BTN_NotReady2", statusId:"Away from desk" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI09_on.gif", off : "./static/call/img_theme2/cti/i_CTI09_off.gif", active : "./static/call/img_theme2/cti/i_CTI09_active.gif"  }
										},
				atLunch			:	{UIID:"BTN_NotReady3", statusId:"At Lunch" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI18_on.gif", off : "./static/call/img_theme2/cti/i_CTI18_off.gif", active : "./static/call/img_theme2/cti/i_CTI18_active.gif"  }
										},
				inMeeting		:	{UIID:"BTN_NotReady4", statusId:"In a meeting" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI19_on.gif", off : "./static/call/img_theme2/cti/i_CTI19_off.gif", active : "./static/call/img_theme2/cti/i_CTI19_active.gif"  }
										},
				atTraining		:	{UIID:"BTN_NotReady5", statusId:"At a training session" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI20_on.gif", off : "./static/call/img_theme2/cti/i_CTI20_off.gif", active : "./static/call/img_theme2/cti/i_CTI20_active.gif"  }
										},
				atRest			:	{UIID:"BTN_NotReady6", statusId:"At Rest" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI21_on.gif", off : "./static/call/img_theme2/cti/i_CTI21_off.gif", active : "./static/call/img_theme2/cti/i_CTI21_active.gif"  }
										},
				mail				:	{UIID:"BTN_NotReady7", statusId:"Mail" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI22_on.gif", off : "./static/call/img_theme2/cti/i_CTI22_off.gif", active : "./static/call/img_theme2/cti/i_CTI22_active.gif"  }
										},										
				complaint		:	{UIID:"BTN_NotReady8", statusId:"Complaint" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI23_on.gif", off : "./static/call/img_theme2/cti/i_CTI23_off.gif", active : "./static/call/img_theme2/cti/i_CTI23_active.gif"  }
										},										
				coach			:	{UIID:"BTN_NotReady9", statusId:"Coach" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI24_on.gif", off : "./static/call/img_theme2/cti/i_CTI24_off.gif", active : "./static/call/img_theme2/cti/i_CTI24_active.gif"  }
										}
			},
			etc		: {
				dial				: 	{UIID:"BTN_Dial", UIType:"", activity:false},
				agent_list			:	{UIID:"BTN_CallStatus", activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_CTI10_on.gif", off : "./static/call/img_theme2/cti/i_CTI10_off.gif", active : "./static/call/img_theme2/cti/i_CTI10_active.gif"  }
										}
			}
	};
	
	var $statusImgs = {
			available : "./static/call/img_theme2/cti/agent_stat_1.gif",
			followup : "./static/call/img_theme2/cti/agent_stat_2.gif",
			awayfromdesk : "./static/call/img_theme2/cti/agent_stat_4.gif",
			atLunch : "./static/call/img_theme2/cti/agent_stat_512.gif",
			inMeeting : "./static/call/img_theme2/cti/agent_stat_1024.gif",
			atTraining : "./static/call/img_theme2/cti/agent_stat_2048.gif",
			atRest : "./static/call/img_theme2/cti/agent_stat_4096.gif",
			loggedIn : "./static/call/img_theme2/cti/agent_stat_8.gif",
			dialing	: "./static/call/img_theme2/cti/agent_stat_16.gif",
			proceeding : "./static/call/img_theme2/cti/agent_stat_16.gif",
			alerting	: "./static/call/img_theme2/cti/agent_stat_32.gif",
			hold		: "./static/call/img_theme2/cti/agent_stat_64.gif",
			onPhone	: "./static/call/img_theme2/cti/agent_stat_128.gif",
			onTransfer : "./static/call/img_theme2/cti/agent_stat_256.gif",
			loggedOut : "./static/call/img_theme2/cti/count_off.gif"	
	};	
	
	
	
	var setConfig = function(p_cti_ip, p_cti_port)
	{
		this.ICWS_SERVER = p_cti_ip;			
		this.ICWS_URI_PORT = p_cti_port;   
	}
	
	var getSwitchOverServer = function(server1){
		var server2 = "";
		if(server1 == "192.168.16.21") server2 = "192.168.16.22";
		else									 server2 = "192.168.16.21";

		return server2;
	};
	
	
	//window.loadIdx += 1;
	//console.log("mConfig:" + window.loadIdx);
	//property get/set 
	//ICWS_BY_KITAELEE
	return{
		ICWS_URI_SCHEME : "http://",
		ICWS_URI_PORT : "8018",		
		ICWS_URI_PATH : "/icws",
		ICWS_MEDIA_TYPE : 'application/vnd.inin.icws+JSON',
		ICWS_MEDIA_CHARSET : 'charset=utf-8',
		ICWS_REQUEST_TIMEOUT_MS : 10000,
		ICWS_SERVER : "cic.ubicus.co.kr",
		ICWS_APP_NAME : "ICWS_WITH_SMART_AGENT",
		ICWS_MESSAGE_RETRIEVAL_INTERVAL_MS : 500,
		ICWS_ACW : "Follow Up",
		ICWS_OCW : { IB : "On Phone", OB : "On Phone Outbound" },
		ICWS_ASYNC : true,
		MERGE_DB_AGENT_LIST : false,
		isLoggedIn : false,
		isStationLess	: true,
		CAPA	: CAPA,
		$btns : $btns,
		setConfig : setConfig,
		$statusImgs : $statusImgs,
		getSwitchOverServer	: getSwitchOverServer		
	};
});