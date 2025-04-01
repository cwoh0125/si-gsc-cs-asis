/**
 * Module Name : mView for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.11.10
 */
require.config({
	paths :{
		mConfig : window.mConfig || "mConfig"
		,urlArgs: "_dummy=" + (new Date()).getTime()
	}
});
if(window.console == undefined) {console = { log : function(){}};}
require(
["./mUIHandler", "./mUIController", "./mConfig", "./mUtil", "./mErrorHandler", "./mIVRNotification"],
	function(mUIHandler, mUIController, mConfig, mUtil, mErrorHandler, mIVRNotification){
	var clickedDiv;

	var $btns = mConfig.$btns;

	
/**UI Event Listener**/
	jQuery(document).ready(function(){
		//CTI연결	
		jQuery("#"+$btns.connection["connect"].UIID).on("click", function(){
				if(this.disabled) return false;			
	
				mConfig.setConfig( jQuery("#CTI_server").val(), jQuery("#CTI_server_port").val() );
				
				mUIHandler.login(
						{
							success : function(){
								mUIController.loginActivity();	
								mUIController.setLoginAfterStatus();	
								
								//mErrorHandler.ICWS_ERROR_LIST.limitCnt = 0;
							},
							failure : mUIController.logoutActivity,
							fin : function(){
							}
						}
				);
				return false;
		});
		
		//CTI해제
		jQuery("#"+$btns.connection["disconnect"].UIID).on("click", function(){
				if(this.disabled) return false;
				mUIHandler.logout(
						{
							success : mUIController.logoutActivity,
							fin : function(){
							}
						}
				);
				return false;
		});	
	
		//통화
		jQuery("#BTN_Dial").on("click",function(){
			if(this.disabled) return false;
			window.fn_softphone_makCallr(jQuery("#phone_number").val());
			return false;
		});
		//받기
		jQuery("#"+$btns.interaction["pickup"].UIID).on("click",function(){		
			if(this.disabled) return false;			
			window.fn_softphone_answer();
			return false;
		});
				
		//끊기
		jQuery("#"+$btns.interaction["disconnect"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.disconnectInteraction({
				fin : function(){
				}
			});
			return false;
		});		
		
		//대기
		jQuery("#"+$btns.status["available"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Available", {
				fin : function(){
				}
			});
			return false;
		});
				
		//이석
		jQuery("#"+$btns.status["awayFromDesk"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus($btns.status["awayFromDesk"].statusId, {
				fin : function(){
				}
			});
			return false;
		});
		
		//식사
		jQuery("#"+$btns.status["atLunch"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus($btns.status["atLunch"].statusId, {
				fin : function(){
				}
			});
			return false;
		});
		
		//회의
		jQuery("#"+$btns.status["inMeeting"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus($btns.status["inMeeting"].statusId, {
				fin : function(){
				}
			});
			return false;
		});
		
		//교육
		jQuery("#"+$btns.status["atTraining"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus($btns.status["atTraining"].statusId, {
				fin : function(){
				}
			});
			return false;
		});
		
		//휴식
		jQuery("#"+$btns.status["atRest"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus($btns.status["atRest"].statusId, {
				fin : function(){
				}
			});
			return false;
		});
		
		//업무중
		jQuery("#"+$btns.status["followUp"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Follow Up", {
				fin : function(){
				}
			});
			return false;
		});
		
		//퇴근
		jQuery("#BTN_Temp").on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Gone Home", {
				fin : function(){
				}
			});
			return false;
		});

		
		//메일
		jQuery("#"+$btns.status["mail"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Mail", {
				fin : function(){
				}
			});
			return false;
		});		
		
		//불만
		jQuery("#"+$btns.status["complaint"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Complaint", {
				fin : function(){
				}
			});
			return false;
		});		
		
		//코칭
		jQuery("#"+$btns.status["coach"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.setUserStatus("Coach", {
				fin : function(){
				}
			});
			return false;
		});		
		
		
		//보류
		jQuery("#"+$btns.interaction["hold"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.holdInteraction({
				fin : function(){
				}
			});
			return false;
		});
		
		//보류해제
		jQuery("#"+$btns.interaction["unhold"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.unholdInteraction({
				fin : function(){
				}
			});
			return false;
		});
		
		//녹취시작
		jQuery("#"+$btns.interaction["record"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.startRecordInteraction({
				fin : function(){
				}
			});
			return false;
		});

		//녹취종료
		jQuery("#"+$btns.interaction["unrecord"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.stopRecordInteraction({
				fin : function(){
				}
			});
			return false;
		});		

		//삼자통화
		jQuery("#"+$btns.conference["create"].UIID).on("click",function(event){
			if(this.disabled) return false;
			mUIHandler.createConference({
				fin : function(){
				}
			});
			return false;
		});
		
		jQuery(".BTN_ConfHold").on("click", function(){
			if(this.disabled) return false;
			var interactionId = jQuery(this).siblings(".interactionId").val();
			if(interactionId) mUIHandler.holdConference(interactionId);
			return false;
		});
		
		jQuery(".BTN_ConfUnHold").on("click", function(){
			if(this.disabled) return false;
			var interactionId = jQuery(this).siblings(".interactionId").val();
			if(interactionId) mUIHandler.unholdConference(interactionId);
			return false;
		});
		
		jQuery(".BTN_ConfDis").on("click", function(){
			if(this.disabled) return false;
			var interactionId = jQuery(this).siblings(".interactionId").val();
			if(interactionId) mUIHandler.disconnectConference(interactionId);
			return false;
		});
		
		jQuery("#BTN_IVR").on("click", function(){
			if(this.disabled) return false;
			
			var call_id = jQuery("#call_id").val();
			var ivr_key = jQuery("#ivr_key").val();
			var cust_no = jQuery("#cust_no").val();
			var cust_new_info = jQuery("#cust_new_info").val();
			var agent_id = jQuery("#CTI_userid").val();
			jQuery("#finish_ivr_value_yn").val("N");
			jQuery("#first_value_yn").val("Y");
			
			if(jQuery("#call_id_key").val().indexOf(call_id) == -1)
			{
				call_id = jQuery("#call_id_key").val().substring(0,10);
				alert("콜아이디가 맞지 않아 자동으로 조정합니다.");
			}
			
			mIVRNotification.sendIVR(call_id , ivr_key, cust_no, cust_new_info, agent_id,  
			{
				//콜백이  오지 않는다. connection 연결 이벤트로 오게 된다.
				success : function(data){
					alert("성공"+data);
				},
				failure : function(){
					alert("실패");					
				},				
				fin : function(){
					alert("종료");
				}
			}
			);
			//mUIHandler.sendToIVR();
		});
		
		//수동로그인		
		//jQuery("#cti_passive_login_window").draggable();
		
		jQuery('#BTN_CTI_pLogin').on('click', function(e){
			if(this.disabled) return false;
			mUIHandler.login(jQuery("#CTI_userid").val(), jQuery("#CTI_password").val(), jQuery("#CTI_station").val(),
					{
						success : function(){
							mUIHandler.logoutActivity();
						},
						fin : function(){							
							jQuery("#cti_passive_login_window").css("display", "none");
						}
					});
		});
		
		jQuery('#BTN_CTI_pLogin_cancel').on('click', function(e){
			if(this.disabled) return false;
			jQuery("#cti_passive_login_window").css("display", "none");
		});
		
		
		//initialize conferenceUI
		mUIHandler.ConferenceUI.create("conferenceUI_div");		
		
		//Global Access CTI Function Setting
		window.$interaction = {
				makeCall		: mUIHandler.createInteraction,
				receiveCall	: mUIHandler.pickupInteraction,
				isCallAlive 	: mUIHandler.isCallAlive
				
		};
		
		window.$agents = {				
				getUserNameFromExtension	: mUIHandler.getUserNameFromExtension
		};
		
		window.$transfer = {
			consult					: mUIHandler.consultInteraction,
			speackToConsultant	: mUIHandler.speakToConsultantInteraction,
			speackToConsultee		: mUIHandler.speakToConsulteeInteraction,
			cancelConsult			: mUIHandler.cancelConsultIneraction,
			transfer					: mUIHandler.transferInteraction
		};

		window.$statistics = {
				agent	: mUIHandler.getAgentStatistics,
				queue	: mUIHandler.getQueueStatistics
		};
		//Browser close callback		
		//window.onBeforeUnLoadEvent = false;
		/*
		window.onbeforeunload= function(event){
			if(!(window.onBeforeUnLoadEvent)){
				//window.alert("CTI로부터 로그아웃을 합니다.");
				window.onBeforeUnLoadEvent = true;
				mUIHandler.logout();
			}
			return "i-SmartAgent를 종료합니다.";
		};
		*/
		//execute login automatically after all modules loaded.
		jQuery("#li_Login").prop("disabled", false).css("display", "").find("#"+$btns.connection["connect"].UIID).attr("src",$btns.connection["connect"].imgs.on);
		jQuery("#li_Logout").prop("disabled", true).css("display", "none").find("#"+$btns.connection["disconnect"].UIID).attr("src",$btns.connection["connect"].imgs.off);
		jQuery("#"+$btns.etc["agent_list"].UIID).prop("disabled", true).attr("src",$btns.etc["agent_list"].imgs.off);
		
//		jQuery("#"+$btns.connection["connect"].UIID).click();
		
		jQuery("#cti_error_popup_list").on("click", ".close", function(){
												if(mErrorHandler.ICWS_ERROR_LIST.limitCnt > 0)	mErrorHandler.ICWS_ERROR_LIST.limitCnt--;
												 $(this).parent().parent().remove();												 
											 });
		
		//console.log(mErrorHandler);
		
		
		jQuery('#BTN_Listen_call').on('click', function(e){
			var call_id = jQuery("#listen_call_id").val();			
			mUIHandler.listenInteraction(call_id);

		} );
		
		jQuery('#BTN_Listen_call_cancle').on('click', function(e){
			var call_id = jQuery("#listen_call_id").val();			
			mUIHandler.listenCancelInteraction(call_id);
			
		} );
		
		
		jQuery('#BTN_AgentInfo_Change').on('click', function(e){

			
			var change_agent_id = jQuery("#change_agent_id").val();			
			var change_agent_nm = jQuery("#change_agent_nm").val();			
			var change_workgroup = jQuery("#change_workgroup").val();			
			
			//alert("change_agent_id : "+change_agent_id );
			//alert("change_workgroup : "+change_workgroup );
			
			if(change_agent_id == "")return;
			
			var arr_workgroup = change_workgroup.split(",");
			
			var cnt = arr_workgroup.length;
			var arr_json_workgroup = new Array(cnt);
			for(var idx=0; idx<cnt; idx++)
			{
				var uri = "/configuration/workgroups/" +arr_workgroup[idx];
				arr_json_workgroup[idx] = {id:arr_workgroup[idx], displayName: arr_workgroup[idx], uri:uri };
			}
			
			//아웃바운드시 문제가 있어 displayname은 넘기지 않음.
			//mUIHandler.setUser(change_agent_id, {mailboxProperties:{displayName: change_agent_nm}, workgroups:arr_json_workgroup});
			mUIHandler.setUser(change_agent_id, {workgroups:arr_json_workgroup});
		});
		
		
	});
});
