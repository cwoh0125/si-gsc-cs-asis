<%@ page contentType="text/html; charset=euc-kr" %>
<%@ page language="java"%>
<html>

<script language="javascript">

    function aa() {
    	  document.title = "666";
       alert('txt_name::+'+document.webform.txt_name.value);
       
    }
    function aa_hidden() {
    	  document.title = "666";
        alert('aa_hidden');
     }
    </script>
<body>test
<form name='webform'>
<input type="button" id="btnok" value="aaÇÔ¼ö" onClick="javascript:aa()">
<input type="hidden" id="btnok_hidden" value="submit" onClick="javascript:aa_hidden()">
<input type="hidden" name="txt_name" value=""/>
</form>
</body>
</html>