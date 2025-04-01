/**
 * Module Name : mUtil for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2016.04.27
 */

define(function(){


	var Map = function(){
	 this.map = new Object();
	};   
	Map.prototype = {   
		put : function(key, value){   
			this.map[key] = value;
		},   
		get : function(key){   
			return this.map[key];
		},
		containsKey : function(key){    
		 return key in this.map;
		},
		containsValue : function(value){    
		 for(var prop in this.map){
		  if(this.map[prop] == value) return true;
		 }
		 return false;
		},
		isEmpty : function(key){    
		 return (this.size() == 0);
		},
		clear : function(){   
		 for(var prop in this.map){
		  delete this.map[prop];
		 }
		},
		remove : function(key){    
		 delete this.map[key];
		},
		keys : function(){   
			var keys = new Array();   
			for(var prop in this.map){   
				keys.push(prop);
			}   
			return keys;
		},
		values : function(){   
		 var values = new Array();   
			for(var prop in this.map){   
			 values.push(this.map[prop]);
			}   
			return values;
		},
		size : function(){
		  var count = 0;
		  for (var prop in this.map) {
			count++;
		  }
		  return count;
		}
	};
	
	var popUpFactory = {
			create : function(title, message, OKCallback){
						var box_div = document.createElement("div");						
						var head_div = document.createElement("div");						
						var body_div = document.createElement("div");
						var anchor = document.createElement("a");
						var icon = document.createElement("i");
						
						var head_text = document.createTextNode(title);
						var body_text = document.createTextNode(message);
						
						icon.className = "icon-exit icon-gray";
						anchor.className = "close";
						anchor.appendChild(icon);
						
						head_div.className = "head";												
						head_div.appendChild(head_text);
						head_div.appendChild(anchor);						
						
						body_div.className = "body";
						body_div.appendChild(body_text);
						
						box_div.className = "msgbox msgbox-white";
						box_div.style.position = "relative";
						box_div.appendChild(head_div);
						box_div.appendChild(body_div);
						
						if(OKCallback instanceof Function){
							var btn_div = document.createElement("div");
							var btn_anchor = document.createElement("a");
							var btn_text = document.createTextNode("해결하기");
							
							btn_anchor.className = "btn btn-purple btn-small";
							btn_anchor.appendChild(btn_text);
							
							btn_div.style.textAlign = "right";
							btn_div.style.marginTop = "20px";
							btn_div.style.marginRight = "5px";
							btn_div.appendChild(btn_anchor);
							btn_div.onclick = OKCallback;
							
							box_div.appendChild(btn_div);
						}

						return box_div;
					}
	};	

	var isVarValidated = function(variable){
		return (!!variable) && (variable != "");
	};	
	
	var getTimerString = function(raw_sec){
		var timerString;
		if(raw_sec < 0){
			timerString = "00:00:00";
		}else{
			var sec = raw_sec % 60;
			var raw_min = (raw_sec-sec)/60;
			var min = raw_min % 60;
			var raw_hour = (raw_min-min)/60;
			
			timerString =(raw_hour < 10 ? "0"+raw_hour : raw_hour)+":"+
							 (min < 10 ? "0"+min : min)+":"+
			                 (sec < 10 ? "0"+sec : sec);
		}
		return timerString;
	}
	
	var getFormattedTimeString = (function(){
		 if(navigator.userAgent.indexOf("MSIE 8.0") > -1){
			
			return 	function(unformatted){		
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				
				var formatted	= new Date(new Date(unformatted.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);
				
				return formatted.toLocaleString();
			};
		 }else{
			return 	function(unformatted){		
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				
				var formatted	= new Date(unformatted.replace(regex,"$1-$2-$3T$4:$5:$6Z"));
				
				return formatted.toLocaleString();
			};
		 }
		
	})();

	
	var getElapsedTime = (function(){
		if(navigator.userAgent.indexOf("MSIE 8.0") > -1){
			return function(before){
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				var today = new Date();	
				var now = today.getTime();
				var changed;

				changed	= new Date(new Date(before.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);
				//mLogger.ObjectLog(user.statusChanged.replace(regex,"$1-$2-$3T$4:$5:$6Z"));
				//mLogger.ObjectLog(new Date(changed));
				//mLogger.ObjectLog(new Date(now));
				var elapsedTime = Math.round((now - changed)/1000);
				var elapsedTimeStr = getTimerString(elapsedTime);

				return elapsedTimeStr;		
			}
		}else{
			return function(before){
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				var today = new Date();	
				var now = today.getTime();
				var changed;

				changed	= new Date(before.replace(regex,"$1-$2-$3T$4:$5:$6Z")).getTime();
				//mLogger.ObjectLog(user.statusChanged.replace(regex,"$1-$2-$3T$4:$5:$6Z"));
				//mLogger.ObjectLog(new Date(changed));
				//mLogger.ObjectLog(new Date(now));
				var elapsedTime = Math.round((now - changed)/1000);
				var elapsedTimeStr = getTimerString(elapsedTime);

				return elapsedTimeStr;		
			}			
		}
	})();	

	var getTimeDifference = (function(){
		if(navigator.userAgent.indexOf("MSIE 8.0") > -1){
			return function(before, after){
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				var before_datetime, after_datetime;

				before_datetime	= new Date(new Date(before.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);
				after_datetime	= new Date(new Date(after.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);

				var elapsedTime = Math.round((after_datetime - before_datetime)/1000);
				var elapsedTimeStr;
				if(elapsedTime) elapsedTimeStr = getTimerString(elapsedTime);
				else				elapsedTimeStr = "00:00:00";
				
				return elapsedTimeStr;		
			}
		}else{
			return function(before, after){
				var regex = /(\d{4})(\d{2})(\d{2}).(\d{2})(\d{2})(\d{2})(.*)/;
				var before_datetime, after_datetime;

				before_datetime	= new Date(new Date(before.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);
				after_datetime	= new Date(new Date(after.replace(regex,"$1/$2/$3 $4:$5:$6")).getTime()+32400000);

				var elapsedTime = Math.round((after_datetime - before_datetime)/1000);
				var elapsedTimeStr;
				if(elapsedTime) elapsedTimeStr = getTimerString(elapsedTime);
				else				elapsedTimeStr = "00:00:00";

				return elapsedTimeStr;		
			}			
		}
	})();

	var ICWSTimer = function(_valueSetCallback){
		var sec = 0;
		var callback;
		var timer_id = null;

		if(_valueSetCallback)	callback = function(val){ _valueSetCallback(getTimerString(val)); };
		else												callback = function(){ };

		this.start = function(){
			sec = 0;
			callback(sec);
			timer_id = setInterval(function(){	callback(++sec);	 }, 1000);
		};

		this.pause = function(){
			//if(timer_id) clearInterval(timer_id);
			//else		  throw "There is no started loop for this timer, 'start' a timer";
			clearInterval(timer_id);
		};

		this.resume = function(){
			//if(sec > 0) timer_id = setInterval(function(){	callback(++sec);	 }, 1000);
			//else		  throw "There is no started loop for this timer, 'start' a timer";
			callback(sec);
			timer_id = setInterval(function(){	callback(++sec);	 }, 1000);
		};

		this.stop = function(){
			clearInterval(timer_id);
			sec = 0;
			callback(sec);
		};

		this.restart = function(){
			if(timer_id)	clearInterval(timer_id);
			sec = 0;
			callback(sec);
			timer_id = setInterval(function(){	callback(++sec);	 }, 1000);
		};

		this.reset = function(){
			if(timer_id)	clearInterval(timer_id);
			sec = 0;
			callback(sec);
		};
	};

	return{
		Map 							:	Map,
		popUpFactory				:	popUpFactory,
		isVarValidated				:	isVarValidated,
		getTimerString				:	getTimerString,
		getFormattedTimeString	:	getFormattedTimeString,
		getElapsedTime				:	getElapsedTime,
		getTimeDifference			:	getTimeDifference
	}
});