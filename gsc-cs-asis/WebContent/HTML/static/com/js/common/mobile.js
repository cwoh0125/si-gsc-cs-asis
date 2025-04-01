
	function makeTable(xmlDoc, div_id, table_id, curPage)
	{
		//String str = "<rows><page>1</page><total>1</total><records>4</records><row id='283'><cell>283</cell><cell>�Ϲݰ���</cell><cell>�׽�Ʈ3</cell><cell>���ö</cell><cell>2013.11.11 11:52:05</cell></row><row id='282'><cell>282</cell><cell>�Ϲݰ���</cell><cell>�������� �׽�Ʈ</cell><cell>�ڼ���</cell><cell>2013.11.11 11:15:05</cell></row><row id='281'><cell>281</cell><cell>�Ϲݰ���</cell><cell>�׽�Ʈ2</cell><cell>���ö</cell><cell>2013.11.11 11:13:04</cell></row><row id='261'><cell>261</cell><cell>�Ϲݰ���</cell><cell>�׽�Ʈ ��������</cell><cell>�ѾƸ�</cell><cell>2013.09.11 11:58:51</cell></row></rows>"
		var header = getTableModel(table_id);

		var _table = [];
		
		if(curPage == 1)
		{
			_table.push('<table data-role="table" id="'+table_id+'" data-mode="columntoggle" class="ui-responsive table-stroke">');
			_table.push('<thead>');
			_table.push('	   <tr class="ui_mobile_head">');
			for(var idx in header)
			{
				if(header[idx].hidden != "true" && header[idx].hidden != true)
				_table.push('	   <th style="text-align:center" data-priority="'+header[idx].priority+'">'+  header[idx].title + '</th>'  );
			}
			_table.push('      </tr>');
			_table.push('</thead>');
			

			_table.push('<tbody id="tbody'+table_id+'">');

			makeTableBody(xmlDoc, table_id, _table, header, curPage);
			
			_table.push('</tbody>');
			_table.push('</table>');
			
			_table = _table.join("");
		      //console.log("@@@@@@@@@ _table : "+_table);
			 jQuery("#"+div_id).html(_table);     
			 jQuery("#"+div_id).trigger( "create" );
		}
		else
		{
			makeTableBody(xmlDoc, table_id, _table, header, curPage);			
			_table = _table.join("");
			jQuery('#tbody'+table_id+" tr:last-child").after(_table);
			console.log(_table);
			//jQuery("#"+div_id).trigger( "refresh" );
			var html = jQuery("#"+div_id).html();
//			console.log(html);
//			 jQuery("#"+div_id).html("");
//			 jQuery("#"+div_id).trigger( "refresh" );
//			 jQuery("#"+div_id).html(html);
			jQuery("#"+table_id).table().table( "refresh" );
		}

		
			var tmpDataSet = getDataSet(xmlDoc, header);	
			xmlDoc = null;
			
			return tmpDataSet;
	}


	/**
	* dataset형태로 가져온다.
	*  dataset명[row_idx].컬럼명
	*/
	function getDataSet(xmlDoc, header)
	{
		
		var tmpDataSet = [];
		var row = jQuery(xmlDoc).find('row').text();
		jQuery(xmlDoc).find('row').each(function(i){
			
			var id = jQuery(this).attr("id");	
			
			tmpDataSet[i] = {};
			tmpDataSet[i].row_id = id;
			
			      jQuery(this).children("cell").each(function(idx){
			    	  tmpDataSet[i][header[idx].name] = jQuery(this).text();
			});		
		});
		return tmpDataSet;			
	}
	

	function makeTableBody(xmlDoc, table_id, _table, header, page)
	{
		//console.log("@@@@@@@@@@@@@"+xmlDoc + " /  "+ table_id+" / "+_table + " / " + header + " / " + page);
		var row = jQuery(xmlDoc).find('row').text();
		var aherf_start = "";	    			
		var aherf_end = "";	    			
		var style = "";
		
		if(jQuery(xmlDoc).find('row').length == 0 && page != 1)
		{
			toast("목록의 마지막 입니다.");
		}
		
		
		if( page==1 && jQuery(xmlDoc).find('total').text() == 0)
		{
			
		  	  _table.push('<tr><td style= "text-align:center" colspan="'+header.length+'"  >' +"데이터가 없습니다."+ '</td></tr>') ;
		}
		
		
		jQuery(xmlDoc).find('row').each(function(i){
			
			var id = jQuery(this).attr("id");	
			_table.push('<tr id='+id+'>');
		
			if( i==0 && header.length != jQuery(this).children("cell").length)
			{
				alert("xml 과 header 컬럼길이가 맞지 않습니다.");
			}
		      jQuery(this).children("cell").each(function(idx){
		    	  	//console.log("@@@@@@@@@@@@@idx : "+idx+" / header[idx] : "+header[idx]);
					if( header[idx].linkFunc != null) 
					{
						aherf_start = "<a href='javascript:"+header[idx].linkFunc+"(\""+table_id+"\","+id+","+i+")'  data-transition='fade'>";
						//console.log("@@"+aherf_start);
				    	aherf_end = "</a>";						
					}
					else
					{
						aherf_start = "";
				    	aherf_end = "";						
					}
					
					if(header[idx].style != null) 
					{
						style = ";"+header[idx].style;
					}
					else
					{
						style = "";
					}
					
		    	    
		    	  	if(page == 1)
		    	  	{
//		    	  	  _table.push('<td style= "text-align:'+header[idx].align+'"  '+style+'>' +aherf_start+  jQuery(this).text() +aherf_end+ '</td>') ;
						if(header[idx].hidden != "true" && header[idx].hidden != true)
		    	  	  _table.push('<td style= "text-align:'+header[idx].align + style+'"  >' +aherf_start+  jQuery(this).text() +aherf_end+ '</td>') ;
		    	  	}
		    	  	else
		    	  	{
		    	  		var tmp = "";
		    	  		if(header[idx].hidden != "true" && header[idx].hidden != true)
		    	  		{
	    					tmp = header[idx].priority > 0 ? "ui-table-priority-"+header[idx].priority : "";
			    	  		_table.push('<td style= "text-align:'+header[idx].align + style+'" class="'+tmp+'" '+style+'>' +aherf_start+  jQuery(this).text() +aherf_end+ '</td>') ;
			    	  		//console.log("_table. : "+_table);
		    	  		}
		    	  	}
		      });
		_table.push('</tr>');
		});		
		
	}
	
	
	function goPage(url)
	{
		location.href = url;
	}
	
	
	var toast=function(msg){
		jQuery("<div class='ui-loader ui-overlay-shadow ui-body-e ui-corner-all'><h3>"+msg+"</h3></div>")
		.css({ display: "block", 
			opacity: 0.90, 
			position: "fixed",
			padding: "7px",
			"text-align": "center",
			width: "270px",
			left: (jQuery(window).width() - 284)/2,
			top: jQuery(window).height()/2 })
		.appendTo( jQuery("body") ).delay( 1500 )
		.fadeOut( 400, function(){
			jQuery(this).remove();
		});
	}
	
	
	function msgStart(text, interval)
	{
		toast(text);
	}
	
	
	function jqm_confirm(msg, func)
	{ 
		jQuery('<div>').simpledialog2({
		    mode: 'button',
		    headerText: 'i-Smart Work',
		    headerClose: true,
		    buttonPrompt: msg,
		    buttons : {
		      '확인': {
		        click: function () {
		        	eval(func);
		        }
		      },
		      '취소': {
		        click: function () { 
		        	jQuery(document).trigger('simpledialog', {'method':'close'});
		        },
		        icon: "delete",
		        theme: "c"
		      }
		    }
		  });
	}
	

	