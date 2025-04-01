<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR" />
<title>스마트에디터 데모 페이지</title>
<link href="css/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/HuskyEZCreator.js" charset="EUC-KR"></script>
</head>
<body>
	<textarea name="ir1" id="ir1" style="width:100%; height:100%"></textarea>
    <input type="hidden" id=pasteBtn name=pasteBtn onclick="pasteHTML()"> </input>    
    <input type="hidden" id=returnHTML name=returnHTML onclick="showHTML()"> </input>	
<script>
var oEditors = [];
// 마지막 옵션은 체감 속도 증진을 위해서 페이지 로딩 완료시 까지 화면 표시를 하지 않는 옵션 입니다. 
// 개발 작업시에는 이 값을 false로 설정 하세요.
nhn.husky.EZCreator.createInIFrame(oEditors, "ir1", "SEditorSkin.html", "createSEditorInIFrame", null, true);

function pasteHTML(){
	//sHTML = "<span style='color:#FF0000'>이미지 등도 이렇게 삽입하면 됩니다.</span>";	
	oEditors.getById["ir1"].setIR("");
	sHTML = document.all.pasteBtn.value;	
	oEditors.getById["ir1"].exec("PASTE_HTML", [sHTML]);		
}

function showHTML(){
	document.all.returnHTML.value = oEditors.getById["ir1"].getIR();	
}
</script>
</body>
</html>
