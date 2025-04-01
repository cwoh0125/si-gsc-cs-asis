/**
 * Module Name : mUIHandler for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2016.01.18
 */

define(["./mStatistics", "./mConnection", "./mUser", "./mQueue", "./mInteraction", "./mConference", "./mConfiguration",
        "./mMessageSubscriber", "./mLogger", "./mConfig", "./mUIController", "./mExternalAjax", "./mErrorHandler"],
	function(mStatistics, mConnection, mUser, mQueue, mInteraction, mConference, mConfiguration,
				mMessageSubscriber, mLogger, mConfig, mUIController, mExternalAjax, mErrorHandler){
	var _moduleName = "mUIHandler";

	
	var extract_Workgroups = function(agentGrpArr){
		mUIController.makeAgentGroupCombo(agentGrpArr);
	};
	
	var extract_Users = function(agentArr){
		mUser.setUpdateUserList_callback(mUIController.updateAgentStatusList);
		if(agentArr.length < 1) agentArr.push({"id":mUser.getMyUserID()});
		mUIController.initAgentStatusList();
		mUser.setUserList(agentArr);

		if(mConfig.MERGE_DB_AGENT_LIST)	mExternalAjax.mergeAgentInfo(mUser.getUserList().map);
		//console.log("DB-GET START---------------------------------------");
		//console.log(mUser.getUserList().map);
		//console.log("DB-GET END-----------------------------------------");
		/* gather statistics for a private user */
		var me = mUser.getMyUserInfo();		
		var myWorkgroups = [];
		for(var queue in me.workgroups)
		{
			myWorkgroups.push(queue);
		}
		mMessageSubscriber.StatisticsValueSubscription.add(myWorkgroups);
		
		/* get all users' status */
		var userIds = [];
		for(var idx in agentArr){
			userIds.push(agentArr[idx].id);
		}
		
		mMessageSubscriber.UserStatusSubscription.add(userIds, function(){
			mUIController.setLoginAfterStatus();	
		});
	};
	
	var extract_StatusMessages = function(statusMessages){		
		mUIController.checkStatusUI(statusMessages);
	};

/**UI Control Functions(View)**/
	
	var login = function(userid, password, stationId, _UICallbacks){
		mConfig.ICWS_ASYNC = false;
		
		mConnection.connect(userid, password, {						
			success : function(){
				mUser.initUser();
				mUser.setMyUserID(userid);
				mUser.startStatusMessages(extract_StatusMessages);
				
				mQueue.createApplicationQueue();
				mMessageSubscriber.SessionSubscription.add([userid]);								

				mMessageSubscriber.QueueContentSubscription.add([{
					queueType : mQueue.QueueType.USER,
					queueName : userid
				}]);						
				//mMessageSubscriber.StatisticsValueSubscription.add(["APGroup", "UbiGroup"]);
				mConfiguration.	getWorkGroups( { "success" : extract_Workgroups } );			
				mConfiguration.getUsers( { "success" : extract_Users } );
				
				if(stationId)
				{
					_UICallbacks["continue_subscribe"] = mMessageSubscriber.startMessagesPolling;
					mConnection.connectStation(stationId,	 _UICallbacks);
				}
				else
				{
					//mMessageSubscriber.startMessagesPolling();
					alert("할당된 IP전화기 정보가 없습니다.");
				}
			},
			fin : _UICallbacks["fin"]				
		});
		
		mConfig.ICWS_ASYNC = true;
	};
	
	var setUser = function(userId, _payload){
		mConfiguration.setUser( userId, _payload );
		
		//mConfiguration.setUser("vivasoul", {mailboxProperties:{displayName: "이기태쿤쿤"}, workgroups:[{id: "HelpGroup", displayName: "HelpGroup", uri: "/configuration/workgroups/UbiGroup"}]})
		
	}
	
	var logout = function(_UICallbacks){		
		if( !(mConnection.isLoggedIn()) ) return false;
		mUser.setUserStatus("Gone Home", {});
		mConnection.disconnect(_UICallbacks);
		mMessageSubscriber.stopMessagesPolling();
		/*
		mUser.setUserStatus("Gone Home", {
			success : mConnection.disconnect(_UICallbacks) ,
			failure : _UICallbacks["fin"],
			fin : mMessageSubscriber.stopMessagesPolling
		})
		*/;
		
		//mConnection.disconnect(_UICallbacks);		
	};	
	
	var ajaxLogin = function(_UICallbacks){
		mExternalAjax.getLoginInfo(login, _UICallbacks);
	};
	
	var checkConn = function(){ 
		mConnection.checkConnection();
	};

	var setUserStatus = function(userStat ,_UIcallbacks){		
		
		var msgs = mUser.getStatusMessages();
		if(msgs.containsKey(userStat)){
			mUser.setUserStatus(userStat, _UIcallbacks);
		}else{
			mLogger.ConsoleLog(_moduleName, userStat+" is not a valid status for this user");
		}
	};

	var getUserStatus = function(){		
		mUser.getUserStatus(display_UserStatusEx);
	};

	var connectionStation = function(station){
		mConnection.connectStation(station, display_Connectionstate);
	};

	var disconnectionStation = function(){
		mConnection.disconnectStation(display_Connectionstate, setStatusToAfterCallWork);
	};

	var createInteraction = function(target, _UICallbacks){
		
		mInteraction.createInteraction(target, _UICallbacks);
	};
	
	var disconnectInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		mInteraction.disconnectInteraction(interactionId, _UICallbacks);
	};

	var holdInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		mInteraction.holdInteraction(interactionId, _UICallbacks);
	};

	var unholdInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		mInteraction.unholdInteraction(interactionId, _UICallbacks);
	};

	var pickupInteraction = function(interactionId, _UICallbacks){
		var interactionId = interactionId || mQueue.getConsultee().id;
		mInteraction.pickupInteraction(interactionId, _UICallbacks); 
	};

	var parkInteraction = function(target, interactionId, _UICallbacks){
		var queueId = mUser.getMyUserID();
		mInteraction.parkInteraction(target, queueId, interactionId, _UICallbacks);
	};

	var blindTransferInteraction = function(target, _UICallbacks){
		var queueId = mUser.getMyUserID();
		var interactionId = mQueue.getConsultee().id;
		mInteraction.blindTransferInteraction(target, queueId, interactionId, _UICallbacks);
	};

	var consultTransferInteraction = function(targetInteractionId, cuurentInteractionId, _UICallbacks){
		mInteraction.consultTransferInteraction(targetInteractionId, cuurentInteractionId, _UICallbacks);
	};
	
	var consultInteraction = function(target, _UICallbacks){
		target = target || "";
		if( target.length > 5) 
		{
			if(target.substring(0,1) == "0")  target = "9"+target;	
		}
		
		var interactionId = mQueue.getConsultee().id;
		mInteraction.consultInteraction(target, interactionId, _UICallbacks);
	};
	
	var cancelConsultIneraction = function(_UICallbacks){
		_UICallbacks = _UICallbacks || {};
		
		var consultant = mQueue.getConsultant();
		var consultee  = consultant.Eic_ConsultingCallId;
		//var consultee = mQueue.getConsultee();
		//mInteraction.cancelConsultInteraction(consultee.id, consultant.id, _UICallbacks);
		
		_UICallbacks.successEx = function(){
			mInteraction.disconnectInteraction(consultant.id);		
			if(mQueue.checkCallAlive(consultee)) 	mInteraction.unholdInteraction(consultee);
		};
		mInteraction.cancelConsultInteraction(consultee, consultant.id, _UICallbacks);
		
	};
	
	var speakToConsultantInteraction = function(_UICallbacks){
		var consultant = mQueue.getConsultant();
		var consultee = mQueue.getConsultee();
		mInteraction.changeAudienceInteraction(consultee.id, consultant.id, true, _UICallbacks);		
	};
	
	var speakToConsulteeInteraction = function(_UICallbacks){
		var consultant = mQueue.getConsultant();
		var consultee = mQueue.getConsultee();
		mInteraction.changeAudienceInteraction(consultee.id, consultant.id, false, _UICallbacks);		
	};	
	
	var transferInteraction = function(target, consult_seq_no, _UICallbacks){		
		
		var consultant = mQueue.getConsultant();
		var consultee = mQueue.getConsultee();
		var payload = {attributes : { IsTransferred:"Y", ConsultSeqNo:consult_seq_no, attStationGroup1:getCmpnyNo()}};
		if(consultant.id){
			//payload.attributes.Eic_Consultee = consultee.id;
			
			mInteraction.postInteraction(consultee.id, payload, {
				success :	function(){
					consultee.IsTransferSent = true;
					consultTransferInteraction(consultee.id , consultant.id, _UICallbacks);
				}					
			});			
		}else{
			mInteraction.postInteraction(consultee.id, payload, {
				success :	function(){
					consultee.IsTransferSent = true;
					blindTransferInteraction(target, _UICallbacks);
				}
			});			
		}
	};
	
	var joinInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		mInteraction.joinInteraction(interactionId, _UICallbacks);
	};

	var createConference = function(_UICallbacks){
		var consulteeId = mQueue.getConsultee().id;
		var consultantId = mQueue.getConsultant().id;
		mConference.createConference([consulteeId, consultantId], _UICallbacks);
	};

	var addConference = function(interactions, currentConference, _UICallbacks){
		mConference.addConference(interactions, currentConference, _UICallbacks);
	};
	
	var disconnectConference = function(interactionId, _UICallbacks){
		mInteraction.disconnectInteraction(interactionId, _UICallbacks);
	};
	
	var holdConference = function(interactionId, _UICallbacks){
		mInteraction.holdInteraction(interactionId, _UICallbacks);
	};

	var unholdConference = function(interactionId, _UICallbacks){
		mInteraction.unholdInteraction(interactionId, _UICallbacks);
	};
	
	var isCallAlive =mQueue.checkCallAlive;
	
	var sendToIVR = function(){
		var interactionId = mQueue.getConsultee().id;
		mExternalAjax.sendToIVR(interactionId);
	};
	
	var startRecordInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		mInteraction.recordInteraction(interactionId, true, false, _UICallbacks);
	};

	var stopRecordInteraction = function(_UICallbacks){
		var interactionId = mQueue.getConsultee().id;
		
		var recordingInteractionID = mQueue.getConsultee()["RecordingInteraction"];
		var recordingFileName = recordingInteractionID ? mQueue.getInteractionInfo(recordingInteractionID)["Eic_RecordFileName"] : "";
		
		mExternalAjax.savePartRecording(interactionId, recordingFileName);
		
		mInteraction.recordInteraction(interactionId, false, false, _UICallbacks);
	};	
	
	var listenInteraction = function(interactionId){
		mInteraction.listenInteraction(interactionId, true, false);
	};

	var listenCancelInteraction = function(interactionId){
		mInteraction.listenInteraction(interactionId, false, false);
	};	
	
	//var getUserStatistics = mUser.getUserStatistics;

	mErrorHandler.ICWS_ERROR_Callbacks["logoutActivity"] = mUIController.logoutActivity;
	mErrorHandler.ICWS_ERROR_Callbacks["autoLogin"] = mUIController.ajaxLogin;
	
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return {
		login								: ajaxLogin,
		logout							: logout,
		checkConn						: checkConn,
		setUserStatus					: setUserStatus,
		getUserStatus					: getUserStatus,
		connectionStation				: connectionStation,
		disconnectionStation			: disconnectionStation,
		createInteraction				: createInteraction,
		disconnectInteraction			: disconnectInteraction,		
		holdInteraction					: holdInteraction,
		unholdInteraction				: unholdInteraction,
		pickupInteraction				: pickupInteraction,
		parkInteraction					: parkInteraction,	
		consultInteraction				: consultInteraction,
		cancelConsultIneraction		: cancelConsultIneraction,
		speakToConsultantInteraction: speakToConsultantInteraction,
		speakToConsulteeInteraction	: speakToConsulteeInteraction,
		transferInteraction				: transferInteraction,
		joinInteraction					: joinInteraction,
		createConference				: createConference,
		addConference					: addConference,
		isCallAlive						: isCallAlive,
		getUserNameFromExtension	: mUser.getUserNameFromExtension,
		disconnectConference			: disconnectConference,
		holdConference					: holdConference,
		unholdConference				: unholdConference,
		getQueueStatistics				: mStatistics.getQueueStatistics,
		getAgentStatistics				: mStatistics.getAgentStatistics,
		ConferenceUI					: mConference.ConferenceUI,
		getQueueList					: mQueue.getApplicationQueue,		
		sendToIVR						: sendToIVR,
		startRecordInteraction			: startRecordInteraction,
		stopRecordInteraction			: stopRecordInteraction,
		listenInteraction				: listenInteraction,
		listenCancelInteraction		: listenCancelInteraction,
		setUser		: setUser
	};
});