/**
 * Module Name : mQueue for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2016.01.18
 */

define(["./mLogger", "./mConfig", "./mUtil", "./mErrorHandler", "./mConference", "./mUser"], function(mLogger, mConfig, mUtil, mErrorHandler, mConference, mUser){
	var _moduleName = "mQueue";	
	
	var queueType = {
		SYSTEM			: 0,
		USER				: 1,
		WORKGROUP	: 2,
		STATION			: 3
	};

	var _applicationQueue;
	
	var _consulteeStack;
	var _consultantStack;
	var _recordingStack;
	
	var _conferenceQeueu;

	var _activeConsultee;
	var _activeConsultant;
	
	var initQueue = function(){
		_applicationQueue = new mUtil.Map();
		_consulteeStack = [];
		_consultantStack = [];
		_activeConsultee = {};
		_activeConsultant = {};
		_conferenceQeueu = {
				id : undefined,
				members : {}
		};
	};
	
	var conferenceCheck = function(interactionId, attributes, callbacks){
		var check = false;
		if(mUtil.isVarValidated(attributes.Eic_ConferenceId)){
			
			if(_conferenceQeueu.id == attributes.Eic_ConferenceId){
			
				mLogger.ConsoleLog(_moduleName,"This conference,"+attributes.Eic_ConferenceId+", is already existing.");
			}
			else{
				_conferenceQeueu.id = attributes.Eic_ConferenceId;
				
				mLogger.ConsoleLog(_moduleName, "A conference is created by interactionId, "+interactionId+".");
				mLogger.ConsoleLog(_moduleName, "The ID of the conference is "+attributes.Eic_ConferenceId+".");	
			}
			
			if(mUtil.isVarValidated(attributes.conferenceParentId)){
				
				if(callbacks["child"] instanceof Function) callbacks["child"](attributes, interactionId);
				check = true;
			}else{
				
				if(callbacks["parent"] instanceof Function) callbacks["parent"](attributes.Eic_ConferenceId, interactionId);
				check = true;
			}
		}
		mLogger.ObjectLog(_conferenceQeueu);
		return check;
	};

	var checkUIRole = function(isParent, interactionId){
		var UIRole;
		if(isParent){
			UIRole = "me";
		}else if(mConference.ConferenceUI.getOtherMember() != null){
			UIRole = mConference.ConferenceUI.getOtherMember(interactionId);
		}
		return UIRole;
	};
	
	var getConferenceParentId = function(){		
		var id = null;
		for(var member in _conferenceQeueu.members){			
			if(_conferenceQeueu.members[member].role == "parent"){
				id =  _conferenceQeueu.members[member].id;
				break;
			}
		}
		return id;
	};
	
	var getConferenceMemberFromUIRole = function(_UIRole){		
		var _member = null;
		for(var member in _conferenceQeueu.members){			
			if(_conferenceQeueu.members[member].UIRole == _UIRole){
				_member =  _conferenceQeueu.members[member];
				break;
			}
		}
		return _member;
	};
	
	var createConferenceQueue = function(confereneceId, interactionId, UICallback){
		_conferenceQeueu.members[interactionId] = {
				id		: interactionId,
				role	: "parent",
				UIRole : checkUIRole(true, interactionId)
		}
		if(UICallback instanceof Function) UICallback(_conferenceQeueu);
		mLogger.ConsoleLog(_moduleName, "This interactionId, "+interactionId+", is a parent of the conference, "+confereneceId+".");
	};

	var addToConferenceQueue = function(attributes, interactionId, UICallback){

		if(_conferenceQeueu.id == attributes.Eic_ConferenceId){
			if(_conferenceQeueu.members[interactionId]){
				mLogger.ConsoleLog(_moduleName, "This interactionId, "+interactionId+", is already in the conference, "+_conferenceQeueu.id+".");
			}else{
				_conferenceQeueu.members[interactionId] = {
					id		: interactionId,
					parent  : attributes.conferenceParentId,
					role	: "child",
					UIRole : checkUIRole(false, interactionId)
				};
				if(UICallback instanceof Function) UICallback(_conferenceQeueu);
				mLogger.ConsoleLog(_moduleName, "This interactionId, "+interactionId+", is a child of the conference, "+_conferenceQeueu.id+".");
				mLogger.ConsoleLog(_moduleName, "The parentInteraction of this interaction is "+attributes.conferenceParentId+".");
			}
		}else{
			mLogger.ConsoleLog("This interaction, "+interactionId+", is not in the conference, "+_conferenceQeueu.id+".");	
		}
	};

	var removeFromConferenceQueue = function(attributes, interactionId, UICallback){
		if(attributes.Eic_ConferenceId == _conferenceQeueu.id){
			delete _conferenceQeueu.members[interactionId];
			if(UICallback instanceof Function) UICallback();
			mLogger.ConsoleLog(_moduleName, "This interactionId, "+interactionId+", is removed from the conference, "+attributes.Eic_ConferenceId+".");
			mLogger.ConsoleLog(_moduleName, "The parentInteraction of this interaction is "+attributes.conferenceParentId+".");				
		}
	};

	var removeConferenceQueue = function(conferenceId, interactionId, UICallback){
		if(conferenceId == _conferenceQeueu.id){
			_conferenceQeueu =  {
					id : undefined,
					members : {}
			};
			mConference.ConferenceUI.init();
			if(UICallback instanceof Function) UICallback();
			mLogger.ConsoleLog(_moduleName, "The conference is removed.");
			mLogger.ConsoleLog(_moduleName, "The ID of the conference is "+conferenceId+".");				
		}
	};	
	
	var createApplicationQueue = function(){
		initQueue();
	};
	
	var getApplicationQueue = function(){
		return _applicationQueue;
	};

	var getConsultee = function(){
		return _activeConsultee;
	};

	var getConsultant = function(){
		return _activeConsultant;
	};
	
	var getInteractionInfo = function(interactionId){
		var interactionInfo = _applicationQueue.get(interactionId);
		if( !(interactionInfo instanceof Object) )  interactionInfo = {};
		return interactionInfo;
	};

	var classifyToStack  = function(interactionId, attributes){
		//mLogger.ObjectLog(attributes);
		var interaction	= {};
		interaction.id = interactionId;
		
		mLogger.ConsoleLog(_moduleName, interactionId +" is classified to Consultee ");
		if(_consulteeStack.length > 0) interaction.prev = _consulteeStack[_consulteeStack.length-1].id;
		_consulteeStack.push(interaction);		
	};

    var setConsulteeRetrievable = function(attributes){
        var consultee, consultee_id;
        var stateString = attributes["Eic_CallStateString"] || "";
         if(stateString.indexOf("Remote Disconnect") > -1 || stateString.indexOf("Local Disconnect") > -1)
         {
            consultee_id = attributes["Eic_ConsultingCallId"];
            consultee = getInteractionInfo(consultee_id);
            if(consultee) consultee.isRetrievable = true;
            mLogger.ConsoleLog(_moduleName, "try to set retrievable for "+consultee_id);
         }
    };
	var eliminateFromStack = function(interactionId, attributes){

		 var passbyConsulteeStack = [];
		 var passbyConsultantStack = [];
		 
		 //mLogger.ConsoleLog("Before eliminating stacks : ");
		 //mLogger.ConsoleLog(_consulteeStack.join(","));
		 
		 var len1 = _consulteeStack.length;
		 for(var idx1 = len1-1; idx1>-1; idx1--){
			 mLogger.ConsoleLog("stackId="+_consulteeStack[idx1].id+" VS. interactionId="+interactionId);
			 if(_consulteeStack[idx1].id == interactionId){
				 _consulteeStack.pop();
				 mLogger.ConsoleLog(_moduleName, interactionId+" is eliminated from consultee-stacks.");
				 break;				 
			 }else{
				 passbyConsulteeStack.push(_consulteeStack.pop());
			 }
		 }
		 if(passbyConsulteeStack.length>0) _consulteeStack = _consulteeStack.concat(passbyConsulteeStack.reverse());
		 
		 var len2 = _consultantStack.length;
		 for(var idx2 = len2-1; idx2>-1; idx2--){
			 if(_consultantStack[idx2].id == interactionId){
				 _consultantStack.pop();
				 setConsulteeRetrievable(attributes);
				 mLogger.ConsoleLog(_moduleName, interactionId+" is eliminated from consultant-stacks.");
				 break;
			 }else{
				 passbyConsultantStack.push(_consultantStack.pop());
			 }
		 }
		 if(passbyConsultantStack.length>0) _consultantStack = _consultantStack.concat(passbyConsultantStack.reverse());

		 //mLogger.ConsoleLog("After eliminating stacks : ");
		 //mLogger.ConsoleLog(_consulteeStack.join(","));
		 
	};
	

	var moveToConsultant = function(interactionId, attributes){

		 var passbyConsulteeStack = [];
		 var tempConsultee;
		 
		 var len1 = _consulteeStack.length;
		 for(var idx1 = len1-1; idx1>-1; idx1--){
			 mLogger.ConsoleLog("stackId="+_consulteeStack[idx1].id+" VS. interactionId="+interactionId);
			 if(_consulteeStack[idx1].id == interactionId){
				 tempConsultee = _consulteeStack.pop();
				 break;				 
			 }else{
				 passbyConsulteeStack.push(_consulteeStack.pop());
			 }
		 }
		 if(passbyConsulteeStack.length>0) _consulteeStack = _consulteeStack.concat(passbyConsulteeStack.reverse());
		 
		 if(tempConsultee) _consultantStack.push(tempConsultee);
		 
		 mLogger.ConsoleLog(_moduleName, interactionId+" is moved to Consultant.");
	};	
	
	
	var validateQueue = function(qObj){
		var isNotValid = false;		
		if(qObj){
			var callState			= qObj.Eic_CallState;
			var callStateString	= qObj.Eic_CallStateString || "";
			var isRemoved 		= qObj.isRemoved;
			
			if(isRemoved)
			{
				isNotValid = true;
			}
			else
			{
				isNotValid  = ( callState == "Disconnected" || (callState == "Connected" && (callStateString != "Input YYMMDD" && callStateString != "Input Process" && callStateString != "IVR Process" && callStateString != "Connected" && callStateString.indexOf("Assigned") < 0 ) ) );
				
				if(callStateString == "Input Process" && callState =="Connected" )
				{
					attributes = qObj;
					ap_ivr_callback(attributes.Eic_CallId, attributes);
				}
			}
		}else{
			isNotValid = true;
		}
		return !isNotValid;
	};
		
	
	var setCidAndName = function(attributes){
		var regex = /(sip:)?([a-zA-Z0-9]*)@?/;
		var cid = regex.exec(attributes.Eic_RemoteAddress)[2];
		if(!cid) cid = attributes.Eic_RemoteAddress;
		attributes.cid = cid;			
		attributes.whois = mUser.getUserNameFromExtension(cid);
	};
	
	var insertToApplicationQueue = function(interactionId, attributes, _UICallbacks){		
		if(!_applicationQueue) return false;

		attributes["isStateChanged"] = true;	
		_applicationQueue.put(interactionId, attributes);
		
		var callStateString = attributes["Eic_CallStateString"] || "";
		
		if(callStateString.indexOf("Recording") < 0){
			setCidAndName(attributes);
			classifyToStack(interactionId, attributes);
		}else{
			var regex = /(Call:)([0-9]*)?/;
			var recordedInteractionId = regex.exec(callStateString)[2];
			if(recordedInteractionId) _applicationQueue.get(recordedInteractionId)["RecordingInteraction"] = interactionId;
			mLogger.ConsoleLog(_moduleName, interactionId+" RECORDING : " + recordedInteractionId);
		}
	};

	var updateToApplicationQueue = function(interactionId, attributes, _UICallbacks){
		if(!_applicationQueue) return false;
		attributes = attributes || {}
		
		var queueObj = _applicationQueue.get(interactionId);
		
		if("Eic_CallState" in attributes){
			mLogger.ConsoleLog(_moduleName, "before state : "+ queueObj["Eic_CallState"]);
			mLogger.ConsoleLog(_moduleName, "after state : "+ attributes["Eic_CallState"]);
			if(queueObj["Eic_CallState"] != attributes["Eic_CallState"]){
				if( queueObj["Eic_CallState"] == "Alerting" && _UICallbacks["pickedup"] instanceof Function)  _UICallbacks["pickedup"](interactionId);
				queueObj["isStateChanged"] = true; 
			}		
		}
		
		for(var attr in attributes){
			queueObj[attr] = attributes[attr];
		}
		
		setCidAndName(queueObj);
	};
	
	var deleteFromApplicationQueue = function(interactionId, queueObj, _UICallbacks){
		if(!_applicationQueue) return;

		eliminateFromStack(interactionId, _applicationQueue.get(interactionId));
		
		if( queueObj["Eic_CallState"] == "Alerting" && _UICallbacks["pickedup"] instanceof Function)  _UICallbacks["pickedup"](interactionId);			
		
		if(queueObj["isRemoved"]) _applicationQueue.remove(interactionId);
		
		return;
	};

	var runQueueLifeCycle = function(interactionId, attributes, _UICallbacks){
		_UICallbacks = _UICallbacks || {};
		
		mLogger.ConsoleLog(interactionId+"-----------------------------START");
		for(var prop in attributes){			
			mLogger.ConsoleLog(prop +":::" +attributes[prop]);			
		}		
		mLogger.ConsoleLog(interactionId+"-----------------------------END");
		
		if(_applicationQueue.containsKey(interactionId))
		{		
			mLogger.ConsoleLog("Update Queue : "+interactionId);
			updateToApplicationQueue(interactionId, attributes, _UICallbacks);
			
			var queueObj = _applicationQueue.get(interactionId);
			
			if(validateQueue(queueObj))
			{	
				if(attributes.Eic_ConsultingCallId) moveToConsultant(interactionId, attributes || {});
				
				conferenceCheck(interactionId, queueObj, {
					parent	: createConferenceQueue,
					child		: addToConferenceQueue
				});
			}
			else
			{
				mLogger.ConsoleLog("Delete Queue : "+interactionId);
				
				for( var queue_el in queueObj)
				{
					mLogger.ConsoleLog(queue_el +"="+queueObj[queue_el] );		
				}
				
				
				if(_UICallbacks["disconnected"] instanceof Function && !queueObj["isRemoved"])  
				{
					//console.log("=========================================> 타는구문"  );
					_UICallbacks["disconnected"](interactionId, queueObj);	//close pop answer when callid is deleted.
				}
				else 
				{
					//console.log("=========================================> @@@ 안타는 구문"  );
				}
				
				conferenceCheck(interactionId, _applicationQueue.get(interactionId), {
					parent	: removeConferenceQueue,
					child		: removeFromConferenceQueue
				});				
				
				deleteFromApplicationQueue(interactionId, queueObj, _UICallbacks);
			}
		}else
		{
			
			if(validateQueue(attributes))
			{
				mLogger.ConsoleLog("Insert Queue : "+interactionId);
				insertToApplicationQueue(interactionId, attributes, _UICallbacks);		
				
				conferenceCheck(interactionId, attributes, {
					parent	: createConferenceQueue,
					child		: addToConferenceQueue
				});
			}
			else
			{

			}
		}
	};
	
	var setActiveInteraction = function(_consultee_callback, _consultant_callback, _combination_callback){		
		mLogger.ConsoleLog(_moduleName, "The length of the consulteeStack is "+_consulteeStack.length);
		mLogger.ConsoleLog(_moduleName, "The length of the _consultantStack is "+_consultantStack.length);
		var isNoConsultee = false;
		var isNoConsultant = false;
		
		if(_consulteeStack.length > 0){
			var consultee_id = _consulteeStack[_consulteeStack.length-1].id;
			if(_activeConsultee ===  getInteractionInfo(consultee_id)){
				mLogger.ConsoleLog(_moduleName,"This consultee is already activated");
			}else{
				_activeConsultee		= getInteractionInfo(consultee_id);
				_activeConsultee.id 	= consultee_id;
				_activeConsultee.role	= "consultee";
			}
		}else{
			_activeConsultee = {
					id : undefined,
					Eic_CallState : undefined,
					Eic_Capabilities : 0
			};
			
			isNoConsultee = true;
		}
		
		if(_consultantStack.length > 0){			
			var consultant_id = _consultantStack[_consultantStack.length-1].id;
			if(_activeConsultant ===  getInteractionInfo(consultant_id)){
				mLogger.ConsoleLog(_moduleName,"This consultant is already activated");
			}else{
				_activeConsultant		= getInteractionInfo(consultant_id);
				_activeConsultant.id	= consultant_id;
				_activeConsultant.role	= "consultant";
			}
		}else{
			_activeConsultant = {
					id : undefined,
					Eic_CallState : undefined,
					Eic_Capabilities : 0
			};		
			
			isNoConsultant = true;
		}
		
		if(_consultee_callback) _consultee_callback(_activeConsultee.id, _activeConsultee);
		
		if(_consultant_callback) _consultant_callback(_activeConsultant.id, _activeConsultant);
		
		if(_combination_callback) _combination_callback(_activeConsultee.id, _activeConsultee, _activeConsultant.id, _activeConsultant, _conferenceQeueu);
		/*
		if(isNoConsultee && isNoConsultant)
		{
			var currentStatusId = mUser.getMyUserInfo() ? mUser.getMyUserInfo().statusId : "";			
			if( (currentStatusId == mConfig.ICWS_OCW.IB) || (currentStatusId == mConfig.ICWS_OCW.OB) ) mUser.setUserStatus(mConfig.ICWS_ACW);
		}
		*/
		mLogger.ConsoleLog(_moduleName,"Set Active Interaction");
		mLogger.ConsoleLog(_moduleName,"Set Consultee");
		mLogger.ObjectLog(_activeConsultee);		
		mLogger.ConsoleLog(_moduleName,"Set Consultant");
		mLogger.ObjectLog(_activeConsultant);
	};
	
	var checkCallAlive = function(interactionId){
		var attributes = getInteractionInfo(interactionId);
		if(attributes != null && attributes.Eic_CallState == "Connected" || attributes.Eic_CallState == "On Hold") 	return true; 
		else return false;
	};
	
	var ap_ivr_callback = function(interactionId, attributes)
	{
		var ivr_json_data = { "attr_service_gubun" : attributes.attr_service_gubun, 
				"attr_user_pw1" : attributes.attr_user_pw1, 
				"attr_bday1" : attributes.attr_bday1, 
				"attr_sex_cd1" : attributes.attr_sex_cd1, 
				"attr_com_co_div_cd1" : attributes.attr_com_co_div_cd1,
				"attr_authn_sbjt_cd1" : attributes.attr_authn_sbjt_cd1, 
				"attr_tr_phone1" : attributes.attr_tr_phone1, 
				"attr_cphn_head_tphn_no1" : attributes.attr_cphn_head_tphn_no1, 
				"attr_cphn_min_tphn_no1" : attributes.attr_cphn_min_tphn_no1,
				"attr_cphn_fnl_tphn_no1" : attributes.attr_cphn_fnl_tphn_no1,
				"attr_sms_authn_dtime" : attributes.attr_sms_authn_dtime, 
				"attr_cust_no" : attributes.attr_cust_no,
				"attr_authn_no" : attributes.attr_authn_no,
				"attr_agrm_1_yn1": attributes.attr_agrm_1_yn1,
				"attr_agrm_2_yn1": attributes.attr_agrm_2_yn1,
				"attr_agrm_3_yn1": attributes.attr_agrm_3_yn1,
				"attr_agrm_4_yn1": attributes.attr_agrm_4_yn1,
				"attr_agrm_5_yn1": attributes.attr_agrm_5_yn1,
				"attrIVRCode": attributes.attrIVRCode
				};
				window.fn_softphone_setIVR(ivr_json_data);
	}
	
	
	mErrorHandler.ICWS_ERROR_Callbacks["deleteFromApplicationQueue"] = deleteFromApplicationQueue;	
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		QueueType							: queueType,
		getApplicationQueue				: getApplicationQueue,
		getConsultee						: getConsultee,
		getConsultant						: getConsultant,
		getInteractionInfo					: getInteractionInfo,
		createApplicationQueue			: createApplicationQueue,
		runQueueLifeCycle					: runQueueLifeCycle,
		setActiveInteraction				: setActiveInteraction,
		checkCallAlive						: checkCallAlive,
		getConferenceMemberFromUIRole : getConferenceMemberFromUIRole
	};
});