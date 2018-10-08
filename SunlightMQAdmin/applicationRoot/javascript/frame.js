
function postModuleAndAction(moduleName, actionName) {
	try {
	    document.getElementById("module").value = moduleName;
	    document.getElementById("action").value = actionName;
	    document.getElementById("mainForm").submit();
    }
    catch (e) {
    	alert("提交数据出错:" + e);
    }
}

function postModuleAndActionToTarget(moduleName, actionName, target) {
    var tmpModule = document.getElementById("module").value;
    var tmpAction = document.getElementById("action").value;
    var tmpTarget = document.getElementById("mainForm").target;
    document.getElementById("module").value = moduleName;
    document.getElementById("action").value = actionName;
    document.getElementById("mainForm").target = target;
    document.getElementById("mainForm").submit();
    document.getElementById("module").value = tmpModule;
    document.getElementById("action").value = tmpAction;
    document.getElementById("mainForm").target = tmpTarget;
}

// check if there is a element in document
function isObjectExist(name) {
    if (eval(document.getElementById(name))) {
        return true;
    }
    else {
        return false;
    }
}

// get the url parameter
function getUrlParameter(key) {
    var paras = window.location.search;
    var index = paras.indexOf(key + "=");
    if (index == -1) {
        return "";
    }
    
    var next = paras.indexOf("&", index);
    var value = paras.substring(index + key.length + 1, (next == -1) ? paras.length : next );
    return value;
}

// set innerHtml of element
function setInnerHTML(element, html) {
    var next = element.nextSibling;
    var parent = element.parentNode;
    parent.removeChild(element);
    element.innerHTML = html;
    if (next) {
        parent.insertBefore(element, next)
    } else {
        parent.appendChild(element);
    }
}

function getRadioValue(radioName) {
    var radio = document.getElementsByName(radioName);
    for (var i = 0; i < radio.length; i++) {
        if (radio[i].checked) {
            return radio[i].value;
        }
    }
    return "";
}

function getCurrentDate(){
    var today=new Date();
    date=today.getDate();
    if(date<=9){
        date="0"+date;
    }
    month=today.getMonth();
    month=month+1;
    if(month<=9){
        month="0"+month;
    }
    year=today.getYear();
    return nowDate=year+'-'+month+'-'+date;
}

function setSelectedValues(selectChoiceName, saveValueElementID) {
    var valueString = "";
    var choices = document.getElementsByName(selectChoiceName);
    for (var i = 0; i < choices.length; i++) {
        if (choices[i].checked) {
            valueString += choices[i].value + ",";
        }
    }
    valueString = valueString.substring(0, valueString.length - 1);
    document.getElementById(saveValueElementID).value = valueString;
}


function initSelectedValues() {
    var valueString = ", " + document.getElementById("selectedValues").value + ", ";
    var choices = document.getElementsByName("selectChoice");
    for (var i = 0; i < choices.length; i++) {
        if (valueString.search(", " + choices[i].value + ", ") >= 0) {
            choices[i].checked = true;
        }
        else {
            choices[i].checked = false;
        }
    }
}


function changeRandomImage() {
    document.getElementById('randomNumberImage').src='/randomNumberImage?temp='+(new Date().getTime().toString(36));
}

function uploadFile() {
    if (checkImage(document.getElementById("file"), "")) {
        doSubmit("uploadFile");
    }
}

function uploadCaiXin() {
    if (checkCaiXinFile(document.getElementById("caiXinFile"))) {
        doSubmit("uploadCaiXin");
    }
}

function checkCaiXinFile(pathField) {
	var imgRe = /^.+\.(jpg|jpeg|gif|png|txt|smil)$/i;
    var path = pathField.value;
    
    if (path.search(imgRe) == -1) {
        alert("请上传格式为jpg、png、gif、txt、smil的文件!");
        return false;
    }
    
    return true;
}

function checkImage(pathField) {
    var imgRe = /^.+\.(jpg|jpeg|gif|png|zip|rar)$/i;
    var path = pathField.value;
    
    if (path.search(imgRe) == -1) {
        alert("请上传格式为JPG、PNG,GIF,ZIP,RAR的文件!");
        return;
    }
    
    return true;
}

function doSubmit(action) {
	if (document.getElementById('submitActionFlag')) {
		if (document.getElementById('submitActionFlag').value == "1") {
	        alert("数据提交中,请耐心等待");
	        return;
	    }
	}
    try {
        var tmpAction = document.getElementById("mainForm").attributes["action"].value;
        var tmpTarget = document.getElementById("mainForm").attributes["target"].value;
        document.getElementById("mainForm").attributes["action"].value = "/ajax";
        document.getElementById("mainForm").attributes["target"].value = "hiddenIframe";
        document.getElementById("module").value="ajax";
        document.getElementById("action").value=action;
        document.getElementById("mainForm").submit();
        if (document.getElementById('submitActionFlag')) {
        	document.getElementById('submitActionFlag').value = "1";	
        }
        document.getElementById("mainForm").attributes["action"].value = tmpAction;
        document.getElementById("mainForm").attributes["target"].value = tmpTarget;
    }
    catch (e) {
        alert("提交数据出错");
    }
}


/********************
* 取得弹出窗口滚动条高度 
******************/
function getScrollTop(obj) {
    var scrollTop = 0;
    scrollTop = obj.scrollTop;
    return scrollTop;
}


/********************
* 取窗口可视范围的高度 
*******************/
function getClientHeight(obj) {
    var clientHeight = 0;
    clientHeight = obj.clientHeight;
    return clientHeight;
}

/********************
* 取文档内容实际高度 
*******************/
function getScrollHeight(obj) {
    var scrollHeight =  obj.scrollHeight;
    return scrollHeight;
}

/**判断滚动条是否触底**/
function isScrollReachButtom(obj) {
	if (getScrollTop(obj) + getClientHeight(obj) == getScrollHeight(obj)) {
		return true;
	} else {
		return false;
	}
}

function colFrame(){
	var fs = parent.document.getElementById("frame");
  	fs.cols = "225,6,*"
 }
 
 function openFrame() {
 	var fs = parent.document.getElementById("frame");
 	fs.cols = "0,6,*";
 }
 
 function jumpPage(pageIndex, max) {
    if (pageIndex < 1 || pageIndex > max) {
        return;
    }
    document.getElementById("pageIndex").value=pageIndex;
    doJumpPage();
}