/**
 * Module Name : mUser for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.12.23
 */

define(["./mXhr","./mLogger","./mConfig","./mUtil", "./mStatistics", "./mErrorHandler"],function(mXhr, mLogger, mConfig, mUtil, mStatistics, mErrorHandler){
	var _moduleName = "mUser";
	
	var default_reqPath = "/status";
	var _userID;
	var _userStatus;
	var _statusMessages;
	var _userList;
	var _userStatistics;
	var _loginInfo = {
		"user_id"		: "",
		"psswd"		: "",
		"station_id"	: ""
	};

	var _agent_status_change_callback = window.fn_softphone_setAgentStatusChange;
	
	var initUser = function(){
		_userID = null;
		_userStatus = null;
		_statusMessages = null;
		_userList =  new mUtil.Map();
		//initUserStatistics();
	};
	
	var initUserStatistics = function(){
		_userStatistics = {
				onPhone : 0,
				available : 0,
				awayFromDesk : 0,
				followUp : 0,
				loggedOut : 0
		};
	};
	
	var updateUserStatistics = function(userList){
		mStatistics.getAgentStatistics().setTotal(userList);
		/*
		for(user in userList){ 
			if(!userList[user].loggedIn){
				++(_userStatistics.loggedIn);
			}else if(userList[user].onPhone){
				++(_userStatistics.onPhone);
			}else{
				switch(userList[user].statusId){
					case	"Available"	:
							++(_userStatistics.available);
								break;
					case	"Away from desk"	:
							++(_userStatistics.awayFromDesk);
								break;
					case	"Follow Up"	:
							++(_userStatistics.followUp);
								break;					
				}
			}
		}
		*/
	};
	
	var getUserStatistics = function(){
		return _userStatistics;
	}
	
	var getMyUserID = function(){
		return _userID;
	};
	
	var setMyUserID = function(userID){
		_userID = userID;	
	};

	var setStatusMessages = function(statusMessageList){
		//console.log(statusMessageList);
		if(!(statusMessageList instanceof Array))
		{
			mLogger.ConsoleLog(_moduleName, "The type of the StatusMessages is NOT Array");			
			return false;
		}else{
			_statusMessages = new mUtil.Map();
			for(var idx in statusMessageList){		
				_statusMessages.put(statusMessageList[idx].statusId, statusMessageList[idx]);
			}
			//console.log(_statusMessages);
		}
	};	
	
	var getStatusMessages = function(){
		return _statusMessages;
	};	
	
	var startStatusMessages_Callback = function(_UICallback){
		
		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Starting StatusMessages");				
				var statusMessageList = data.statusMessageList;				
				setStatusMessages(statusMessageList);				
				if(_UICallback) _UICallback(_statusMessages);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Not Starting StatusMessages");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "status_messges", "GET", error.message);
				errHandler.show();				
			}
		};
	};

	var startStatusMessages = function(_UICallback){

		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/status-messages";
		var	payload = "";
		var externalCallback = startStatusMessages_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var getUserStatus_Callback = function(_UICallback){

		return function(status, data){
			
			if(status == "200"){
				var userStatus = data.statusId;
				mLogger.ConsoleLog(_moduleName, "UserStatus Got");
				mLogger.ConsoleLog(_moduleName,userStatus);
				if(_UICallback) _UICallback(userStatus);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "UserStatus Missed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "user_status", "GET", error.message);
				errHandler.show();				
			}
		};
	};

	var getUserStatus = function(_UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/user-statuses/"+_userID;
		var	payload = "";
		var externalCallback = getUserStatus_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);

	};

	var setUserStatus_Callback = function(statusId, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
			
			if(status == "202"){
				mLogger.ConsoleLog(_moduleName, "UserStatus Change Accepted");
				
				if(statusId == "Gone Home")
				{
					try
					{
//						sendCtiTimeInfo (_userStatus, statusId, false, false);
					}catch(e){}					
				}
				
				_userStatus = statusId;
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "UserStatus Change Denied");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "user_status", "PUT", error.message);
				errHandler.show();				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var setUserStatus = function(statusId, _UICallbacks, notes, forwardNumber, until){
		
		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
 		var reqPath = default_reqPath+"/user-statuses/"+_userID;
		var	payload = {
			statusId	  : statusId,
			notes		  : notes,
			forwardNumber : forwardNumber,
			until		  : until
		};
		var externalCallback = setUserStatus_Callback(statusId, _UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);

	};	
	
	var getUserList = function(){
		return _userList;
	};

	var setUserList = function(userList){
		if(userList instanceof Array){
			if(!(_userList instanceof mUtil.Map))	_userList = new mUtil.Map();
			for(var idx in userList){
				_userList.put(userList[idx].id, userList[idx]);
			}			
		}else{
			mLogger.ConsoleLog(_moduleName,"userList is not an Array");
		}
	};


	var _updateUserList_callback;
	
	var setUpdateUserList_callback = function(callback){
		if(callback instanceof Function) _updateUserList_callback = callback;
		else								   _updateUserList_callback = function(){};
	};
	
	var validateACWable = function(userBefore, userAfter){
		var isACWable = false;
		
		if(userBefore.onPhone)
		{
			if(!userAfter.onPhone) isACWable = true;
		}
		else
		{
			if( (userAfter.statusId == mConfig.ICWS_OCW.IB) || (userAfter.statusId == mConfig.ICWS_OCW.OB) ) isACWable = true;
		}
		return isACWable;
	};
	
	var updateUserList = function(newUserList){
		//initUserStatistics();		
		for(var idx in newUserList){
			var userAfter	= newUserList[idx];
			var userBefore	= _userList.get(userAfter.userId);
			if(userBefore){		
				if( (userBefore.id == getMyUserID()) ){
					mLogger.ConsoleLog(_moduleName, "Before user status : "+userBefore.statusId+"("+userBefore.onPhone+")");
					mLogger.ConsoleLog(_moduleName, "After user status : "+userAfter.statusId+"("+userAfter.onPhone+")");
					
					userBefore.isACWable = validateACWable(userBefore, userAfter);
					
					if( ! (userBefore.statusId == userAfter.statusId && userBefore.onPhone == userAfter.onPhone) )
					{
						if(_agent_status_change_callback instanceof Function) _agent_status_change_callback(userAfter.statusId);
					}
//					try
//					{
//						sendCtiTimeInfo (userBefore.statusId, userAfter.statusId, userBefore.onPhone, userAfter.onPhone);
//					}catch(e){}
				}
				userBefore.loggedIn = userAfter.loggedIn;				
				userBefore.statusId = userAfter.statusId;					
				userBefore.onPhone = userAfter.onPhone;
			}			
		}
		_updateUserList_callback(getMyUserID(), _userList.map);
		updateUserStatistics(_userList.map);
	};
	
	var getMyUserInfo = function(){
		if(_userList)	return _userList.get(getMyUserID());
		else			return {};
	}
	
	var getUserInfo = function(userid){
		if(_userList)	return _userList.get(userid); 
		else			return {};
	};
	
	var getUserNameFromExtension = function(p_extension){
		
		for(user in _userList.map){			
			if(_userList.map[user].extension == p_extension) return _userList.map[user].name;
		}
		return "-";
	};

	var setUserBusy = function(call_Direction){
		var status = getMyUserInfo().statusId;
		
		if( (status != mConfig.ICWS_OCW.IB) && (status != mConfig.ICWS_OCW.OB) ) {
			switch(call_Direction){
				case	"I"		:
					setUserStatus(mConfig.ICWS_OCW.IB);
					break;
				case	"O"	:
					setUserStatus(mConfig.ICWS_OCW.OB);
					break;
			}					
		}		
	};
	
	var setLoginInfo = function(user_id, psswd, station_id){
		_loginInfo["user_id"] = user_id;
		_loginInfo["psswd"] = psswd;
		_loginInfo["station_id"] = station_id;
	};

	var getLoginInfo = function(){
		return _loginInfo;
	};

	var updateUser = function(target, _UICallbacks, workGroup){
		//mLogger.ConsoleLog(_moduleName, "created");
		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
 		var reqPath = "/configuration/users/"+target.id	
		console.log("AGENT LICENCE UPDATE " + target);
 		var	payload = {
 				configurationId: 
 			    { 
 			        "id": target.id, 
 			        "displayName": target.id
 			    },
 			    licenseProperties:
		    	{
 			    	"extno" : target.extno,
 			    	"licenseActive" : target.licenseActive,
 			    	"allocationType" : target.allocationType,
 			    	"hasClientAccess" : target.clientAccess,
 			    	"mediaLevel" : target.mediaLevel,
					"mediaTypes" :
					[
						1	
					]
		    	}
		};
 		
 		
		var externalCallback = createUpdateUser_callback(_UICallbacks);
		
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var createUpdateUser_callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(xmlHttp){
			var responseText;
			try{
				responseText = JSON.parse(xmlHttp.responseText);
			}catch(e){
				responseText = {};
			}			
			if(xmlHttp.status == "200"||xmlHttp.status == "201"){
				mLogger.ConsoleLog(_moduleName, "createUser/updateUser Succeeded");				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](responseText.members);
			}else{
				var error = responseText;
				mLogger.ConsoleLog(_moduleName, "createUser/updateUser Succeeded");		
				mLogger.ConsoleLog(_moduleName, xmlHttp.status +" : "+ error.message);				
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};		
	};


	//window.loadIdx += 1;
	//mLogger.ConsoleLog(_moduleName,"mUser:" + window.loadIdx);
	mErrorHandler.ICWS_ERROR_Callbacks["user"] = "user";
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		initUser							: initUser,
		getMyUserID					: getMyUserID,
		setMyUserID						: setMyUserID,
		getStatusMessages 			: getStatusMessages,
		startStatusMessages 			: startStatusMessages,
		getUserStatus					: getUserStatus,
		setUserStatus					: setUserStatus,
		getUserList						: getUserList,
		setUserList						: setUserList,
		updateUserList					: updateUserList,
		setUpdateUserList_callback	: setUpdateUserList_callback,
		getMyUserInfo					: getMyUserInfo,
		getUserInfo						: getUserInfo,
		getUserStatistics				: getUserStatistics,
		getUserNameFromExtension	: getUserNameFromExtension,
		setUserBusy						: setUserBusy,
		setLoginInfo						: setLoginInfo,
		getLoginInfo						: getLoginInfo,
		updateUser			: updateUser

	};
});