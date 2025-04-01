var wsSocket = null;
var node_server_host = null;


/*$(document).ready(function(){		

	process('advan94');
	
});*/
	
function js_nodeListenerStart(userid)
{	
	wsSocket = io.connect(node_server_host);
	wsSocket.emit('join', userid);
	
	console.log('node join user:'+userid);
	
	wsSocket.on('whisper', function(data){
		jsNodeReceiver(data);
	});		
}	


function jsNodeReceiver(jsonData)
{
	console.log(jsonData);
}