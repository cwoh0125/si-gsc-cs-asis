/**
 * Module Name : mLogger for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.03.20
 */

define(function(){
	var getNowDate = function (fullFormat){
		var t = new Date();
		var aa = [t.getFullYear(),t.getMonth()+1,t.getDate(),t.getHours(),t.getMinutes(),t.getSeconds()];
		for(var i in aa){ if(aa[i]<10){aa[i]='0'+aa[i];} }
		t = null;
		var ret;
		if(fullFormat){
			ret = aa[0]+"/"+aa[1]+"/"+aa[2]+" "+aa[3]+":"+aa[4]+":"+aa[5];
		}else{
			ret = aa.join('');
		}
		return ret;
	};

	var ConsoleLog = function(_moduleName,log){
	  if(typeof console == 'undefined'){ return; }
	  if(typeof ConsoleLog == 'undefined'){ return; }
	  if(log) 				console.log("["+getNowDate()+"]"+_moduleName+" $> "+log);
	  else					console.log(_moduleName);	
	};

	var EventLog = function(log){
	  if(typeof console == 'undefined'){ return; }
	  if(typeof ConsoleLog == 'undefined'){ return; }
	  console.log("["+getNowDate()+"]EVENT $> "+log);
	};
	
	var ObjectLog = function(log){
		  if(typeof console == 'undefined'){ return; }
		  if(typeof ConsoleLog == 'undefined'){ return; }
		  console.log("["+getNowDate()+"]Object Start ---------------");
		  console.log(log);
		  console.log("["+getNowDate()+"]Object End ---------------");
	};
	

	//window.loadIdx += 1;
	//console.log("mLogger:" + window.loadIdx);

	return {
		ConsoleLog  : ConsoleLog,
		EventLog	  	: EventLog,
		ObjectLog	: ObjectLog
	};
});