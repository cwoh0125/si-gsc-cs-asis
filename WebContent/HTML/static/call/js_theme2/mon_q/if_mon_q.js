	/*
	*처리후에 모니터링 카운트를 조정한다.
	*/
	function if_mon_q_getMonQCnt()
	{
		try
		{
			fn_mon_q_getMonQCnt();
			fn_mon_q_detail(jQuery("#mon_q_task").val());
		}catch(e) 
		{
			console.log('fn_mon_q_getMonQCnt===>'+e);
		}
	}
	
	/**
	 * 상담현황모니터링 건수만 갱신한다.
	 */
	function if_mon_q_getMonQCntOnly()
	{
		try
		{
			fn_mon_q_getMonQCnt();
		}catch(e) 
		{
			console.log('fn_mon_q_getMonQCnt===>'+e);
		}
	}
	
	
	/*
	*고객판넬에서 받아서 상담이력판넬로 전달해준다.
	*/
	function if_mon_q_getCnsltHst(cust_no)
	{
		try
		{
			if_cnslt_hist_getCnsltHst(cust_no);
			fn_happy_hist_getHappyHst(cust_no);
		}catch(e) 
		{
			console.log('if_mon_q_getCnsltHst===>'+e);
		}
	}
	

	/*
	* 전화걸기할때 모니터링항목이 선택된 상태가 아니라면 별도의상담으로 넣는다.
	*/
	function if_mon_q_setMakeCallStatus(cid)
	{
		jQuery("#cnslt_reg_dt").val(fn_mon_q_getCurTime()); 	//등록시간
		
		if(jQuery("#mon_q_task").val() == "")
		{
			jQuery("#mon_q_task").val("ETC");
			jQuery("#rg_chnl_cd").val("CVCZ");							//접수채널
			jQuery("#rcv_chnl_cd").val("ISMARCMC999");			//접수매채
			jQuery("#rcv_data_no").val(""); 
			jQuery("#io").val("O"); 
			jQuery("#cid").val(cid);			
			jQuery("#call_id").val(""); 
			jQuery("#proc_chnl_cd").val("ISMAPCMC001");
			jQuery("#cntct_rslt_cd").val("ISMACTRT005");
		}
		else( jQuery("#mon_q_task").val() == "HAPPY" || jQuery("#mon_q_task").val() == "RSCH")
		{
			jQuery("#io").val("O"); 
			jQuery("#cid").val(cid);
			if_survey_detail_setCid(cid);
		}
	}
	
	/*
	* 모니터링 변수 및 콜변수를 초기화한다.
	*/
	function if_mon_q_init()
	{
		fn_mon_q_init();
	}
	
	

	/**
	* 전체를 초기화한다.
	*/
	function if_mon_q_init_all()
	{
		fn_mon_q_init_all();
	}



		/**
		 * 현재사용안함.
		 *  if_mon_q_setApBeforeConnect 통해서 fn_mon_q_callReceive을 호출함. 
		 */
		function if_mon_q_callReceive()
		{
			fn_mon_q_callReceive();
		}
		
		/**
		*  메인화면에 있는 상담요소 값을 가져온다.
		*/
		function if_mon_q_getResult()
		{
			return jQuery('#formMonq').serialize(true)+"&mon_q_task="+jQuery("#mon_q_task").val(); 
		}
	
		
		/**
		* 인바운드시 아웃바운시 전화연결후에 콜아이디를 세팅한다.
		*/
		function if_mon_q_apSetAfterConnect(call_id)
		{
			jQuery("#call_id").val(call_id);
			
			if( jQuery("#mon_q_task").val() == "INBOUND" )
			{
				var cid = jQuery("#inbound_cid").val();
				jQuery("#cid").val(cid );
				jQuery("#last_cntct").val( cid );
				jQuery("#phone_number").val(  cid  );
				if_cust_getCustInfo("tel_no", cid);
				fn_mon_q_callReceive();
				
				var _url = '/call/cnslt_hist/action/cnslt_hist.jspx?cmd=connectedInbound';
				var http = jQuery.ajax( {
			   		url: _url,	   		
			   		type: "POST",
					data : jQuery('#formMonq').serialize(true),		
			   		async : false,	   		
			   		error 	: function(xhr)
			   		{
						alert(xhr.status);
					},
					success: function(xmlDoc)
					{				
						var code = jQuery(xmlDoc).find('code').text();
				        var msg = jQuery(xmlDoc).find('msg').text();
					}			
				});
			}
		}
		
		
		/**
		* 인바운드시  전화연결전에 발신자번호와 IVR값을 세팅한다.
		*/	
		function if_mon_q_setApBeforeConnect(cid, ivr)
		{
			fn_mon_q_init_all();
			jQuery("#mon_q_task").val("INBOUND");
			jQuery("#inbound_cid").val(getNumOnly(cid));			
		}

		
		/**
		* 호전환받은 상담원이 전환완료시점에 호출된다.
		*/
		function if_mon_q_setTransferd(cid, ivr, call_id, cnslt_seq_no)
		{
			fn_mon_q_init_all();
			
			if(cid.substring(0,1) == "9")
			{
				cid = cid.substring(1);
			}
			
			jQuery("#call_id").val(call_id);
			jQuery("#cid").val(getNumOnly(cid));
			jQuery("#last_cntct").val(getTelnoHipen(cid));
			jQuery("#phone_number").val(getTelnoHipen(cid));
			fn_mon_q_callTransferReceive(cnslt_seq_no, call_id);
		}
	
		
		/**
		 * 함께 처리할 seq_no를 지정한다.
		 * @param rcv_chnl_cd
		 * @param seq_no
		 */
		function if_mon_q_together_proc(rcv_chnl_cd, seq_no)
		{
			//홈페이지 
			if(rcv_chnl_cd == "ISMARCMC005")
			{
				jQuery("#together_mon_q_task").val("HOMEPAGE");
			}	
			
			jQuery("#together_mon_q_seq_no").val(seq_no);
		}
		
		function if_mon_q_getDualTask()
		{
			return jQuery("#together_mon_q_task").val();	
		}
		
		
		function if_mon_q_getDaulTaskName()
		{
			var kor_nm = "";
			if( jQuery("#together_mon_q_task").val() == "HOMEPAGE") kor_nm  == "홈페이지";
			return kor_nm;
		}
		
		
		function if_mon_q_dual_task_start(org_cnslt_seq_no, org_cust_no)
		{
			
			var v_seq_no =  jQuery("#together_mon_q_seq_no").val();
			jQuery("#mon_q_task").val( jQuery("#together_mon_q_task").val()  ) 
			fn_mon_q_detail(jQuery("#mon_q_task").val());
			
			//홈페이지건에대한 고객번호을 등록한다.
			if(org_cust_no != "")
			{
				var _url = '/call/cnslt_hist/action/cnslt_hist.jspx?cmd=rcvChnlInsertCust';
				var http = jQuery.ajax( {
			   		url: _url,	   		
			   		type: "POST",
					data : "seq_no="+v_seq_no+"&mon_q_task="+jQuery("#mon_q_task").val()+"&cust_no="+org_cust_no,		
			   		async : false,	   		
			   		error 	: function(xhr)
			   		{
						alert(xhr.status);
					},
					success: function(xmlDoc)
					{				
						var code = jQuery(xmlDoc).find('code').text();
				        var msg = jQuery(xmlDoc).find('msg').text();
					}			
				});
			}
			
			if(jQuery("#mon_q_task").val() == "HOMEPAGE")
			{
				jQuery("#tr_mon_q3_"+v_seq_no).click();
			}
			
			jQuery("#together_mon_q_task").val(""); 
			jQuery("#together_mon_q_seq_no").val(""); 
			
			
		}
		
		
		