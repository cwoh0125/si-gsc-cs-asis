/***************************/
//Author: Adrian "yEnS" Mato Gondelle
//website: www.yensdesign.com
//email: yensamg@gmail.com
//license: Feel free to use it, but keep this credits please!					
/***************************/

//SETTING UP OUR POPUP
//0 means disabled; 1 means enabled;
var popupStatus = 0;

//loading popup with jQuery magic!
function loadPopup(){
	//loads popup only if it is disabled
	if(popupStatus==0){
		jQuery("#backgroundPopup").css({
			"opacity": "0.7"
		});
		jQuery("#backgroundPopup").fadeIn("slow");
		jQuery("#popupContact").fadeIn("slow");
		popupStatus = 1;
	}
}

//disabling popup with jQuery magic!
function disablePopup(){
	//disables popup only if it is enabled
	if(popupStatus==1){
		jQuery("#backgroundPopup").fadeOut("slow");
		jQuery("#popupContact").fadeOut("slow");
		popupStatus = 0;
	}
}
function positionPopup(obj)
{
	var ie = document.all;
	var leftpos = 0;
	var toppos = 0;
	var targetObj = obj;
	var aTag = obj;
	
	do {
	aTag = aTag.offsetParent;
	leftpos += aTag.offsetLeft;
	toppos += aTag.offsetTop;

	} while(aTag.tagName !="BODY" && aTag.tagName !="HTML" );

	var topPosition = 0;
	var leftPosition = 0;

	if(ie)
	{
	leftPosition = targetObj.offsetLeft + leftpos ;
	topPosition = targetObj.offsetTop + toppos + targetObj.offsetHeight + 2;
	}else{
	leftPosition = targetObj.offsetLeft + leftpos + 'px';
	topPosition = targetObj.offsetTop + toppos + targetObj.offsetHeight + 2 + 'px';
	}

	//centering
	jQuery("#popupContact").css({
	"position": "absolute",
	"top": topPosition,
	"left": leftPosition
	});

}
//centering popup
function centerPopup(formObj){
	//request data for centering
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = jQuery("#popupContact").css('height');
	var popupWidth = jQuery("#popupContact").css('width');
	popupWidth = popupWidth.substring(0, popupWidth.indexOf('px'));
	popupHeight = popupHeight.substring(0, popupHeight.indexOf('px'));
	var scrollTop = jQuery(window).scrollTop();
	
	//var popupHeight = jQuery("#popupContact").height();
	//var popupWidth = jQuery("#popupContact").width();	

	//centering
	jQuery("#popupContact").css({
		"position": "absolute",
		"top": parseInt(scrollTop)+50,
		//"top": windowHeight/2-popupHeight/2,
		"left": windowWidth/2-200
	});
	//only need force for IE6
	
	jQuery("#backgroundPopup").css({
		"height": windowHeight
	});
	
}
function popupWidth(width)
{
	jQuery("#popContainer").css({
		"width": width
	});
}
function popupInit()
{
	jQuery("#popupContactClose").click(function(){
		disablePopup();
	});
	//Click out event!
	jQuery("#backgroundPopup").click(function(){
		disablePopup();
	});
	//Press Escape event!
	jQuery(document).keypress(function(e){
		if(e.keyCode==27 && popupStatus==1){
			disablePopup();
		}
	});
}
