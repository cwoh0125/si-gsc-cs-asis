/**
 * Module Name : mInteraction for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.11.10
 */

define(["./mQueue", "./mXhr","./mLogger","./mConfig","./mUtil", "./mErrorHandler"], function(mQueue, mXhr, mLogger, mConfig, mUtil, mErrorHandler){
	var _moduleName = "mInteraction";	
	var default_reqPath = "/interactions";
	var interactionList = new mUtil.Map();
	
	var createInteraction_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "201"){
				var interactionId = data.interactionId;
				mLogger.ConsoleLog(_moduleName, "Interaction Created");
				//mQueue.addToApplicationQueue(interactionId, {consult})
				//mLogger.ConsoleLog(_moduleName, interactionId);
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Create Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interactions", "POST", error.message);
				errHandler.show();
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var createInteraction = function(target, _UICallbacks, attrPairs, workGroup){
		//mLogger.ConsoleLog(_moduleName, "created");
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath
		var	payload = {
			__type				 : "urn:inin.com:interactions:createCallParameters",
			additionalAttributes :  { attStationGroup1:getCmpnyNo() },
			workgroup			 :	workGroup,
			target				 :	target
		//  callMadeStage		 :  callMadeStage
		};
		var externalCallback = createInteraction_Callback(_UICallbacks);
		
		
		console.log("@@@@@@@@@@@@@@@@@@@@@@@ createInteraction");
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	var disconnectInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Disconnected");
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](currentInteraction);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Disconnect Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);			
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var disconnectInteraction = function(currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/disconnect";
		var	payload = {};
		var externalCallback = disconnectInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var holdInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Held");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](currentInteraction);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Hold Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var holdInteraction = function(currentInteraction, _UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/hold";
		var	payload = {on : true};
		var externalCallback = holdInteraction_Callback(currentInteraction, _UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var unholdInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
				
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction UnHeld");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](currentInteraction);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction UnHold Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var unholdInteraction = function(currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/hold";
		var	payload = {on : false};
		var externalCallback = unholdInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var pickupInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
				
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Picked Up");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](currentInteraction);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Pickup Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var pickupInteraction = function(currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/pickup";
		var payload = {on : true};
		var externalCallback = pickupInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	}
	
	var parkInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Parked");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Park Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var parkInteraction = function(target, queueId, currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/park";
		var	payload = {
				target : target,
				queueId : {
					queueType : 1,
					queueName : queueId
				}
			};
		var externalCallback = parkInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);		
	};

	var blindTransferInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Blind-Transferred");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Blind-Transfer Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var blindTransferInteraction = function(target, queueId, currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/blind-transfer";
		var	payload = {
				target : target,
				queueId : {
					queueType : 1,
					queueName : queueId
				}
			};
		var externalCallback = blindTransferInteraction_Callback(currentInteraction, _UICallbacks);
		console.log("@@@@@@@@@@@@@@@@@@@@@ blindTransferInteraction");
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var consultInteraction_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "201" || status =="200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Consulted");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Consult Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				
				error.message = error.message || "";
				
				if(error.message.indexOf("The telephony command was canceled") > -1) return;
				
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				
				if(errHandler.status != 999){
					errHandler.show();					
				}
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};		
	};
	
	var consultInteraction  = function(target, consulteeId, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+consulteeId+"/consult";
		var	payload = {
				target : target
			};
		var externalCallback = consultInteraction_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	var consultTransferInteraction_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "204" || status=="1223"){
				mLogger.ConsoleLog(_moduleName, "Interaction Concluded");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Conclude Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show();				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();				
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};		
	};
	
	var consultTransferInteraction  = function(consulteeId, consultantId, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
		var reqPath = default_reqPath+"/"+consulteeId+"/consult/"+consultantId+"/conclude";
		var	payload = {};
		var externalCallback = consultTransferInteraction_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	var cancelConsultInteraction_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "204"){
				mLogger.ConsoleLog(_moduleName, "Interaction Consult Canceled");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
				if(_UICallbacks["successEx"] instanceof Function) _UICallbacks["successEx"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Consult Cancel Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "DELETE", error.message);
				errHandler.show();				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();				
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};		
	};
	
	var cancelConsultInteraction  = function(consulteeId, consultantId, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "DELETE";
		var reqPath = default_reqPath+"/"+consulteeId+"/consult/"+consultantId;
		var	payload = {};
		var externalCallback = cancelConsultInteraction_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};	

	var changeAudienceInteraction_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "204"){
				mLogger.ConsoleLog(_moduleName, "Interaction Audience Changed");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Audience Change Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show();				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();				
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};		
	};
	
	var changeAudienceInteraction  = function(consulteeId, consultantId, isConsultant,_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
		var reqPath = default_reqPath+"/"+consulteeId+"/consult/"+consultantId+"/change-audience";
		var	payload = {
				audience : {
					originalParty			:	!(isConsultant),
					consultedParty		:	!!(isConsultant)
				}
		};
		var externalCallback = changeAudienceInteraction_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};	
	
	
	var joinInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
				
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction Joined");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction Join Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};
	
	var joinInteraction = function(currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/join";
		var payload = {};
		var externalCallback = joinInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var getInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Interaction got");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction get Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "GET", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};	
	
	var getInteraction = function(currentInteraction, _UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/"+currentInteraction+"?select="+["Eic_CallId", "Eic_CallIdKey", "Eic_CallState", "Eic_CallStateString",
 		                                                                 				   "Eic_Capabilities", "Eic_CallType", "Eic_CallDirection",
 		                                                                 				   "Eic_IsConsult", "Eic_Consultee", "Eic_ConferenceId",
 		                                                                 				   "Eic_ConferenceMembers", "Eic_RemoteAddress", "Eic_UserName",
 		                                                                 				   "Eic_UserQueueTimestamp", "Eic_UserSpecificQueueName",
 		                                                                 				   "IsTransferred","ConsultSeqNo","attLocalTN" ].join(",");
		var payload = {};
		var externalCallback = getInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);		
	};

	var postInteraction_Callback = function(currentInteraction, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "200"){				
				mLogger.ConsoleLog(_moduleName, "Interaction posted");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction post Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var postInteraction = function(currentInteraction, payload,_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction;
		var _payload = payload;
		var externalCallback = postInteraction_Callback(currentInteraction, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, _payload, externalCallback);
	};	
	
	
	var listenInteraction_Callback = function(_UICallback){

		return function(xmlHttp){
			if(xmlHttp.status == "200"){				
				mLogger.ConsoleLog(_moduleName, "Interaction listened");				
				if(_UICallback) _UICallback();
			}else{
				xmlHttp.responseText = xmlHttp.responseText || "";
				mLogger.ConsoleLog(_moduleName, "Interaction listen Failed");
				
				if( xmlHttp.responseText != "" && xmlHttp.responseText != "undefined" && xmlHttp.responseText != undefined)
				{
					var error = JSON.parse(xmlHttp.responseText);
					mLogger.ConsoleLog(_moduleName, xmlHttp.status +" : "+ error.message);
				}
			}
		};
	};

	var listenInteraction = function(currentInteraction, _on, _supervisor,_UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/listen";
		var	payload = {on:_on, supervisor:_supervisor};
		var externalCallback = listenInteraction_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	
	var recordInteraction_Callback = function(_UICallbacks, _on){
		
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			if(status == "200"){				
				mLogger.ConsoleLog(_moduleName, "Interaction is " + (_on ? "started" : "stopped" )+ " to recording");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Interaction record "+ (_on ? "start" : "stop" )+" Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "interaction", "POST", error.message);
				errHandler.show(currentInteraction);				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var recordInteraction = function(currentInteraction, _on, _supervisor,_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/"+currentInteraction+"/record";
		var	payload = {on:_on, supervisor:_supervisor};
		var externalCallback = recordInteraction_Callback(_UICallbacks, _on);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};	
	
	mErrorHandler.ICWS_ERROR_Callbacks["interaction"] = "interaction";
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return {
		createInteraction				: createInteraction,
		disconnectInteraction			: disconnectInteraction,
		holdInteraction					: holdInteraction,
		unholdInteraction				: unholdInteraction,
		pickupInteraction				: pickupInteraction,
		parkInteraction					: parkInteraction,
		blindTransferInteraction		: blindTransferInteraction,
		consultInteraction  			: consultInteraction,
		consultTransferInteraction		: consultTransferInteraction,
		cancelConsultInteraction		: cancelConsultInteraction,
		changeAudienceInteraction	: changeAudienceInteraction,
		joinInteraction					: joinInteraction,		
		getInteraction					: getInteraction,
		postInteraction					: postInteraction,
		recordInteraction				: recordInteraction,
		listenInteraction				: listenInteraction
	};
});