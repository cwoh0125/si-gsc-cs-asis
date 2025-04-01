/**
 * Module Name : mConnection for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.11.10
 */

define(["./mXhr", "./mConfig", "./mLogger", "./mErrorHandler"], function(mXhr, mConfig, mLogger, mErrorHandler){	
	var _moduleName = "mConnection";
	var default_reqPath = "/connection";
	var _loggendIn = false;
	
	var try_limit = 5;
	var try_cnt = 0;

	var SupportedMediaType = {
			NONE	: 0, CALL		: 1, CHAT		: 2, EMAIL		: 3,
			GENERIC : 4, CALLBACK	: 5, SMSMESSAGE : 6, WORKITEM	: 7
	};

	var StationConnectionMode = {
			DEFAULT									: 0,
			INDEPENDENTOFSESSION					: 1,
			INDEPENDENTCONNECTIONSLOGOUTWITHSESSION : 2	
	};	

	var connect_Callback = function(userid, _UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		
		return function(status, data){
		
			if(status == "201"){
				mLogger.ConsoleLog(_moduleName,"Connect Success");
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
				_loggendIn = true;
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Connect Failure");								
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "connection", "POST", error.message);
				errHandler.show();
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
				
				if(errHandler.status == "503")
				{
					jQuery("#cti_error_popup_list .close").click();
					mConfig.ICWS_SERVER = mConfig.getSwitchOverServer(mConfig.ICWS_SERVER);
					//mConfgi.ICWS_SERVER = error.alternateHostList[0];
					if(try_cnt > try_limit) return false;
					try_cnt++;
					connect(userid, "1234", _UICallbacks);					
				}
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var connect = function(userid, passwd, _UICallbacks){

		var server = mConfig.ICWS_SERVER;
		var method = "POST";
		var reqPath = default_reqPath;
		var payload = {
				__type: 'urn:inin.com:connection:icAuthConnectionRequestSettings',
				applicationName: mConfig.ICWS_APP_NAME,
				userID: userid,
				password: passwd
		};
		var resultCallback = connect_Callback(userid, _UICallbacks);

		mXhr.startingRequest(server, method, reqPath, payload, resultCallback); 
	};

	var disconnect_Callback = function(_UICallbacks){

		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "DisConnect Success");
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
				_loggendIn = false;
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "DisConnect Failure");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, null, "DELETE", error.message);
				errHandler.show();		
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks && _UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var disconnect = function(_UICallbacks){
		
		var server = mConfig.ICWS_SERVER;
		var method = "DELETE";
		var reqPath = default_reqPath;
		var payload = {};
		var resultCallback = disconnect_Callback(_UICallbacks);

		mXhr.endingRequest(server, method, reqPath, payload, resultCallback);
	};
	
	var checkConnection_Callback = function(status, data){

		if(status == "200"){
			var respText = data;			
			mLogger.ObjectLog(respText);
		}else{
			var error = data;
			mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);								
		}
	};

	var checkConnection = function(){

		var server = mConfig.ICWS_SERVER;
		var method = "GET";
		var reqPath = default_reqPath;
		var payload = {};
		var resultCallback = checkConnection_Callback;

		mXhr.goingRequest(server, method, reqPath, payload, resultCallback);
	};

	var connectStation_Callback = function(_UICallbacks){

		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Station Connect Success");
				if(_UICallbacks["continue_subscribe"] instanceof Function) _UICallbacks["continue_subscribe"]();
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();

				mConfig.isStationLess = false;
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Station Connect Failure");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, "connection", "PUT", error.message);
				errHandler.show();
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var connectStation = function(station,_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
		var reqPath = default_reqPath+"/station";
		var payload = {
				__type: 'urn:inin.com:connection:workstationSettings',
				supportedMediaTypes  : [SupportedMediaType.CALL],
				stationConnectionMode: StationConnectionMode.DEFAULT,
				readyForInteraction  : true,
				workstation			 : station

		};
		var resultCallback = connectStation_Callback(_UICallbacks);

		mXhr.goingRequest(server, method, reqPath, payload, resultCallback);
	};

	var disconnectStation_Callback = function(_UICallbacks){
		if(!_UICallbacks) _UICallbacks = {};
		return function(status, data){
				
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Station Disconnect Success");
				if(_UICallbacks["success"] instanceof Function) _UICallbacks["success"]();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Station DisConnect Failure");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
				var errHandler = new mErrorHandler.ICWSError(status, null, "DELETE", error.message);
				errHandler.show();
				if(_UICallbacks["failure"] instanceof Function) _UICallbacks["failure"]();
			}
			if(_UICallbacks["fin"] instanceof Function) _UICallbacks["fin"]();
		};
	};

	var disconnectStation = function(_UICallbacks){
		var server = mConfig.ICWS_SERVER;
		var method = "DELETE";
		var reqPath = default_reqPath+"/station";
		var payload = {};
		var resultCallback = disconnectStation_Callback(_UICallbacks);

		mXhr.goingRequest(server, method, reqPath, payload, resultCallback);
	};

	var isLoggedIn = function(){
		return _loggendIn;
	};
	
	mErrorHandler.ICWS_ERROR_Callbacks["connection"] = "connection";
	mLogger.ConsoleLog(_moduleName,"Loaded");	
	
	return {
		connect				: connect,
		disconnect			: disconnect,
		checkConnection	: checkConnection,
		connectStation		: connectStation,
		disconnectStation	: disconnectStation,
		isLoggedIn			: isLoggedIn
	};
});