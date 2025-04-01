	
	/**
	* 주기적으로 모니터링 건수를 가져온다.
	*/
	function fn_mon_q_polling()
	{
		fn_mon_q_getBoardCnt();
		fn_mon_q_getMonQCnt();
		setTimeout("fn_mon_q_polling()", 300000);	//5분
	}
	
	//미확인 공지 갯수를 가져온다.
	function fn_mon_q_getBoardCnt(){
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getUnconfirm_boardCnt',				 
			async:false,
			type: 'POST',
			dataType: 'json', 
			success : function(data){
				try
				{
					if(data.result.code == 200)
					{
						var jsonArr = data.result.data;
						var jsonArr = data.result.data;						
						if(jsonArr != null &&  jsonArr.unconfirm_cnt.length > 0)
						{
							var unconfirm_info = jsonArr.unconfirm_cnt[0]; 
							jQuery("#spNOTICE_CNT").html(unconfirm_info.notice);
							jQuery("#spBOARD_CNT").html(unconfirm_info.board);
							jQuery("#spKNOWLEDGE_CNT").html(unconfirm_info.knowledge);
							jQuery("#spNOTE_CNT").html(unconfirm_info.note);
							jQuery("#spCOACHING_CNT").html(unconfirm_info.coaching);
						}
						
					}
				} catch(e3)
				{
					console.log('fn_mon_q_getBoardCnt===>'+e3);
				}
			}
		});
	}
	
	//모니터링 갯수를 가져온다.
	function fn_mon_q_getMonQCnt(){
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getMonQCnt',				 
			//data : "voc_id="+Data.voc_id+"&cust_id="+Data.cust_id+"&ssn_no="+Data.ssn_no,
	   		async:false,
			type: 'POST',
			dataType: 'json',
			success : function(data){
						try
						{
							//console.log("cust load data: " + data);
							if(data.result.code == 200)
							{
								var jsonArr = data.result.data;
								var a = jsonArr.mon_q_cnt_list;
								
								jQuery("#spTODAY_CNSLT_CNT").val(a[0].mon_q_cnt);
								jQuery("#spCALLBACK_CNT").html(a[1].mon_q_cnt);
								jQuery("#spHOMEPAGE_CNT").html(a[2].mon_q_cnt);
								jQuery("#spMAIL_CNT").html(a[3].mon_q_cnt);
								jQuery("#spSMS_CNT").html(a[4].mon_q_cnt);
								jQuery("#spTEMP_CNT").html(a[5].mon_q_cnt);
								jQuery("#spRSRV_CNT").html(a[6].mon_q_cnt);
								jQuery("#spTRANSFER_ACCEPT_CNT").html(a[7].mon_q_cnt);
								jQuery("#spRETRANSFER_CNT").html(a[8].mon_q_cnt);
								jQuery("#spMOVE_CNT").html(a[9].mon_q_cnt);
								jQuery("#spHAPPY_CNT").html(a[10].mon_q_cnt);
								jQuery("#spRSCH_CNT").html(a[11].mon_q_cnt);
								jQuery("#spCHANGE_CNT").html(a[15].mon_q_cnt);
								jQuery("#spCHANGE_REQ_CNT").html(a[16].mon_q_cnt);
								jQuery("#spTRANSFER_SEND_CNT").html(a[17].mon_q_cnt);
								jQuery("#spTRANSFER_TAKEN_CNT").html(a[18].mon_q_cnt);
								
								jQuery("#spMOVE_SEND_CNT").html(a[19].mon_q_cnt);
								jQuery("#spMOVE_ACCEPT_CNT").html(a[20].mon_q_cnt);
								jQuery("#spMOVE_TAKEN_CNT").html(a[21].mon_q_cnt);
								jQuery("#spMOVE_REJECT_CNT").html(a[22].mon_q_cnt);
								jQuery("#spMOVE_FEEDBACK_CNT").html(a[23].mon_q_cnt);
								
								//예약통화가 있다면
								if(a[12].mon_q_cnt > 0)
								{
									fn_mon_q_popAlram('rsrvAlram');
								}
								//예약통화가 있다면(해피콜))
								if(a[13].mon_q_cnt > 0)
								{
									fn_mon_q_popAlram('happyAlram');
								}
								//예약통화가 있다면(설문조사)
								if(a[14].mon_q_cnt > 0)
								{
									fn_mon_q_popAlram('surveyAlram');
								}
							}
						} catch(e3)
						{
							console.log('getMonQCnt===>'+e3);
						}
					}
		});
	}
	
	

	//모니터링 상세내역을 보여준다.
	// rsrv_yn  설문조사를 필터하기위한 파라미터
	function fn_mon_q_detail(mon_q_item,rsrv_yn)
	{
		jQuery('.modal_mon_q_table_div').css("display", "none");
		
		console.log( "mon_q_item ==>"+mon_q_item);
		if(mon_q_item == null || mon_q_item == "" || mon_q_item == "INBOUND" || mon_q_item == "ETC" ) return;
		
		jQuery('#mon_q_task_name').val(mon_q_item);
		
		lf_setAccordion(0, 1);
		
		var action = "";
		switch( mon_q_item )
		{
			case "CALLBACK" :
									jQuery("#mon_q_title").html("콜 백");
									action = "getMonQCallback";		
									break;
			case "HOMEPAGE" :
									jQuery("#mon_q_title").html("홈페이지");
									action = "getMonQHomepage";		
									break;
			case "MAIL" :
									jQuery("#mon_q_title").html("이메일");
									action = "getMonQMail";		
									break;
			case "SMS" :
									jQuery("#mon_q_title").html("문 자");
									action = "getMonQSms";		
									break;
			case "TEMP" :
									jQuery("#mon_q_title").html("임시저장");
									action = "getMonQTemp";		
									break;
			case "RSRV" :
									jQuery("#mon_q_title").html("통화예약");
									action = "getMonQRsrv";		
									break;
			case "TRANSFER_ACCEPT" :
									jQuery("#mon_q_title").html("이첩접수");
									action = "getMonQTransfer";		
									break;
			case "RETRANSFER" :
									jQuery("#mon_q_title").html("이첩반려");
									action = "getMonQReTransfer";		
									break;
			case "MOVE" :
									jQuery("#mon_q_title").html("이 관");
									action = "getMonQReTransfer";		
									break;
			case "HAPPY" :
									jQuery("#mon_q_title").html("해피콜");
									action = "getMonQHappy";		
									if(rsrv_yn == "Y")
									{
										action = "getMonQHappyByRsrv";
									}
									break;
			case "RSCH" :
									jQuery("#mon_q_title").html("설 문");
									action = "getMonQRsch";		
									if(rsrv_yn == "Y")
									{
										action = "getMonQRschByRsrv";
									}
									break;
			case "CHANGE" :
									jQuery("#mon_q_title").html("담당자변경승인요청");
									action = "getMonQChange";		
									break;
			case "CHANGE_REQ" :
									jQuery("#mon_q_title").html("담당자변경요청");
									action = "getMonQReqChange";		
									break;
			case "TRANSFER_SEND" :
									jQuery("#mon_q_title").html("이첩요청");
									action = "getMonQTransferSend";		
									break;
			case "TRANSFER_TAKEN" :
									jQuery("#mon_q_title").html("이첩회수");
									action = "getMonQTransferTaken";		
									break;
									
			case "MOVE_SEND" :
									jQuery("#mon_q_title").html("이관요청");
									action = "getMonQMoveSend";		
									break;
			case "MOVE_ACCEPT" :
									jQuery("#mon_q_title").html("이관접수");
									action = "getMonQMoveAccept";		
									break;
			case "MOVE_REJECT" :
									jQuery("#mon_q_title").html("이관반려");
									action = "getMonQMoveReject";		
									break;
			case "MOVE_TAKEN" :
									jQuery("#mon_q_title").html("이관회수");
									action = "getMonQMoveTaken";		
									break;
			case "MOVE_FEEDBACK" :
									jQuery("#mon_q_title").html("이관피드백");
									action = "getMonQMoveFeedback";		
									break;
							

		}

		if(action == null || action == "" || action == "INBOUND" || action == "ETC" ) return;
		
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getMonQDetail&item='+action,				 
			//data : "voc_id="+Data.voc_id+"&cust_id="+Data.cust_id+"&ssn_no="+Data.ssn_no,
	   		data : '0=0',
			async:true,
			type: 'GET',
			dataType: 'json',
			success : function(jsonObj){
						try
						{
							if(jsonObj.result.code == 200)
							{
								var jsonArr = jsonObj.result.data;
								
								var mon_q_detail_list = null;
								
								var table_num = fn_mon_q_getTableNum(mon_q_item);
								var table_id = "table_mon_q" + table_num;
								var modal_id = "modal_mon_q" + table_num;
								
								mon_q_detail_list = table_num == "6" ? jsonArr.mon_q_rsch_list : jsonArr.mon_q_detail_list;  
								
								var json 	= mon_q_detail_list;
								var jsonCnt = mon_q_detail_list.length;
								var listHtml = [];
							
								fn_mon_q_paintTable(mon_q_item, table_num, table_id, modal_id, mon_q_detail_list);
								
								if(jsonCnt > 0) {
									
									if(mon_q_item == "CALLBACK" || mon_q_item == "SMS" || mon_q_item == "TEMP" || mon_q_item == "RSRV"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;">'); //  onclick="fn_mon_q_clicked('+mon_q_item+','+json[i]+')"
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].tel_no+'</td>');
											listHtml.push('	<td align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('</tr>');
										}
									} 
									else if(mon_q_item == "TRANSFER_ACCEPT" || mon_q_item == "RETRANSFER"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].req_cntnt+'">'+json[i].req_cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "HOMEPAGE"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" id="tr_mon_q3_'+json[i].seq_no+'">');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center" class="EllipsText" title="'+json[i].title+'">'+json[i].title+'</td>');
											listHtml.push('	<td align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MAIL"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center" class="EllipsText" title="'+json[i].from_email+'">'+json[i].from_email+'</td>');
											listHtml.push('	<td align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MOVE"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center" class="EllipsText" title="'+json[i].cntnt+'">'+json[i].cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "HAPPY"  || mon_q_item == "RSCH"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="배정일시:'+json[i].assgn_dt+'/예약일시:'+json[i].rsrv_dt+'">'+json[i].assgn_ymd+'</td>');
											listHtml.push('	<td align="center" title="제목:'+json[i].title+'">'+json[i].tel_no+'</td>');
											listHtml.push('	<td align="center" title="제목:'+json[i].title+'">'+json[i].cust_nm+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "CHANGE"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].req_emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].title+'">'+json[i].title+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "CHANGE_REQ"){
		
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].aprv_emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].title+'">'+json[i].title+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "TRANSFER_SEND"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].req_cntnt+'">'+json[i].req_cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "TRANSFER_TAKEN"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].last_cntct+'">'+json[i].last_cntct+'</td>');
											listHtml.push('</tr>');
										}
									}
									
									
									
									
									else if(mon_q_item == "MOVE_SEND"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].req_cntnt+'">'+json[i].req_cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MOVE_ACCEPT"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].req_cntnt+'">'+json[i].req_cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MOVE_REJECT"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].req_cntnt+'">'+json[i].req_cntnt+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MOVE_TAKEN"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('	<td class="EllipsText" title="'+json[i].last_cntct+'">'+json[i].last_cntct+'</td>');
											listHtml.push('</tr>');
										}
									}
									else if(mon_q_item == "MOVE_FEEDBACK"){
										
										for(i=0;i<jsonCnt;i++){
											
											listHtml.push('<tr style="cursor:pointer;" >');
											listHtml.push('	<td align="center">'+(i+1)+'</td>');
											listHtml.push('	<td align="center" title="'+json[i].reg_dt+'">'+json[i].reg_ymd+'</td>');
											listHtml.push('	<td align="center">'+json[i].emp_nm+'</td>');
											listHtml.push('	<td class="EllipsText" align="center">'+json[i].cust_nm+'</td>');
											listHtml.push('</tr>');
										}
									}
									
								} else {
									listHtml.push('<tr><td colspan="4" align="center">데이터가 없습니다.</td></tr>');	
								}
														
								fn_mon_q_showTable(table_num);
							} else {
								msgStart(msg_com_code_010+"("+jsonObj.result.msg+")", 'danger');
							}
							
							
							jQuery('#monq'+table_num+'_gridDiv').html(listHtml.join(''));
							
							//모달창 제어
							jQuery("#modal_title").html(jQuery('#mon_q_title').text());
							jQuery('#modal_type_'+table_num).css("display", "");
							jQuery('#list_div_'+table_num).html(listHtml.join(''));
							
							
						} catch(e3)
						{
							console.log('fn_mon_q_detail===>'+e3);
						}
					}
		});
		
		
	}
	
	var click_mon_q_detail_list = null;
	var click_mon_q_item = null;
	var rowIndex = 0;
	
	function fn_mon_q_paintTable(mon_q_item, table_num, table_id, modal_id, mon_q_detail_list)
	{
		
		jQuery("#"+table_id+" tbody").empty();
		jQuery("#"+modal_id+" tbody").empty();
		
		
		if(mon_q_detail_list.length > 0)		
		{
			click_mon_q_detail_list = mon_q_detail_list;
			click_mon_q_item = mon_q_item;
			
			jQuery("#"+table_id+" tbody").off().on('click', 'tr', function () {
				rowIndex = jQuery(this).index();
				
				
				if(table_id == 'table_mon_q2'){ // 이첩접수건에서 목록 클릭시, 이첩접수 처리이력 추가. 
					var isok = fn_mon_q_addTransferAcceptProc(click_mon_q_detail_list[rowIndex].cnslt_seq_no);
					
					if( isok == 'Y' ){
						fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
						
					}else{
						com_alarm_dialog("alarm_dialog", "알림창", "해당 상담건은 이첩요청자가 회수하여 처리할 수 없습니다.", 320, 120);
					}
					
				}else if(table_id == 'table_mon_q13'){ // 이관접수건에서 목록 클릭시, 이관접수 처리이력 추가. 
					var isok = fn_mon_q_addMoveAcceptProc(click_mon_q_detail_list[rowIndex].cnslt_seq_no, click_mon_q_detail_list[rowIndex].trans_no);
					
					if( isok == 'Y' ){
						fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
						
					}else{
						com_alarm_dialog("alarm_dialog", "알림창", "해당 상담건은 이관요청자가 회수하여 처리할 수 없습니다.", 320, 120);
					}
					
				}else{
					fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
					
				}
				
				return false;
			});
			
			//모달창도 기능 동일하게 추가
			jQuery("#"+modal_id+" tbody").off().on('click', 'tr', function () {
				rowIndex = jQuery(this).index();
				
				
				if(modal_id == 'modal_mon_q2'){ // 이첩접수건에서 목록 클릭시, 이첩접수 처리이력 추가. 
					var isok = fn_mon_q_addTransferAcceptProc(click_mon_q_detail_list[rowIndex].cnslt_seq_no);
					
					if( isok == 'Y' ){
						fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
						
					}else{
						com_alarm_dialog("alarm_dialog", "알림창", "해당 상담건은 이첩요청자가 회수하여 처리할 수 없습니다.", 320, 120);
					}
					
				}else if(modal_id == 'modal_mon_q13'){ // 이관접수건에서 목록 클릭시, 이관접수 처리이력 추가. 
					var isok = fn_mon_q_addMoveAcceptProc(click_mon_q_detail_list[rowIndex].cnslt_seq_no, click_mon_q_detail_list[rowIndex].trans_no);
					
					if( isok == 'Y' ){
						fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
						
					}else{
						com_alarm_dialog("alarm_dialog", "알림창", "해당 상담건은 이관요청자가 회수하여 처리할 수 없습니다.", 320, 120);
					}
					
				}else{
					fn_mon_q_clicked(click_mon_q_item, click_mon_q_detail_list[rowIndex] );
					
				}
				
				return false;
			});
		}
		else
		{
			click_mon_q_detail_list = null;
			click_mon_q_item = null;
			var nodata = '<tr><td colspan="4" align="center">데이터가 없습니다.</td></tr>';
			
			jQuery("#"+table_id+" tbody").append(nodata);	
			jQuery("#"+modal_id+" tbody").append(nodata);	
		}
	}
	
	function fn_mon_q_getTableNum(mon_q_item)
	{
		var table_num = "";
		if( mon_q_item == "CALLBACK" || mon_q_item == "SMS" || mon_q_item == "TEMP" || mon_q_item == "RSRV")
		{
			table_num = "1";
		}
		else if(mon_q_item == "TRANSFER_ACCEPT")
		{
			table_num = "2";
		}
		else if(mon_q_item == "HOMEPAGE"  )
		{
			table_num = "3";
		}
		else if(mon_q_item == "MAIL"  )
		{
			table_num = "4";
		}
		else if(mon_q_item == "MOVE"  )
		{
			table_num = "5";
		}
		else if(mon_q_item == "HAPPY"  || mon_q_item == "RSCH" )
		{
			table_num = "6";
		}		
		else if(mon_q_item == "CHANGE"  )
		{
			table_num = "7";
		}		
		else if(mon_q_item == "CHANGE_REQ"  )
		{
			table_num = "8";
		}
		else if(mon_q_item == "TRANSFER_SEND")
		{
			table_num = "9";
		}
		else if(mon_q_item == "TRANSFER_TAKEN")
		{
			table_num = "10";
		}
		else if(mon_q_item == "RETRANSFER")
		{
			table_num = "11";
		}
		
		else if(mon_q_item == "MOVE_SEND")
		{
			table_num = "12";
		}
		else if(mon_q_item == "MOVE_ACCEPT")
		{
			table_num = "13";
		}
		else if(mon_q_item == "MOVE_REJECT")
		{
			table_num = "14";
		}
		else if(mon_q_item == "MOVE_TAKEN")
		{
			table_num = "15";
		}
		else if(mon_q_item == "MOVE_FEEDBACK")
		{
			table_num = "16";
		}
		return table_num;
	}
	
	
	function fn_mon_q_showTable(table_num)
	{
		for(i=1;i<=16;i++)
		{
			jQuery("#table_mon_q"+i).hide();
		}
		jQuery("#table_mon_q"+table_num).show();	
	}
	
	
	function fn_mon_q_init()
	{
		jQuery("#mon_q_task").val(""); 
		jQuery('#mon_q_task_name').val("");
		jQuery("#rcv_chnl_cd").val(""); 
		jQuery("#rcv_data_no").val(""); 
		jQuery("#io").val(""); 
		jQuery("#call_id").val(""); 
		jQuery("#cid").val("");
		
		jQuery("#dnis_block").css("display","none")
		jQuery("#dnis").val("");
		jQuery("#call_time").val("");
	}
	

	
	/**
	*mon_q쪽에서 load되는 방식이라 여기서 제거해준다.
	*/
	function fn_mon_q_surveyInit()
	{
		jQuery("#cnslt_survey_history").empty();
		jQuery("#cnslt_survey_detail").empty();		
	}
	
	
	function fn_mon_q_init_all()
	{
		try{ jQuery('#phone_number').val(''); /*소프트폰 영역 밑 전화번호 초기화*/}catch(e){console.log('phone_number=-1==>'+e);}
		try{ fn_cust_custModal_dialog_init(); /*고객검색팝업 초기화*/ }catch(e){console.log('fn_cust_custModal_dialog_init=0==>'+e);}
		try{ fn_mon_q_init(); /*모니터링영역초기화*/ }catch(e){console.log('fn_mon_q_init_all=1==>'+e);}
		try{ if_cust_info_init(); /*고객정보 초기화*/ }catch(e){console.log('fn_mon_q_init_all=2==>'+e);}
		try{ if_cnslt_hist_getCnsltHst("-1");	/*상담이력 초기화*/ }catch(e){console.log('fn_mon_q_init_all=3==>'+e);}
		try{ if_happy_hist_getHappyHst("-1"); /*해피콜이력 초기화.*/}catch(e){console.log('fn_mon_q_init_all=4==>'+e);}
		try{ if_cnslt_detail_chnl_init(); /*상담상세 채널 초기화*/ }catch(e){console.log('fn_mon_q_init_all=5==>'+e);}
		try{ cnslt_info_info = null; }catch(e){console.log('fn_mon_q_init_all=6==>'+e);}
		try{ if_cnslt_detail_info_init(); /*상담상세 초기화*/ }catch(e){console.log('fn_mon_q_init_all=7==>'+e);}
		try{ if_cnslt_detail_rsrv_init(); /*통화예약 초기화*/ }catch(e){console.log('fn_mon_q_init_all=8==>'+e);}
		try{ fn_mon_q_surveyInit(); /*설문초기화*/ }catch(e){console.log('fn_mon_q_init_all=9==>'+e);}
		try{ if_cnslt_hist_panel_show(); }catch(e){console.log('fn_mon_q_init_all=10==>'+e);}
		try{ if_cnslt_detail_panel_show(); }catch(e){console.log('fn_mon_q_init_all=11==>'+e);}
	}
	

	
	function fn_mon_q_clicked(mon_q_item, row)
	{
		//if(mon_q_item == "CHANGE_REQ") return;
		
		var seq_no = row.seq_no;
		var cnslt_seq_no = row.cnslt_seq_no;
		var cust_no = row.cust_no;
		var tel_no = row.tel_no;
		var cust_nm = row.cust_nm;
		var email = row.from_email;
		var rsrv_tel_no = row.rsrv_tel_no;
		var last_cntct = row.last_cntct;
		var rsrv_dt = row.rsrv_dt;
		var rsrv_tel_no = row.rsrv_tel_no; 
		var rsrv_cntnt = row.rsrv_cntnt; 
		var req_cntnt = row.req_cntnt;
		
		var survey_dstn_no = row.survey_dstn_no;
		var survey_id = row.survey_id;
		var survey_title = row.survey_title;
		
		////STEP 0. 클리어
		fn_mon_q_init_all();
		
		if( mon_q_item == "TRANSFER_SEND" ){
			if(row.proc_stat_cd != 'ISMACNST016'){
				com_alarm_dialog("alarm_dialog", "알림창", "이첩접수 된 상담이력이라 회수할 수 없습니다.", 320, 120);
			}
		}else if( mon_q_item == "MOVE_SEND" ){
			if(row.proc_stat_cd != 'ISMACNST017'){
				com_alarm_dialog("alarm_dialog", "알림창", "이관처리중인 상담이력이라 회수할 수 없습니다.", 320, 120);
			}
		}
		
		//STEP 1.고객호출
		try
		{
			if(cust_no != "" && cust_no != 'undefined')
			{
				if_cust_getCustInfo("cust_no", cust_no);
			}
			else if(tel_no != "" && tel_no != 'undefined')
			{
				if_cust_getCustInfo("tel_no", tel_no);
			}
			else if(email != "" && email != 'undefined')
			{
				if_cust_set_etc(cust_nm, email);
			}
			
		}catch(e) 
		{
			console.log('fn_mon_q_paintTable===>'+e);
		}
		
		//STEP 2. 고객에 대한 이력호출 if_mon_q_getCnsltHst 호출당함


		//STEP 3. 모니터링 내용을 메인화면에 출력 		
		jQuery("#mon_q_task").val(mon_q_item);	
		
		
		//상담 등록시점 설정
		jQuery("#cnslt_reg_dt").val(fn_mon_q_getCurTime());	
			
		//해피콜인 경우 탭 보여줌
		if(mon_q_item == "HAPPY"){
			jQuery("#happy_tab").css("display", "");
		} else {
			jQuery("#happy_tab").css("display", "none");
			jQuery("#happy_history_span").css("display", "none");
		}
	 
		changeCustTab("CUST_INFO_TAB");	// 고객선택 시 무조건 기본정보 탭으로 가도록
		
		switch( mon_q_item )
		{
			case "CALLBACK" :
									if_cnslt_detail_setCnsltInfoPanel("CALLBACK", row);
									fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);	//콜백도 접촉결과코드에의해 저장이 되고 다시 부를 수 있다.
									break;
			case "HOMEPAGE" :
									if_cnslt_detail_setCnsltInfoPanel("HOMEPAGE", row);
									break;
			case "MAIL" :
									if_cnslt_detail_setCnsltInfoPanel("MAIL", row);
									break;
			case "SMS" :
									if_cnslt_detail_setCnsltInfoPanel("SMS", row);
									action = "getMonQSms";		
									break;
			case "TEMP" :
									var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
									
									var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
									if(row2 != null && row2.rcv_chnl_nm != undefined)
									{
										if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);	
									}
									break;
			case "RSRV" :
									var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
									var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
									if(row2 != null && row2.rcv_chnl_nm != undefined)
									{
										if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);	
									}
									if_cnslt_detail_setCnsltInfoPanel("RSRV", row);
									
									jQuery("#phone_number").val(rsrv_tel_no);
									break;
			case "TRANSFER_ACCEPT" :
									var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
									var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
									
									if(row2 != null && row2.rcv_chnl_nm != undefined)
									{
										if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);	
									}
									if_cnslt_detail_setCnsltInfoPanel("TRANSFER_ACCEPT", row);
									break;
			case "RETRANSFER" :
									var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);		
									var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
									if(row2 != null && row2.rcv_chnl_nm != undefined)
									{
										if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);	
									}
									if_cnslt_detail_setCnsltInfoPanel("RETRANSFER", row);
									break;
			case "HAPPY" :
									if_cnslt_detail_panel_hide();
									fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no)
									
									jQuery("#cnslt_survey_detail").load("/call/survey/theme2/_happycall_detail.jsp",function (){
										if_survey_detail_info(survey_dstn_no, survey_title, cnslt_seq_no, cust_no);
										if_survey_qstn_info(survey_dstn_no, survey_id);										
									});
									
									jQuery("#phone_number").val(tel_no); 
									jQuery("#BTN_Dial").click();									
									break;
									
			case "RSCH" :
								if_cnslt_detail_panel_hide();
								if_cnslt_hist_panel_hide();
								jQuery("#cnslt_survey_history").load("/call/survey/theme2/_survey_hist.jsp",function (){
									if_survey_hist(cust_no);										
								});
								
								jQuery("#cnslt_survey_detail").load("/call/survey/theme2/_survey_detail.jsp",function (){
									if_survey_detail_info(survey_dstn_no, survey_title, cnslt_seq_no, cust_no);
									if_survey_qstn_info(survey_dstn_no, survey_id);			
								});
								
								jQuery("#phone_number").val(tel_no);
								jQuery("#BTN_Dial").click();
								
								
								break;
			case "CHANGE" :
								if_cnslt_detail_setCnsltInfoPanel("HOMEPAGE", row);
								popConfirm("담당자변경 승인요청", "담당자를 변경을 승인하시겠습니까?","승인","javacscript:fn_mon_q_chrgChange('Y','"+row.rcv_chnl_cd+"',"+seq_no+","+row.req_emp_no+")","거부","javacscript:fn_mon_q_chrgChange('N','"+row.rcv_chnl_cd+"',"+seq_no+","+row.req_emp_no+")" );
								break;								
			
			case "CHANGE_REQ" :
								if_cnslt_detail_setCnsltInfoPanel("HOMEPAGE", row);
								break;
			
			//이첩 요청 - 회수를 위해 보여줌.					
			case "TRANSFER_SEND" :
								var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
								var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
								if(row2 != null && row2.rcv_chnl_nm != undefined)
								{
									if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);
								}
								if_cnslt_detail_setCnsltInfoPanel("TRANSFER_SEND", row);
								break;
								
			//이첩 회수.					
			case "TRANSFER_TAKEN" :
								var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
								var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
								if(row2 != null && row2.rcv_chnl_nm != undefined)
								{
									if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);
								}
								if_cnslt_detail_setCnsltInfoPanel("TRANSFER_TAKEN", row);
								break;
								
								
			//이관요청/처리중/반려/회수/답변/피드백					
			case "MOVE_SEND" :
			case "MOVE_ACCEPT" :
			case "MOVE_REJECT" :
			case "MOVE_TAKEN" :
			case "MOVE_FEEDBACK" :
								var v_cnslt_seq_no = fn_mon_q_cnslt(mon_q_item,cnslt_seq_no,seq_no);
								var row2 = fn_mon_q_getCnsltFirtRcvChnl(v_cnslt_seq_no);
								if(row2 != null && row2.rcv_chnl_nm != undefined)
								{
									if_cnslt_detail_setCnsltInfoPanel(row2.rcv_chnl_nm, row2);
								}
								if_cnslt_detail_setCnsltInfoPanel(mon_q_item, row);
								break;
		}
		
		//콜백이 아닐경우는 전화접촉결과코드를 고정한다.
		if(mon_q_item != "CALLBACK" && mon_q_item != "HAPPY" && mon_q_item != "RSCH")
		{
			if_cnslt_detail_inbound_set_rslt_cd();
		}
		
		//담당자 변경승인요청은 본인 상담이 아니므로 버튼 제어.
		if(mon_q_item == "CHANGE_REQ")
		{
			jQuery('#detail_form_init_btn').hide();// 새로입력 숨김.
			jQuery('#cnslt_tmp_reg_btn').hide();// 임시저장버튼 숨김.
			jQuery('#cnslt_reg_btn').hide();	// 저장버튼 숨김.
			jQuery('#detail_rsrv_btn').hide();	//이관과 통화예약은 같이 할 수 없다.
			jQuery('#spBtnLastDpmtList').hide();
			jQuery('#proc_chnl_cd').prop('disabled', true);
			jQuery('#trans_emp_nm').prop('disabled', true);
		}
	}
	
	//전에 상담했던 내역이 있으면 상담내역을 출력한다. 
	function fn_mon_q_cnslt(mon_q_item, cnslt_seq_no, seq_no)
	{
		
		var v_cnslt_seq_no = "";
		console.log("mon_q_item : " + mon_q_item + "/ cnslt_seq_no : " +cnslt_seq_no + " / seq_no : "+seq_no )
		
		
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getCnsltInfo&mon_q_item='+mon_q_item,				 
			data : "cnslt_seq_no="+cnslt_seq_no+"&rcv_data_no="+seq_no,
	   		async:false,
			type: 'POST',
			dataType: 'json',
			success : function(data){
						try
						{
							console.log("cust load data: " + data);
							if(data.result.code == 200)
							{
								var jsonArr = data.result.data;
								if(jsonArr != null &&  jsonArr.mon_q_cnslt_info.length > 0)
								{
									var cnslt_info = jsonArr.mon_q_cnslt_info[0]; 
									v_cnslt_seq_no = cnslt_info.cnslt_seq_no;
									if_cnslt_detail_setCnsltInfo(cnslt_info);
								}
							}
						} catch(e3)
						{
							console.log('fn_mon_q_cnslt===>'+e3);
						}
					}
		});
		return v_cnslt_seq_no;
	}
	
	
	function fn_mon_q_getCurTime()
	{
		var time = "";
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getCurTime',				 
			//data : "voc_id="+Data.voc_id+"&cust_id="+Data.cust_id+"&ssn_no="+Data.ssn_no,
	   		async:false,
			type: 'POST',
			dataType: 'json',
			success : function(data){
						try
						{
							//console.log("cust load data: " + data);
							if(data.result.code == 200)
							{
								var jsonArr = data.result.data;
								var time_info = jsonArr.cur_time[0];
								time = time_info.cur_time;
							}
						} catch(e3)
						{
							console.log('fn_mon_q_getCurTime===>'+e3);
						}
					}
		});
		return time;
	}
	
	function fn_mon_q_getCnsltFirtRcvChnl(cnslt_seq_no)
	{
		var row = {};
		var mon_q_detail = null;
		jQuery.ajax({
			url : '/call/mon_q/action/mon_q.jspx?cmd=getCnsltFirtRcvChnl',				 
			data : "cnslt_seq_no="+cnslt_seq_no,
	   		async:false,
			type: 'POST',
			dataType: 'json',
			success : function(data){
						try
						{
							var jsonArr = data.result.data;
							if(jsonArr != null &&  jsonArr.mon_q_detail_list.length > 0)
							{
								mon_q_detail = jsonArr.mon_q_detail_list[0]; 
								//row.data  = mon_q_detail;
							}
						} catch(e3)
						{
							console.log('fn_mon_q_getCnsltFirtRcvChnl===>'+e3);
						}
					}
		});
		return mon_q_detail;
	}
	
	
	function fn_mon_q_popAlram(id)
	{
		
		var html = "";
		if(id == "rsrvAlram")		
		{
			html = '<a onClick="fn_mon_q_detail(\'RSRV\');fn_mon_q_closeAlram(\'rsrvAlram\')" style="cursor:hand"><span class="fb stress"  id="recvTelNo">예약된 통화시간<br>이 되었습니다.</span></a>';
		}
		else if(id == "happyAlram")
		{
			html = '<a onClick="fn_mon_q_detail(\'HAPPY\',\'Y\');fn_mon_q_closeAlram(\'rsrvAlram\')" style="cursor:hand"><span class="fb stress"  id="recvTelNo">예약된 해피콜통화<br>시간이 되었습니다.</span></a>';
		}
		else if(id == "surveyAlram")
		{
			html = '<a onClick="fn_mon_q_detail(\'RSCH\',\'Y\');fn_mon_q_closeAlram(\'rsrvAlram\')" style="cursor:hand"><span class="fb stress"  id="recvTelNo">예약된 설문통화<br>시간이 되었습니다.</span></a>';
		}
		jQuery("#spRsrv").html(html);
		jQuery("#rsrvAlram").css("display","");
		
	} 
	
	function fn_mon_q_closeAlram(id)
	{
		jQuery("#rsrvAlram").css("display","none");		 
	}
	
	function fn_mon_q_popCnslt()
	{
		goMenuLink('Y', '-1_1_start_1_call_1_call_counsel_1_call_10100', '/call/consult/consult.jspx?cmd=consult_list&from=main');
	}
	


	function fn_cnslt_hst_save()
	{
		var var_mon_q = if_mon_q_getResult();
		var _url = '/call/cnslt_hist/action/cnslt_hist.jspx?cmd=createConsult';
		var http = jQuery.ajax( {
	   		url: _url,	   		
	   		type: "POST",
			data : jQuery('#form').serialize(true) + "&"+ var_mon_q,		
	   		async : false,	   		
	   		error 	: function(xhr)
	   		{
				alert(xhr.status);
			},
			success: function(xmlDoc)
			{				
				var code = jQuery(xmlDoc).find('code').text();
		        var msg = jQuery(xmlDoc).find('msg').text();
		        
		        if(code == '200')
		        {
		        	if_mon_q_getMonQCnt();
		        	//초기화해주고
		        } else
		        {
		        	msgStart(msg_com_code_010+"("+msg+")");
		        }
			}			
		});
	}
	
	
	function fn_mon_q_callReceive()
	{
		jQuery("#mon_q_task").val("INBOUND");
		jQuery("#rg_chnl_cd").val("CVCT");							//접수채널
		jQuery("#rcv_chnl_cd").val("ISMARCMC001");			//접수매채
		jQuery("#proc_chnl_cd").val("ISMAPCMC001");			//처리매체
		jQuery("#cnslt_reg_dt").val(fn_mon_q_getCurTime()); 	//접수시간			
		jQuery("#cntct_rslt_cd").val("ISMACTRT005");			//접촉결과코드
		jQuery("#io").val("I");
		if_cnslt_detail_inbound_set_rslt_cd();   						//접촉결과고정
	}


			
	/**
	* 호전환받았을때 설정해준다.
	*  상담번호를 받지만 첫번째상담원이 상담저장을 했을경우 한건이 쌓이고 호전환 시점에서 상담이 하나 더 생긴다.
	*  그러므로 그럴경우에는 나중에 생긴 건에 대해 삭제를하고 원래 상담번호를 가져와서 처리한다. 
	*/
	function fn_mon_q_callTransferReceive(cnslt_seq_no, call_id)
	{
        console.log("@@받는 상담번호="+cnslt_seq_no + "/"+call_id);

		var v_cnslt_seq_no = "";
		var v_cust_no = "";
		var _url = '/call/cnslt_hist/action/cnslt_hist.jspx?cmd=getTransferConsult';
		var http = jQuery.ajax( {
	   		url: _url,	   		
	   		type: "POST",
			data : "call_id="+call_id+"&cnslt_seq_no="+cnslt_seq_no,		 
	   		async : false,	   		
	   		error 	: function(xhr)
	   		{
				alert(xhr.status);
			},
			success: function(xmlDoc)
			{				
				var code = jQuery(xmlDoc).find('code').text();
		        var msg = jQuery(xmlDoc).find('msg').text();
		        try
		        {
		        	v_cnslt_seq_no = jQuery(xmlDoc).find('cnslt_seq_no').text();
		        	v_cust_no = jQuery(xmlDoc).find('cust_no').text();
		        }catch(e){console.log(e);}
		        
		        if(v_cnslt_seq_no == "")
		        {
		        	v_cnslt_seq_no = cnslt_seq_no;
		        	v_cust_no = "";
		        }
			}			
		});
		
		jQuery("#mon_q_task").val("INBOUND");
		jQuery("#rg_chnl_cd").val("CVCT");							//접수채널
		jQuery("#rcv_chnl_cd").val("ISMARCMC001");			//접수매채
		jQuery("#proc_chnl_cd").val("ISMAPCMC001");			//처리매체
		jQuery("#cnslt_reg_dt").val(fn_mon_q_getCurTime()); 	//접수시간			
		jQuery("#cntct_rslt_cd").val("ISMACTRT005");			//접촉결과코드
		jQuery("#io").val("I");
		
		if(v_cust_no != "") if_cust_getCustInfo("cnslt_seq_no", v_cnslt_seq_no);
		else
		{
			//console.log("fn_mon_q_callTransferReceive ===================>"+jQuery("#cid").val());

			var regExp_ctn = /^01([016789])([1-9]{1})([0-9]{2,3})([0-9]{4})$/;
			if(regExp_ctn.test(jQuery("#cid").val()))
			{
				jQuery("#mobile").val( jQuery("#cid").val());
				//jQuery("btnMobileCustModal").click();
			}
			else
			{
				jQuery("#tel").val( jQuery("#cid").val());
				//jQuery("btnTelCustModal").click();
			}
			if_cust_getCustInfo("tel_no", jQuery("#cid").val());
		}
		
		
		fn_mon_q_cnslt("CALL_TRANSFER", v_cnslt_seq_no, "0");
		
		//호전환을 두개이력을 쓸경우
		if(g_transfer_two_hist_yn == "Y")
		{
			jQuery("#cnslt_seq_no").val("");
			jQuery("#cnslt_reg_dt").val(fn_mon_q_getCurTime()); 	//접수시간						
		}
	}
		
		
	//호전환요청 처리 / cntct_btn_action / ISMACNST010
	//호상태조회에서 상담저장할 수 있는 부분 호출 
	//저장하고 나서 호상태조회의 호전환부분 호출 
	function aaa()
	{
		jQuery("#cntct_btn_action").val("ISMACNST010");
	}
	
	function bbb()
	{
		if_mon_q_setTransferd("01024135402","","","265");
	}
		
		
		
	/**
	 * 담당자변경을 승인또는 거절한다.
	 */
	function fn_mon_q_chrgChange(apprvYn, chnl_cd, seq_no, req_emp_no)
	{
		var _url = '/call/consult/action/consult_chnl_rsrv.jspx?cmd=confirmChrgChange';
		var http = jQuery.ajax({
			url: _url,
			type: "POST",
			data : "accpt_yn="+apprvYn+"&rcv_chnl_cd="+chnl_cd+"&rcv_data_no="+seq_no+"&tgt_emp_no="+req_emp_no,
			dataType : "json",
			async : false,
			error : function(xhr){
				alert(xhr.status);
			},
			success : function(json)
			{
				var code = json.result.code;
				var msg = json.result.msg;
				var item = json.result.data;
				if(code == '200')
				{						
					msgStart(msg_com_code_009);
					closeConfirm();						
					if_mon_q_getMonQCnt();
					
				} else
				{
					msgStart(msg_com_code_010+"("+msg+")", 'danger');
				}
			}
		});
	}
		
		
		
	/**
	 * 이첩접수 모니터링 목록 클릭시 처리이력 추가.
	 */
	function fn_mon_q_addTransferAcceptProc(p_cnsltSeqNo)
	{
		var isok = '';
		
		var _url = '/call/mon_q/action/mon_q.jspx?cmd=addTransferAcceptProc';
		var http = jQuery.ajax({
			url: _url,
			type: "POST",
			data : {
				cnslt_seq_no : p_cnsltSeqNo
			},
			dataType : "json",
			async : false,
			error : function(xhr){
				alert(xhr.status);
			},
			success : function(json)
			{
				var code = json.result.code;
				var msg = json.result.msg;
				var item = json.result.data;
				if(code == '200')
				{			
					if(item.hst_seq_no == ''){
						isok = 'N';
					}else{
						isok = 'Y';
					}
					
				} else
				{
					msgStart(msg_com_code_010+"("+msg+")", 'danger');
				}
			}
		});
		
		return isok;
	}
	
	/**
	 * 이관접수 모니터링 목록 클릭시 처리이력 추가.
	 */
	function fn_mon_q_addMoveAcceptProc(p_cnsltSeqNo, p_transNo)
	{
		var isok = '';
		
		var _url = '/call/mon_q/action/mon_q.jspx?cmd=addMoveAcceptProc';
		var http = jQuery.ajax({
			url: _url,
			type: "POST",
			data : {
				cnslt_seq_no : p_cnsltSeqNo,
				rcv_data_no : p_transNo
			},
			dataType : "json",
			async : false,
			error : function(xhr){
				alert(xhr.status);
			},
			success : function(json)
			{
				var code = json.result.code;
				var msg = json.result.msg;
				var item = json.result.data;
				if(code == '200')
				{			
					if(item.hst_seq_no == ''){
						isok = 'N';
					}else{
						isok = 'Y';
					}
					
				} else
				{
					msgStart(msg_com_code_010+"("+msg+")", 'danger');
				}
			}
		});
		
		return isok;
	}
	
	
	var favorites_info = new Array;
	
	//즐겨찾기를 가져온다
	function fn_getFavorite(){
		jQuery.ajax({
			url : '/mngr/action/favoritesMgr.jspx?cmd=getFavorites&rtnType=widget',				 
			async:false,
			type: 'POST',
			dataType: 'json',
			success : function(data){
				try
				{
					if(data.result.code == 200)
					{
						var jsonArr = data.result.data;
						var listHtml = [];
						var linkHtml = [];
						var linkcount = 0;
						var jsonCnt = jsonArr.favorites_list.length;
						favorites_info = new Array;
						
						if(jsonArr != null &&  jsonCnt > 0)
						{
							favorites_info = jsonArr.favorites_list; 
			
							//개인것 갯수
							var lk00_cnt = 0;
							
							for(i=0; i<jsonCnt; i++){
								
								if(favorites_info[i].targetgrp == "LK00" )  {
									listHtml.push('	<tr id="tr_' + i + '" onclick="rowClick('+ i +');" style="cursor:pointer;">'); //  onclick="fn_mon_q_clicked('+mon_q_item+','+json[i]+')"
									listHtml.push('	<td align="center">'+favorites_info[i].fav_nm+'</td>');
									listHtml.push('	</tr>');
									lk00_cnt++;
								}else{
									if(jQuery('#my_p_dept_no').val() == favorites_info[i].auth_dept_no || favorites_info[i].targetgrp == "LK99"){
										linkcount++;
										if(linkcount%2 == 1){
											linkHtml.push('<tr>');
										}
										
										linkHtml.push('	<td style="width:50%;" class="go_link_td">');
										if(favorites_info[i].fav_nm.length > 8){
											linkHtml.push('		<div onclick="rowClick('+ i +');" class="W100P" style="cursor:pointer; font-size:11px;height:25px;line-height:15px;padding-top:2px;padding-left:20px;text-align:left; ">');
											linkHtml.push('			<b>'+favorites_info[i].fav_nm.substring(0,12)+'</b> ');
											//linkHtml.push('			'+favorites_info[i].fav_nm.substring(0,favorites_info[i].fav_nm.length/2)+'<br> ');
											//linkHtml.push('			'+favorites_info[i].fav_nm.substring(favorites_info[i].fav_nm.length/2,favorites_info[i].fav_nm.length)+'</div> ');
										}else{
											linkHtml.push('		<div onclick="rowClick('+ i +');" class="W100P" style="cursor:pointer; font-size:11px;height:25px;line-height:20px;padding-left:20px;text-align:left; ">');
											linkHtml.push('			<b>'+favorites_info[i].fav_nm.substring(0,12)+'</b></div> ');
										}
										linkHtml.push('	</td>');
										
										if(linkcount/2 == 1){
											linkHtml.push('</tr>');
										}
										
									}
								}
								
							}
							
							if(lk00_cnt == 0)
							{
								listHtml.push('	<tr><td colspan="4" align="center">즐겨찾기를 추가해주세요</td></tr>');
							}
							
							console.log(">>>>>>>>>" + listHtml.length);
							
							if(listHtml.length > 0){
								jQuery('#fav_gridDiv').html(listHtml.join(''));
							}
							if(linkHtml.length > 0){
								jQuery('#link_gridDiv').html(linkHtml.join(''));
								$("#div_accordion_5").show();
							}
							
						}
						
					}
				} catch(e3)
				{
					console.log('fn_getFavorite===>'+e3);
				}
			}
		});
	}
	
	 var tempVal = 0;	// rowClick관련 임시 저장
	    
		function rowClick(i){
			var id = "tr_" + i;
			var tmpId = "tr_"+tempVal;
			
			var path;
			var shape;

			if(jQuery('#'+tmpId) != null){
				jQuery('#'+tmpId).addClass("tbl_m_grid_off");
				jQuery('#'+id).addClass("tbl_m_grid_on");
	        }else{
	        	jQuery('#'+id).addClass("tbl_m_grid_on");
	        } 
			 
			path = favorites_info[i].fav_url;
			
			if(path.indexOf("http://") < 0 && path.indexOf("https://")){
				path = "http://" + path;
	        }
			//shape = "width=" + favorites_info[i].width + ",height=" + favorites_info[i].height + ",";
	        shape += "top=" + favorites_info[i].top + ",left=" + favorites_info[i].left + ",";
	        shape += "channelmode=" + (favorites_info[i].channelmodefg == "" ? "0" : favorites_info[i].channelmodefg);
	        shape += ",directories=no";
	        shape += ",fullscreen=" + (favorites_info[i].fullscreenfg == "" ? "0" : favorites_info[i].fullscreenfg);
	        shape += ",location=" + (favorites_info[i].locationfg == "" ? "0" : favorites_info[i].locationfg);
	        shape += ",menubar=" + (favorites_info[i].menubarfg == "" ? "0" : favorites_info[i].menubarfg);
	        shape += ",resizable=" + (favorites_info[i].resizablefg == "" ? "0" : favorites_info[i].resizablefg);
	        shape += ",scrollbars=" + (favorites_info[i].scrollbarsfg == "" ? "0" : favorites_info[i].scrollbarsfg);
	        shape += ",status=" + (favorites_info[i].statusfg == "" ? "0" : favorites_info[i].statusfg);
	        shape += ",titlebar=" + (favorites_info[i].titlebarfg == "" ? "0" : favorites_info[i].titlebarfg);
	        shape += ",toolbar=" + (favorites_info[i].toolbarfg == "" ? "0" : favorites_info[i].toolbarfg);

			window.open(path, favorites_info[i].target, shape);
			
			tempVal = i;
		}


		// 상담유형 TOP 10
		function js_getMonqCnsltType(){
			
			if( $("#div_cnslt_type").html().trim() != "") return;
			
			jQuery.ajax({
				url : '/call/statistics/action/type_trend.jspx?cmd=getTotalDataMonQ',				 
				async:false,
				type: 'POST',
				dataType: 'json',
				success : function(data){
					js_rcvMonqCnsltType(data);
					js_rcvMonqKeyword(data);
				}
			});
		}		
		
		function js_rcvMonqCnsltType(data)
		{
			try
			{
                if(data.result.code == 200)
                {
                    var jsonArr = data.result.data;
                    var unit_list = jsonArr.unit_list;
                    var total_data = jsonArr.total_data;                        
                    var total_data_cnt = total_data.length;
                   	var html = [];
                	var cnslt_type_data_cnt = 0;                   	
                   	
                    	html.push('        <div class="widget-box" style="width:100%;">      ');
                    	html.push('             <div class="widget-body">      ');
                    	html.push('                 <div class="widget-main no-padding">      ');
                    	html.push('                     <table class="table table-striped table-bordered table-hover">      ');
                    	html.push('                         <tbody>      ');                                    
                    	

                    	for(var ydx=0; ydx<total_data_cnt; ydx++)  
                    	{
                    		if(total_data[ydx].cd != "keyword_monq_data")
                    		{
                    			var variation = total_data[ydx].variation;
                    			var type_data_cnt = total_data[ydx].cnt; 
                    			var icon = variation.indexOf("-") > -1 ? "icon-arrow-down red" : variation == "0" ? "-" : variation == "new" ? "blue" : variation == "" ? "" : "icon-arrow-up green";   
		                    	html.push('                             <tr>      ');
		                    	html.push('                                 <td  style="width: 30px; height: 25px; text-align: center; 	">'+total_data[ydx].rnum+'</td>      ');
		                    	html.push('                                 <td title="접수:'+type_data_cnt+'건">      ');
		                    	html.push('                                     '+total_data[ydx].cd_nm+'     ');
		                    	html.push('                                 </td>      ');
		                    	html.push('                                 <td style="text-align:center;width:50px">      ');
		                    	html.push('                                       <i class="'+icon+'" >'+ (variation == "0"?"-":variation) +'</i>      ');
		                    	html.push('                                 </td>      ');
		                    	html.push('                             </tr>      ');                                     
		                    	cnslt_type_data_cnt++;
                    		}
                    	}
                    	
                    	if(cnslt_type_data_cnt == 0)
                    	{
	                    	html.push('                             <tr>      ');                    		
	                    	html.push('                                 <td  style="width: 30px; height: 25px; text-align: center; 	">수집된 데이터가 없습니다.<br>데이터는 일정간격으로 수집됩니다.<br>(5분이내)</td>      ');	                    	
	                    	html.push('                     		        </td>  ');	                    	
	                    	html.push('                             </tr>      ');	                    	
                    	}
                    	
                    	html.push('                         </tbody>      ');
                    	html.push('                     </table>      ');
                    	html.push('                 </div>      ');
                    	html.push('             </div>      ');
                    	html.push('         </div>     ');
                    	jQuery('#div_cnslt_type').html(html.join(""));
                    	
                    	$("#accordion_6 i").css("font-family","FontAwesome")
                    	$("#accordion_6 i").css("border","solid 0px #FFF")
                    	$("#accordion_6 i").css("background-color","rgba(255,255,255,0.15)")
                }
			} catch(e3)
			{
				console.log('js_rcvMonqCnsltType===>'+e3);
			}			
		}
		
		function js_rcvMonqKeyword(data)
		{
			try
			{
				if(data.result.code == 200)
				{
					var jsonArr = data.result.data;
					var unit_list = jsonArr.unit_list;
					var total_data = jsonArr.total_data;                        
					var total_data_cnt = total_data.length;
                	var keyword_data_cnt = 0;          					
					var html = [];
					
					html.push('        <div class="widget-box" style="width:100%;">      ');
					html.push('             <div class="widget-body">      ');
					html.push('                 <div class="widget-main no-padding">      ');
					html.push('                     <table class="table table-striped table-bordered table-hover">      ');
					html.push('                         <tbody>      ');                                    
					for(var ydx=0; ydx<total_data_cnt; ydx++)  
					{
						if(total_data[ydx].cd == "keyword_monq_data")
						{
							var variation = total_data[ydx].variation;
							var type_data_cnt = total_data[ydx].cnt; 
							var icon = variation.indexOf("-") > -1 ? "icon-arrow-down red" : variation == "0" ? "-" : variation == "new" ? "blue" : variation == "" ? "" : "icon-arrow-up green";   
							html.push('                             <tr>      ');
							html.push('                                 <td  style="width: 30px; height: 25px; text-align: center; 	">'+total_data[ydx].rnum+'</td>      ');
							html.push('                                 <td title="접수:'+type_data_cnt+'건">      ');
							html.push('                                     '+total_data[ydx].cd_nm+'     ');
							html.push('                                 </td>      ');
							html.push('                                 <td style="text-align:center;width:50px">      ');
							html.push('                                       <i class="'+icon+'" >'+ (variation == "0"?"-":variation) +'</i>      ');
							html.push('                                 </td>      ');
							html.push('                             </tr>      ');                                     
							keyword_data_cnt++;
						}
					}
					
                	if(keyword_data_cnt == 0)
                	{
                    	html.push('                             <tr>      ');                    		
                    	html.push('                                 <td  style="width: 30px; height: 25px; text-align: center; 	">수집된 데이터가 없습니다.<br>데이터는 일정간격으로 수집됩니다.<br>(5분이내)</td>      ');	                    	
                    	html.push('                     		        </td>  ');	                    	
                    	html.push('                             </tr>      ');	                    	
                	}					
					
					html.push('                         </tbody>      ');
					html.push('                     </table>      ');
					html.push('                 </div>      ');
					html.push('             </div>      ');
					html.push('         </div>     ');
					jQuery('#div_keyword').html(html.join(""));
					
					$("#accordion_7 i").css("font-family","FontAwesome")
					$("#accordion_7 i").css("border","solid 0px #FFF")
					$("#accordion_7 i").css("background-color","rgba(255,255,255,0.15)")
				}
			} catch(e3)
			{
				console.log('js_rcvMonqCnsltType===>'+e3);
			}			
		}
		
		function js_rcvMonqCenterInfo(data)
		{

			var jsonArr = data.result.data;
			var a = jsonArr.center_agent_status_info[0];
			//var chartHeaderJson =  { "label" : "대기", "value" : a.ready_cnt },
			var chartDataJson = [ 
			                  { "label" : "대기", "value" : a.ready_cnt },
			                  { "label" : "이석", "value" : a.not_ready_cnt },
			                  { "label" : "후처리", "value" : a.acw_cnt },
			                  { "label" : "통화중", "value" : a.on_phone_cnt },
			                  { "label" : "로그인", "value" : a.login_cnt }
			                  ];
			                  
            FusionCharts.ready(function () {
                var chartObj = new FusionCharts({
                  "type": "column2d",
                  "renderAt": "div_center_agent_status",
                  "width": "100%",
                  "height": "150",
                  "dataFormat": "json",
                  "dataSource":   { 
                	  "chart" : {
                	        "bgcolor": "FFFFFF",
                	        "useroundedges": "1",
                	        "showborder": "0"                		  
                	  },
                	  "data" : 
                		  chartDataJson
                	 
                  }
                	                  		  
                });
                chartObj.render();
              });
            
		}
		
		function js_rcvMonqAgentInfo(data)
		{
			var jsonArr = data.result.data;
			var a = jsonArr.agent_status_info[0];
			$("#inbound_cnt").html( a.inbound_cnt );
			$("#outbound_cnt").html( a.outbound_cnt );
			$("#busy_sec").html( a.busy_sec );
			$("#avg_call_time").html( a.avg_call_time );
			$("#avg_ready_time").html( a.avg_ready_time );
			$("#avg_acw_time").html( a.avg_acw_time );
		}
