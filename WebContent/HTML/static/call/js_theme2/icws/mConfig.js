/**
 * Module Name : mConfig for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.04.28
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
									imgs: { on : "./static/call/img_theme2/cti/i_cti1_on.gif", off : "./static/call/img_theme2/cti/i_cti1_off.gif", active : "./static/call/img_theme2/cti/i_cti1_over.gif"  }
									},
				disconnect	:	{UIID:"BTN_CTI_Logout", activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_cti2_on.gif", off : "./static/call/img_theme2/cti/i_cti2_off.gif", active : "./static/call/img_theme2/cti/i_cti2_over.gif"  }
									}
			},
			interaction : {

				disconnect	:	{UIID:"BTN_Release", UIType:"", bit:2, activity:false, controlBy : "auto",
									imgs: { on : "./static/call/img_theme2/cti/i_cti4_on.gif", off : "./static/call/img_theme2/cti/i_cti4_off.gif", active : "./static/call/img_theme2/cti/i_cti4_over.gif" 	}
									},
				hold			:	{UIID:"BTN_Hold", UIType:"", bit:4, activity:false, controlBy : "manual",
									imgs: { on : "./static/call/img_theme2/cti/i_cti5_on.gif", off : "./static/call/img_theme2/cti/i_cti5_off.gif", active : "./static/call/img_theme2/cti/i_cti5_over.gif" 	}
									},
				unhold		:	{UIID:"BTN_UnHold", UIType:"", bit:260, activity:false, controlBy : "manual",
									imgs: { on : "./static/call/img_theme2/cti/i_cti6_on.gif", off : "./static/call/img_theme2/cti/i_cti6_off.gif", active : "./static/call/img_theme2/cti/i_cti6_over.gif" 	}
									},									
				pickup		:	{UIID:"BTN_PickUp", UIType:"", bit:256, activity:false, controlBy : "manual",
									imgs: { on : "./static/call/img_theme2/cti/i_cti16_on.gif", off : "./static/call/img_theme2/cti/i_cti16_off.gif", active : "./static/call/img_theme2/cti/i_cti16_over.gif" 	}
									},
				transfer		:	{UIID:"BTN_Transfer", UIType:"", bit:4096, activity:false, controlBy : "auto",
									imgs: { on : "./static/call/img_theme2/cti/i_cti12_on.gif", off : "./static/call/img_theme2/cti/i_cti12_off.gif", active : "./static/call/img_theme2/cti/i_cti12_over.gif" 	}
									}
			},
			consult : {
				dial			:	{UIID:"BTN_Consult", UIType:"", bit:2, activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_cti13_on.gif", off : "./static/call/img_theme2/cti/i_cti13_off.gif", active : "./static/call/img_theme2/cti/i_cti13_over.gif" 	}
									},
				disconnect	:	{UIID:"BTN_CancelConsult", UIType:"", bit:2, activity:false, 
									imgs: { on : "./static/call/img_theme2/cti/i_cti14_on.gif", off : "./static/call/img_theme2/cti/i_cti14_off.gif", active : "./static/call/img_theme2/cti/i_cti4_over.gif" 	}
									}
			},
			conference : {
				create 		:	{UIID:"BTN_Conference", UIType:"", bit:32768, activity:false,
									imgs: { on : "./static/call/img_theme2/cti/i_cti17_on.gif", off : "./static/call/img_theme2/cti/i_cti17_off.gif", active : "./static/call/img_theme2/cti/i_cti17_over.gif" 	}
									}
			},
			status : {
				available			:	{UIID:"BTN_Ready", statusId:"Available" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_cti7_on.gif", off : "./static/call/img_theme2/cti/i_cti7_off.gif", active : "./static/call/img_theme2/cti/i_cti7_over.gif"  }
										},
				followUp			:	{UIID:"BTN_Work", statusId:"Follow Up" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_cti11_on.gif", off : "./static/call/img_theme2/cti/i_cti11_off.gif", active : "./static/call/img_theme2/cti/i_cti11_over.gif"  }
										},										
				awayFromDesk	:	{UIID:"BTN_NotReady", statusId:"Away from desk" ,activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_cti8_on.gif", off : "./static/call/img_theme2/cti/i_cti8_off.gif", active : "./static/call/img_theme2/cti/i_cti8_over.gif"  }
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
										}
										
			},
			etc		: {
				dial				: 	{UIID:"BTN_Dial", UIType:"", activity:false},
				agent_list			:	{UIID:"BTN_CallStatus", activity:false,
										imgs: { on : "./static/call/img_theme2/cti/i_cti9_on.gif", off : "./static/call/img_theme2/cti/i_cti9_off.gif", active : "./static/call/img_theme2/cti/i_cti9_over.gif"  }
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
	
	var getMsgStat = function(statusId){
		statusId = statusId +"";
		var msgStat;
		switch(statusId){
			case	"0"	:
				//로그인
				msgStat = 0;
				break;
			case	"1"	:
				//로그아웃
				msgStat = -1;
				break;
			case	"20":
				//이석
				msgStat = 1;
				break;
			case	"21":
				//교육
				msgStat = 10;
				break;
			case	"22":
				//회의
				msgStat = 6;
				break;
			case	"23":
				//식사
				msgStat = 9;
				break;
			case	"24":
				//휴식
				msgStat = 5;
				break;
			case	"3"	:
				//대기
				msgStat = 11;
				break;
			case	"4"	:
				//통화중
				msgStat = 4;
				break;
			case	"5"	:
				//작업중(후처리)
				msgStat = 2;
				break;
			default :
				msgStat = -1;
		}
		return msgStat;
	};
	//property get/set 
	//ICWS_BY_KITAELEE
	return{
		ICWS_URI_SCHEME : "http://",
		ICWS_URI_PORT : "8018",
		ICWS_URI_PATH : "/icws",
		ICWS_MEDIA_TYPE : 'application/vnd.inin.icws+JSON',
		ICWS_MEDIA_CHARSET : 'charset=utf-8',
		ICWS_REQUEST_TIMEOUT_MS : 10000,
		ICWS_SERVER : "voc.ubicus.co.kr",
		ICWS_APP_NAME : "ICWS_WITH_SMART_AGENT",
		ICWS_MESSAGE_RETRIEVAL_INTERVAL_MS : 1000,
		ICWS_ACW : "Follow Up",
		ICWS_OCW : { IB : "On Phone", OB : "On Phone Outbound" },		
		ICWS_ORIGIN : "http://voc.ubicus.co.kr",
		CAPA			: CAPA,
		$btns			: $btns,
		$statusImgs	: $statusImgs,
		getMsgStat	: getMsgStat		
	};
});