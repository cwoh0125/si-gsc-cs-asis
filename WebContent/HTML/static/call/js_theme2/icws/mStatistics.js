define(["./mXhr","./mLogger","./mConfig"],function(mXhr, mLogger, mConfig){

	var _moduleName = "mStatistics";
	var default_reqPath = "/statistics";
	
	var QueueStat = function(){
		this.entered 		= 0;
		this.waiting		= 0;
		this.abandoned	= 0;
		this.answered	= 0;
		this.completed	= 0;
		this.answerRate	= 0;		
	};

	QueueStat.prototype.init = function(){
		this.entered 		= 0;
		this.waiting		= 0;
		this.abandoned	= 0;
		this.answered	= 0;
		this.completed	= 0;
		this.answerRate	= 0;
	};
	
	QueueStat.prototype.setAnswerRate = function(){
		if(this.entered > 0) this.answerRate = (this.answered/this.entered)*100;
	};		
	
	var AgentStat = function(){
		this.available		= 0;
		this.followup		= 0;
		this.awayfromdesk	= 0;
		this.atRest	= 0;
		this.onPhone	= 0;
		this.onPhoneOB	= 0;
		this.loggedIn	= 0;
		this.loggedOut	= 0;		
		this.totalAgent	= 0;		
	};

	AgentStat.prototype.init = function(){
		this.available		= 0;
		this.followup		= 0;
		this.awayfromdesk	= 0;
		this.atRest	= 0;		
		this.onPhone	= 0;
		this.onPhoneOB	= 0;
		this.loggedIn	= 0;
		this.loggedOut	= 0;		
		this.totalAgent	= 0;			
	};	

	AgentStat.prototype.setLoggedOut = function(){
		if(this.totalAgent > this.loggedIn) this.loggedOut = this.totalAgent - this.loggedIn;
	};
	
	var $queueStatistics = {
			addMember : function(_member){
				this.members[_member] = new QueueStat();
			},
			removeMember : function(_member){
				delete this.members[_member];
			},
			members : {},
			total  : new QueueStat(),
			setTotal : function(){
				this.total.init();
				
				for(var member in this.members){
					this.total.entered += this.members[member].entered;
					this.total.answered += this.members[member].answered;
					this.total.completed += this.members[member].completed;
					this.total.abandoned += this.members[member].abandoned;					
					this.total.waiting += this.members[member].waiting;					
				}
				
				this.total.setAnswerRate();
			}  
	};
	
	var $agentStatistics = {
			addMember : function(_member){
				this.members[_member] = new AgentStat();
			},
			removeMember : function(_member){
				delete this.members[_member];
			},
			members : {},
			updateMembers : function(){
				
			},			
			total : new AgentStat(),
			setTotal : function(agentList){
				this.total.init();
				
				for(var agent in agentList){ 
					if(agentList[agent].loggedIn){
						++(this.total.loggedIn);
						
						switch(agentList[agent].statusId){
							case	"Available"	:
									++(this.total.available);
										break;
							case	"Away from desk"	:
									++(this.total.awayfromdesk);
										break;
							case	"At Rest"	:
								++(this.total.atRest);
								break;
							case	"Follow Up"	:
									++(this.total.followup);
										break;	
							case	"On Phone"	:
									++(this.total.onPhone);
										break;
							case	"On Phone Outbound"	:
									++(this.total.onPhoneOB);
										break;										
						}
					}
					++(this.total.totalAgent);
				}
				/*
				for(var member in this.members){
					this.total.available += this.members[member].available;
					this.total.followup += this.members[member].followup;
					this.total.awayfromdesk += this.members[member].awayfromdesk;
					this.total.onPhone += this.members[member].onPhone;					
					this.total.loggedIn += this.members[member].loggedIn;					
					this.total.loggedOut += this.members[member].loggedOut;
					this.total.totalAgent += this.members[member].totalAgent;
				}
				*/
				this.total.setLoggedOut();				
			}			
	};
	
	var getQueueStatistics = function(){
		return $queueStatistics;
	};
	
	var getAgentStatistics = function(){
		return $agentStatistics;
	};
	
	var updateStatistics = function(statisticsValues){
		var agentStatGatheringCount = 0;
		var queueStatGatheringCount = 0;
		
		for(var idx in statisticsValues){
			var statsKey = statisticsValues[idx].statisticKey;
			var statsValue;
			if(statisticsValues[idx].statisticValue == null) statsValue = 0;
			else											 	   statsValue = statisticsValues[idx].statisticValue.value;
			
			var groupId = statsKey.parameterValueItems[0].value;
			
			if( !(groupId in $queueStatistics.members) )  $queueStatistics.addMember(groupId);
			if( !(groupId in $agentStatistics.members) )	$agentStatistics.addMember(groupId);
				
			switch(statsKey.statisticIdentifier){
				case "inin.workgroup:InteractionsEntered" :
					$queueStatistics.members[groupId].entered = statsValue;
					queueStatGatheringCount++;
					break;
				case "inin.workgroup:InteractionsWaiting" :
					$queueStatistics.members[groupId].waiting = statsValue;
					queueStatGatheringCount++;
					break;
				case "inin.workgroup:InteractionsAbandoned" :
					$queueStatistics.members[groupId].abandoned = statsValue;
					queueStatGatheringCount++;
					break;
				case "inin.workgroup:InteractionsAnswered" :
					$queueStatistics.members[groupId].answered = statsValue;
					queueStatGatheringCount++;
					break;
				case "inin.workgroup:InteractionsCompleted" :
					$queueStatistics.members[groupId].completed = statsValue;
					queueStatGatheringCount++;
					break;
				case "inin.workgroup:AgentsInStatus" :
					var status = statsKey.parameterValueItems[1].value;
					console.log("==============================> status : "+status);
					switch(status){
						case "Available" :
							$agentStatistics.members[groupId].available = statsValue;
							agentStatGatheringCount++;
							break;
						case "Follow Up" :
							$agentStatistics.members[groupId].followup = statsValue;
							agentStatGatheringCount++;
							break;
						case "Away from desk" :
							$agentStatistics.members[groupId].awayfromdesk = statsValue;
							agentStatGatheringCount++;
							break;
						case "At Rest" :
							$agentStatistics.members[groupId].atRest = statsValue;
							agentStatGatheringCount++;
							break;
						case "On Phone" :
							$agentStatistics.members[groupId].onPhone = statsValue;
							agentStatGatheringCount++;
							break;
						case "On Phone Outbound"	:
							$agentStatistics.members[groupId].onPhoneOB = statsValue;
							agentStatGatheringCount++;							
							break;
					}
					break;
				case "inin.workgroup:AgentsLoggedIn" :
					$agentStatistics.members[groupId].loggedIn = statsValue;
					agentStatGatheringCount++;
					break;
				case "inin.workgroup:TotalAgents" :
					$agentStatistics.members[groupId].totalAgent = statsValue;
					agentStatGatheringCount++;
					break;
			}
		}
		
		for(var member in $queueStatistics.members){
			if(queueStatGatheringCount > 0)  $queueStatistics.members[member].setAnswerRate();
			if(agentStatGatheringCount > 0)  $agentStatistics.members[member].setLoggedOut();
		}		
		if(queueStatGatheringCount > 0) $queueStatistics.setTotal();
		//if(agentStatGatheringCount > 0) $agentStatistics.setTotal();
		
		mLogger.ObjectLog($queueStatistics);
		mLogger.ObjectLog($agentStatistics);
	};
	
	var getStatisticsKeys = function(workGroupArray){
		if(!(workGroupArray instanceof  Array)) return [];
		var statisticKeys = new Array();			
		
		for(var idx in workGroupArray){
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:InteractionsAnswered",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.Queue:Interval" , value : "CurrentShift"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:InteractionsAbandoned",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.Queue:Interval" , value : "CurrentShift"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:InteractionsCompleted",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.Queue:Interval" , value : "CurrentShift"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:InteractionsEntered",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.Queue:Interval" , value : "CurrentShift"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:InteractionsWaiting",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "Available"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "Follow Up"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "Away from desk"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "At Rest"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "On Phone Outbound"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsInStatus",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}, {parameterTypeId : "ININ.People:Status" , value : "On Phone"}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:AgentsLoggedIn",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}]
			});
			statisticKeys.push({
				statisticIdentifier : "inin.workgroup:TotalAgents",
				parameterValueItems	: [{parameterTypeId : "ININ.People.WorkgroupStats:Workgroup" , value : workGroupArray[idx]}]
			});
		}		
		return statisticKeys;
	};
	
	var getStatisticsValue_Callback = function(_UICallback){

		return function(status, data){
			if(status == "200"){
				var resp  = data;
				mLogger.ObjectLog(resp);

			}else{
				var error = data;

				mLogger.ConsoleLog(_moduleName, status +" : "+ error.message);
			}
		};
	};

	var getStatisticsValue = function(_UICallback){
		var server = mConfig.ICWS_SERVER;
		var method = "POST";
 		var reqPath = default_reqPath+"/statistic-parameter-values/queries";
		var	payload = {
					parameterTypeId : "ININ.Queue:Interval"
					//parameterTypeId : "ININ.People:Status"
		};
		var externalCallback = getStatisticsValue_Callback(_UICallback);
	
		mXhr.goingRequest(server, method, reqPath, payload, externalCallback);
	};

	mLogger.ConsoleLog(_moduleName,"Loaded");

	return{
		getStatisticsValue	: getStatisticsValue,
		getStatisticsKeys	: getStatisticsKeys,
		getQueueStatistics	: getQueueStatistics,
		getAgentStatistics	: getAgentStatistics,
		updateStatistics		: updateStatistics		
	};
});