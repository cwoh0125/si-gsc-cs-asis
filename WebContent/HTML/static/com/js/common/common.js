/*if(window.console == null || window.console == undefined)
{
	console = {log : function(){}};
}*/

if (!window.console) window.console = {};
if (!window.console.log) window.console.log = function () { };

Map = function(){
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
	    },
	    addAll : function(_map){
	    	var that = _map;
	    	for(var prop in that.map){
	    		this.map[prop] =  that.map[prop];	    		
	    	}
	    }
	};




var OrgDiagramOptions = {
		gubun: "Org",
        title: "", 
        width: "",
        height: "auto",
        target_id : "",
        empl_nm_id : "",
        empl_nm_srch : "",
        empl_no_id : "",	
        data_type : "",
        empl_nm_index : "",		
        empl_no_index : "",	
        callback : "",
        permit_rel : {}			
};

var DeptDiagramOptions = {
		gubun: "Dept",
        title : "", 
        width : "",	
        height: "auto",
        dept_nm_id : "",	
        dept_no_id : "",	
        data_type : "",
        dept_nm_index : "",		
        dept_no_index : "",	
        callback : "",
        page_keep : "N",
        permit_rel : {}			              
};

//var dateFrmt =/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/; 
var dateFrmt =/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/; 
var yyyyMMFrmt = /^[0-9]{4}-[0-9]{2}$/;
var timeFrmt =/^[0-9]{2}:[0-9]{2}$/;
var numberFrmt =/^[0-9]+(.[0-9]+)?$/;
var emailFrmt = /^([0-9a-zA-Z_.-]+)@([0-9a-zA-Z_-]+)(\.[0-9a-zA-Z_-]+){1,2}$/;
var idFrmt = /^([0-9a-zA-Z_.-])+$/;
var birthDayFrmt =/^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$/;
var hpFrmt = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/;
var telFrmt=/^\d{2,3}-?\d{3,4}-?\d{4}$/;
var passwdFrmt = /^.*(?=^.{11,30}$)(?=.*\d)(?=.*[a-zA-Z])(?=.*[~,!,@,#,$,*,(,),=,+,_,.,|]).*$/; //작성규칙 : 대문자,소문자,숫자,특수문자 각각이 포함되어야하며 12자이상 20자리 이하
/*
 * form의 validation 체크
 * */
function checkFormField(f, as_type)
{
	var result = true;
	jQuery(f).find('input, select, textarea').each(			
			function(i){
	            var objtype = jQuery(this);
	            objtype.each(
	            	function(j){	
	            		//console.log(jQuery(this).attr('type'));
		            	if(jQuery(this).attr('required') != null && jQuery(this).attr('required') != false)
		            	{	
		            		if(jQuery(this).attr('type') == 'checkbox')
		            		{
		            			if(jQuery(':checkbox[name='+jQuery(this).attr('name')+']:checked').val() == null || jQuery(':checkbox[name='+jQuery(this).attr('name')+']:checked').val() == '')
		            			 {
		            				 var addStr = '';
				            			if(jQuery(this).attr('alt') != null && jQuery(this).attr('alt') != 'undefined')
				            			{		            				
				            				var alts = jQuery(this).attr('alt');
					            			if(alts != '') addStr = '[' +alts+ ']';
				            			}			            			
					            			
					            		jQuery(this).addClass('ui-state-error-custom');
					            		jQuery(this).focus();
					            		jQuery(this).select();
					            		
					            		//console.log(jQuery(this).attr('id'));
					            		
					            		result = false;
					            		if(as_type){
					            			alert(msg_com_code_002+addStr);
					            		} else 
					            		{			            			
					            			msgStart(msg_com_code_002+addStr, 'warning', null);
					            		}
					            		return false;
		            			 } else
		            			 {
		            				 jQuery(this).removeClass('ui-state-error');
		            			 }
		            		}
		            		else if(jQuery(this).attr('type') == 'radio')
		            		{
		            			//console.log(jQuery(':radio[name='+jQuery(this).attr('name')+']:checked').val());
		            			 if(jQuery(':radio[name='+jQuery(this).attr('name')+']:checked').val() == null || jQuery(':radio[name='+jQuery(this).attr('name')+']:checked').val() == '')
		            			 {
		            				 var addStr = '';
				            			if(jQuery(this).attr('alt') != null && jQuery(this).attr('alt') != 'undefined')
				            			{		            				
				            				var alts = jQuery(this).attr('alt');
					            			if(alts != '') addStr = '[' +alts+ ']';
				            			}			            			
					            			
					            		jQuery(this).addClass('ui-state-error-custom');
					            		jQuery(this).focus();
					            		jQuery(this).select();
					            		
					            		//console.log(jQuery(this).attr('id'));
					            		
					            		result = false;
					            		if(as_type){
					            			alert(msg_com_code_002+addStr);
					            		} else 
					            		{			            			
					            			msgStart(msg_com_code_002+addStr, 'warning', null);
					            		}
					            		return false;
		            			 } else
		            			 {
		            				 jQuery(this).removeClass('ui-state-error-custom');
		            			 }
		            		} else
		            		{
			            		if(jQuery(this).val() == '' || (jQuery(this).attr('alt') != null && jQuery(this).attr('alt') == jQuery(this).val() ))
			            		{
			            			var addStr = '';
			            			if(jQuery(this).attr('alt') != null && jQuery(this).attr('alt') != 'undefined')
			            			{		            				
			            				var alts = jQuery(this).attr('alt');
				            			if(alts != '') addStr = '[' +alts+ ']';
			            			}			            			
				            			
				            		jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		
				            		//console.log(jQuery(this).attr('id'));
				            		
				            		result = false;
				            		if(as_type){
				            			alert(msg_com_code_002+addStr);
				            		} else 
				            		{			            			
				            			msgStart(msg_com_code_002+addStr, 'warning', null);
				            		}
				            		return false;			            		
			            		} else
			            		{
			            			jQuery(this).removeClass('ui-state-error-custom');
			            		}
		            		}
		            		
		            	}
		            	
		            	if(jQuery(this).attr('maxvalue') != null)
		            	{	
		            		if(isNumPoint(jQuery(this).val()))
		            		{
		            			var temp = jQuery(this).attr('maxvalue');
		            			if(parseInt(jQuery(this).val()) > parseInt(temp))
		            			{
		            				
		            				var addStr = '';
			            			if(jQuery(this).attr('alt') != null && jQuery(this).attr('alt') != 'undefined')
			            			{		            				
			            				var alts = jQuery(this).attr('alt');
				            			if(alts != '') addStr = '[' +alts+ ']';
			            			}
			            			
			            			jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		
		            				result = false;
				            		if(as_type){
				            			alert(msg_com_code_026+"(input:"+jQuery(this).val()+" max:"+temp+")"+addStr);
				            		} else 
				            		{			            			
				            			msgStart(msg_com_code_026+"(input:"+jQuery(this).val()+" max:"+temp+")"+addStr, 'warning', null);
				            		}
				            		
				            		return false;	
		            			}
		            		} else
		            		{
		            			jQuery(this).removeClass('ui-state-error-custom');
		            		}            		
		            	}
		            	
		            	if(jQuery(this).attr('minvalue') != null)
		            	{
		            		//console.log('min:'+jQuery(this).attr('minvalue')+", input:"+jQuery(this).val());
		            		if(isNumPoint(jQuery(this).val()))
		            		{
		            			var temp = jQuery(this).attr('minvalue');
		            			if(parseInt(jQuery(this).val()) < parseInt(temp))
		            			{
		            				var addStr = '';
			            			if(jQuery(this).attr('alt') != null && jQuery(this).attr('alt') != 'undefined')
			            			{		            				
			            				var alts = jQuery(this).attr('alt');
				            			if(alts != '') addStr = '[' +alts+ ']';
			            			}
		            				
			            			jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		
		            				result = false;
				            		if(as_type){
				            			alert(msg_com_code_027+"(input:"+jQuery(this).val()+" min:"+temp+")"+addStr);
				            		} else 
				            		{			            			
				            			msgStart(msg_com_code_027+"(input:"+jQuery(this).val()+" min:"+temp+")"+addStr, 'warning', null);
				            		}
				            		return false;	
		            			}
		            		} else
		            		{
		            			jQuery(this).removeClass('ui-state-error-custom');
		            		} 
		            		
		            	}
		            	
		            	
		            	if(jQuery(this).attr('comparedates') != null)
		            	{	
		            		var temp = jQuery(this).attr('comparedates');			            		
		            		var compareFields = temp.split(':')[0];			            		
		            		var nameOrId = temp.split(':')[1];	//id or name		            		
		            		
		            		//console.log('compareFields:'+compareFields);
		            		//console.log('nameOrId:'+nameOrId);
		            		var compareFiledArray = compareFields.split(',');
		            		var start_dt = 0;
		            		var end_dt = 0;			            		
		            		if(nameOrId.toLowerCase() == 'id')
		            		{
		            			//console.log("===========>"+compareFiledArray[0]+', '+compareFiledArray[1]);
		            			start_dt = jQuery('#'+compareFiledArray[0]).val();
		            			end_dt = jQuery('#'+compareFiledArray[1]).val();
		            			//console.log('start_dt:'+start_dt+', end_dt:'+end_dt);
		            		} else if(nameOrId.toLowerCase() == 'name')
		            		{
		            			start_dt = jQuery('input[name='+compareFiledArray[0]+']').val();
		            			end_dt = jQuery('input[name='+compareFiledArray[1]+']').val();
		            		} else
		            		{
		            			start_dt = 0;
		            			end_dt = 0;
		            		}
		            		//console.log('1start_dt:'+start_dt);
		            		//console.log('1end_dt:'+end_dt);
		            		if(start_dt != 0 && end_dt != 0)
		            		{
		            			start_dt = charReplace(start_dt, "-", "");
		            			end_dt = charReplace(end_dt, "-", "");
		            			
		            			start_dt = charReplace(start_dt, ":", "");
		            			end_dt = charReplace(end_dt, ":", "");
		            			//console.log('2start_dt:'+start_dt);
			            		//console.log('2end_dt:'+end_dt);
		            			try
		            			{
			            			start_dt = parseInt(start_dt);
			            			end_dt = parseInt(end_dt);
			            			//console.log('3start_dt:'+start_dt);
				            		//console.log('3end_dt:'+end_dt);
			            			if(start_dt > end_dt)
			            			{
			            				result = false;
			            				if(as_type)
					            		{
					            			alert(msg_com_code_003);
					            		} else {
					            			msgStart(msg_com_code_003, 'warning', null);
					            		}
			            				jQuery(this).addClass('ui-state-error-custom');
					            		jQuery(this).focus();
					            		jQuery(this).select();
					            		return false;
			            			} else
			            			{
			            				jQuery(this).removeClass('ui-state-error-custom');
			            			}
			            			
		            			} catch(e)
		            			{			            				
		            			}
		            		}
		            		
		            	}
		            	
		            	/*else
		            	{
		            		result = true;
		            		return true;
		            	}*/
	            		
		            	var format = jQuery(this).attr('format');
	            		if(format != 'undefined' && jQuery(this).val() != '')
	            		{	
	            			if(format == 'dateFrmt')
	            			{	
	            				if(!dateFrmt.test(jQuery(this).val()) || !validDate(jQuery(this).val()))	            				
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		result = false;
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:yyyy-MM-dd)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:yyyy-MM-dd)', 'warning', null);
				            		}
				            		
				            		
				            		return false;
				            
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format=='yyyyMMFrmt')
	            			{
	            				if(!yyyyMMFrmt.test(jQuery(this).val()) || !validDate(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		result = false;
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:yyyy-MM)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:yyyy-MM)', 'warning', null);
				            		}					            		
				            		return false;
				            
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format == 'timeFrmt')
	            			{
	            				if(!timeFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		result = false;	
				            		
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:hh:mi)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:hh:mi)', 'warning', null);
				            		}
				            		
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format == 'numberFrmt')
	            			{
	            				if(!numberFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		result = false;	
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:number)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:number)', 'warning', null);
				            		}
				            		
				            		
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format == 'emailFrmt')
	            			{
	            				if(!emailFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:email)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:email)', 'warning', null);
				            		}
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			}  else if(format == 'idFrmt')
	            			{
	            				if(!idFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type){
				            			alert(user_join_00003);
				            		} else {
				            			msgStart(user_join_00003, 'warning', null);
				            		}
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            				
	            			} else if(format == 'birthDayFrmt')
	            			{
	            				if(!birthDayFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:dd/mm/yyyy ex)30/07/1990)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:dd/mm/yyyy ex)30/07/1990)', 'warning', null);
				            		}
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            				
	    					} else if(format == 'hpFrmt')
	            			{
	            				if(!hpFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:hp) ');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:hp)', 'warning', null);
				            		}
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format == 'telFrmt')
	            			{
	            				if(!telFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type){
				            			alert(msg_com_code_004+' (required:tel)');
				            		} else {
				            			msgStart(msg_com_code_004+' (required:tel)', 'warning', null);
				            		}
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            			} else if(format == 'passwdFrmt')
	            			{	
	            				
	            				var samePass_0 = 0; //동일문자 카운트
	            				var samePass_1 = 0; //연속성(+) 카운드
	            				var samePass_2 = 0; //연속성(-) 카운드
	            				
	            				var chr_pass_0;
	            				var chr_pass_1;
	            				for(var i=0; i < jQuery(this).val().length; i++) 
	            				{
	            					  chr_pass_0 = jQuery(this).val().charAt(i);
	            					  chr_pass_1 = jQuery(this).val().charAt(i+1);
	            					  
	            					  //동일문자 카운트
	            					  if(chr_pass_0 == chr_pass_1) 
	            					  {
	            						  samePass_0 = samePass_0 + 1;
	            					  } 
	            					  
	            					  //연속성(+) 카운드
	            					  if(chr_pass_0.charCodeAt(0) - chr_pass_1.charCodeAt(0) == 1) {
	            						  samePass_1 = samePass_1 + 1;
	            					  } // if
	            					 
	            					  //연속성(-) 카운드
	            					  if(chr_pass_0.charCodeAt(0) - chr_pass_1.charCodeAt(0) == -1) {
	            						  samePass_2 = samePass_2 + 1;
	            					  } // if
	            					  
	            				}
	            				
	            				//console.log('동일문자 samePass_0:'+samePass_0); 동일문자 세개이상, 연속성(+)세개이상,연속성(-)세개이상
	            				//console.log('연속성(+) samePass_1:'+samePass_1);
	            				//console.log('연속성(-) samePass_2:'+samePass_2);
	            				
	            				
	            				if(samePass_0 >2 || samePass_1 > 2 || samePass_2 > 2 )  
	            				{
	            					
	            					if(as_type)
				            		{
				            			alert(msg_com_code_106);
				            		} else 
				            		{
				            			msgStart(msg_com_code_106, 'warning', null);
				            		}
	            					result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}

	            				
	            				if(!passwdFrmt.test(jQuery(this).val()))
	            				{
	            					jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		if(as_type)
				            		{
				            			alert(msg_com_code_103);
				            		} else 
				            		{
				            			msgStart(msg_com_code_103, 'warning', null);
				            		}
				            		
				            		result = false;	
				            		return false;
	            				} else
	            				{
	            					jQuery(this).removeClass('ui-state-error-custom');
	            				}
	            				
	            				
	            				
	            			}  
	            			
	            			
	            		}
	            		
	            		if(jQuery(this).attr('type') == 'textarea' && jQuery(this).attr('maxlength') != '-1')
		            	{	
		            		var maxLength = parseInt(jQuery(this).attr('maxlength'));			            		
		            		var textAreaLength = parseInt(jQuery(this).val().length);
		            		
		            		if(textAreaLength > maxLength)
		            		{
		            			jQuery(this).addClass('ui-state-error-custom');
			            		jQuery(this).focus();
			            		jQuery(this).select();
			            		result = false;
			            		if(as_type)
			            		{
			            			alert(msg_com_code_005+'(actual:'+textAreaLength+' limit:'+maxLength+')');
			            		} else {
			            			msgStart(msg_com_code_005+'(actual:'+textAreaLength+' limit:'+maxLength+')', 'warning', null);
			            		}
			            		
			            		
			            		return false;
		            		} else
		            		{
		            			jQuery(this).removeClass('ui-state-error-custom');
		            		}
		            	}
		            	
		            	if(jQuery(this).attr('multiple') == null && jQuery(this).attr('type') != 'select' && jQuery(this).attr('minlength') != '-1')
		            	{	
		            		try
		            		{
			            		var minLength = parseInt(jQuery(this).attr('minlength'));
			            		var currentLength = parseInt(jQuery(this).val().length);
			            		if(currentLength < minLength)
			            		{
			            			jQuery(this).addClass('ui-state-error-custom');
				            		jQuery(this).focus();
				            		jQuery(this).select();
				            		result = false;
				            		if(as_type)
				            		{
				            			alert(msg_com_code_006+'(actual:'+currentLength+' minimum:'+minLength+')');
				            		} else {
				            			msgStart(msg_com_code_006+'(actual:'+currentLength+' minimum:'+minLength+')', 'warning', null);
				            		}
				            		
				            		
				            		return false;
			            		} else
			            		{
			            			jQuery(this).removeClass('ui-state-error-custom');
			            		}
		            		} catch(e)
		            		{
		            			console.log(jQuery(this).attr('id'));
		            		}
		            	}
	            	}
	            	
	            );
	       		return result;
            }
			
		);
		return result;	
}

function checkNowDateTime(start_dt, end_dt){
	
	var dateCheck = false;
	var start = new Date(start_dt);
	var end = new Date(end_dt);
	var now = new Date();
	
	var start_time = start.getHours()*60+start.getMinutes();
	var end_time = end.getHours()*60+end.getMinutes();
	var now_time = now.getHours()*60+now.getMinutes();
	
	if(end_time == 0)
		end_time = 24*60;
			
	if( (start.getFullYear() <= now.getFullYear()) && (now.getFullYear() <= end.getFullYear()) ){
		if( (start.getMonth() <= now.getMonth()) && (now.getMonth() <= end.getMonth()) ){
			if( (start.getDate() <= now.getDate()) && (now.getDate() <= end.getDate()) ){
				if( (start_time <= now_time) && (now_time <= end_time) ){
					dateCheck = true;
				}
			}
		}
	}
	
	return dateCheck;
}

function getAgoDate(yyyy, mm, dd)
{
	var today = new Date();
    var year = today.getFullYear();
   	var month = today.getMonth();
   	var day = today.getDate();
   	
   	var resultDate = new Date(yyyy+year, month+mm, day+dd);
  	 
  	year = resultDate.getFullYear();
    month = resultDate.getMonth() + 1;
    day = resultDate.getDate();
 
    if (month < 10)
    	month = "0" + month;
    
    if (day < 10)
    	day = "0" + day;
 
    return year + "-" + month + "-" + day;
}

function getAgoTime()
{
	var today = new Date();
    var hour = today.getFullYear();
   	var min = today.getMonth();
   	
   	var resultDate = new Date();
  	 
   	hour = resultDate.getHours();
   	min = resultDate.getMinutes();
 
    if (hour < 10)
    	hour = "0" + month;
    
    if (min < 10)
    	min = "0" + day;
 
    return hour + ":" + min;
}

/**
 * msgStart(msgcode);
 */
function msgStart(text, status, interval, width, height)
{
	if( status == null || status == 'undefined' )
		status ='success';
	
	
	if( interval == null || interval == 'undefined' )
		interval = parseInt(10)*parseInt(1000);
	
	jQuery('#msg_box_msg').empty();
	jQuery('#msg_box_msg').html(text);	

	jQuery("#popWindow").css('top', posTop() + 75 );	
	jQuery("#popWindow").css('left',posLeft());

	var className = 'alert alert-info alert-dismissable';

	if(status === 'danger'){
		className = 'alert alert-danger alert-dismissable';
	}else if(status === 'success'){
		className = 'alert alert-success alert-dismissable';	
	}else if(status === 'warning'){
		className = 'alert alert-warning alert-dismissable';
	}else if(status === 'info'){
		className = 'alert alert-info alert-dismissable';
	}

	jQuery("#clsAlertStatus").attr('class', className);
	var options = null;
	//jQuery("#popWindow").show(options,100,msgStop(interval));
	
	com_alarm_dialog("alarm_dialog", "알림창", text, width, height);
	//document.getElementById('popWindow').focus();
	
}


/**
 * 메세지창을 멀티창으로 연다 
 * @param agument 6  (기본값:멀티창, 옵션 기존창) 추가
 * msgStartMulti("멀티창1");
 * msgStartMulti("멀티창2");
 * msgStartMulti("멀티창3");
 * msgStartMulti("단일창","","","","","Y");
 */
function msgStartMulti(text, status, interval, width, height, overlap_yn)
{
	
	var msg_win_cnt = jQuery("div.ui-dialog").length;
	var id_num = (msg_win_cnt+1);
	com_alarm_dialogMulti(id_num, "알림창", text, width, height, overlap_yn);
}


function com_alarm_dialogMulti(id_num, title_text, content_text, width, height,  overlap_yn)
{
	//멀티창이냐 단일창이냐~
	if(overlap_yn != null && overlap_yn=="Y" )
	{
		if(id_num == 1)
		{
			jQuery("#alarm_dialog").parent().append( jQuery("#alarm_dialog").clone().wrapAll("<div/>").parent().html().replace("alarm_dialog","alarm_dialog"+id_num).replace("alarm_content","alarm_content"+id_num));
		}
		else
		{
			id_num = 1;
		}
	}
	else
	{
		jQuery("#alarm_dialog").parent().append( jQuery("#alarm_dialog").clone().wrapAll("<div/>").parent().html().replace("alarm_dialog","alarm_dialog"+id_num).replace("alarm_content","alarm_content"+id_num));		
	}
	
	var id = "alarm_dialog"+id_num;	
	com_dialogMulti(id, title_text, false, width, height);
	jQuery('.ui-widget-overlay').remove();
	jQuery('.alarm_content'+id_num).html('" '+content_text+' "');
	jQuery('.alarm_content'+id_num).css({"line-height": "20px","font-weight": "bold", "width": "100%", "height": "100%",  "text-align": "center",  "color": "red" }	);
	
	var $obj = jQuery("div[aria-describedby='"+id+"']");

	//메세지창이 1개이상이면 약간씩 움직인다.
	if(id_num > 1)
	{
		var top = $obj.offset().top;
		var left = $obj.offset().left;
		$obj.css("top", top+ (10*id_num));
		$obj.css("left", left+ (10*id_num));
	}
	jQuery("div[aria-describedby='alarm_dialog"+id_num+"'] .ui-dialog-titlebar-close").hide();
}

function com_dialogMulti(id, title_text, modal_yn, width, height){
	
	if(modal_yn == null)
		modal_yn = true;
		
	if(height == null || height == "")
		height = 150;
	
	if(width == null || width == "")
		width = 350;
	
	jQuery( "#"+id ).removeClass('hide').dialog({
		resizable: false,
		modal: modal_yn,
		width:width,
		maxWidth:500,
		height: height,
		maxHeight:500,
		title: title_text,
		title_html: true,
		dialogClass : "ismart-dialog",		
		buttons: [
			{
				id:id+"_close",
				html: "<i class='fa fa-remove bigger-110'></i>&nbsp;닫기",
				"class" : "btn btn-minier btn-success",
				click: function() {
					jQuery( this ).remove();
				}
			}
		]
	});
}





function msgStart_alarm_dialog(id, body_text, modal_yn, width, height, title, callbackFunc)
{
	var v_title = "알림창";
	if( arguments.length == 6)
	{
		v_title = title;
	}
	
	if(callbackFunc == undefined || callbackFunc == "undefined"   || callbackFunc == null )
	{
		callbackFunc = "";
	}
		
		
	var html = []; 
	html.push('<div class="head"><span id="msg_title">'+v_title+'</span></div>' );
	html.push('<div class="body">');
	html.push('		<span id="msg_body">'+body_text+'</span>');
	html.push('<div class="body">');
	html.push('<div style="text-align: center; margin-top: 45px;">');
	html.push('			<a onClick="javascript:jQuery(\'#popWindow\').hide();'+callbackFunc+'" class="btn btn-purple btn-small">확인</a> ');
	html.push('</div>');
	html.push('</div>');
	
	jQuery("#clsAlertStatus").html( html.join(""));
	jQuery("#popWindow").addClass("confirmMsgLocation");
	jQuery("#popWindow").show();
	jQuery("#popWindow" ).draggable();	
}



function com_alarm_dialog(id, title_text, content_text, width, height){
	com_dialog(id, title_text, false, width, height);
	jQuery('.ui-widget-overlay').remove();
	jQuery('.alarm_content').html('" '+content_text+' "');
	jQuery('.alarm_content').css('line-height', '20px' );
}
//dialog 띄우기
function com_dialog(id, title_text, modal_yn, width, height){
	
	if(modal_yn == null)
		modal_yn = true;
		
	if(height == null || height == "")
		height = 150;
	
	if(width == null || width == "")
		width = 350;
	
	jQuery( "#"+id ).removeClass('hide').dialog({
		resizable: false,
		modal: modal_yn,
		width:width,
		maxWidth:500,
		height: height,
		maxHeight:500,
		title: title_text,
		title_html: true,
		dialogClass : "ismart-dialog",		
		buttons: [
			{
				id:id+"_close",
				html: "<i class='fa fa-remove bigger-110'></i>&nbsp;닫기",
				"class" : "btn btn-minier btn-success",
				click: function() {
					jQuery( this ).dialog( "close" );
				}
			}
		]
	});
}

var msgStopObj = null;
function msgStop(interval)
{
	msgStopObj = setTimeout(function(){
		var options = {};	
		jQuery("#popWindow").hide(null,0,"");					
		//jQuery("#effect:visible").removeAttr('style').hide().fadeOut();
	}, interval);	
}

function msgStopNow()
{
	jQuery("#popWindow").hide(null,0,"");
	clearTimeout(msgStopObj);
}


function posLeft() {
	return typeof window.pageXOffset != 'undefined' ? window.pageXOffset :document.documentElement && document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft ? document.body.scrollLeft : 0;
} 

function posTop() {

	/*console.log("window.pageYOffset: " + window.pageYOffset);
	console.log("document.documentElement.scrollTop: " + document.documentElement.scrollTop);
	console.log("document.body.scrollTop: " + document.body.scrollTop);
	console.log("window.pageXOffset: " + window.pageXOffset);*/
	return typeof window.pageYOffset != 'undefined' ?  window.pageYOffset : document.documentElement && document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ? document.body.scrollTop : 0;
} 

function posRight() {return posLeft()+pageWidth();} 

function posBottom() {return posTop()+pageHeight();}

function createXMLFromString(string) 
{
	var xmlDocument;
	var xmlParser;
	if(window.ActiveXObject)
	{ //IE일 경우
		xmlDocument = new ActiveXObject('Microsoft.XMLDOM');
		xmlDocument.async = false;
		xmlDocument.loadXML(string);
	} else if (window.XMLHttpRequest) 
	{ 
		//Firefox, Netscape일 경우
		xmlParser = new DOMParser();
		xmlDocument = xmlParser.parseFromString(string, 'text/xml');
	} else 
	{
		return null;
	}
	return xmlDocument;
}

function processGridError(xhr, str, err)
{
	if(xhr.status == '200')
	{
		var xml = createXMLFromString(xhr.responseText);	
		processSimpleResponseXML(xml, null);
	} else
	{
		msgStart('Unknown error:'+err, 'danger');
	}
}


function processSimpleResponseXML(response, _type)
{
	try
	{
		var xmlDoc = null;
		if(_type == 'grid')
		{
			xmlDoc = response.responseXML; 
			//xmlDoc = createXMLFromString(response);
		} else
		{
			xmlDoc = response;
		}
		
        var code = jQuery(xmlDoc).find('code').text();
        var msg = jQuery(xmlDoc).find('msg').text();
        
        if(code == '200')
        {
        	msgStart(msg_com_code_009, 'success');    
        	if(_type == 'grid') return [true,'',''];
        } else
        {
        	msgStart(msg_com_code_010+"("+msg+")", 'warning');
        	if(_type == 'grid') return [false,'','fail'];
        	//return [true,'',''];
        	//return [false,'','fail'];
        }
        
        
	} catch(e)
	{}
}

function makeCodeSelectBoxOnGridByXml(response, value)
{
	var selectObj = document.createElement('select');
	//var xmlDoc = response.responseXML;
	var xmlDoc = createXMLFromString(response);
	//alert(xmlDoc);
	var code = jQuery(xmlDoc).find('code').text(); 		
	var data = jQuery(xmlDoc).find('data').text();
	var msg =  jQuery(xmlDoc).find('msg').text();
	var cd = null;
	var val = null;
	var options = null;
	if(code == '200')
	{
		jQuery(xmlDoc).find('cd_list').each(function(){			   			
		cd = jQuery(this).find('cd').text();
		val = jQuery(this).find('cd_nm').text();
		//console.log(cd);
		options = document.createElement('option');
		 	options.setAttribute('value', cd);
		 	options.innerHTML = jQuery.trim(val);
									
		 	selectObj.appendChild(options);
		});	
		var _html = jQuery(selectObj).clone().wrapAll("<div/>").parent().html();
		return _html;
	} else
	{
		msgStart(msg, 'warning');
		return "";
	}
	
}


/*
 * var args = new Array();
args.push("ㅁㅁㅁㅁ");

window[func].apply(null,[arg1, arg2]);
 */
/**
 *	
 */
function makeSelectBox(url, htmlObj, data, _callbackFunc, isFirstOptionDel)
{
	var _url = url;
	var _data = data;	

	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
		data : _data,
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			var etc1 = null;
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, isFirstOptionDel);   	
				
   				var selectObj = document.getElementById(htmlObj);
   				
   				var fn = window[_callbackFunc];
   				
   				if( fn != null && fn != undefined && fn != 'undefined')
   				{
   				
	   		   		if(selectObj.addEventListener){   		   		
		   				selectObj.addEventListener('change',fn, false);
	   		   		}
		   			else if(selectObj.attachEvent){
		   				selectObj.attachEvent('onchange',fn);
		   			}
		   			else if(selectObj.onchange){
		   				selectObj.onchange = fn;
		   			}
   				}
   				var options = null;
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			etc1 = jQuery(this).find('etc1').text();
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.setAttribute('etc1', etc1);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}


function CreateSelectBox(url, htmlObj, blockName, selectCd, selectText, isFirstOptionDel)
{
	var _url = url;

	var http = jQuery.ajax( {
   		url: _url,	   		
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, isFirstOptionDel);   					
   				selectObj = document.getElementById(htmlObj);
   				
	   			jQuery(xmlDoc).find(blockName).each(function(){			   			
		   			cd = jQuery(this).find(selectCd).text();
		   			val = jQuery(this).find(selectText).text();
		   			code = jQuery(this).find('cd').text();
		   			
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.setAttribute('code', code);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function createSelectBoxFromUrl(url, htmlObj, blockName, selectCd, selectText, isFirstOptionDel)
{
	var _url = url;

	var http = jQuery.ajax( {
   		url: _url,	   		
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var msg =  jQuery(xmlDoc).find('msg').text();
				
			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, isFirstOptionDel);   					
   				var selectObj = document.getElementById(htmlObj);
   				var options = null;
	   			jQuery(xmlDoc).find(blockName).each(function(){			   			
		   			cd = jQuery(this).find(selectCd).text();
		   			val = jQuery(this).find(selectText).text();
		   		  code = 		jQuery(this).find(cd).text();
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.setAttribute('code', code);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function YearSelectBox(htmlObj, nextYear, isFirstOptionDel)
{
	var _url = '/common/action/code.jspx?cmd=getYear';
	
	var day = new Date();
	var year = day.getFullYear();
	
	if(nextYear==""){
		nextYear = 0;
	}
	
	
	var http = jQuery.ajax( {
   		url: _url,	   		
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, isFirstOptionDel);   					
   				var selectObj = document.getElementById(htmlObj);
   				var options = null;
   				jQuery(xmlDoc).find('year_list').each(function(){		
		   			cd = jQuery(this).find('year').text();
		   			val = jQuery(this).find('year').text();
		   		});		   
					
					for(var i=nextYear; i>0; i--){
   					options = document.createElement('option');
   				 	options.setAttribute('value', parseInt(cd)+i);
   				 	options.innerHTML = jQuery.trim(parseInt(cd)+i+"년");
		   										
   				 	selectObj.appendChild(options);
   				}
					
					
   				for(var i=0; i<10; i++){
	   				options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	
   				 	if(i==0){
	   					options.setAttribute('selected', 'selected');
	   				}
   				 	
   				 	options.innerHTML = jQuery.trim(cd+"년");
		   										
   				 	selectObj.appendChild(options);
   				 	cd--;
	   			}
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

//사용중 - 변경X
function MonthSelectBox(htmlObj, current, isFirstOptionDel)
{
	var day = new Date();
	var month = day.getMonth();

	removeOptionAll(htmlObj, isFirstOptionDel);   					
	var selectObj = document.getElementById(htmlObj);
	var options = null;
	for(var mon=1; mon<=12; mon++){

		if(mon<10){
			mon = "0"+mon;
		}
		
		options = document.createElement('option');
		options.setAttribute('value', mon);
		if(mon == month+1){
			options.setAttribute('selected', 'selected');
		}
		options.innerHTML = jQuery.trim(mon+"월");
		
		selectObj.appendChild(options);
		
		if( mon == month+1 && !(current == null) ){
			break;
		}
		
	}	
}

//month = EX) 201408.
//current_day = 현재 일.
function DaySelectBox(htmlObj, month, current_day, isFirstOptionDel)
{
	var _url = '/common/action/code.jspx?cmd=getDay&month='+month;
	
	var http = jQuery.ajax( {
   		url: _url,	   		
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(json) 
   		{
			var code = json.result.code; 		
	   		var msg =  json.result.msg;

	   		var last_day = "";
	   		
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, isFirstOptionDel);   					
   				selectObj = document.getElementById(htmlObj);
   				
   				last_day = json.result.data.last_day_list[0].last_day;

   				for(var day=1; day<=last_day; day++){
   					
   					if(day<10){
   						day = "0"+day;
   					}
	   				options = document.createElement('option');
   				 	options.setAttribute('value', day);
	   				if(day == current_day){
	   					options.setAttribute('selected', 'selected');
	   				}
   				 	options.innerHTML = jQuery.trim(day+"일");
		   										
   				 	selectObj.appendChild(options);
	   			}
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});
}

function makeCodeSelectBox(div_cd, p_code, htmlObj, isFirstOptionDel, isUseEnable)
{	

	var _url = '/common/action/code.jspx?cmd=getCodeList';

	//사용가능한 코드만 
	if(isUseEnable){
		_url = '/common/action/code.jspx?cmd=getUseCodeList';
	}
	var selectObj = null; 
	var options =null; 
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "GET",
		data : 'div_cd='+div_cd+'&prnt_cd='+p_code,
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			var use_yn = null;

			if(code == '200')
	   		{
	   			if(p_code == '')
   				{
   					removeOptionAll(htmlObj, isFirstOptionDel);
   					selectObj = document.getElementById(htmlObj);
   				} else
   				{
   					removeOptionAll(htmlObj, isFirstOptionDel);
   					selectObj = document.getElementById(htmlObj);		   					
	   			}
		   		
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			
				   	options = document.createElement('option');
		   			options.setAttribute('value', cd);
		   			options.innerHTML = jQuery.trim(val);	   				 	
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function makeHourSelectBox(htmlObj)
{	
	selectObj = document.getElementById(htmlObj);		   					
	
	var hour = 24;
	var hdx;
	
	for(hdx=1; hdx<=hour; hdx++){
		cd = time_Form(hdx);
		val = time_Form(hdx);
		
		options = document.createElement('option');
		options.setAttribute('value', cd);
		options.innerHTML = jQuery.trim(val+"시");	   	
		
		selectObj.appendChild(options);
	}
}

function makeMinSelectBox(htmlObj)
{	
	selectObj = document.getElementById(htmlObj);		   					
	
	var min = 60;
	var mdx;
	
	for(mdx=0; mdx<min; mdx++){
		if(mdx%5 == 0){
			cd = time_Form(mdx);
			val = time_Form(mdx);
			
			options = document.createElement('option');
			options.setAttribute('value', cd);
			options.innerHTML = jQuery.trim(val+"분");	   	
			
			selectObj.appendChild(options);
		}
	}
}

/*
 * 분류코드, 부모코드, html찍을 객체, radio Html ID, 필수체크여부, 사용가능한코드만노출여부, 한줄에 보일 최대갯수
 * */
function getCodeRadio(div_cd, p_code, htmlObj, radioID, isRequired, isUseEnable, rowMaxNum)
{	

	var _url = '/common/action/code.jspx?cmd=getCodeList';

	//사용가능한 코드만 
	if(isUseEnable){
		_url = '/common/action/code.jspx?cmd=getUseCodeList';
	}

	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "GET",
		data : 'div_cd='+div_cd+'&prnt_cd='+p_code,
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			var use_yn = null;

			if(code == '200')
	   		{
				jQuery(htmlObj).html('');
		   		var _html = [];
		   		var i = 0;
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			if(!isRequired)
		   			{
		   				_html.push("<input type='radio' id='"+radioID+"' name='"+radioID+"' value='"+cd+"'>");
		   			} else
		   			{
		   				_html.push("<input type='radio' id='"+radioID+"' name='"+radioID+"' value='"+cd+"' required='true'>");
		   			}
		   			_html.push(val);
		   			
		   			i++;
		   			
		   			if(i != 0 && i%rowMaxNum == 0) _html.push("<p/>");
		   			
		   			
		   		});
	   			
	   			jQuery('#'+htmlObj).html(_html.join(""));
	   			_html = [];
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

/*
 * 분류코드, 부모코드, html찍을 객체, radio Html ID, 필수체크여부, 사용가능한코드만노출여부, 한줄에 보일 최대갯수
 * */
function getCodeCheckBox(div_cd, p_code, htmlObj, isRequired, isUseEnable, rowMaxNum)
{	

	var _url = '/common/action/code.jspx?cmd=getCodeList';

	//사용가능한 코드만 
	if(isUseEnable){
		_url = '/common/action/code.jspx?cmd=getUseCodeList';
	}

	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "GET",
		data : 'div_cd='+div_cd+'&prnt_cd='+p_code,
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			var use_yn = null;

			if(code == '200')
	   		{
				jQuery(htmlObj).html('');
		   		var _html = [];
		   		var i = 0;
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			
		   			_html.push("<span id=span_"+cd+">");
		   			
		   			if(!isRequired)
		   			{
		   				_html.push("<input type='checkbox' id='"+cd+"' name='"+cd+"' value='Y'>");
		   			} else
		   			{
		   				_html.push("<input type='checkbox' id='"+cd+"' name='"+cd+"' value='Y' required='true'>");
		   			}
		   			_html.push(val);
		   			_html.push("</span>");
		   			
		   			i++;
		   			
		   			if(i != 0 && i%rowMaxNum == 0) _html.push("<br/>");
		   			
		   			
		   		});
	   			
	   			jQuery('#'+htmlObj).html(_html.join(""));
	   			_html = [];
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}


function makeSelectBoxCodeFromURL(_url, htmlObj, isFirstOptionDel)
{	
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   			var cd= null;
	   			var val = null;
	   			var options = null;
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function makeSelectBoxFromURL(_url, htmlObj, isFirstOptionDel, blockname, p_cd, p_val)
{	
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   			var cd= null;
	   			var val = null;
	   			var options = null;
	   			jQuery(xmlDoc).find(blockname).each(function(){			   			
		   			cd = jQuery(this).find(p_cd).text();
		   			val = jQuery(this).find(p_val).text();
		   			
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

/*
 * _url : server url
 * htmlObj : selectbox가 놓일 html Object
 * initObj : 초기값(ex ["", "없음"])
 * isFirstOptioDel : true, false(select object가 새로 그려질 경우 첫번째 options를 지울지 여부)
 * */
function makeSelectBoxCodeInitValueFromURL(_url, htmlObj, initObj, isFirstOptionDel)
{
	var initVal = initObj[0];
	var initTxt = initObj[1];
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   			
				var options = document.createElement('option');
				options.setAttribute('value', initVal);	
				options.innerHTML = jQuery.trim(initTxt);
				selectObj.appendChild(options);
				
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function makeExtendSelectBoxCodeFromURL(_url, htmlObj, isFirstOptionDel, extendElements)
{	
	console.log('extendElements.length:'+extendElements.length);
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   					   		
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			var cd = jQuery(this).find('cd').text();
		   			var val = jQuery(this).find('cd_nm').text();
		   			
		   			var options = document.createElement('option');
		   			options.setAttribute('value', cd);
		   			options.innerHTML = jQuery.trim(val);
		   			for(i=0;i<extendElements.length; i++)
		   			{		   				
		   				var temp = jQuery(this).find(extendElements[i]).text();
		   				
		   				options.setAttribute(extendElements[i], temp);
		   			}							
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}


function makeSelectBoxGroupList(htmlObj, isFirstOptionDel)
{	
	var http = jQuery.ajax( {
   		url: '/common/action/common.jspx?cmd=getGroupList',	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   					   		
	   			jQuery(xmlDoc).find('group_list').each(function(){			   			
		   			var cd = jQuery(this).find('grp_no').text();
		   			var val = jQuery(this).find('grp_cd').text()+"("+jQuery(this).find('grp_nm').text()+")";
		   			var upd_enable_yn = jQuery(this).find('upd_enable_yn').text();
		   			var options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.setAttribute('upd_enable_yn', upd_enable_yn);
   				 	options.setAttribute('etc1', jQuery(this).find('grp_nm').text());
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}

function makeSelectBoxSurveyList(htmlObj, isFirstOptionDel, yyyy, cust_type)
{	
	var http = jQuery.ajax( {
   		url: '/survey/action/survey_stats_common.jspx?cmd=getSurveyList&survey_yyyy='+yyyy + '&srch_cust_type=' + cust_type,	   		
   		type: "POST",
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{	   			
				removeOptionAll(htmlObj, isFirstOptionDel);
				var selectObj = document.getElementById(htmlObj);		   					
	   					   		
	   			jQuery(xmlDoc).find('survey_list').each(function(){			   			
		   			var cd = jQuery(this).find('survey_id').text();
		   			var paper_id = jQuery(this).find('paper_id').text();
		   			var cust_type = jQuery(this).find('cust_type').text();
		   			var survey_div_cd = jQuery(this).find('survey_div_cd').text();
		   			var val = jQuery(this).find('survey_title').text();
		   			
		   			var options = document.createElement('option');
   				 	options.setAttribute('value', cd + ':' + paper_id + ':' + cust_type + ':' + survey_div_cd );
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}



function removeOptionAll(objID, isFirstOptionDel)
{
	if(!isFirstOptionDel)
	{
		jQuery('#'+objID).find('option:not(:first)').remove();
	}
	else
	{
		jQuery('#'+objID).find('option:not(:first)').remove();
		jQuery('#'+objID).children().remove();
	}
	
	//jQuery("#prtn_div_cd").addOption(defaultOption, true);

    /*
	var elSel = document.getElementById(objID);	
	for (var i=elSel.length-1; i>=0; i--)
	{
		elSel.remove(i);
	}
	*/
}


function charReplace(Str,BeforeChar,AfterChar) 
{    
	//console.log(Str);
    var AfterString='';
    var cnt = 0;
    for (cnt = 0; cnt < Str.length; cnt++) {
        switch(Str.charAt(cnt)) {
            case BeforeChar:
                AfterString+=AfterChar;
                break;
            default:
                AfterString+=Str.charAt(cnt);
                break;
        }
    }
    
    return AfterString;
}	


function validDate(strDate)
{
	var END_OF_MONTH = [0,31,28,31,30,31,30,31,31,30,31,30,31];
	var nYear = null;
	var nMonth = null;
	var nDay = null;
	if(strDate.length == 8)
	{
		nYear = Number(strDate.substring(0,4));
		nMonth = Number(strDate.substring(4,6));
		nDay = Number(strDate.substring(6,8));
		//console.log(nMonth+'-'+nDay);
	}
	else
	{
	
		nYear = Number(strDate.substring(0,4));
		nMonth = Number(strDate.substring(5,7));
	
		if(strDate.length>7){
			nDay = Number(strDate.substring(8,10));
		}
	}
	//년 확인
	if(nYear <= 0) return false;

	//월 확인
	if(nMonth < 1 || nMonth > 12){
		return false;
	}

	//윤달 확인
	if(nYear % 4 == 0)
	 if(nYear % 100 != 0 || nYear % 400 == 0)
		 END_OF_MONTH[2] = 29;

	if(strDate.length>7){
		//일 확인
		if(nDay < 1 || END_OF_MONTH[nMonth] < nDay){
			return false;
		}
	}
	return true;
}


var __Uploader = null;
/*
 *   파일업로더(swfuploader) 초기화
	_size_limit :  개별파일당 제한 사이즈(KBytes)
	_upload_limit : 한번에 업로드할수 있는 파일수
	_types : All(*.*), (*.mov;*.avi;*.mpg;*.mpeg;*.mpe;*.wmv) : 허용되는 확장자
	_post_params_array : Array type( {"key1":value1, "key2":value2} ) : 파일 업로드시 같이 넘어가야할 파라미터
	_hidden_field_array : 업로드 결과 정보를 받아줄 히든필드 {"filename":"filenames", "filepath":"filepaths", "filesize":"filesizes", "extname":"extnames"}
*/

function initUploader(_size_limit, _upload_limit, _types, _post_params_array, _hidden_field_array, __success_call_func, isMultiSelectable, __button_image_url, _button_size)
{
	var btn_action = null;
	
	if(isMultiSelectable == null) SWFUpload.BUTTON_ACTION.SELECT_FILES;
	else
	{	
		if(!isMultiSelectable)
			btn_action = SWFUpload.BUTTON_ACTION.SELECT_FILE;
		else
			btn_action = SWFUpload.BUTTON_ACTION.SELECT_FILES;	
	}
	
	if(__button_image_url == null)
	{
		__button_image_url = "/static/lib/swfupload/img/XPButtonUploadText_70x22_ko.png"
	} else
	{
	}
	
	if(_button_size == null)
	{
		_button_size = new Array();
		_button_size[0] = 70;
		_button_size[1] = 22;
	}
	
	//console.log('__button_image_url'+__button_image_url);
	//console.log('_button_size'+_button_size);
	
	
	var settings = {
			flash_url : "/static/lib/swfupload/swfupload.swf",
			upload_url: '/bin/FileUploader',				
			post_params: _post_params_array,	
			// File Upload Settings
			file_size_limit : _size_limit,
			file_types : _types,    					
			file_types_description : "Select files",
			file_upload_limit : _upload_limit,
			file_queue_limit : 0,
			custom_settings : {
				progressTarget : "swfUploadProgress",
				cancelButtonId : "swfUploadBtnCancel",
				fileInfoHiddenFields : _hidden_field_array,
				callbackFunc : __success_call_func,
				statusDivId : 'divStatus'
			},	
			button_image_url : __button_image_url,	// Relative to the SWF file
			button_placeholder_id : "swfUploadBtn",
			button_width: _button_size[0],
			button_height: _button_size[1],   				
			// Event Handler Settings (all my handlers are in the Handler.js file)
			swfupload_loaded_handler : swfUploadLoaded,//1
			//file_dialog_start_handler : fileDialogStart,
			file_queued_handler : fileQueued,
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_start_handler : uploadStart,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccess,
			upload_complete_handler : uploadComplete,    
			queue_complete_handler : queueComplete,
			
			//button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			//button_cursor: SWFUpload.CURSOR.HAND,
			button_action : btn_action,						
			minimum_flash_version : "9.0.28",
			swfupload_pre_load_handler : swfUploadPreLoad,
			swfupload_load_failed_handler : swfUploadLoadFailed,			
			// Debug Settings
			debug: false
	};
	__Uploader = new SWFUpload(settings);
}

/*
 *   파일업로더(swfuploader) 초기화
 *   _uploaderObj : SWFUpload객체
 *   _targetDivArray : 버튼 등 초기화될 htmlObject의 Array(progressTarget, cancelButtonId, button_placeholder_id, status_id)
	_size_limit :  개별파일당 제한 사이즈(KBytes)
	_upload_limit : 한번에 업로드할수 있는 파일수
	_types : All(*.*), (*.mov;*.avi;*.mpg;*.mpeg;*.mpe;*.wmv) : 허용되는 확장자
	_post_params_array : Array type( {"key1":value1, "key2":value2} ) : 파일 업로드시 같이 넘어가야할 파라미터
	_hidden_field_array : 업로드 결과 정보를 받아줄 히든필드 {"filename":"filenames", "filepath":"filepaths", "filesize":"filesizes", "extname":"extnames"}
*/
function initUploaderMulti(_targetDivArray, _size_limit, _upload_limit, _types, _post_params_array, _hidden_field_array, __success_call_func, __param_form, isMultiSelectable)
{
	var btn_action = null;
	
	if(isMultiSelectable == null) SWFUpload.BUTTON_ACTION.SELECT_FILES;
	else
	{	
		if(!isMultiSelectable)
			btn_action = SWFUpload.BUTTON_ACTION.SELECT_FILE;
		else
			btn_action = SWFUpload.BUTTON_ACTION.SELECT_FILES;	
	}
	
	var _progressTarget = _targetDivArray[0];
	var _cancelButtonId = _targetDivArray[1];
	var _button_placeholder_id = _targetDivArray[2];
	var _statusDivId = _targetDivArray[3];
	var settings = {
			flash_url : "/static/lib/swfupload/swfupload.swf",
			upload_url: '/bin/FileUploader',				
			post_params: _post_params_array,	
			// File Upload Settings
			file_size_limit : _size_limit,
			file_types : _types,    					
			file_types_description : "Select files",
			file_upload_limit : _upload_limit,
			file_queue_limit : 0,
			custom_settings : {
				progressTarget : _progressTarget,
				cancelButtonId : _cancelButtonId,
				fileInfoHiddenFields : _hidden_field_array,
				callbackFunc : __success_call_func,
				statusDivId : _statusDivId,
				paramForm : __param_form
			},	
			button_image_url : "/static/lib/swfupload/img/XPButtonUploadText_70x22_en.png",	// Relative to the SWF file
			button_placeholder_id : _button_placeholder_id,
			button_width: 70,
			button_height: 22,   				
			// Event Handler Settings (all my handlers are in the Handler.js file)
			swfupload_loaded_handler : swfUploadLoaded,//1
			//file_dialog_start_handler : fileDialogStart,
			file_queued_handler : fileQueued,
			file_queue_error_handler : fileQueueError,
			file_dialog_complete_handler : fileDialogComplete,
			upload_start_handler : uploadStart,
			upload_progress_handler : uploadProgress,
			upload_error_handler : uploadError,
			upload_success_handler : uploadSuccessByjQueurySelector,
			upload_complete_handler : uploadComplete,    
			queue_complete_handler : queueComplete,
			
			//button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
			//button_cursor: SWFUpload.CURSOR.HAND,
			button_action : btn_action,						
			minimum_flash_version : "9.0.28",
			swfupload_pre_load_handler : swfUploadPreLoad,
			swfupload_load_failed_handler : swfUploadLoadFailed,			
			// Debug Settings
			debug: false
	};
	
	return settings;
	//_uploaderObj = new SWFUpload(settings);
	//alert(_uploaderObj);
}




/*
 * xls의 반올림 방식 구현
 * */
function roundXL(n, digits)
{
  if (digits >= 0) return parseFloat(n.toFixed(digits)); 

  digits = Math.pow(10, digits); 
  var t = Math.round(n * digits) / digits;

  return parseFloat(t.toFixed(0));
}

/*
 * 숫자에 세자리마다 ,를 찍는다.
 * */
function getNumberFormat(nStr)
{
	nStr += '';
	var x = nStr.split('.');
	var x1 = x[0];
	if( x1 == '') x1='0';
	var x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1)) {
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	
	return x1 + x2;
}

var FILE_EXTENTION_ICONS = ["bmp","doc", "docx", "gif", "hwp", "jpg", "jpeg", "tif", "txt", "xls", "xlsx", "zip", "pdf", "ppt", "pptx"];

/*
 * 공통 파일 다운로드 그리도
 * target_div : 그리드가 그려질 html object
 * isEnableDelete : 삭제 버튼 노출 여부
 * attach_div_cd : 첨부파일 구분자
 * data_no : 첨부파일이 귀속된 컨텐츠의 번호
 * */
function js_getFileEntries(target_div, isEnableDelete, attach_div_cd, data_no)
{	
	console.log('here'+'target_div:'+target_div+', attach_div_cd:'+attach_div_cd+', data_no'+data_no);
	var listEntries ="fileEntriesDiv_"+target_div;
	var pagerDiv = "pager_"+target_div;
	jQuery('#'+target_div).html("<table id=\""+listEntries+"\" class=\"table-bordered table-hover\" width=\"100%\"></table><div id=\"pagerDiv\"></div>");		
	
	jQuery.ajax({
		url: '/common/action/attach.jspx?cmd=getAttachList',
		data: 'attach_div_cd=' + attach_div_cd + '&data_no=' + data_no,
		type: 'POST',
		dataType: 'json',
		success: function(jsonObj)
		{
			if(jsonObj.result.code == 200){
				var listHtml = [];
				var json = jsonObj.result.data.file_entries;
				var jsonCnt = jsonObj.result.data.file_entries.length;
				
				if(jsonCnt > 0){
					listHtml.push('<thead>');
					listHtml.push('		<th style="display:none" class="filelist-header">파일번호</th>');
					listHtml.push('		<th style="text-align:center" class="filelist-header">파일명</th>');
					listHtml.push(' 	<th style="display:none" class="filelist-header">파일종류</th>');
					listHtml.push('		<th style="width:120px; text-align:center" class="filelist-header">파일사이즈</th>');
					listHtml.push('	 	<th style="width:150px; text-align:center" class="filelist-header">등록일시</th>');
					listHtml.push('	 	<th style="display:none" class="filelist-header">데이터번호</th>');
					listHtml.push('	 	<th style="display:none" class="filelist-header">분류코드</th>');
					listHtml.push(' 	<th style="display:none" class="filelist-header">서버파일명</th>');
					
					if(isEnableDelete == true) {
						listHtml.push('	<th style="width:50px; text-align:center" class="filelist-header">삭제</td>')
					} else {
						
					}
					
					listHtml.push('</thead>');
					listHtml.push('<tbody>');
					
					for(var i=0; i<jsonCnt; i++) {
						listHtml.push('		<tr id="trFileID_'+json[i].file_no+'" style="height:30px;">');
						listHtml.push('			<td style="display:none">' + json[i].file_no + '</td>');
						listHtml.push('			<td class="grid_link_download" style="cursor: pointer;" onclick="location.href= \'/common/action/attach.jspx?cmd=doDownload&file_no=' + json[i].file_no + '\'">' + getFileImg(json[i].ext_nm) + json[i].user_file_nm + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].ext_nm + '</td>');
						listHtml.push('			<td style="text-align:center">' + grid_fmt_fieSize(json[i].file_size) + '</td>');
						listHtml.push('			<td style="text-align:center">' + json[i].reg_dt + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].data_no + '</td>');	
						listHtml.push('			<td style="display:none">' + json[i].attach_div_cd + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].file_nm + '</td>');
						
						if(isEnableDelete == true) {
							listHtml.push('		<td align="center"><div class="btn-xs btn-danger" style="cursor: pointer; text-align:center;" onclick="js_delFileEntries(\''+json[i].file_no+'\'); return false;"><i class="icon-trash bigger-120"></i></div></td>');
						} else {
							
						}
						
						listHtml.push('		</tr>');
					}
					listHtml.push('</tbody>');
					
				} else {
					listHtml.push('		<tr>');
					listHtml.push('		  <td colspan="11" style="text-align:left;">첨부파일이 없습니다.</td>');
					listHtml.push('		</tr>');
				}
				
				jQuery("#"+listEntries).html(listHtml.join(''));
				listHtml = [];
			}
		}
	});
}

/*
 * 공통 파일 다운로드 그리도
 * target_div : 그리드가 그려질 html object
 * isEnableDelete : 삭제 버튼 노출 여부
 * attach_div_cd : 첨부파일 구분자
 * data_no : 첨부파일이 귀속된 컨텐츠의 번호
 * */
function js_getFileEntriesCnsltEmail(isEnableDelete, attach_div_cd, data_no)
{	
	var listEntries ="fileEntriesDiv_"+attach_div_cd;
	var listHtml = [];
	listHtml.push("<table id=\""+listEntries+"\" class=\"table-bordered table-hover\" width=\"100%\">");		
	
	jQuery.ajax({
		url: '/common/action/attach.jspx?cmd=getAttachList',
		data: 'attach_div_cd=' + attach_div_cd + '&data_no=' + data_no,
		type: 'POST',
		dataType: 'json',
		async:false,
		success: function(jsonObj)
		{
			if(jsonObj.result.code == 200){
				
				var json = jsonObj.result.data.file_entries;
				var jsonCnt = jsonObj.result.data.file_entries.length;
				
				if(jsonCnt > 0){
					listHtml.push('<thead>');
					listHtml.push('		<th style="display:none" class="filelist-header">파일번호</th>');
					listHtml.push('		<th style="text-align:center" class="filelist-header">파일명</th>');
					listHtml.push(' 	<th style="display:none" class="filelist-header">파일종류</th>');
					listHtml.push('		<th style="width:120px; text-align:center" class="filelist-header">파일사이즈</th>');
					listHtml.push('	 	<th style="width:150px; text-align:center" class="filelist-header">등록일시</th>');
					listHtml.push('	 	<th style="display:none" class="filelist-header">데이터번호</th>');
					listHtml.push('	 	<th style="display:none" class="filelist-header">분류코드</th>');
					listHtml.push(' 	<th style="display:none" class="filelist-header">서버파일명</th>');
					
					if(isEnableDelete == true) {
						listHtml.push('	<th style="width:50px; text-align:center" class="filelist-header">삭제</td>')
					} else {
						
					}
					
					listHtml.push('</thead>');
					listHtml.push('<tbody>');
					
					for(var i=0; i<jsonCnt; i++) {
						listHtml.push('		<tr id="trFileID_'+json[i].file_no+'" style="height:30px;">');
						listHtml.push('			<td style="display:none">' + json[i].file_no + '</td>');
						listHtml.push('			<td class="grid_link_download" style="cursor: pointer;" target="_blank" onclick="js_openFile( \'/common/action/attach.jspx?cmd=doDownloadForMail&file_no=' + json[i].file_no + '\');">' + getFileImg(json[i].ext_nm) + json[i].user_file_nm + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].ext_nm + '</td>');
						listHtml.push('			<td style="text-align:center">' + grid_fmt_fieSize(json[i].file_size) + '</td>');
						listHtml.push('			<td style="text-align:center">' + json[i].reg_dt + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].data_no + '</td>');	
						listHtml.push('			<td style="display:none">' + json[i].attach_div_cd + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].file_nm + '</td>');
						
						if(isEnableDelete == true) {
							listHtml.push('		<td align="center"><div class="btn-xs btn-danger" style="cursor: pointer; text-align:center;" onclick="js_delFileEntries(\''+json[i].file_no+'\'); return false;"><i class="icon-trash bigger-120"></i></div></td>');
						} else {
							
						}
						
						listHtml.push('		</tr>');
					}
					listHtml.push('</tbody>');
					
				} else {
					listHtml.push('		<tr>');
					listHtml.push('		  <td colspan="11" style="text-align:left;">첨부파일이 없습니다.</td>');
					listHtml.push('		</tr>');
				}				
			}
		}
	});
	listHtml.push("</table><div id=\"pagerDiv\"></div>");
	return listHtml.join('');
}

function js_openFile(url)
{
	window.open(url, '_black');
}

function js_getActFileEntries(target_div, isEnableDelete, attach_div_cd, data_no, act_no, _delCallBackFn)
{	
	var listEntries ="fileEntriesDiv_"+target_div;
	var pagerDiv = "pager_"+target_div;
	jQuery('#'+target_div).html("<table id=\""+listEntries+"\" class=\"table table-bordered table-hover\" width=\"100%\"></table><div id=\""+pagerDiv+"\"></div>");		
	
	jQuery.ajax({
		url: '/common/action/attach.jspx?cmd=getActAttachList',
		data: 'attach_div_cd='+attach_div_cd+'&data_no='+data_no+'&act_no='+act_no,
		type: "POST",
		dataType: 'json',
		success: function(jsonObj)
		{
			if(jsonObj.result.code == 200){
				var listHtml = [];
				var json = jsonObj.result.data.file_entries;
				var jsonCnt = jsonObj.result.data.file_entries.length;
				
				if(jsonCnt > 0){
					listHtml.push('<thead>');
					listHtml.push('		<th style="display:none" class="widget-header">파일번호</th>');
					listHtml.push('		<th style="text-align:center" class="widget-header">파일명</th>');
					listHtml.push(' 	<th style="display:none" class="widget-header">파일종류</th>');
					listHtml.push('		<th style="width:120px; text-align:center" class="widget-header">파일사이즈</th>');
					listHtml.push('	 	<th style="width:150px; text-align:center" class="widget-header">등록일시</th>');
					listHtml.push('	 	<th style="display:none" class="widget-header">데이터번호</th>');
					listHtml.push('	 	<th style="display:none" class="widget-header">분류코드</th>');
					listHtml.push(' 	<th style="display:none" class="widget-header">서버파일명</th>');
					
					if(isEnableDelete == true) {
						listHtml.push('	<th style="width:50px; text-align:center" class="widget-header">삭제</td>')
					} else {
						
					}
					
					listHtml.push('</thead>');
					listHtml.push('<tbody>');
					
					for(var i=0; i<jsonCnt; i++) {
						listHtml.push('		<tr id="trFileID_'+json[i].file_no+'" style="height:30px;">');
						listHtml.push('			<td style="display:none">' + json[i].file_no + '</td>');
						listHtml.push('			<td class="grid_link_download" style="cursor: pointer;" onclick="location.href= \'/common/action/attach.jspx?cmd=doDownload&file_no=' + json[i].file_no + '\'">'  + getFileImg(json[i].ext_nm) + json[i].user_file_nm + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].ext_nm + '</td>');
						listHtml.push('			<td style="text-align:center">' + grid_fmt_fieSize(json[i].file_size) + '</td>');
						listHtml.push('			<td style="text-align:center">' + json[i].reg_dt + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].data_no + '</td>');	
						listHtml.push('			<td style="display:none">' + json[i].attach_div_cd + '</td>');
						listHtml.push('			<td style="display:none">' + json[i].file_nm + '</td>');
						
						if(isEnableDelete == true) {
							listHtml.push('		<td align="center"><div class="btn-xs btn-danger" style="cursor: pointer; text-align:center;" onclick="js_delFileEntries(\''+json[i].file_no+'\',\''+ _delCallBackFn+'\'); return false;"><i class="icon-trash bigger-120"></i></div></td>');
						} else {
							
						}
						
						listHtml.push('		</tr>');
					}
					listHtml.push('</tbody>');
					
				} else {
					listHtml.push('		<tr>');
					listHtml.push('		  <td colspan="11" style="text-align:center;">조회된 결과가 없습니다.</td>');
					listHtml.push('		</tr>');
				}
				
				jQuery("#"+listEntries).html(listHtml.join(''));
				listHtml = [];
			}
		}
	});
}

// 첨부파일 삭제
function js_delFileEntries(file_no , _delCallBackFn)
{
	confirmation(msg_upload_code_11).then(function (res) {
		if( res == 'false' ){
			 return;
		 }else{
			jQuery.ajax({
				url: "/common/action/attach.jspx?cmd=doDelete",
				data: 'file_no='+file_no,
				datatype: 'xml',
				mtype: 'post',
				success: function(xmlDoc){
					
					var code = jQuery(xmlDoc).find('code').text();
			   		var message = jQuery(xmlDoc).find('msg').text();	
			   		
					if(code == '200') {
						
						jQuery('#trFileID_'+file_no).remove();
						msgStart(msg_com_code_009);
						
						if( _delCallBackFn != null && _delCallBackFn != 'undefined' && typeof(_delCallBackFn) != 'undefined')
							callFunc(_delCallBackFn , file_no);
						
					} else {
			   			msgStart(msg_com_code_010+"("+message+")", 'danger');
					}
				}
			});
		 }
	
	});	
}

// 첨부파일 아이콘
function getFileImg(extFile) {
	var img = "";
	try{
		//extFile = extFile.substring(1);
		extFile = extFile.toUpperCase();
		img = '<img src="/plugin/uploader/fileImg/' + extFile + '.png" height="16" width="16" style="vertical-align:middle; float: left; margin: 0px 4px 0px 4px;" onerror="jQuery(this).attr(\'src\', \'/plugin/uploader/fileImg/Default.png\')" />';
	}
	catch(e){
		img = '<img src="/plugin/uploader/fileImg/Default.png" height="16" width="16" style="vertical-align:middle; float: left;" />';
	}
	
	return img;
}

var grid_fmt_fileIconExtention = function(cellval,options,rowdata) {
	
	//alert(jQuery.inArray(cellval.toLowerCase(), FILE_EXTENTION_ICONS));
	
	var rtnStr = null;
	//if(FILE_EXTENTION_ICONS.indexOf(cellval.toLowerCase()) == -1)
	if(jQuery.inArray(cellval.toLowerCase(), FILE_EXTENTION_ICONS) == -1)
	{
		rtnStr = "<img src = '/static/com/img/file_extention_icon/none.gif' title='"+cellval.toLowerCase()+"' style=''>";
	} else
	{
		rtnStr = "<img src = '/static/com/img/file_extention_icon/"+cellval.toLowerCase()+".gif' title='"+cellval.toLowerCase()+"' style=''>";
	}
	rtnStr = rtnStr + "("+cellval.toLowerCase()+")";
			
	return rtnStr; //formatted_cellval;
}

function fmt_fileIconExtention(val, _title) {
	//console.log('---->'+val+', '+_title);
	var rtnStr = null;
	//if(FILE_EXTENTION_ICONS.indexOf(val.toLowerCase()) == -1)
	
	if(jQuery.inArray(val.toLowerCase(), FILE_EXTENTION_ICONS) == -1)
	{
		rtnStr = "<img src = '/static/com/img/file_extention_icon/none.gif' title='"+_title+"' style=''>";
	} else
	{
		rtnStr = "<img src = '/static/com/img/file_extention_icon/"+val.toLowerCase()+".gif' title='"+_title+"' style=''>";
	}
	//rtnStr = rtnStr + "("+val.toLowerCase()+")";
			
	return rtnStr; //formatted_cellval;
}



var grid_fmt_fieSize = function(cellval,options,rowdata) {
	
	var rtnStr = 0;		
	rtnStr = getNumberFormat(roundXL(cellval/1024, 0))+" KB";
	return rtnStr;
}


var grid_dateformat = function(cellvalue, options,rowdata)
	{
		if(cellvalue == null || cellvalue == '') return '';
		try
		{
	 		var year = cellvalue.substring( 0, 4 );
	 	    var month = cellvalue.substring( 4, 6 );
	 	    var day = cellvalue.substring(6);
	 	    return year+"."+month+"."+day;
		} catch(e)
		{
			return cellvalue;
		}
};

var grid_zip_code_format = function(cellvalue, options,rowdata)
{
	if(cellvalue == null || cellvalue == '' ) return '';
	try
	{
		if(cellvalue.indexOf('-') != -1) return cellvalue;
		
 		var a = cellvalue.substring( 0, 3 );
 	    var b = cellvalue.substring( 3 );
 	    
 	    return a+"-"+b
	} catch(e)
	{
		return cellvalue;
	}
};


var grid_telNoformat = function(cellvalue, options,rowdata)
{
	if(cellvalue == null || cellvalue == '') return '';
	try
	{
		return formattedTelnoHipen(cellvalue);
	} catch(e)
	{
		return cellvalue;
	}
};

function removeHtml(text)
{
	 var text = text.replace(/<br>/ig, "\n"); // <br>을 엔터로 변경
	 text = text.replace(/&nbsp;/ig, " "); // 공백      
	 // HTML 태그제거
	 text = text.replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, "");
	 
	 // shkim.add.
	 text = text.replace(/<(no)?script[^>]*>.*?<\/(no)?script>/ig, "");
	 text = text.replace(/<style[^>]*>.*<\/style>/ig, "");
	 text = text.replace(/<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>/ig, "");
	 text = text.replace(/<\\w+\\s+[^<]*\\s*>/ig, "");
	 text = text.replace(/&[^;]+;/ig, "");
	 text = text.replace(/\\s\\s+/ig, "");
	 
	 return text;
}




function prettyHtml(text)
{
	var temp = text.replace(/\r\n/ig, "<br>");
	temp = temp.replace(/\n/ig, "<br>");
	
	return unescape(temp);	
}


/*  Function Equivalent to java.net.URLEncoder.encode(String, "UTF-8")

Copyright (C) 2002, Cresc Corp.

Version: 1.0

*/

function urlEncode(str)
{	
    var s0, i, s, u;

    s0 = "";                // encoded str

    for (i = 0; i < str.length; i++){   // scan the source

        s = str.charAt(i);

        u = str.charCodeAt(i);          // get unicode of the char

        if (s == " "){s0 += "+";}       // SP should be converted to "+"

        else {

            if ( u == 0x2a || u == 0x2d || u == 0x2e || u == 0x5f || ((u >= 0x30) && (u <= 0x39)) || ((u >= 0x41) && (u <= 0x5a)) || ((u >= 0x61) && (u <= 0x7a))){       // check for escape

                s0 = s0 + s;            // don't escape

            }

            else {                  // escape

                if ((u >= 0x0) && (u <= 0x7f)){     // single byte format

                    s = "0"+u.toString(16);

                    s0 += "%"+ s.substr(s.length-2);

                }

                else if (u > 0x1fffff){     // quaternary byte format (extended)

                    s0 += "%" + (oxf0 + ((u & 0x1c0000) >> 18)).toString(16);

                    s0 += "%" + (0x80 + ((u & 0x3f000) >> 12)).toString(16);

                    s0 += "%" + (0x80 + ((u & 0xfc0) >> 6)).toString(16);

                    s0 += "%" + (0x80 + (u & 0x3f)).toString(16);

                }

                else if (u > 0x7ff){        // triple byte format

                    s0 += "%" + (0xe0 + ((u & 0xf000) >> 12)).toString(16);

                    s0 += "%" + (0x80 + ((u & 0xfc0) >> 6)).toString(16);

                    s0 += "%" + (0x80 + (u & 0x3f)).toString(16);

                }

                else {                      // double byte format

                    s0 += "%" + (0xc0 + ((u & 0x7c0) >> 6)).toString(16);

                    s0 += "%" + (0x80 + (u & 0x3f)).toString(16);

                }

            }

        }

    }

    return s0;

}

 

/*  Function Equivalent to java.net.URLDecoder.decode(String, "UTF-8")

    Copyright (C) 2002, Cresc Corp.

    Version: 1.0

*/

function urlDecode(str)
{
	  if(str == null) return null;
	  
    var s0, i, j, s, ss, u, n, f;

    s0 = "";                // decoded str

    for (i = 0; i < str.length; i++){   // scan the source str

        s = str.charAt(i);

        if (s == "+"){s0 += " ";}       // "+" should be changed to SP

        else {

            if (s != "%"){s0 += s;}     // add an unescaped char

            else{               // escape sequence decoding

                u = 0;          // unicode of the character

                f = 1;          // escape flag, zero means end of this sequence

                while (true) {

                    ss = "";        // local str to parse as int

                        for (j = 0; j < 2; j++ ) {  // get two maximum hex characters for parse

                            sss = str.charAt(++i);

                            if (((sss >= "0") && (sss <= "9")) || ((sss >= "a") && (sss <= "f"))  || ((sss >= "A") && (sss <= "F"))) {

                                ss += sss;      // if hex, add the hex character

                            } else {--i; break;}    // not a hex char., exit the loop

                        }

                    n = parseInt(ss, 16);           // parse the hex str as byte

                    if (n <= 0x7f){u = n; f = 1;}   // single byte format

                    if ((n >= 0xc0) && (n <= 0xdf)){u = n & 0x1f; f = 2;}   // double byte format

                    if ((n >= 0xe0) && (n <= 0xef)){u = n & 0x0f; f = 3;}   // triple byte format

                    if ((n >= 0xf0) && (n <= 0xf7)){u = n & 0x07; f = 4;}   // quaternary byte format (extended)

                    if ((n >= 0x80) && (n <= 0xbf)){u = (u << 6) + (n & 0x3f); --f;}         // not a first, shift and add 6 lower bits

                    if (f <= 1){break;}         // end of the utf byte sequence

                    if (str.charAt(i + 1) == "%"){ i++ ;}                   // test for the next shift byte

                    else {break;}                   // abnormal, format error

                }

            s0 += String.fromCharCode(u);           // add the escaped character

            }

        }

    }

    return s0;

}

/*
 * s: 반복될 문자
 * n: 반복할 횟수
 * 지정한 문자 s를 n만큼 반복한다.
 * */
function repeat(s, n){
    var a = [];
    while(a.length < n){
        a.push(s);
    }
    return a.join('');
}

/*
 * htmlObj: 적용될  htmlobject
 * curpage : 현재 페이지
 * total : 전체 게시물 갯수
 * row_per_pages : 한페이지에 보여줄 게시물 갯수
 * scriptfunc : 페이징시 호출될 javascript함수.
 * javascript로 페이징을 구현한다.
 * */
function js_Paging(htmlObj, curpage, total, rows_per_page, scriptFunc)
{
	var sb = [];
	var cntSize = 0 ;		//보여질 페이지 버튼 개수
	
		  curpage				//현재 페이지 번호 
	var totalPage  = 0;		//마지막 페이지 수. 
	var startNumber  = 0;	//첫번째 버튼 번호.
	var endnNmber  = 0;	//마지막 버튼 번호.
	
	cntSize = 10;		//페이지 버튼 개수 10개로 지정.
	
	//마지막 페이지 수 체크.
	if ( (total%rows_per_page) == 0 ) 
		totalPage = parseInt(total/rows_per_page);
	else  
		totalPage = parseInt(total/rows_per_page)+1;
	
	//첫번째 버튼 번호 체크.
	if( (curpage%cntSize) == 0 )	//10, 20, 30... [ 10의 배수 페이지 일경우 ]
		startPage = (parseInt(curpage/cntSize)-1)*cntSize+1;
	else
		startPage = (parseInt(curpage/cntSize))*cntSize+1;
	
	//마지막 버튼 번호 체크.
	endPage   = parseInt(startPage) +cntSize-1;
	if ( endPage>=totalPage ) endPage= totalPage;
		
	//alert("현재 페이지 수 = "+curpage+" / 마지막 페이지 수 = "+totalPage+" / 첫번째 버튼 번호 = "+startPage+" / 마지막 버튼 번호 = "+endPage)
	if (  parseInt(curpage) > 1 )
	{
		sb.push("<li><a href=\"javascript:"+scriptFunc+"('1');\" onMouseMove=\"window.status='Go first page.';\" style=\"background-color: oldlace; padding:3px 6px;\"><i class=\"icon-double-angle-left\"></i></a></li>");
	} 
	else
	{
		sb.push("<li class='disabled'><a href='#' style=\"padding:3px 6px;\"><i class=\"icon-double-angle-left\"></i></a></li>");
	}
	if ( parseInt(curpage) > 1)
	{
		sb.push("<li><a href=\"javascript:"+scriptFunc+"('"+(parseInt(curpage)-1)+"'); \" onMouseMove=\"window.status='Go previous page.';\" style=\"background-color: oldlace; margin-right:10px; padding:3px 6px;\"><i class=\"icon-angle-left\"></i></a></li>");
	}
	else
	{
		sb.push ( "<li class='disabled'><a href='#' style=\"margin-right:10px; padding:3px 6px;\"><i class=\"icon-angle-left\"></i></a></li>");
	}
	
	
	if (total == 0) 
	{
		sb.push ("<li class='active'><a href='#' style=\"padding:3px 6px;\">1</a></li>");
	}
	else
	{
	  for (i=startPage ; i<= endPage ; i++)
	  {	  	
		  if (i == curpage)
		  	sb.push("<li class='active'><a href=\"javascript:"+scriptFunc+"('"+i+"'); \" style=\"padding:3px 6px;\" onMouseMove=\"window.status='Go "+i+" page.';\"><b>"+i+"</b></a></li>");
		  else //if( i <= totalPage)
			sb.push("<li><a href=\"javascript:"+scriptFunc+"('"+i+"'); \" style=\"padding:3px 6px;\" onMouseMove=\"window.status='Go "+i+" page.';\">"+i+"</a></li>");
	  }
	}
	
	
	if ( totalPage > parseInt(curpage) )
	{
		sb.push("<li><a href=\"javascript:"+scriptFunc+"('"+(parseInt(curpage)+1)+"'); \" onMouseMove=\"window.status='Go next page.';\" style=\"background-color: azure; margin-left:10px; padding:3px 6px;\"><i class=\"icon-angle-right\"></i></a></li>");		   	
	}
	else
	{
		sb.push ("<li class='disabled'><a href='#' style=\"margin-left:10px; padding:3px 6px;\"><i class=\"icon-angle-right\"></i></a></li>");
	}
	if ( totalPage > parseInt(curpage) )
	{
		sb.push("<li><a href=\"javascript:"+scriptFunc+"('"+totalPage+"'); \" onMouseMove=\"window.status='Go last page.';\" style=\"background-color: azure;padding:3px 6px;\"><i class=\"icon-double-angle-right\"></i></a></li>");			
	}
	else{
		sb.push ("<li class='disabled'><a href='#'\" style=\"padding:3px 6px;\"><i class=\"icon-double-angle-right\"></i></a></li>") ;
	}
	
	jQuery('#'+htmlObj).children().remove();
	jQuery('#'+htmlObj).append(sb.join(''));
	
}

/*
 * 그리드 가로사이즈 조절
 * 
 * 	1. 그리드옵션	autowidth : true,  추가
 * 2. jQuery(document).ready 하단부에 아래 두줄 추가하여 사용   	
       jQuery(window).resize(function() { setScale("그리드 아이디") }); 
       setScale("그리드 아이디");
 * 
 */
function setScale(id, parentId)
{	
	var width = null;
	if(parentId !== 'undefined'){
		width = jQuery('#'+parentId).width();
	}else{	
		width = jQuery(this).width() -240;
	}
	
	width = jQuery('#'+id).parent().width();	
	jQuery("#"+id).setGridWidth( width * 1);		
}

/*
 * jqgrid에서 페이지 이동시 사용 
 *  url 및 파라미터 수정후 사용 
 * 		onPaging : function(pgButton)
			{
				var nextPageNum = gridOnPaging(pgButton, "그리드아이디");
				jQuery('#그리드아이디').setGridParam({url:'/knowledge/action/project.jspx?cmd=getProjectDivList&cur_pg='+nextPageNum+'&row_per_page='+jQuery('.ui-pg-selbox').val()+'&'+jQuery("#searchForm").serialize()}).trigger('reloadGrid');
			}
 * 
 */
function gridOnPaging(pgButton, id)
{
	var nextPageNum = 0;
	var nextPageRow = 0;
	if(pgButton == 'user')
	{
		nextPageNum = jQuery('.ui-pg-input').val();
	}
	else if(pgButton.indexOf('first') != -1)
	{
		nextPageNum = '1';
	} else if(pgButton.indexOf('prev') != -1)
	{
		nextPageNum = parseInt(jQuery("#"+id).jqGrid("getGridParam", "page")) -1;
	} else if(pgButton.indexOf('next') != -1)
	{
		nextPageNum = parseInt(jQuery("#"+id).jqGrid("getGridParam", "page")) + 1;
	} else if(pgButton.indexOf('last') != -1)
	{
		nextPageNum = jQuery("#"+id).getGridParam("lastpage");
	}					
	return nextPageNum;		
}


//jqGrid 입력폼에서 사업자번호 체크  
//editrules:{custom: true, custom_func:chkJqBizNo}
function chkJqBizNo(value, colName)
{
	  var bizNo = getBizNoDash(value)
	    if (value == "" || value.length === 12) {
	        return [true, ""];
	    }
	    else {
	        return [false, colName+"가 " + msg_com_code_019];
	    }
}

//jqGrid 입력폼에서 전화번호 체크 
//editrules:{custom: true, custom_func:chkJqTelNo}
function chkJqTelNo(value, colName)
{
	  var bSucess = isNumHipen(value)
	    if (value == "" || bSucess === true) {
	        return [true, ""];
	    }
	    else {
	        return [false, colName+"는 " + msg_com_code_023];
	    }
}

//jqGrid 입력폼에서 이메일을 체크
//editrules:{custom: true, custom_func:chkJqEmail}	  
function chkJqEmail(value, colName)
{
	  var bSucess = isEmail(value)
	    if (value == "" || bSucess === true) {
	        return [true, ""];
	    }
	    else {
	        return [false, colName+"가 " + msg_com_code_019];
	    }
}

//사업자번호 체크
function getBizNoDash(bizNo)
{
	if(bizNo == "") return "";
	var value = getNumOnly(bizNo);
	if(value.length > 0 && value.length != 10){
		return value;
	}else if(value.length == 10){
    	return value.substring(0, 3)+"-"+value.substring(3, 5)+"-"+value.substring(5);
   	}
}


//숫자만 리턴
function getNumOnly(str)
{
    if(str==null || str=="") return str;
    str = str+"";
    var strCnt = str.length;
    var value="";
    for(var i=0; i< strCnt; i++)
    {
        var ch = str.charAt(i);
        if(ch>='0' && ch<='9')
        {
            value += ch;
        }
    }
    return value;
}



//해당되는 문자가 포함되어 있는지 체크
function containsCharsOnly(input,chars) 
{
    if (typeof(input) == "string") 
    {
        for (var inx = 0; inx < input.length; inx++) 
        {
            if (chars.indexOf(input.charAt(inx)) == -1) 
                return false;
        }            
    }
    else
    {
        for (var inx = 0; inx < input.value.length; inx++) 
        {
            if (chars.indexOf(input.value.charAt(inx)) == -1) 
                return false;
        }
    }
    return true;
}



function isTelno(input)
{
	if(input == "") return  true;
    var chars = ")-0123456789";
    return containsCharsOnly(input,chars);
}

//숫자와 Dash만체크  / 포함 true, 포함안되면 false
function isNumHipen(input) {
		if(input == "") return  true;
	     var chars = "-0123456789";
	     return containsCharsOnly(input,chars);
	 }


//숫자만체크	 
function isNum(input) 
{
	if(input == "") return  true;
    var chars = "0123456789.";
    return containsCharsOnly(input,chars);
}

//영문만체크	 
function isAlphabet(input) 
{ 
	if(input == "") return  true;
 var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return containsCharsOnly(input,chars);
}

//영문숫자만체크
function isAlphabetNum(input) 
{ 
	if(input == "") return  true;
     var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    return containsCharsOnly(input,chars);
}

//영문숫자점만체크
function isNumPoint(input) 
{
	if(input == "") return  true;
    var chars = "-.0123456789";
    return containsCharsOnly(input,chars);
}


function formattedTelnoHipen(str)
{
	var regExp = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
	
	if(!regExp.test(str)){
		return str;
	}
	
	var rtnStr = '';
	var sMsg = str;
	var onlynum = "" ;
	var i = 0 ;
	var s_telno = "";
	for(var j=0; j<sMsg.length; j++){
		if(sMsg.charAt(j) != " ")
		{
			s_telno += sMsg.charAt(j);
		}
	 }
	 sMsg = s_telno;
	 if( isNumHipen(sMsg) == false) return str = str.substring(0,str.value.length-1) ;
	 onlynum = removeHipen(sMsg);
	 
	  if (onlynum.substring(0,2) == "02") 
	  {
	   if (onlynum.getBytes() <= 1) rtnStr = onlynum ;
	   if (onlynum.getBytes() == 2) rtnStr = onlynum + "-" ;
	   if (onlynum.getBytes() == 3) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,3);
	   if (onlynum.getBytes() == 4) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,4);
	   if (onlynum.getBytes() == 5) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" ;
	   if (onlynum.getBytes() == 6) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,6) ;
	   if (onlynum.getBytes() == 7) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,7) ;
	   if (onlynum.getBytes() == 8) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,8) ;
	   if (onlynum.getBytes() == 9) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,9) ;
	   if (onlynum.getBytes() == 10)rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,6) + "-" + onlynum.substring(6,10) ;
	  }
	  else 
	  {
	   if (onlynum.getBytes() <= 2) rtnStr = onlynum ;
	   if (onlynum.getBytes() == 3) rtnStr  = onlynum + "-";
	   if (onlynum.getBytes() == 4) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,4);
	   if (onlynum.getBytes() == 5) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,5) ;
	   if (onlynum.getBytes() == 6) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" ;
	   if (onlynum.getBytes() == 7) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,7) ;
	   if (onlynum.getBytes() == 8) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,8) ; 
	   if (onlynum.getBytes() == 9) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,9) ;
	   if (onlynum.getBytes() == 10)rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,10) ;   if (onlynum.getBytes() == 11)  rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,7) + "-" + onlynum.substring(7,11) ;
	  }
	 
	 return rtnStr;
}


//자동으로 -채워서 반환  
//  <input type="text" onkeyup="autoTelnoHipen(this)" />
function autoTelnoHipen(telObj)
{
 var sMsg = telObj.value;
 
 var regExp = /^[0-9-]+$/;
	
 if(!regExp.test(sMsg)){
	return sMsg;
 }
 
 var onlynum = "" ;
 var i = 0 ;
 var s_telno = "";
 for(var j=0; j<sMsg.length; j++){
	if(sMsg.charAt(j) != " "){
		s_telno += sMsg.charAt(j);
	}
 }
 sMsg = s_telno;
 if( isNumHipen(sMsg) == false) return telObj.value = telObj.value .substring(0,telObj.value.length-1) ;
 onlynum = removeHipen(sMsg);
 if(event.keyCode != 8 ) {
  if (onlynum.substring(0,2) == "02") 
  {
   if (onlynum.getBytes() <= 1) telObj.value = onlynum ;
   if (onlynum.getBytes() == 2) telObj.value = onlynum + "-" ;
   if (onlynum.getBytes() == 3)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,3);
   if (onlynum.getBytes() == 4)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,4);
   if (onlynum.getBytes() == 5)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" ;
   if (onlynum.getBytes() == 6)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,6) ;
   if (onlynum.getBytes() == 7)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,7) ;
   if (onlynum.getBytes() == 8)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,8) ;
   if (onlynum.getBytes() == 9)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,9) ;
   if (onlynum.getBytes() == 10)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,6) + "-" + onlynum.substring(6,10) ;
  }
  else 
  {
   if (onlynum.getBytes() <= 2) telObj.value = onlynum ;
   if (onlynum.getBytes() == 3)  telObj.value  = onlynum + "-";
   if (onlynum.getBytes() == 4)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,4);
   if (onlynum.getBytes() == 5)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,5) ;
   if (onlynum.getBytes() == 6)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" ;
   if (onlynum.getBytes() == 7)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,7) ;
   if (onlynum.getBytes() == 8)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,8) ; 
   if (onlynum.getBytes() == 9)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,9) ;
   if (onlynum.getBytes() == 10)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,10) ;   if (onlynum.getBytes() == 11)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,7) + "-" + onlynum.substring(7,11) ;
  }
 } 
}

//-을 삭제해서 반환
function removeHipen(sNo) 
{
 var reNo = "";
 for(var i=0; i<sNo.length; i++) {
  if ( sNo.charAt(i) != "-" ) {
   reNo += sNo.charAt(i);
  }
 }
 return reNo ;
}

//이메일 주소를 체크한다.
function isEmail(strText)
{
    if(strText == "") return true;
    
    
     if (strText.match(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+.[a-zA-Z.]+$/))
     {
      return true;
     }
     else
     {
      return false;
     }
}

String.prototype.getBytes = function() 
{
    var str = this;
    var l = 0;
    for (var i=0; i<str.length; i++) l += (str.charCodeAt(i) > 127) ? 2 : 1;
    return l;
};

/*
 * 폼 객체들을 활성화 또는 비활성화 한다.
 * form(#폼객체명),isEnable(활성화여부)
 * */
function toogleInFormObject(form, isEnable)
{
	
	jQuery(form).find('input, select, textarea, checkbox').each(			
		function(i)
		{
			if(isEnable)
			{
				jQuery(this).removeAttr('disabled');
			} else
			{
				jQuery(this).attr('disabled', 'true');
			}
			
				
		}
	);
}


function openWaiting(msg)
{
	if(!msg) msg = msg_com_code_041;
	jQuery.blockUI({ message: '<h4><img src="/static/com/img/ajax-loader.gif" />&nbsp;'+msg+'</h4>' });
}

function closeWaiting()
{
	jQuery.unblockUI();
}

//조직도 다이얼로그 로딩
//url : 불러들일 외부 url
//dialog option(호출하는 페이지에서 정의)
//permit_rel : 조직도 트리에서 더블클릭시 유효한 데이터 타입들(ex:default,folder,drive)
function openOrgDiagramDialog(url, options, permit_rel, _isModal)
{	
	var isModal = true;
	var height = options.height;
	
	if(options.height == "auto"){
		height = "400";
	}
	
	if(_isModal == null || isModal == ''){ isModal = true;}
	else isModal = false;
	
	options.permit_rel = permit_rel.split(',');
	
	//jQuery("#dialog").css("wdith", "400px");
	var dlg = jQuery("#dialog").dialog({ 
	    autoOpen: true, 
	    modal: isModal, 
	    resizable : true,
	    //height: height,
	    title: options.title,
	    width: options.width,
	    resizable: false,
	    //position: { my: 'left top', at: 'center top', of: event, offset:"20 20"}, 
	    position: [(jQuery(window).width() / 2) - (options.width / 2), (jQuery(window).height() / 2) - (height / 2)],
	  	open: function(event, ui) {
		   	var _dlg = jQuery(this);
		   	jQuery.get(url, function(data){
	       		jQuery(_dlg).html(data);
		   	});		   	
	  	}, 
	  	//buttons : options.buttons,
	  	close : function(){
	   		jQuery(this).dialog("destroy");
	  		jQuery(this).children().remove();
	  	},
	  	buttons: [ 
					{
						text: "선택",
						"class" : "btn btn-primary btn-xs",
						click: function() {
							if(options.gubun == "Org"){
								org_set_values();
							} else if(options.gubun == "Dept"){
								dept_set_values();
							}
						} 
					},
					{
						text: "닫기",
						"class" : "btn btn-xs",
						click: function() {
							$( this ).dialog( "close" ); 
						} 
					}
				]
	 });
	 return dlg;			
}

/**
 *  
 */
function getCodeMap(div_cd, regex, replacement, _url){
	
	if(arguments.length != 4){
		_url = "/common/action/code.jspx?cmd=getUseCodeList";
	}
	
	var codeMap = new Map();
	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
		data : {'div_cd' : div_cd},
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
					
					if(regex && replacement){
						cd = cd.replace(regex, replacement);	
					}
					
					codeMap.put(cd, val);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});
	
	return codeMap;
}

/**
 * 함수를 호출해주는 함수
 * @param  {[type]} _callbackFunc [description] 호출할 함수명
 * @return {[type]}               [description]
 */
function callFunc(_callbackFunc, formParam) 
{
	if(_callbackFunc != null && _callbackFunc !== undefined) 
	{
		var fn = window[_callbackFunc];
		try
		{
			fn.call(window, formParam);
		} catch(e)
		{
			console.log(_callbackFunc+' is not exist!!'+e);
		}
	}
}

/**
 * INPUT HIDDEN 폼 태그 생성 or 값 매핑.
 * @param p_objForm
 * @param p_strId
 * @param p_strValue
 */
function AddHiddenElement(p_objForm, p_strId, p_strValue)
{	
	if(document.getElementById(p_strId) != null){
		document.getElementById(p_strId).value = p_strValue;
	}else
	{
		var inpHidden = "";
		try{
	    	inpHidden = document.createElement('<input type="hidden" id="'+p_strId+'" name="'+p_strId+'">');
		}catch(e){
			inpHidden = document.createElement("input");
			inpHidden.type = "hidden";
			inpHidden.name = p_strId;
			inpHidden.id = p_strId;
		}
	    inpHidden.value = p_strValue;
	    eval("document." + p_objForm).appendChild(inpHidden);
	}
}

/**
 * INPUT HIDDEN 폼 태그 생성 or 값 매핑. : name으로 무조건 생성=>체크박스 배치 용도.
 * @param p_objForm
 * @param p_strId
 * @param p_strValue
 */
function AddCheckedHiddenElement(p_objForm, p_strId, p_strValue)
{	
	var inpHidden = "";
	try{
		inpHidden = document.createElement('<input type="hidden" name="'+p_strId+'">');
	}catch(e){
		inpHidden = document.createElement("input");
		inpHidden.type = "hidden";
		inpHidden.name = p_strId;
	}
	inpHidden.value = p_strValue;
	eval("document." + p_objForm).appendChild(inpHidden);
}

function getUrlParam(_url, _param)
{
    //var sPageURL = window.location.search.substring(1);
    var sPageURL = _url ;
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == _param) 
        {
            return sParameterName[1];
        }
    }
}

function setHourSelectBox(selectBoxID, htmlID){
	var Hour_Box = [];
	Hour_Box.push("<select id='"+selectBoxID+"' name='"+selectBoxID+"'>\n");
	Hour_Box.push("<option value=''>::전체::</option>\n");
	for(var H=1; H<24; H++){
		if(H<10){	
			Hour_Box.push("<option value='0"+H+"'>0"+H+"시</option>\n");		
		} else{
			Hour_Box.push("<option value='"+H+"'>"+H+"시</option>\n");
		}
	}
	Hour_Box.push("</select>");		
	jQuery('#'+htmlID).html(Hour_Box.join(""));
}


function setMinSelectBox(selectBoxID, htmlID, interval){
	var Hour_Box = [];
	Hour_Box.push("<select id='"+selectBoxID+"' name='"+selectBoxID+"'>\n");
	Hour_Box.push("<option value=''>::전체::</option>\n");
	
	if(typeof(interval) != 'number'){

		interval = 1;
	}
	
	var minLength = 60 / interval;
	var minValue = 0;
	
	for(var M=0; M<minLength; M++){
		minValue = M*interval;
		if(minValue<10){	
			Hour_Box.push("<option value='0"+minValue+"'>0"+minValue+"분</option>\n");		
		} else{
			Hour_Box.push("<option value='"+minValue+"'>"+minValue+"분</option>\n");
		}
	}
	Hour_Box.push("</select>");		
	jQuery('#'+htmlID).html(Hour_Box.join(""));
	Hour_Box = [];
}

function setHourSelectBoxCalendar(selectBoxID, htmlID, startHour, endHour){
	var startH = 0;
	var endH = 23;
	
	if(startHour){
		startH = startHour;
	}
	
	if(endHour){
		endH = endHour;
	}
	
	var Hour_Box = [];
	Hour_Box.push("<select id='"+selectBoxID+"' name='"+selectBoxID+"'>\n");
	
	for(var H=startH; H<=endH; H++){
		if(H<10){	
			Hour_Box.push("<option value='0"+H+"'>0"+H+"시</option>\n");		
		} else{
			Hour_Box.push("<option value='"+H+"'>"+H+"시</option>\n");
		}
	}
	Hour_Box.push("</select>");		
	jQuery('#'+htmlID).html(Hour_Box.join(""));
}


function setMinSelectBoxCalendar(selectBoxID, htmlID, interval, setMin){
	var Hour_Box = [];
	Hour_Box.push("<select id='"+selectBoxID+"' name='"+selectBoxID+"'>\n");
	
	if(typeof(interval) != 'number'){

		interval = 1;
	}
	
	var minLength = 60 / interval;
	var minValue = 0;
	var flag = false;
	
	for(var M=0; M<minLength; M++){
		minValue = M*interval;
		
		if(((minValue - setMin) / interval >= 0 && (minValue - setMin) / interval < 1)){
			flag = true;			
		}
		
		if(minValue<10){	
			if(flag == true){
				Hour_Box.push("<option value='0"+minValue+"' selected >0"+minValue+"분</option>\n");
			}else{
				Hour_Box.push("<option value='0"+minValue+"' >0"+minValue+"분</option>\n");
			}		
		} else{
			if(flag == true){
				Hour_Box.push("<option value='"+minValue+"' selected>"+minValue+"분</option>\n");
			}else{
				Hour_Box.push("<option value='"+minValue+"' >"+minValue+"분</option>\n");
			}
		}
		
		flag = false;
	}
	
	Hour_Box.push("</select>");		
	jQuery('#'+htmlID).html(Hour_Box.join(""));	
	
}


function setSecSelectBoxCalendar(selectBoxID, htmlID, interval){
	var Hour_Box = [];
	Hour_Box.push("<select id='"+selectBoxID+"' name='"+selectBoxID+"'>\n");
	
	if(typeof(interval) != 'number'){

		interval = 1;
	}
	
	var minLength = 60 / interval;
	var minValue = 0;
	
	for(var M=0; M<minLength; M++){
		minValue = M*interval;
		if(minValue<10){	
			Hour_Box.push("<option value='0"+minValue+"'>0"+minValue+"초</option>\n");		
		} else{
			Hour_Box.push("<option value='"+minValue+"'>"+minValue+"초</option>\n");
		}
	}
	Hour_Box.push("<option value='59'>59초</option>\n");
	Hour_Box.push("</select>");		
	jQuery('#'+htmlID).html(Hour_Box.join(""));
}


function makeSelectBoxToArgs(url, htmlObj, data, _callbackFunc, num)
{
	var _url = url;
	var _data = data;	

	var http = jQuery.ajax( {
   		url: _url,	   		
   		type: "POST",
		data : _data,
   		async : false,
   		error : function(xhr)
   		{
			alert(xhr.status);
		},
		success:function(xmlDoc) 
   		{
			
			var code = jQuery(xmlDoc).find('code').text(); 		
	   		var data = jQuery(xmlDoc).find('data').text();
	   		var msg =  jQuery(xmlDoc).find('msg').text();

			var cd = null;
			var val = null;				
			
	   		if(code == '200')
	   		{
				removeOptionAll(htmlObj, false);
				
   				var selectObj = document.getElementById(htmlObj);	   				
   				
   				try
   				{
   				var fn = window[_callbackFunc];
   				
   				if( fn != null && fn != undefined && fn != 'undefined')
   				{	
   					selectObj.param = num;
   					var isseted = false;
	   		   		if(!isseted && selectObj.addEventListener){   		   		
		   				selectObj.addEventListener('change',function(e){
		   					fn(num);
		   					isseted = true;
		   				}, false);
	   		   		}
		   			else if(!isseted && selectObj.attachEvent){
		   				selectObj.attachEvent('onchange',function(){
		   					fn(num);
		   					isseted = true;
		   				});
		   			}
		   			else if(!isseted && selectObj.onchange){
		   				selectObj.onchange = function(){
		   					fn(num);
		   					isseted = true;
		   				};
		   			}
   				}
   				}catch(e){alert(e);}
   				var options = null;
	   			jQuery(xmlDoc).find('cd_list').each(function(){			   			
		   			cd = jQuery(this).find('cd').text();
		   			val = jQuery(this).find('cd_nm').text();
		   			
		   			options = document.createElement('option');
   				 	options.setAttribute('value', cd);
   				 	options.innerHTML = jQuery.trim(val);
		   										
   				 	selectObj.appendChild(options);
		   		});		   			
	   		}else
			{
	   			msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
   		}
	});	
}


//function setEmpTextAndValue(text,val)
function setEmpTextAndValue(item)
{	
	jQuery("<div>").text(item.value).prependTo("#empNameSuggestResult");
	jQuery('#proc_emp_no').val(item.emp_no);
	jQuery("#empNameSuggestResult").scrollTop(0);
	
} 

function resizeJqGridWidth(grid_id, div_id, width){
	
    // window에 resize 이벤트를 바인딩 한다. 
    jQuery(window).bind('resize', function() {

        // 그리드의 width를 div 에 맞춰서 적용.
        jQuery('#' + grid_id).setGridWidth(jQuery('#' + div_id).width() , false); //Resized to new width as per window
        
        //그리드 헤더 width를 div 에 맞춰서 적용.
        jQuery('.ui-jqgrid-htable').css('width', '100%');
        jQuery('.ui-jqgrid-btable').css('width', '100%');
        
        //그리드 리스트 width를 div 에 맞춰서 적용.
        jQuery('#' + grid_id).css('width', '100%');
        
        //그리드 리스트 스크롤바 구간.
        jQuery('.ui-jqgrid-bdiv').css('padding-right','20px');
        
     }).trigger('resize');
}

function updatePagerIcons(table) {
	var replacement = 
	{
			'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
			'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
			'ui-icon-seek-next' : 'icon-angle-right bigger-140',
			'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
	};
	jQuery('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
		var icon = $(this);
		var $class = jQuery.trim(icon.attr('class').replace('ui-icon', ''));
		
		if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
	});
}

function updatePagerIcons_grid(table) {
	var replacement = 
	{
			'ui-icon-seek-first' : 'icon- fa fa-angle-double-left bigger-140',
			'ui-icon-seek-prev' : 'icon- fa fa-angle-left bigger-140',
			'ui-icon-seek-next' : 'icon- fa fa-angle-right bigger-140',
			'ui-icon-seek-end' : 'icon- fa fa-angle-double-right bigger-140'
	};
	jQuery('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
		var icon = jQuery(this);
		var $class = jQuery.trim(icon.attr('class').replace('ui-icon', ''));
		
		if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
	});
}

function updateNavGridButton(gridDiv, pageDiv, table, exel_view, pagerOption)
{
	if(pagerOption == null)
	{
		jQuery('#'+gridDiv).jqGrid('navGrid','#'+pageDiv,
		{ 	
			edit: false,
			editicon : 'fa fa-pencil blue',
			add: false,
			addicon : 'fa fa-plus purple',
			del: false,
			delicon : 'fa fa-trash-o red',
			search: false,
			searchicon : 'fa fa-search orange',
			refresh: true,
			refreshicon : 'fa fa-refresh green',
			view: false,
			viewicon : 'fa fa-eye grey'
		});
	} else
	{	
		pagerOption.addicon = 'fa fa-plus purple',
		pagerOption.delicon = 'fa fa-trash-o red',
		pagerOption.searchicon = 'fa fa-search orange',
		pagerOption.refreshicon = 'fa fa-refresh green',
		pagerOption.viewicon ='fa fa-eye grey',
		jQuery('#'+gridDiv).jqGrid('navGrid','#'+pageDiv, pagerOption);
		
	}
	
	if(exel_view == null) exel_view = true;
	
	//엑셀버튼.
	if(exel_view)
	{
		jQuery('#'+table.id+'_exel_down').remove();
		jQuery('#'+pageDiv+'_left').append('<a href="#"><i id="'+table.id+'_exel_down"class="icon-table bigger-140" title="XLS 다운로드" style="padding:2px 0px 0px 3px;"></i></a>');		

		var current_url = jQuery('#'+table.id).jqGrid('getGridParam', 'url');
		var params = jQuery('#'+table.id).jqGrid('getGridParam', 'postData');
		
		jQuery('#'+table.id+'_exel_down').bind('click', function(){exportXls(current_url, params);});
	}
	
}

// jqgrid용 엑셀
function exportXls(url, params)
{
	var xls_url = url;
	xls_url = xls_url+'&rtnType=xls&templet-bypass=true';
	
	// postdata 파라메터 넣어주기
	try
	{
		jQuery.each(params, function(index, val){
			var data_param = '&'+index+'=' + val;
			xls_url = xls_url + data_param;
		});
	} catch(e)
	{
		xls_url = xls_url+params;
	}
	
	location.href=xls_url;
}

// 통계용 엑셀
function exportXls2(url)
{
	var xls_url = url;
	xls_url = xls_url+'&rtnType=xls&templet-bypass=true';
	
	location.href=xls_url;
	//window.open(xls_url);
	
}

function dateStringFormat(date)
{
	if(date == null || date == '') return '';
	try
	{
		var year = date.substring(0, 4);
		var month = date.substring(4, 6);
		var day = date.substring(6, 8);
		
		return year+"-"+month+"-"+day;
	}
	catch(e)
	{
		return date;
	} 
}

//****************** 시계 / 초시계 관련 *********************/
var currentsec=0;
var currentmin=0;
var currenthour=0;

// 시간별 색상 변경 여부.
var hourFlg  = true ;
var minFlg   = true ;
// 시간 체크 여부.
var keepgoin = true;
function startStatusTimer()
{
	var warning_hour = [] ; 
	var warning_min  = [] ;
	var warning_hour_color = [] ; 
	var warning_min_color  = [] ;

	if(keepgoin){
		
		currentsec+=1;	

		if (currentsec==60){
			currentsec=0;
			currentmin+=1; 
			//invokeNeonSignMin(currentmin, currenthour) ;
		}
		
		if (currentmin==60){
			currentmin=0;
			currenthour+=1; 
			//invokeNeonSignHour(currenthour) ;
		}
		
		Strsec=""+currentsec;	
		Strmin=""+currentmin;
		Strhour=""+currenthour;
		
		if (Strsec.length!=2){
			Strsec="0"+currentsec; 
		}	
		
		if (Strmin.length!=2){
			Strmin="0"+currentmin; 
		}
		
		if (Strhour.length!=2){
			Strhour="0"+currenthour; 
		}
		
		jQuery("#seconds").val(Strsec);
		jQuery("#minutes").val(Strmin);
		jQuery("#hours").val(Strhour);

		setTimeout("startStatusTimer()", 1000);
 	}
}

var timerInterVal = null;
function startTimer(){
	
	if(timerInterVal != null){
		restartStatusTimer();
		clearInterval(timerInterVal);
		keepgoin = true;
	}
	
	startStatusTimer();
	timerInterVal = setInterval(function(){startStatusTimer()}, 1000);
}


function restartStatusTimer(){

	// 상태시간 네온사인 초기화.
	initNeonSign() ;
	
	currentsec=0;
	currentmin=0;
	currenthour=0;
	Strsec="00";
	Strmin="00";
	Strhour="00";
	
	keepgoin = false;

	jQuery("#seconds").val(Strsec);
	jQuery("#minutes").val(Strmin);
	jQuery("#hours").val(Strhour);
}

// 상태시간 네온사인 초기화.
function initNeonSign()
{
	jQuery("#hours, #minutes, #seconds").css("color","#FFF")
	jQuery("#hours, #minutes, #seconds").css("opacity","1")
	jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=60)")
}
// 상태시간 네온사인 hour 변경시 발생.
function invokeNeonSignHour(currenthour){
	
	if(currenthour > 0){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","white")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8")
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)")
			});
		});
	}
}
// 상태시간 네온사인 minute 변경시 발생.
function invokeNeonSignMin(currentmin, currenthour){

	// 2분경과.
	if(currenthour == 0 && currentmin == 2){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","orange")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8")
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)")
			});
		});
	}
	
	// 5분경과.
	if(currenthour == 0 && currentmin == 5){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","red")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8")
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)")
			});
		});
	}
	
	// 매 1분마다.(첫 5분은 제외.)
	if(currentmin != 5){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8")
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)")
			});
		});
	}
	
}

// 일정길이의 남는 공간을 지정한 문자로 채운다.
function lpad(str, num, chr) {   
    if (! str || ! chr || str.length >= num) {
        return str;
    }

    var max = num - str.length;
    for (var i = 0; i < max; i++) {
        str = chr + str;
    }

    return str;
}

function rpad(str, num, chr) { 
    if (! str || ! chr || str.length >= num) {
        return str;
    }

    var max = num - str.length;
    for (var i = 0; i < max; i++) {
        str += chr;
    }

    return str;
}
//********************* 시계 / 초시계 관련 ********************//




function escapeXSS(val)
{
	return val.replace(/&#40;/g,'(').replace(/&#41;/g,')').replace(/&/g,'').replace(/=/g,'').replace(/:/g,'').replace(/\?/g,'').replace(/\//g,'').replace(/</g,'').replace(/>/g,'').replace(/\'/g,'').replace(/\"/g,'').replace();
}

function escapeHTML(val) 
{
    return val.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/\'/g,'&#039;').replace(/\"/g,'&quot;').replace();	
}

function unescapeHTML(val) {
    return val.replace(/&amp;/g,'&').replace(/&lt;/g,'<').replace(/&gt;/g,'>').replace(/&#039;/g,'\'').replace(/&quot;/g,'\"');
}

function prNl2Br(str) {
	var length = str.length;
	var reStr = "";
	var i;

	for(i=0;i<length;i++) {
		if(escape(str.charAt(i)) == "%0A") {
			reStr = reStr + "<br>";
		} else {
			reStr = reStr + str.charAt(i);
		}
	}
	return reStr;
}

var ajaxChartDataRenderer = function(url, plot, options) {
	    var ret = null;
	    jQuery.ajax({
	      // have to use synchronous here, else the function 
	      // will return before the data is fetched
	      async: false,
	      url: url,
	      dataType:"json",
	      success: function(data) {
	        ret = data;
	      }
	    });
	    return ret;
	};
	
	function goMenuLink(popup_yn, id, url, width, height)
	{
		if(popup_yn == 'Y')
		{
			
			var custModal_width = 1024;
			var custModal_height = 768;
			
			var _left = jQuery(window).width()/2 - custModal_width/2;
			var _top = jQuery(window).height()/2 - custModal_height/2;
			
			//console.log('_left:'+_left);
			//console.log('_top:'+_top);
			var _width = 1024;
			var _height = 768;
			
			if(width){
				_width = width;	
			}
			
			if(height){
				_height = height;
			}
			
			//sparrow 난수생성
			//id = id+'_'+(Math.floor(Math.random() * 10) + 1);
			id = id+'_'+Math.floor(((new Date().getTime()) *9301+49297)%233280/(233280.0) * 10 + 1);
			id = id.replace('-',''); //ie9에서 팝업 호출이 안되므로 변경
			
			var rlt = window.open(url, id, 'toolbar=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width='+_width+', height='+_height+', top='+_top+', left='+_left);
			//return rlt;
			
		} else
		{
			location.href = url;
		}
	}

function time_Form(data){
	if(data < 10){
		data = "0"+data;
	}
	return data;
}

function confirmation(question) {
    var defer = $.Deferred();
    jQuery('<div></div>')
        .html(question)
        .dialog({
            autoOpen: true,
            modal: true,
            title: '확인',
            buttons: {
                "확인": function () {
                    defer.resolve("true");
                    $(this).dialog("close");
                },
                "취소": function () {
                    defer.resolve("false");
                    $(this).dialog("close");
                }
            },
            close: function () {
                $(this).remove();
            }
        });
    return defer.promise();
}


//TABLE HTML 엑셀다운로드 - IE에선 기능안됨.
function table_export(table_div, file_nm, type){
	var Exel = document.createElement('a');
	var table_html = "";
	var data_type = 'data:application/vnd.ms-excel';
	
	jQuery("#"+table_div+" th").css('border', '1px dotted #000000');		//EXEL HEAD DOTTED.
    jQuery("#"+table_div+" td").css('border', '1px dotted #000000');		//EXEL BODY DOTTED.
    jQuery("#"+table_div+" th").css('background-color', '#ddd');				//EXEL BACKCOLOR.
    table_html = encodeURIComponent(jQuery("#"+table_div).html());   	//TABLE HTML SET.     
    jQuery("#"+table_div+" th").css('background-image', 'linear-gradient(to bottom,#f8f8f8 0,#ececec 100%)');	//EXEL BACKCOLOR INIT.
    
    if(type == 'grid'){
    	jQuery("#"+table_div+" th").css('border', '0px solid #ddd');				//EXEL HEAD INIT.
    	jQuery("#"+table_div+" td").css('border', '0px solid #ddd');				//EXEL HEAD INIT.
    	jQuery("#"+table_div+" td").css('border-right', '1px solid #ddd');		//EXEL HEAD INIT.
    	jQuery("#"+table_div+" td").css('border-bottom', '1px solid #ddd');	//EXEL HEAD INIT.
    	jQuery(".jqgfirstrow td").css('border', '0px');										//EXEL HEAD INIT.
    }else{
    	jQuery("#"+table_div+" th").css('border', '1px solid #ddd');			//EXEL HEAD INIT.
        jQuery(".border_head").css('border', '1px solid #000000');			//EXEL HEAD INIT.
        jQuery("#"+table_div+" td").css('border', '1px solid #ddd');			//EXEL BODY INIT.	
    }
    
    Exel.href = data_type + ', ' + table_html;
    Exel.download = file_nm+'.xls';
    Exel.click();
}

//IE에서 TABLE HTML 엑셀다운로드.
//path에 EXCEL다운을 위한 JSP파일 경로지정.
function ie_table_export(path, table_id, filename) {
	var form = document.createElement("form"); 
	
	//var TABLE_HTML = document.getElementById(table_id).innerHTML;
	var TABLE_HTML = jQuery("#"+table_id).html();
	
	TABLE_HTML=TABLE_HTML.replace(/<A(.*?)>/gi,"");
	TABLE_HTML=TABLE_HTML.replace(/<(\/?)A>/gi,"");
	
	var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "TABLE");
    hiddenField.setAttribute("value", TABLE_HTML);  
    form.appendChild(hiddenField);
    
    var hiddenField = document.createElement("input");
    hiddenField.setAttribute("type", "hidden");
    hiddenField.setAttribute("name", "FILENAME");
    hiddenField.setAttribute("value", urlEncode(filename));  
    form.appendChild(hiddenField);
    
    form.setAttribute("method", "post");
    form.setAttribute("action", path);
    
    document.body.appendChild(form);   
	    form.submit();
 }

var oEditors = [];

function initEditor(field, initHtml)
{
	nhn.husky.EZCreator.createInIFrame({
	    oAppRef: oEditors,
	    elPlaceHolder: field,
	    sSkinURI: "/static/lib/nhneditor/SmartEditor2Skin.html",
	    htParams : {
			bUseToolbar : true,				// 툴바 사용 여부 (true:사용/ false:사용하지 않음)
			bUseVerticalResizer : true,		// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
			bUseModeChanger : true,			// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
			fOnBeforeUnload : function(){	//	alert("아싸!");
			}
		}, //boolean
		fOnAppLoad : function(){
			oEditors.getById[field].setDefaultFont('', '11');
			oEditors.getById[field].exec("PASTE_HTML", [initHtml]);
		},
		fCreator: "createSEditor2"
	});
}



//날짜검색 달력그리기.
function Create_datepicker(class_id){
	jQuery('.'+class_id).datepicker({autoclose:true, language:"kr",
  										       onSelect: function() {
  										    	   jQuery('.'+class_id).change();
  										       }
	}).next().on(ace.click_event, function(){
		jQuery(this).prev().focus();
	}).ready(function(){
		jQuery('.icon-arrow-left').addClass('icon- fa fa-arrow-left');
		jQuery('.icon-arrow-right').addClass('icon- fa fa-arrow-right');				
	});	
}

//날짜검색 달력그리기 시작일 세팅.
// start_day : d(일), m(월), y(년) 이전은 음수로 이후는 양수로 표현 
function set_start_datepicker(class_id, day){
	jQuery('.'+class_id).datepicker({autoclose:true, language:"kr",
  										       onSelect: function() {
  										    	   jQuery('.'+class_id).change();
  										       }
  										       ,startDate: day 
	}).next().on(ace.click_event, function(){
		jQuery(this).prev().focus();
	}).ready(function(){
		jQuery('.icon-arrow-left').addClass('icon- fa fa-arrow-left');
		jQuery('.icon-arrow-right').addClass('icon- fa fa-arrow-right');				
	});	
}


//날짜검색 달력그리기 종료일 세팅.
 
function set_end_datepicker(class_id, day){
	jQuery('.'+class_id).datepicker({autoclose:true, language:"kr",
  										       onSelect: function() {
  										    	   jQuery('.'+class_id).change();
  										       }
  										       ,endDate: day 
	}).next().on(ace.click_event, function(){
		jQuery(this).prev().focus();
	}).ready(function(){
		jQuery('.icon-arrow-left').addClass('icon- fa fa-arrow-left');
		jQuery('.icon-arrow-right').addClass('icon- fa fa-arrow-right');				
	});	
}

//엔터로 검색하기.
//특정 태그에서는 엔터로 인한 검색을 막아주기 위해서는 태그안에 notpress='true' 를 넣어주면 됩니다.
function auto_search(target, search_btn_id){

	jQuery(target).on('keypress',function(e){

		if(e.which == 13 && jQuery(e.target).attr('notpress') != "true"){
			jQuery('#'+search_btn_id).click();
			return false;
		}

	});
}

function form_post(data){
	
	if(data>=6){
		data = data.substring(0,3) + "-" + data.substring(3,6)
	}
	return data;
}


function jsonToString(json){
	var str = "";
	for(var prop in json){
		str+="&"+prop+"="+json[prop];	
	}
	return str;
}

function set_grid_paper(grid_id, prev_key, next_key){
	jQuery('#first_'+grid_id).hide();
	jQuery('#last_'+grid_id).hide();
	jQuery('.ui-separator').hide();
	
	jQuery('#'+grid_id+'_prev_key').val(prev_key);
	jQuery('#'+grid_id+'_next_key').val(next_key);
	
	if( parseInt(prev_key) > -1 ){
		jQuery('.ui-pg-table .ui-pg-button#prev_'+grid_id).removeClass("ui-state-disabled");			
	}
	
	if( parseInt(next_key) > -1 ){
		jQuery('.ui-pg-table .ui-pg-button#next_'+grid_id).removeClass("ui-state-disabled");
	}
}

function unescapeHTML2(escaped){
	var cntnt = jQuery.parseHTML(escaped);
	if(!cntnt)  cntnt = escaped;
	else		 cntnt = cntnt[0].data;	
	
	return cntnt;
}



function js_sencr(se)
{
	var s = "";
    jQuery.ajax({
        url : '/common/action/common.jspx?cmd=sencr',                
        data : "s="+se,
        async:false,
        type: 'POST',
        dataType: 'json',
        error: function(xhr){   alert(xhr.status);  },            
        success : function(data){
                    try
                    {
                        if(data.result.code == 200)
                        {
                            s = data.result.data;
                        }
                    } catch(e3)
                    {
                    }
                }
    });
    return s;
}
 


/**
 * 두 날짜 사이의 일 수 계산
 * from : 시작일자 (yyyy-mm-dd or yyyymmdd)
 * to : 종료일자 (yyyy-mm-dd or yyyymmdd)
 * @return 일수 [정상:일수, 오류:-1]
 */
function js_dateDiff(from, to) {
	var from_dt = from.replace(/-/ig, "");
	var to_dt = to.replace(/-/ig, "");
	var result = -1;
	
	if (from_dt.length == 8 && to_dt.length == 8) {
		var from_year = from_dt.substring(0, 4);
		var from_month = parseInt(from_dt.substring(4, 6), 10) - 1;
		var from_day = from_dt.substring(6, 8);
		var from_date = new Date(from_year, from_month, from_day);
		
		var to_year = to_dt.substring(0, 4);
		var to_month = parseInt(to_dt.substring(4, 6), 10) - 1;
		var to_day = to_dt.substring(6, 8);
		var to_date = new Date(to_year, to_month, to_day);
		
		if (from_date != "Invalid Date" && to_date != "Invalid Date") {
			result = (Date.parse(to_date) - Date.parse(from_date)) / (1000*60*60*24);
		}
	}
	
	return result;
}

// 월수를 계산한다.
function js_monthDiff(strtDate,endDate)
{
	strtDate = strtDate.replace("-","").replace("-","");
	endDate = endDate.replace("-","").replace("-","");    	

	var strtYear = parseInt(strtDate.substring(0,4));
	var strtMonth = parseInt(strtDate.substring(4,6));

	var endYear = parseInt(endDate.substring(0,4));
	var endMonth = parseInt(endDate.substring(4,6));

	var month = (endYear - strtYear)* 12 + (endMonth - strtMonth);
	return month;
}


//현재달의 마지막날(일자) 구하기
function js_getLastDay(yyyymmdd)
{
	var yyyymmdd = yyyymmdd.replace(/-/ig, "");
	year = yyyymmdd.substring(0,4);
	month = yyyymmdd.substring(4,6); 
   var lastDay = (new Date( year, month, 0) ).getDate().toString();            
   return lastDay;                             
}

//선택한 달력의 월의시작일자로 반환한다.
function js_getMonthStartDate(startDate)
{
	var convert_date = startDate.substring(0,startDate.length-2)+"01";
	return convert_date;
}


//선택한 달력의 월의 마지막날자를 기준으로 반환한다.
function js_getMonthEndDate(endDate)
{
	var last_day = js_getLastDay(endDate);
	
	var convert_date = endDate.substring(0,endDate.length-2)+last_day;   
	return convert_date;
}



//3자리마다 콤마를 붙힌다.
function addComma(cur)
{
	if(cur == null || cur == "") return cur;
	// 문자인경우 그대로 반환.
	if(! isNum(cur)) return cur;

	var setMinus = "";
    if (cur.charAt(0) == "-") 
	{
        setMinus = "-";
    }

    cur=cur.replace(/[^.0-9]/g ,"");
    cur=cur.replace(/[.]+/g ,".");

	leftString = cur;
	rightString = ".";
	dotIndex = 0;
	  
	for(i = 0; i < cur.length; i++){
		// 1) '.'이 처음에 입력 되었을때 앞에 0을 더해 "0."을 리턴
		// 2) "0."이외의 입력 일 때 "0"만 리턴
		if(cur.charAt(i) == "." || (cur.length > 1 && cur.charAt(0) == "0" && cur.charAt(1) != "."))
		{
			dotIndex = i;
			if(dotIndex == 0)
			{
	            if   (cur.charAt(0) == ".")   leftString="0.";
	            else                          leftString="";
				return leftString;
			}
			break;
		}
	}
	
	 if(dotIndex != 0)	//dot가 있을 경우..
	{
		leftString = cur.substr(0, dotIndex);
		rightString = cur.substr(dotIndex+1);
		rightString = rightString.replace(/\./g,"");
	}
	else //없으면..
	{
		leftString = cur;
	}
	
	len=leftString.length-3;
	while(len>0) 
	{
	    leftString=leftString.substr(0,len)+","+leftString.substr(len);
	    len-=3;
	}           
	
	if(rightString != ".")
	    return setMinus + (leftString + "." + rightString); 
	else
	    return setMinus + leftString;

}


//초를 시분초로 변환
function convSecToHMS(seconds) {
	  var pad = function(x) { return (x < 10) ? "0"+x : x; }
	  return pad(parseInt(seconds / (60*60))) + ":" +
	         pad(parseInt(seconds / 60 % 60)) + ":" +
	         pad(seconds % 60)
	}



function formattedTelnoHipen(str)
{
	var regExp = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$/;
	
	if(!regExp.test(str)){
		return str;
	}
	
	var rtnStr = '';
	var sMsg = str;
	var onlynum = "" ;
	var i = 0 ;
	var s_telno = "";
	for(var j=0; j<sMsg.length; j++){
		if(sMsg.charAt(j) != " ")
		{
			s_telno += sMsg.charAt(j);
		}
	 }
	 sMsg = s_telno;
	 if( isNumHipen(sMsg) == false) return str = str.substring(0,str.value.length-1) ;
	 onlynum = removeHipen(sMsg);
	 
	  if (onlynum.substring(0,2) == "02") 
	  {
	   if (onlynum.getBytes() <= 1) rtnStr = onlynum ;
	   if (onlynum.getBytes() == 2) rtnStr = onlynum + "-" ;
	   if (onlynum.getBytes() == 3) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,3);
	   if (onlynum.getBytes() == 4) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,4);
	   if (onlynum.getBytes() == 5) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" ;
	   if (onlynum.getBytes() == 6) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,6) ;
	   if (onlynum.getBytes() == 7) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,7) ;
	   if (onlynum.getBytes() == 8) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,8) ;
	   if (onlynum.getBytes() == 9) rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,9) ;
	   if (onlynum.getBytes() == 10)rtnStr  = onlynum.substring(0,2) + "-" + onlynum.substring(2,6) + "-" + onlynum.substring(6,10) ;
	  }
	  else 
	  {
	   if (onlynum.getBytes() <= 2) rtnStr = onlynum ;
	   if (onlynum.getBytes() == 3) rtnStr  = onlynum + "-";
	   if (onlynum.getBytes() == 4) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,4);
	   if (onlynum.getBytes() == 5) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,5) ;
	   if (onlynum.getBytes() == 6) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" ;
	   if (onlynum.getBytes() == 7) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,7) ;
	   if (onlynum.getBytes() == 8) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,8) ; 
	   if (onlynum.getBytes() == 9) rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,9) ;
	   if (onlynum.getBytes() == 10)rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,10) ;   if (onlynum.getBytes() == 11)  rtnStr  = onlynum.substring(0,3) + "-" + onlynum.substring(3,7) + "-" + onlynum.substring(7,11) ;
	  }
	 
	 return rtnStr;
}



//자동으로숫자만 반환  
//<input type="text" onkeyup="autoNumOnly(this)" />
// getNumOnly() 함수 의존성 있음.
function autoNumOnly(numObj)
{
	var value = numObj.value;
	value = getNumOnly(value);
	numObj.value = value;
}


//자동으로 -채워서 반환  
//  <input type="text" onkeyup="autoTelnoHipen(this)" />
function autoTelnoHipen(telObj)
{
 var sMsg = telObj.value;
 
 var regExp = /^[0-9-]+$/;
	
 if(!regExp.test(sMsg)){
	return sMsg;
 }
 
 var onlynum = "" ;
 var i = 0 ;
 var s_telno = "";
 for(var j=0; j<sMsg.length; j++){
	if(sMsg.charAt(j) != " "){
		s_telno += sMsg.charAt(j);
	}
 }
 sMsg = s_telno;
 if( isNumHipen(sMsg) == false) return telObj.value = telObj.value .substring(0,telObj.value.length-1) ;
 onlynum = removeHipen(sMsg);
 if(event.keyCode != 8 ) {
  if (onlynum.substring(0,2) == "02") 
  {
   if (onlynum.getBytes() <= 1) telObj.value = onlynum ;
   if (onlynum.getBytes() == 2) telObj.value = onlynum + "-" ;
   if (onlynum.getBytes() == 3)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,3);
   if (onlynum.getBytes() == 4)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,4);
   if (onlynum.getBytes() == 5)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" ;
   if (onlynum.getBytes() == 6)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,6) ;
   if (onlynum.getBytes() == 7)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,7) ;
   if (onlynum.getBytes() == 8)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,8) ;
   if (onlynum.getBytes() == 9)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,5) + "-" + onlynum.substring(5,9) ;
   if (onlynum.getBytes() == 10)  telObj.value  = onlynum.substring(0,2) + "-" + onlynum.substring(2,6) + "-" + onlynum.substring(6,10) ;
  }
  else 
  {
   if (onlynum.getBytes() <= 2) telObj.value = onlynum ;
   if (onlynum.getBytes() == 3)  telObj.value  = onlynum + "-";
   if (onlynum.getBytes() == 4)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,4);
   if (onlynum.getBytes() == 5)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,5) ;
   if (onlynum.getBytes() == 6)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" ;
   if (onlynum.getBytes() == 7)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,7) ;
   if (onlynum.getBytes() == 8)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,8) ; 
   if (onlynum.getBytes() == 9)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,9) ;
   if (onlynum.getBytes() == 10)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,6) + "-" + onlynum.substring(6,10) ;   if (onlynum.getBytes() == 11)  telObj.value  = onlynum.substring(0,3) + "-" + onlynum.substring(3,7) + "-" + onlynum.substring(7,11) ;
  }
 } 
}



//-을 삭제해서 반환
function removeHipen(sNo) 
{
var reNo = "";
for(var i=0; i<sNo.length; i++) {
if ( sNo.charAt(i) != "-" ) {
 reNo += sNo.charAt(i);
}
}
return reNo ;
}


//param : pStartDate - 시작일    yyyyMMdd
//param : pEndDate  - 마지막일    yyyyMMdd
//param : pType       - 'D':일수, 'M':개월수  
//Update. 2014.11.07. 변수명 변경 : strGapDT->strTermCnt  
//Update. 2014.11.07. 개월수 계산 시 년도가 다른 경우 부정확성 보완 : floor->round AND 365.25->365
function fn_calcDayMonthCount(pStartDate, pEndDate, pType) {  
  var strSDT = new Date(pStartDate.substring(0,4),pStartDate.substring(4,6)-1,pStartDate.substring(6,8));  
  var strEDT = new Date(pEndDate.substring(0,4),pEndDate.substring(4,6)-1,pEndDate.substring(6,8));  
  var strTermCnt = 0;  
     
  if(pType == 'D') {  //일수 차이  
      strTermCnt = (strEDT.getTime()-strSDT.getTime())/(1000*60*60*24);  
  } else {            //개월수 차이  
      //년도가 같으면 단순히 월을 마이너스 한다.  
      // => 20090301-20090201 의 경우(윤달이 있는 경우) 아래 else의 로직으로는 정상적인 1이 리턴되지 않는다.  
      if(pEndDate.substring(0,4) == pStartDate.substring(0,4)) {  
          strTermCnt = pEndDate.substring(4,6) * 1 - pStartDate.substring(4,6) * 1;  
      } else {  
          //strTermCnt = Math.floor((strEDT.getTime()-strSDT.getTime())/(1000*60*60*24*365.25/12));  
          strTermCnt = Math.round((strEDT.getTime()-strSDT.getTime())/(1000*60*60*24*365/12));  
      }  
  }  
    
  return strTermCnt;  
}  

/* 
 * jqGrid 공통 모듈
 *  등록일자		: 2016.10.07
 *  개발자		: 이기태
 */
jQuery.fn.jqGridEX2 = function(option){	
	if(this.size() === 0)						return this;
	else if(this.size() > 1)						throw "Too many jquery result, just choose One!";
	else if( typeof option !== "object" )	throw "Invalid type of option : "+(typeof option)+" , not a JSON";

	this.find(".jqgrow").remove();

	if(!this[0].grid){
		this.jqGrid(option);
	}else{
		this.jqGrid('clearGridData');
				
		this.setGridParam({
					url : option.url,
					datatype : option.datatype,
					postData : option.postData
		}).trigger('reloadGrid');
	}
	return this;
};