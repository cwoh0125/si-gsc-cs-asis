	var wsSocket = null;
	
	function js_ListenerStart(mode, host, userid)
	{
		if(mode == 'websocket')
			js_webSockListenerStart(host, userid)
		else if(mode == 'nodejs')
			js_nodeListenerStart(host, userid)
		
		
	}
	
	function js_nodeListenerStart(host, userid)
	{
		try
		{		
			wsSocket = io.connect(host,{
				'reconnect':true
			});
			
			
			wsSocket.emit('join', userid);
			
			console.log('node join user:'+userid);
			
			wsSocket.on('whisper', function(data){
				jsDataReceiver(data);
			});
		} catch(e)
		{
			console.log('node.js 서버 비정상....');
		}
	}
	
	function js_webSockListenerStart(host, userid)
	{
		try
		{		
			wsSocket = new WebSocket(host+userid);
			
			wsSocket.onopen = function() {
			    console.log('websocket open');				    
			}
			
			wsSocket.onmessage = function(e) {					
			    console.log('msg rcv');				
			    jsDataReceiver(jQuery.parseJSON(e.data));
			};
			
			wsSocket.onclose = function(){
				console.log('reconnecting..............');
				setTimeout(function(){js_webSockListenerStart(host, userid)}, 5000);
			}    
			    
			console.log('websocket connection established..');    
			    
		} catch(e)
		{
			console.log(e);			
			//if(wsSocket != null) wsSocket.close();
			
			//js_pushListenerStart(userid);
		}
	}
	
	
	function js_sendWebSocket(msg)
	{
		if(wsSocket!=null)
		{
			wsSocket.send(msg);
		}
	}
	
	
	//var jsonData = null;
	//var jsonObj = null;
	/**
	*  bpm Task 실행결과에대한 이벤트를 받는다.
	*  @param data   
	*/
	function jsDataReceiver(jsonData)
	{	
		if(jsonData == null || jsonData =="") return;
		
		var jsonObj = null;
		
		try
		{		
			jsonObj = jQuery.parseJSON(jsonData);
			
			console.log(jQuery.parseJSON(jsonData));
			
		} catch(e)
		{
			console.log('parseJSON error'+e);
			jsonObj = jsonData;
		}
		
		console.log('code:'+jsonObj.result_code);
		
		var cmd = jsonObj.cmd;
		var _alam_type = null;
		var endPix = null;
		
		if(cmd == null || cmd == '')
		{
			try
			{	
				js_runResult(jsonObj.biz_key, jsonObj.task_no , jsonObj.task_nm, "", jsonObj.task_run_no, jsonObj.upd_time);
			}catch(e)
			{
			}
			
			if(jsonObj.result_code == '200')
			{
				_alam_type = 'info';
				endPix = '이(가) 처리가 완료 되었습니다.';
			} else
			{
				_alam_type = 'error';
				endPix = "이(가) 처리중 오류가 발생 하였습니다.";
			}
			
			js_alamNoti(_alam_type, '시스템 알림', jsonObj.task_nm+endPix+'<br/><a href="#" class="orange">업무번호:'+jsonObj.biz_key+'</a><br/>처리시각 :'+utc2local(jsonObj.upd_time) );			
		} else
		{
			console.log('cmd:'+cmd);
			
			if(cmd == 'NOTE_RECEIPT')
			{
				//static/call/js_theme2/common/common.js 
				//쪽지 불러오는 함수 호출..
				js_openNoteWindow(jsonObj.note_no);
			}
			else if(cmd == "KEYWORD_RECEIPT")
			{
				if( ( $("#accordion_title_6").html() || '' ) != '')
				{
					js_rcvMonqCnsltType(jsonObj);
				}
				
				if( ( $("#accordion_title_7").html() || '' ) != '')
				{				
					js_rcvMonqKeyword(jsonObj);
				}
			}
			else if(cmd == "AGENT_STATUS_RECEIPT")
			{
				if( ( $("#accordion_title_8").html() || '' ) != '')
				{
					js_rcvMonqCenterInfo(jsonObj);
				}

				if( ( $("#accordion_title_9").html() || '' ) != '')
				{
					js_rcvMonqAgentInfo(jsonObj);				
				}
			}
				
		}
	}
	

	function  utc2local(in_time)
	{
		var tempDate = new Date(Date.parse(in_time));
		var formatDate = tempDate.getFullYear()+"-"+(tempDate.getMonth()+1)+"-"+tempDate.getDate()+" "+tempDate.getHours()+":"+tempDate.getMinutes()+":"+tempDate.getSeconds();
		return formatDate; 
	}
	
	
	/*
	msg : 알람 메세지
	type : success, warning, error, info
*/
function js_alamNoti(type, ttl, msg)
{
	
	try {
		js_userDefineEvent(type, ttl, msg) ;
	} catch (e) {
		console.log(e);
		// TODO: handle exception
	}
	
	if(ttl == null || ttl == '')
		ttl = "시스템 알림 메세지";
	
	var _css = null;
	
	
	if(type == 'success') _css = "gritter-success";
	else if(type == 'info') _css = "gritter-info";
	else if(type == 'error') _css = "gritter-error";
	else if(type == 'warning') _css = "gritter-warning";
	try
	{
	
	jQuery.gritter.add({
		title: ttl,
		sticky: false,
		time:5000,
		// (string | mandatory) the text inside the notification
		text: msg,//'This will fade out after a certain amount of time. Vivamus eget tincidunt velit. Cum sociis natoque penatibus et <a href="#" class="orange">magnis dis parturient</a> montes, nascetur ridiculus mus.',
		class_name: _css//'gritter-info'
		//gritter-warning gritter-success gritter-error
	});
	} catch(e)
	{
		//console.log('js_alamNoti error:'+e);
	}

	try {
		js_userDefineEvent(type, ttl, msg) ;
	} catch (e) {
		//console.log(e);
		// TODO: handle exception
	}
	

}
	