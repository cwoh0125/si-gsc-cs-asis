/**
 * Module Name : mXhr for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.11.10
 */

define(["./mConfig","./mLogger", "./mErrorHandler"], function(mConfig, mLogger, mErrorHandler){
	var _moduleName = "mXhr";
/*
**  These are Private members.	
*/
	var ICWSTK, SID;
/*
**	@Scope		Private 
**	@Para		[XmlHttpReqeust]
*/
	var setSessionInfo = function(status, data){
		if(status == "201"){
			ICWSTK = data.csrfToken;
			SID = data.sessionId;
			mLogger.ConsoleLog(_moduleName, "Session Created");
		}else{
			var error = data;
			mLogger.ConsoleLog(_moduleName, "Session Not Created");
			mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);		
		}
	};
/*
**	@Scope		Private 
**	@Para		[XmlHttpReqeust]
*/
	var clearSessionInfo = function(status, data){		
		if(status == "200"){
			ICWSTK = "";
			SID = "";
			mLogger.ConsoleLog(_moduleName, "Sesssion Cleared");
		}else{
			var error = data;
			mLogger.ConsoleLog(_moduleName, "Session Not Cleared");
			mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);		
		}
	};
/*
**	@Scope		Private 
**	@Para		[Function]
**	@Para		[Function]
**	@return		[Function]
*/
	var xhrOnreadystatechangeCallback = function(externalCallback, internalCallback){
		
		return function(){
			var readyState = this.readyState;
			
			if(readyState == "4") {
				
				var status = this.status;				
				var data;
				try{
					data = JSON.parse(this.responseText);
				}catch(e){
					data = {};
				}
				
				if(internalCallback) internalCallback(status, data);
				externalCallback(status, data);				
			}
		};
	};
/*
**	@Scope		Public
**	@return		[Boolean]
*/
	var isSessioned = function(){
		if(!ICWSTK || !SID) return false;
		else return true;
	};
/*
**	@Scope		Private
**	@Para		[String]
**	@Para		[String]
**	@Para		[String]
**	@Para		[Object]
**	@Para		[Function]
*/
	var sendRequest = function(server, sessionID, CSRF_Token, method, requestPath, payload, resultCallback){
		var xmlHttp, url, aync;

		if(window.XMLHttpRequest)
		{						
			xmlHttp = new XMLHttpRequest();							
			//msie
		}
		else if(window.ActiveXObject)
		{	
			if(window.navigator.userAgent.indexOf("MSIE 9.0") > -1){
				xmlHttp = new XDomainRequest();
			}else{
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
				//xmlHttp = new XMLHttpRequest();
			}			
			//no-msie
		}
		else return false;
		

		xmlHttp.onreadystatechange = resultCallback;

		uri = mConfig.ICWS_URI_SCHEME + server + ":" + mConfig.ICWS_URI_PORT + mConfig.ICWS_URI_PATH

		if(sessionID) uri += "/" + sessionID;
		if(requestPath.substring(0,1) !== "/") {
			uri += "/";
		}
		uri += requestPath;
		/*
		if( !(window.onBeforeUnLoadEvent) ) async = true;
		else										  async = false;
		*/
		async = mConfig.ICWS_ASYNC;
		xmlHttp.open(method, uri, async);
		xmlHttp.withCredentials = true;
		if(async) xmlHttp.timeout = mConfig.ICWS_REQUEST_TIMEOUT_MS;

		if(CSRF_Token) xmlHttp.setRequestHeader("ININ-ICWS-CSRF-Token", CSRF_Token);
		
		//xmlHttp.setRequestHeader("Origin", mConfig.ICWS_ORIGIN);
		xmlHttp.setRequestHeader("Accept-Language", "En-US");
		xmlHttp.setRequestHeader("Content-type", mConfig.ICWS_MEDIA_TYPE + ";" + mConfig.ICWS_MEDIA_CHARSET);

		if(typeof payload !== "string" && !(payload instanceof String)){
			payload = JSON.stringify(payload);
			//payload = JSONtoString(payload);
			//console.log("@@@@@@@@@@@@@@payload : " + payload);
			//console.log("@@payload" + payload);
		}

		xmlHttp.send(payload);

	};
	
	function JSONtoString(object) {
	    var results = [];
	    for (var property in object) {
	        var value = object[property];
	        if (value)
	            results.push('"'+property.toString() +'"'+ ': ' + '"'+value+'"');
	        	//console.log('"'+property.toString() +'"'+ ': ' + '"'+value+'"');
	        }
	                
	        return '{' + results.join(', ') + '}';
	}
	
/*
**	@Scope		Public
**	@Para		[String]
**	@Para		[String]
**	@Para		[String]
**	@Para		[Object]
**	@Para		[Function]
*/
	var startingRequest = function(server, method, requestPath, payload, externalCallback){
		var resultCallback = xhrOnreadystatechangeCallback(externalCallback, setSessionInfo);
		sendRequest(server, null, null, method, requestPath, payload, resultCallback);
	};
/*
**	@Scope		Public
**	@Para		[String]
**	@Para		[String]
**	@Para		[String]
**	@Para		[Object]
**	@Para		[Function]
*/	
	var goingRequest = function(server, method, requestPath, payload, externalCallback){
		var resultCallback = xhrOnreadystatechangeCallback(externalCallback, null);
		sendRequest(server, SID, ICWSTK, method, requestPath, payload, resultCallback);
	};
/*
**	@Scope		Public
**	@Para		[String]
**	@Para		[String]
**	@Para		[String]
**	@Para		[Object]
**	@Para		[Function]
*/
	var endingRequest = function(server, method, requestPath, payload, externalCallback){
		var resultCallback = xhrOnreadystatechangeCallback(externalCallback, clearSessionInfo);
		sendRequest(server, SID, ICWSTK, method, requestPath, payload, resultCallback);
	};

	mErrorHandler.ICWS_ERROR_Callbacks["xhr"] = "xhr";
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return {
		isSessioned				: isSessioned,
		startingRequest 		: startingRequest,			
		goingRequest			: goingRequest,
		endingRequest			: endingRequest		
	};
	
});