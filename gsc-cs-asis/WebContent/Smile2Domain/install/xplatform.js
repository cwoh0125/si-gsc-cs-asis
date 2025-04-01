var strBaseURL = "https://csc.gsmbiz.com:4431/Smile2Domain";
var strKey = "GSIB";

var request = new Request(
    (
        function() {
            var parameters = unescape(location.search).substring(1).split(/\&|\=/g);
            var obj = {}, i;
            for(i in parameters) obj[parameters[i++]]=parameters[i++];
            return obj;
        }
    )()
);

function Request(properties) {
    for(var i in properties) {
        this["get"+String(i.match(/^[a-z]/)).toUpperCase()+i.substring(1)] = (
            function (_i){
                return function() {
                    return properties[_i];
                }
            }
        )(i);
    } //FOR END
}

function checkOS() {

	var strOS = "XP";

	if( navigator.appVersion.indexOf("Windows NT 6.0") != -1) {
		strOS = "VISTA";
	} else if( navigator.appVersion.indexOf("Windows NT 6.1") != -1) {
		strOS = "Windows7";
	} else if( navigator.appVersion.indexOf("Windows 98") != -1) {
		strOS = "98";
	} else if( navigator.appVersion.indexOf("Windows NT 5.0") != -1) {
		strOS = "2000";
  	} else if( navigator.appVersion.indexOf("Windows NT 5.1") != -1) {
  		strOS = "XP";
	} else if( navigator.appVersion.indexOf("Windows NT 5.2") != -1) {
		strOS = "2003";
    }

  	return strOS;
}

var bOnError = false;

function fn_objectOnError() {
    bOnError = true;
}

function isError() {
    return bOnError;
}

// Engine
function createEngine(strID,strWidth,strHeight) {

    // XPLATFORM Engine

    document.write('<OBJECT ID="'+strID+'" CLASSID="CLSID:FCB889AC-D683-47a0-B04C-8FC42E257E5B" width="'+strWidth+'" height="'+strHeight+'" '
            + 'CodeBase="./download/XPLATFORM9.1_SetupEngine.cab#VERSION=2012,5,22,1" onError="fn_objectOnError()">'
            + '</OBJECT>');
}

// Launcher
function createLauncher(strID,strKey) {

    // XPLATFORM Launcher
    document.write('<OBJECT ID="'+strID+'" CLASSID="CLSID:3371DC5B-9C2F-4d01-AA1D-E7D62B217697" width="0" height="0" '
            + 'codebase="./download/XPLATFORM9.1_XPLauncher.cab#VERSION=2012,5,22,1" onError="fn_objectOnError()">'
            + '<PARAM NAME="key" VALUE="'+strKey+'">'
            + '</OBJECT>');
}

// Engine Plugin
function createEnginePlugin(strID,ErrFunc,SetFunc,NotifyFunc,W,H)
{
  document.write("<embed id='" + strID + "' type='Application/XPlatform9.1-PlugIn' ");
  document.write(" width='"+W+"' height='"+H+"' ");
  document.write(" error='"+ErrFunc+"' ");
  document.write(" loadingglobalvariables='"+SetFunc+"' ");
  document.write(" usernotify='"+NotifyFunc+"' ");
  document.write("</embed>");
}

// Launcher Plugin
function createLauncherPlugin(strID,ErrFunc)
{
  document.write("<embed id='" + strID + "' type='application/XLauncher9.1-PlugIn' ");
  document.write(" WIDTH='0' HEIGHT='0' error='" + ErrFunc + "'> ");
  document.write("</embed>");
}

//??? object ?? ???...
function createEtcObject() {
    
}



