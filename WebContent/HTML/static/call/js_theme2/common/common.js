
function initPartnerSuggest(objID, rtrnID)
{
	if(typeof(objID) === 'string'){
		objID = '#' + objID;
	}
	
	jQuery( objID ).autocomplete({
        source: function( request, response ) {
            jQuery.ajax({
                url: '/common/action/common.jspx?cmd=suggestPartnerName',
                data: {q : jQuery(objID).val() },
                dataType: "json",
                success: function( data ) {
                    response( jQuery.map( data.partnerList, function( item ) {
                        if (true)
                        {
                            return {
                                label: item.prtn_nm.replace(jQuery( objID ).val(),"<span style='font-weight:bold;color:Blue;'>" + jQuery( objID ).val() + "</span>"),
                                value: item.prtn_nm,                                
                                prtn_no : item.prtn_no
                            };
                        }
                    }));
                }
            });
        },
        minLength: 1,
        autoFocus:false,
        focus : function(){
        	return false;
        },
        search: function(){jQuery(this).addClass('ui-autocomplete-loading');},
        select: function( event, ui ) {
        	
        	if (event.keyCode == 13) { 
        	       jQuery(this).next("input").focus().select();
        	}
        	
                setTextAndValue(ui.item.value, ui.item.prtn_no, rtrnID);
        },
        open: function() {
            jQuery( this ).autocomplete("widget").width("323px");
            jQuery( this ).removeClass("ui-autocomplete-loading").removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
        },
        close: function() {
            jQuery( this ).removeClass("ui-autocomplete-loading").removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
        },
        error: function(xhr, ajaxOptions, thrownError){ alert(thrownError);  alert(xhr.responseText); }
    })
    .data('uiAutocomplete')._renderItem = function( ul, item ) {
        return jQuery( "<li style='cursor:hand; cursor:pointer;'></li>" )
            .data( "item.autocomplete", item )
            .append("<a onclick=\"setTextAndValue('" + item.value + "', '"+item.prtn_no+"', '"+ rtrnID+"');\">"  + unescape(item.label) + "</a>")
        .appendTo( ul );
    };
    
    jQuery('.ui-autocomplete').css('zIndex',1000);

}




function listenerSuggestEmpList(objID, _callbackFunc)
{	
	
	//var objID = "#proc_emp_nm";
	jQuery( objID ).autocomplete({
		source: function( request, response ) {
            jQuery.ajax({
                url: '/common/action/common.jspx?cmd=suggestEmpList',
                data: {q : jQuery(objID).val() },
                dataType: "json",
                success: function( data ) {
                    response( jQuery.map( data.list, function( item ) {
                        if(true)
                        {
                        	var temp = item.srch_txt.split('^');                        	
                        	var emp_nm = temp[0];
                        	var postn_nm = temp[1];
                        	var dept_nm = temp[2];
                        	var emp_no = temp[3];
                            return {
                            	label : emp_nm.replace(jQuery( objID ).val(),"<span style='font-weight:bold;color:Blue;'>" + jQuery( objID ).val() + "</span>") + ' ' + postn_nm+'('+dept_nm+')',
                            	emp_no : emp_no,
                            	value : emp_nm  
                                
                            };
                        }
                    }));
                }
            });
        },
        minLength: 1,
        autoFocus:false,
        focus : function(event, ui)
        {	
        	
        	
        	callFunc(_callbackFunc, ui.item); 
        	//setEmpTextAndValue(ui.item.value,ui.item.emp_no);
        	//event.preventDefault();	
        	//jQuery('#menu_id').val(ui.item.menu_id);
        	//return false;
        },
        search: function(){jQuery(this).addClass('ui-autocomplete-loading');},
        select: function( event, ui ) {
        	//
        	if (event.keyCode == 13) { 
        	       jQuery(this).next("input").focus().select();
        	}
        	//console.log('text:'+ui.item.text);
        	//console.log('value:'+ui.item.value)
        	//setEmpTextAndValue(ui.item.value,ui.item.emp_no);
        	callFunc(_callbackFunc, ui.item);
        	//event.preventDefault();
        	//jQuery('#menu_id').val(ui.item.menu_id);            
        },
        open: function() {
            jQuery( this ).autocomplete("widget").width("190px");
            jQuery( this ).removeClass("ui-autocomplete-loading").removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
        },
        close: function() {
            jQuery( this ).removeClass("ui-autocomplete-loading").removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
        },
        error: function(xhr, ajaxOptions, thrownError){ alert(thrownError);  alert(xhr.responseText); }
		
	})
	.data('uiAutocomplete')._renderItem = function( ul, item ) {
        return jQuery( "<li style='cursor:hand; cursor:pointer;'></li>" )
            .data( "item.autocomplete", item )		            
            //.append("<a onclick=\"javascript:setEmpTextAndValue('" + item.value + "', '"+item.emp_no+"');\">"  + unescape(item.label) + "</a>")
            .append("<a onclick=\"javascript:callFunc('" + _callbackFunc + "', '"+item+"');\">"  + unescape(item.label) + "</a>")
        	.appendTo( ul );
    };
    
    jQuery('.ui-autocomplete').css('zIndex',9999);
	
}

/**
 * 중분류 코드 생성
 * @return {[type]} [description]
 */
function js_mclssCode(){
	
	makeSelectBox('/common/action/code.jspx?cmd=getCodeListByPrntCd','m_cnslt_div_cd','prnt_cd='+jQuery('#b_cnslt_div_cd').val()+"&div_cd=C99",'js_sclssCode');
	try
	{
		makeSelectBox('/common/action/code.jspx?cmd=getCodeListByPrntCd','cnslt_div_cd','prnt_cd='+jQuery('#m_cnslt_div_cd').val()+"&div_cd=C99");
	} catch(e)
	{		
	}
}

/**
 * 소분류 코드 생성
 * @return {[type]} [description]
 */
function js_sclssCode(){
	makeSelectBox('/common/action/code.jspx?cmd=getCodeListByPrntCd','cnslt_div_cd','prnt_cd='+jQuery('#m_cnslt_div_cd').val()+"&div_cd=C99");		
}

//그리드내 고객명 클릭시 고객정보 조회.
function search_cust_info(cust_no){
	//window.close(); 팝업창 닫힘 제거.
	
	if(cust_no == null || cust_no == ''){
		//고객번호를 가져오지 못했을 경우.
		msgStart(msg_com_code_098+" [고객번호 X]");
		return false;
	}
	
	var _url = '/call/common/action/common.jspx?cmd=checkCust';
	var http = jQuery.ajax({
   		url: _url,	   		
   		type: "GET",
   		data : "cust_no="+cust_no,
   		dataType: "json",
   		async : false,	   		
   		error 	: function(xhr)
   		{
			alert(xhr.status);
		},
		success: function(json)
		{
			var code = json.result.code;
			var msg = json.result.msg;
			var item = json.result.data;

			if(code == '200')
			{
				//고객번호로 등록된 고객이 존재하지 않을 경우.
				if(item.checkCust_info[0].exist_cust == '0'){
					msgStart(msg_com_code_098+" [고객번호 O]");
				}
				else{
					if_cust_custModal(cust_no);
				}
			} else
			{
				msgStart(msg_com_code_010+"("+msg+")", 'warning');
			}
		}
	});
	
}



//****************** 시계 / 초시계 관련 *********************/
var currentsec=0;
var currentmin=0;
var currenthour=0;

//시간별 색상 변경 여부.
var hourFlg  = true ;
var minFlg   = true ;
//시간 체크 여부.
var keepgoin = true;
function startStatusTimer()
{
	var warning_hour = [] ; 
	var warning_min  = [] ;
	var warning_hour_color = [] ; 
	var warning_min_color  = [] ;

	if(keepgoin){
		
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

		currentsec+=1;
	}
}
var timerInterVal = null;
function startTimerAgent(){
	
	if(timerInterVal != null){
		restartStatusTimer();
		clearInterval(timerInterVal);
		keepgoin = true;
	}
	
	startStatusTimer();
	timerInterVal = setInterval(function(){startStatusTimer();}, 1000);
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

//상태시간 네온사인 초기화.
function initNeonSign()
{
	jQuery("#hours, #minutes, #seconds").css("color","#FF0000");
	jQuery("#hours, #minutes, #seconds").css("opacity","1");
	jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=60)");
}
//상태시간 네온사인 hour 변경시 발생.
function invokeNeonSignHour(currenthour){
	
	if(currenthour > 0){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","red")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8");
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)");
			});
		});
	}
}
//상태시간 네온사인 minute 변경시 발생.
function invokeNeonSignMin(currentmin, currenthour){

	// 2분경과.
	if(currenthour == 0 && currentmin == 2){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","red")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8");
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)");
			});
		});
	}
	
	// 5분경과.
	if(currenthour == 0 && currentmin == 5){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				setTimeout('jQuery("#hours, #minutes, #seconds").css("color","red")',600) ;
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8");
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)");
			});
		});
	}
	
	// 매 1분마다.(첫 5분은 제외.)
	if(currentmin != 5){
		var options = {} ;
		jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
			jQuery("#hours, #minutes, #seconds").effect('highlight', options, 300, function(){
				jQuery("#hours, #minutes, #seconds").css("opacity","0.8");
				jQuery("#hours, #minutes, #seconds").css("filter","alpha(opacity=80)");
			});
		});
	}
	
}
//********************* 시계 / 초시계 관련 ********************//





/**
* 숫자만 가져온다.
* @param {String} 원본문자열
* @return {String} 숫자만 가져옴 
* 
* ex) alert(  getNumOnly("가12나")  );        결과 12

*/

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




/**
* 숫자만 있는 전화번호에 -을 채워 반환하는 함수
* @param telno 전화번호
* @return 구분자 있는 전화번호
* @classDescription getNumOnly()함수에 약하게 의존함..
* 
* ex) alert(  getTelnoHipen("0117832848")  );    결과 011-783-2848
*/


	function getTelnoHipen(telno) 
   {
		var TelNo = "";
		var DDD = "";
		telno = getNumOnly(telno);
		var telLen = telno.length;

		// 성립안됨
		if (telLen < 9) {
			return telno;
		}

		var headStr = telno.substring(0,3);
		var    headInt = 0;
		try {
			headInt = parseInt(headStr,10);
		} catch(e) 
		{
			return telno;
		}
		
		try
		{
          if (headStr == "070" ) 
          {
               if(telLen == 10){
                   DDD = telno.substring(0,3);
                   TelNo =  telno.substring(3,6) + "-" + telno.substring(6);
               }else{
                   DDD = telno.substring(0,3);
                   TelNo = telno.substring(3,7) + "-" + telno.substring(7);
               }
   			newTel = (DDD + "-" + TelNo);
               return newTel;
           }
          
          if (headStr == "050" ) 
          {
       	   var idx = telno.indexOf('~');
       	   var idx2 = telno.indexOf(',');
       	   
               if(telLen == 11){
                   DDD = telno.substring(0,4);
                   TelNo =  telno.substring(4,7) + "-" + telno.substring(7);
               }else if(telLen == 12){
                   DDD = telno.substring(0,4);
                   TelNo =  telno.substring(4,8) + "-" + telno.substring(8);
               }else{
               	if(idx == 11){
               		DDD = telno.substring(0,4);
                       TelNo = telno.substring(4,7) + "-" + telno.substring(7);
               	}
               	if(idx == 12){
               		DDD = telno.substring(0,4);
                       TelNo = telno.substring(4,8) + "-" + telno.substring(8);
               	}
               }

   			newTel = (DDD + "-" + TelNo);
               return newTel;
           }

			if( headInt >= 10 && headInt < 20 ){
			    //핸드폰
			    if (headStr == "013" ) {
					if(telLen == 11){
					    DDD = telno.substring(0,4);
					    TelNo =  telno.substring(4,7) + "-" + telno.substring(7,11);
					} else {
					    DDD = telno.substring(0,4);
					    TelNo = telno.substring(4,8) + "-" + telno.substring(8,12);
					}
				}else{
					if(telLen == 10){
					    DDD = telno.substring(0,3);
					    TelNo =  telno.substring(3,6) + "-" + telno.substring(6,10);
					}else{
					    DDD = telno.substring(0,3);
					    TelNo = telno.substring(3,7) + "-" + telno.substring(7,11);
					}
			    }
			}else if(headInt >= 20 && headInt < 30 ){

			    //서울
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');
				
			    if(telLen == 9){
			    	DDD = telno.substring(0,2);
					TelNo = telno.substring(2,5) + "-" + telno.substring(5);
				}else if(telLen == 10){
			    	DDD = telno.substring(0,2);
					TelNo = telno.substring(2,6) + "-" + telno.substring(6);
				}else{
					if(idx2 == 9){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					if(idx2 == 10){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
					
					if(idx == 9){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					
					if(idx == 10){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
			    }

			}else if(headInt == 30){

			    //서울


			    if(telLen == 12){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,8) + "-" + telno.substring(8);
				    }else {
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,7) + "-" + telno.substring(7);
			    }


			}else if(headInt > 30 && headInt < 50 ){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');
				
			    //지방


			    if(telLen == 10){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    }else if(telLen == 11){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,7) + "-" + telno.substring(7);
			    }else{
			    	
			    	if(idx == 10){
			    		DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    	}
			    }


			}else if(headInt == 50){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');


				if(telLen == 11){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,7) + "-" + telno.substring(7);
			    }else if(telLen == 12){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,8) + "-" + telno.substring(8);
			    }else {
			    	if(idx == 10){
			    		DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    	}
			    }


			}else if(headInt > 50 && headInt < 70 ){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');


			    //지방


			    if(telLen == 10){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6,10);
			    }else if(telLen == 11){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6,10);
			    }else{
			    	if(idx2 == 9){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					
					if(idx2 == 10){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
					
					if(idx == 10){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
					}
					
					if(idx == 11){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(3,7) + "-" + telno.substring(7);
					}

			    }

			}else if(headInt == 70 ){
				// VOIP

				DDD = telno.substring(0,3);
				TelNo = telno.substring(3,7) + "-" + telno.substring(7,11);
			}else{
			    //외국
			    console.log("외국 " + telno);
			    TelNo = telno;
			}
		}catch(e)
		{
			DDD = "";
			TelNo = "";
		}

		var newTel = "";
		if(DDD =="" && TelNo == "")
		{
			newTel = telno;
		}
		else
		{
			newTel = (DDD + "-" + TelNo);
		}

		return  newTel;
	}


	/**
	 *  컨펌창을 띄운다.
	 * @param title
	 * @param msg
	 * @param btnNm
	 * @param href  이동할 주소  
	 */
function popConfirm(title, msg, btnNm, href, btnNm2, href2)
{
	jQuery("#msg_title").html(title);
	jQuery("#msg_body").html(msg);
	jQuery("#msg_agree").html(btnNm);
	//jQuery("#msg_agree").attr("href",href);
	jQuery("#msg_agree").on("click", function(e) {		setTimeout(href,0); 	});
	
	if(arguments.length > 4)
	{
		jQuery("#msg_not_agree").html(btnNm2);	
		
		jQuery("#msg_not_agree").on("click", function(e) {		setTimeout(href2,0); 	});
		//jQuery("#msg_not_agree").attr("href",href2);	
		jQuery("#msg_not_agree").show();	
	}
	else
	{
		jQuery("#msg_not_agree").hide();	
	}
	
	jQuery("#div_msgbox").addClass("confirmMsgLocation");
	jQuery("#div_msgbox").show();
	jQuery( "#div_msgbox" ).draggable();		 
}	

/**
 * 컨펌창을 닫는다.
 */
function closeConfirm()
{
	jQuery("#div_msgbox").hide();
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

// TIMER의 시간을 가져온다.
function getTimerTime()
{
	
	var hour_sec = parseInt( jQuery("#hours").val() , 10) * 60 * 60; 
	var min_sec = parseInt( jQuery("#minutes").val() , 10) * 60; 
	var sec = parseInt( jQuery("#seconds").val() , 10) ; 
	
	var total_sec = 0;
	try
	{
		total_sec =  hour_sec + min_sec + sec;
		total_sec = getNumOnly(total_sec);
	}catch(e)
	{
	}
	return total_sec;
}

/*쪽지 가져오기 및 창 띄우기(2016.11.24 김영교)*/
function js_openNoteWindow(note_no)
{
	console.log("note No. >> " + note_no);
	js_toggleModalPopup('note_receive');
	js_receiveNote(note_no);
}




/*!
 * cf_call.js
 */



//전화걸기
function cf_calling(number_id){
	jQuery('#phone_number').val(jQuery('#'+number_id).val());
	jQuery('#BTN_Dial').click();
}


//호전환코드 세팅
function cf_call_setTransferd(){
	

}

function cf_getPhone_number(){
	var number = "";
	try{
		number = jQuery('#phone_number').val();
	}catch(e){
		number = "";
	}
	return number;
}

/**
 * 호상태정보 추가
 * @param tel_no
 * @param emp_nm
 * @param statusId
 * @param onPhone
 */
function cf_call_append_call_status(agent_id, tel_no, emp_nm, status_id, workgroups){
	//console.log("cf_call_append_call_status ================ "+tel_no);
	var data_row		= document.createElement("tr");
	data_row.id			= tel_no;
	data_row.groups	= workgroups;
	
	var agent_id_td		   = document.createElement("td");
	agent_id_td.innerHTML = agent_id; 
	
	var name_td			= document.createElement("td");
	name_td.innerHTML = emp_nm;
	
	var status_td	= document.createElement("td");
	var st_span		= document.createElement("span");
	st_span.id			= agent_id;
	st_span.innerHTML		= status_id;
	
	status_td.appendChild(st_span);
	
	var ext_td			= document.createElement("td");
	ext_td.innerHTML = tel_no;

	data_row.appendChild(agent_id_td);
	data_row.appendChild(name_td);
	data_row.appendChild(status_td);
	data_row.appendChild(ext_td);

	jQuery('.call_status_body').append(data_row);
	
	var grp_no = jQuery("#agent_grp_combo").val();
	
	filterAgentsByGroup(grp_no);
}

/**
 * 호상태정보 변경
 * @param target
 * @param tel_no
 * @param emp_nm
 * @param statusId
 * @param onPhone
 * @returns
 */
function cf_call_change_call_status(target, tel_no, emp_nm, status_id, grp_collection){
	//console.log("cf_call_change_call_status ============== "+target);
	target.find("span").html(status_id);
}

/**
 * 호상태정보 삭제
 * @param target
 */
function cf_call_remove_call_status(target){
	//console.log("cf_call_remove_call_status ============== "+target);
	target.remove();
}



function getTelnoHipen(telno) 
{
		var TelNo = "";
		var DDD = "";
		telno = getNumOnly(telno);
		var telLen = telno.length;

		// 성립안됨
		if (telLen < 9) {
			return telno;
		}

		var headStr = telno.substring(0,3);
		var    headInt = 0;
		try {
			headInt = parseInt(headStr,10);
		} catch(e) 
		{
			return telno;
		}
		
		try
		{
       if (headStr == "070" ) 
       {
            if(telLen == 10){
                DDD = telno.substring(0,3);
                TelNo =  telno.substring(3,6) + "-" + telno.substring(6);
            }else{
                DDD = telno.substring(0,3);
                TelNo = telno.substring(3,7) + "-" + telno.substring(7);
            }
			newTel = (DDD + "-" + TelNo);
            return newTel;
        }
       
       if (headStr == "050" ) 
       {
    	   var idx = telno.indexOf('~');
    	   var idx2 = telno.indexOf(',');
    	   
            if(telLen == 11){
                DDD = telno.substring(0,4);
                TelNo =  telno.substring(4,7) + "-" + telno.substring(7);
            }else if(telLen == 12){
                DDD = telno.substring(0,4);
                TelNo =  telno.substring(4,8) + "-" + telno.substring(8);
            }else{
            	if(idx == 11){
            		DDD = telno.substring(0,4);
                    TelNo = telno.substring(4,7) + "-" + telno.substring(7);
            	}
            	if(idx == 12){
            		DDD = telno.substring(0,4);
                    TelNo = telno.substring(4,8) + "-" + telno.substring(8);
            	}
            }

			newTel = (DDD + "-" + TelNo);
            return newTel;
        }

			if( headInt >= 10 && headInt < 20 ){
			    //핸드폰
			    if (headStr == "013" ) {
					if(telLen == 11){
					    DDD = telno.substring(0,4);
					    TelNo =  telno.substring(4,7) + "-" + telno.substring(7,11);
					} else {
					    DDD = telno.substring(0,4);
					    TelNo = telno.substring(4,8) + "-" + telno.substring(8,12);
					}
				}else{
					if(telLen == 10){
					    DDD = telno.substring(0,3);
					    TelNo =  telno.substring(3,6) + "-" + telno.substring(6,10);
					}else{
					    DDD = telno.substring(0,3);
					    TelNo = telno.substring(3,7) + "-" + telno.substring(7,11);
					}
			    }
			}else if(headInt >= 20 && headInt < 30 ){

			    //서울
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');
				
			    if(telLen == 9){
			    	DDD = telno.substring(0,2);
					TelNo = telno.substring(2,5) + "-" + telno.substring(5);
				}else if(telLen == 10){
			    	DDD = telno.substring(0,2);
					TelNo = telno.substring(2,6) + "-" + telno.substring(6);
				}else{
					if(idx2 == 9){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					if(idx2 == 10){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
					
					if(idx == 9){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					
					if(idx == 10){
						DDD = telno.substring(0,2);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
			    }

			}else if(headInt == 30){

			    //서울


			    if(telLen == 12){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,8) + "-" + telno.substring(8);
				    }else {
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,7) + "-" + telno.substring(7);
			    }


			}else if(headInt > 30 && headInt < 50 ){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');
				
			    //지방


			    if(telLen == 10){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    }else if(telLen == 11){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,7) + "-" + telno.substring(7);
			    }else{
			    	
			    	if(idx == 10){
			    		DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    	}
			    }


			}else if(headInt == 50){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');


				if(telLen == 11){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,7) + "-" + telno.substring(7);
			    }else if(telLen == 12){
					DDD = telno.substring(0,4);
					TelNo = telno.substring(4,8) + "-" + telno.substring(8);
			    }else {
			    	if(idx == 10){
			    		DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
			    	}
			    }


			}else if(headInt > 50 && headInt < 70 ){
				
				var idx = telno.indexOf('~');
				var idx2 = telno.indexOf(',');


			    //지방


			    if(telLen == 10){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6,10);
			    }else if(telLen == 11){
					DDD = telno.substring(0,3);
					TelNo = telno.substring(3,6) + "-" + telno.substring(6,10);
			    }else{
			    	if(idx2 == 9){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(2,5) + "-" + telno.substring(5);
					}
					
					if(idx2 == 10){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(2,6) + "-" + telno.substring(6);
					}
					
					if(idx == 10){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(3,6) + "-" + telno.substring(6);
					}
					
					if(idx == 11){
						DDD = telno.substring(0,3);
						TelNo = telno.substring(3,7) + "-" + telno.substring(7);
					}

			    }

			}else if(headInt == 70 ){
				// VOIP

				DDD = telno.substring(0,3);
				TelNo = telno.substring(3,7) + "-" + telno.substring(7,11);
			}else{
			    //외국
			    console.log("외국 " + telno);
			    TelNo = telno;
			}
		}catch(e)
		{
			DDD = "";
			TelNo = "";
		}

		var newTel = "";
		if(DDD =="" && TelNo == "")
		{
			newTel = telno;
		}
		else
		{
			newTel = (DDD + "-" + TelNo);
		}

		return  newTel;
	}

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

