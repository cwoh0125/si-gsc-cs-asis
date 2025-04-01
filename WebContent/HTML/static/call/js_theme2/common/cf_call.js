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