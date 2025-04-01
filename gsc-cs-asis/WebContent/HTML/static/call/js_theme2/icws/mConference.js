/**
 * Module Name : mConference for ICWS
 * Programmer   : KiTae Lee
 * First Created  : 2015.03.02
 * Last Updated : 2015.11.10
 */

define(["./mXhr","./mLogger","./mConfig","./mUtil"], function(mXhr, mLogger, mConfig, mUtil){
	var _moduleName = "mConference";	
	var default_reqPath = "/interactions/conferences";
	var _conferenceUIMembers = {
			"me" : {},
			"other" : {},
			"theOther" : {}
	}
	
	var createConference_Callback = function(_UICallback){

		return function(status, data){
			
			if(status == "201"){				
				mLogger.ConsoleLog(_moduleName, "Conference Created");				
				if(_UICallback instanceof Function) _UICallback(data.conferenceId);
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Conference Create Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
			}
		};
	};

	var createConference = function(interactions, _UICallback){		
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath;
		var	payload = {
				interactions : interactions
			};
		var externalCallback = createConference_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	var addConference_Callback = function(_UICallback){

		return function(status, data){
		
			if(status == "200"){
				mLogger.ConsoleLog(_moduleName, "Conference Added");				
				if(_UICallback) _UICallback();
			}else{
				var error = data;
				mLogger.ConsoleLog(_moduleName, "Conference Add Failed");
				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
			}
		};
	};
	
	var addConference = function(interactions, currentConference, _UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "PUT";
 		var reqPath = default_reqPath+"/"+currentConference;
		var	payload = {
				interactions : interactions
			};
		var externalCallback = addConference_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};
	
	var ConferenceUI = {};

	ConferenceUI.create = function(initDiv){
		mLogger.ConsoleLog(_moduleName,"init UI");
		var paper = Raphael(initDiv, 550, 350);
		
		var center = {x : 275, y : 205};
		var peopleShape = "a10,10,0, 1,0,-10,0c0,0,-10,3,-10,10v10m20,-20c0,0,10,3,10,10v10h-30";
		
		var ConferenceMemberUI = function(_x, _y){			
			this.x = _x;
			this.y = _y;
			this.UIArr = [];
			this.UIArr.push(paper.path("M"+center.x+","+center.y+"L"+this.x+","+this.y));
			this.UIArr.push(paper.circle(this.x, this.y, 40));
			this.UIArr.push(paper.path("M"+(this.x+5)+","+(this.y)+peopleShape));
			
			this.UIArr[0].attr("stroke", "#AAAAAA");
			this.UIArr[0].attr("stroke-width", "10");
			this.UIArr[0].attr("stroke-linecap", "round");
			
			this.UIArr[1].attr("fill", "#FFF");
			this.UIArr[1].attr("stroke", "#AAAAAA");
			this.UIArr[1].attr("stroke-width", "10");
						
			this.UIArr[2].attr("fill","#AAAAAA");
			this.UIArr[2].attr("stroke-width","0");
		};
		
		ConferenceMemberUI.prototype.setColor = function(_color){
			this.UIArr[0].attr("stroke", _color);			
			this.UIArr[1].attr("stroke",_color);
			this.UIArr[2].attr("fill", _color);
		};
				
		_conferenceUIMembers["other"].id   = undefined;
		_conferenceUIMembers["other"].UI	= new ConferenceMemberUI(400, 250);
		_conferenceUIMembers["theOther"].id = undefined;
		_conferenceUIMembers["theOther"].UI		= new ConferenceMemberUI(150, 250);
		_conferenceUIMembers["me"].id    = undefined;		
		_conferenceUIMembers["me"].UI= new ConferenceMemberUI(275,100);
	};
	
	ConferenceUI.init = function(){
		if(_conferenceUIMembers["me"]) _conferenceUIMembers["me"].id    = undefined;
		if(_conferenceUIMembers["other"]) _conferenceUIMembers["other"].id   = undefined;
		if(_conferenceUIMembers["theOther"]) _conferenceUIMembers["theOther"].id = undefined;
	}
	
	ConferenceUI.getOtherMember = function(interactionId){
		if(!_conferenceUIMembers["other"].id){
			_conferenceUIMembers["other"].id = interactionId;
			return "other";
		}else if(!_conferenceUIMembers["theOther"].id){
			_conferenceUIMembers["theOther"].id = interactionId;
			return "theOther";
		}else{
			return null;
		}
	};
	
	ConferenceUI.get = function(id){
		if(id){
			if(_conferenceUIMembers["me"].id == id){
				return _conferenceUIMembers["me"];
			}else if(_conferenceUIMembers["other"].id == id){
				return _conferenceUIMembers["other"];
			}else if(_conferenceUIMembers["theOther"].id == id){
				return _conferenceUIMembers["theOther"];
			}else{
				return null;
			}
		}else{
			return _conferenceUIMembers;
		}
	};
	
	ConferenceUI.set = function(_id, _who){
		switch(_who){
			case "me" :
				_conferenceUIMembers["me"].id = _id;
				break;
			case "other" :
				_conferenceUIMembers["other"].id = _id;
				break;
			case "theOther" :
				_conferenceUIMembers["theOther"].id = _id;
				break;				
		}
	};
	
	var conferenceChangeByConference = function(mQueue, conference){
		jQuery("#conference_div .name").text("");
		jQuery("#conference_div .interactionId").val("");
		
		var conferenceUIMembers = ConferenceUI.get();
		for(var UIRole in conferenceUIMembers){
			var color;
			var name;
			var interactionId;
			
			var isHeld = false;
			var isConnected = false;
			var isDisconnected = false;
			
			var member = mQueue.getConferenceMemberFromUIRole(UIRole);
			//mLogger.ConsoleLog(UIRole);		
			if(member){
				//mLogger.ConsoleLog("멤버아이디는 "+member.id);		
				var interactionInfo =  mQueue.getInteractionInfo(member.id);
				
				if(interactionInfo.Eic_CallState == "On Hold")
				{
					 //stateString = "보류";
					color = "#1E90FF";
					isHeld = true;
				}
				else if(interactionInfo.Eic_CallState == "Disconnected")
				{
					color = "#AAAAAA";
					isDisconnected = true;
				}
				else if( (interactionInfo.Eic_CallState == "Connected") && (interactionInfo.Eic_CallStateString == "Connected" || interactionInfo.Eic_CallStateString.indexOf("Assigned") > -1) )
				{
					//stateString = "연결";
					color = "#25DD25";
					isConnected = true;
				}else{
					color = "#AAAAAA";
				}
				
				if(interactionInfo.whois == "-") name = interactionInfo.cid;
				else 								  name = interactionInfo.whois;	
				
				interactionId = member.id
			}else{
				color = "#AAAAAA";
				interactionId = "";
				name = "-";
				isDisconnected = true;
			}
			
			conferenceUIMembers[UIRole].UI.setColor(color);
			
			isHeld = !isDisconnected && isHeld;
			isConnected = !isDisconnected && isConnected;
			
			var $memberDiv = jQuery("#conference_div #member_"+UIRole);
			if(UIRole == "me"){
				$memberDiv.find(".name").text(name)
								.end()
								.find(".interactionId").val(interactionId);
			}else{
				$memberDiv.find(".name").text(name)
				.end()
				.find(".interactionId").val(interactionId)
				.end()
				.find(".BTN_ConfUnHold").css("opacity", isHeld ? "1.0" : "0.5").prop("disabled", !isHeld)
				.end()
				.find(".BTN_ConfHold").css("opacity", isConnected ? "1.0" : "0.5").prop("disabled", !isConnected)
				.end()
				.find(".BTN_ConfDis").css("opacity", !isDisconnected ? "1.0" : "0.5").prop("disabled", isDisconnected);				
			}
		}
	};	
	
	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		createConference					: createConference,
		addConference						: addConference,
		ConferenceUI						: ConferenceUI,
		conferenceChangeByConference	: conferenceChangeByConference
	};
});