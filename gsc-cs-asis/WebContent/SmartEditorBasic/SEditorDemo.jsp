<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR" />
<title>����Ʈ������ ���� ������</title>
<link href="css/default.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/HuskyEZCreator.js" charset="EUC-KR"></script>
</head>
<body>
	<textarea name="ir1" id="ir1" style="width:100%; height:100%"></textarea>
    <input type="hidden" id=pasteBtn name=pasteBtn onclick="pasteHTML()"> </input>    
    <input type="hidden" id=returnHTML name=returnHTML onclick="showHTML()"> </input>	
<script>
var oEditors = [];
// ������ �ɼ��� ü�� �ӵ� ������ ���ؼ� ������ �ε� �Ϸ�� ���� ȭ�� ǥ�ø� ���� �ʴ� �ɼ� �Դϴ�. 
// ���� �۾��ÿ��� �� ���� false�� ���� �ϼ���.
nhn.husky.EZCreator.createInIFrame(oEditors, "ir1", "SEditorSkin.html", "createSEditorInIFrame", null, true);

function pasteHTML(){
	//sHTML = "<span style='color:#FF0000'>�̹��� � �̷��� �����ϸ� �˴ϴ�.</span>";	
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
