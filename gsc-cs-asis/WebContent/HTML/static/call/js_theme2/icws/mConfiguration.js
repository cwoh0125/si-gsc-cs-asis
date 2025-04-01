/**
 * Module Name : mConfiguration for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2017.06.02
 * Last Updated : 2016.06.02
 */


define(["./mXhr","./mLogger","./mConfig"],function(mXhr, mLogger, mConfig){

	var _moduleName = "mConfiguration";
	var default_reqPath = "/configuration";

	var getWorkGroups_Callback = function(_UICallbacks){

		if(!_UICallbacks) _UICallbacks = {};
		
		return function(status, data){
			if(status == "200"){				
				var items = data.items;
				
				var groupList = [];
				for(var i=0; i<items.length; i++)
				{
					if(items[i].members)
					{	
						groupList.push({
							"grp_no" : items[i].configurationId.id,
							"grp_nm" : items[i].configurationId.displayName,
							"grp_extension" : items[i].extension
						});
					}
				}

				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](groupList);
				mLogger.ConsoleLog(_moduleName, "WorkGroup Got");
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "WorkGroup Missed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var getWorkGroups = function(_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/workgroups?select=configurationId,extension,members&rightsFilter=view";
		var payload = "";
		var externalCallback = getWorkGroups_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var getUsers_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		
		return function(status, data){
			if(status == "200"){				
				var items = data.items;
				
				var userList = [];
				for(var user in items)
				{
					if(items[user].workgroups)
					{	
						var workgroupInfo = {};
						
						for(var j=0; j<items[user].workgroups.length; j++)
						{
							workgroupInfo[items[user].workgroups[j].id] = items[user].workgroups[j];
						}
						
						userList.push({
							"id" : items[user].configurationId.id,
							"name" : items[user].configurationId.displayName,
							"extension" : items[user].extension,
							"workgroups" : workgroupInfo
						});
					}
				}					
				
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"](userList);
				mLogger.ConsoleLog(_moduleName, "Users Got");
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Users Missed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var getUsers = function(_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/users?select=configurationId,extension,workgroups&rightsFilter=view&orderBy=configurationId.displayName";
		var payload = "";
		var externalCallback = getUsers_Callback(_UICallbacks);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var getUser_Callback = function(rtvObj, _UICallback){
		
		return function(status, data){
			if(status == "200"){				
				mLogger.ConsoleLog(_moduleName, "UserConfig Got");				
				if(_UICallback) _UICallback();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "UserConfig Missed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
			}
			rtvObj.value = data;
		};
	};
	
	
	var getUser = function(userId, _UICallback){
		var rtvObj = {};
		
		var server = mConfig.ICWS_SERVER;
		var method = "GET";
 		var reqPath = default_reqPath+"/users/"+encodeURI(userId)+"?select=configurationId,extension&rightsFilter=view";
		var	payload = "";
		var externalCallback = getUser_Callback(rtvObj, _UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
		
		return rtvObj.value;
	};
	
	var setUser_Callback = function(_UICallback){
		
		return function(status, data){
			if(status == "200"){				
				mLogger.ConsoleLog(_moduleName, "UserConfig Set");				
				if(_UICallback) _UICallback();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "UserConfig Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				alert(status +" : "+ error.message+"권한이 불충분합니다.");
			}
		};
	};
	
	
	var setUser = function(userId, _payload, _UICallback){
		
		var rtvObj = {};
		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
 		var reqPath = default_reqPath+"/users/"+encodeURI(userId);
		var	payload = _payload;
		var externalCallback = setUser_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
		
		return rtvObj.value;
	};	
		
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		getWorkGroups	: getWorkGroups,
		getUsers	 : getUsers,
		getUser		 : getUser,
		setUser		 : setUser
	};
});