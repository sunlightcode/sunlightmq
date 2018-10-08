function jumpToPage() {
	var jumpToPageIndex = document.getElementById("pageIndexInput").value;
	var jumpToPageIndexValue = parseInt(jumpToPageIndex);
	if (!jumpToPageIndexValue) {
		alert("请输入一个正确的数值");
		document.getElementById("pageIndexInput").focus();
		return;
	}
	var jumpToPageMaxValue = parseInt(document.getElementById("pageCount").value);
	if (jumpToPageIndex < 1 || jumpToPageIndex > jumpToPageMaxValue) {
		alert("请输入一个介于1和" + jumpToPageMaxValue + "的数值");
		document.getElementById("pageIndexInput").focus();
		return;
	}
	showPage(jumpToPageIndex);
}

function jumpToPage2() {
	var jumpToPageIndex = document.getElementById("pageIndexInput").value;
	var jumpToPageIndexValue = parseInt(jumpToPageIndex);
	if (!jumpToPageIndexValue) {
		alert("请输入一个正确的数值");
		document.getElementById("pageIndexInput").focus();
		return;
	}
	var jumpToPageMaxValue = parseInt(document.getElementById("pageCount").value);
	if (jumpToPageIndex < 1 || jumpToPageIndex > jumpToPageMaxValue) {
		alert("请输入一个介于1和" + jumpToPageMaxValue + "的数值");
		document.getElementById("pageIndexInput").focus();
		return;
	}
	showPage2(jumpToPageIndex);
}

function returnCurrentPage() {
	clearFiles();
	showPage(document.getElementById("pageIndex").value);
}

function showPage(pageIndex) {
	document.getElementById("pageIndex").value = pageIndex;
	postModuleAndAction(document.getElementById("pageModule").value, "list");
}

function clearFiles() {
    var nodeArray = document.getElementsByTagName("input");
    for(i=0;i<nodeArray.length;i++) {
        var tmpobj = nodeArray[i];
        var stype = tmpobj.type;
        if (stype == "file") {
        	tmpobj.outerHTML=tmpobj.outerHTML;
        }
    }
}

function refreshDiscount(yiTiJia, shiChangJia){
	var discount = yiTiJia / shiChangJia +"";
	discount = discount.substring(0,4);
	if(isNaN(discount)){
		document.getElementById("discount").innerHTML="";
	} else {
		document.getElementById("discount").innerHTML=discount;
	}
}

function doAction(action) {
    try {
        document.getElementById("ajaxAction").value = action;
        refreshItem("hiddenSpan");
    }
    catch(e) {
        alert("提交数据出错");
    }
}

function checkUserID(userID) {
    document.getElementById("checkUserID").value = userID;
    doAction("checkUser");
}

//弹出Div效果
function setCoordination() {
    document.getElementById("mask").style.height = document.documentElement.clientHeight + "px";
    document.getElementById("mask").style.width = document.documentElement.clientWidth + "px";
}

function showWindow(displayedID) {
    var displayedWindow = document.getElementById(displayedID);
    var height = parseInt(displayedWindow.clientHeight);
    var width = parseInt(displayedWindow.clientWidth);
    var position = getPosition();
    var topAdded = (position.height - height) / 2;
//  var topAdded = (document.getElementById("wrapper").clientHeight - height ) / 2;
    var leftAdded = (position.width - width) / 2;

    displayedWindow.style.top = ( position.top + topAdded ) + "px";
    displayedWindow.style.left = ( position.left + leftAdded ) + "px";
    displayedWindow.style.visibility = "visible";
//    displayedWindow.style.display = "block";
    var mask = document.getElementById("mask");
    mask.style.visibility = "visible";
}

function hideWindow(hiddenID) {
    var hiddenWindow = document.getElementById(hiddenID);
    hiddenWindow.style.visibility = "hidden";
//   hiddenWindow.style.display = "none"; 
    var mask = document.getElementById("mask");
    mask.style.visibility = "hidden";
}

function getPosition() {
    var top    = document.documentElement.scrollTop;
    var left   = document.documentElement.scrollLeft;
    var height = document.documentElement.clientHeight;
    var width  = document.documentElement.clientWidth;
    return {top:top,left:left,height:height,width:width};
}

function openWindow(action) {
    try {
        document.getElementById("ajaxAction").value = action;
        refreshItem("windowInsideDIV");
    }
    catch(e) {
        alert("提交数据出错");
    }
}

function closeWindow() {
    document.getElementById("windowInsideDIV").innerHTML = "";
    hideWindow("infoWindow");
}

function trLine0(obj) {
    obj.onmouseover = function () {
        obj.style.backgroundColor = "#EEEEFF";
    }
    
    obj.onmouseout = function () {
        obj.style.backgroundColor = "#FFFFFF";
    }
}

function trLine1(obj) {
    obj.onmouseover = function () {
        obj.style.backgroundColor = "#EEEEFF";
    }
    
    obj.onmouseout = function () {
        obj.style.backgroundColor = "#ECE9D8";
    }
}

function openInfoWindow(action) {
    try {
        document.getElementById("ajaxAction").value = action;
        refreshItem("windowInsideDIV");
    }
    catch(e) {
        alert("提交数据出错");
    }
}

function closeInfoWindow() {
    document.getElementById("windowInsideDIV").innerHTML = "";
    hideWindow("infoWindow");
}

function closeInfoWindow2(id) {
    hideWindow(id);
}

function calendar(object) {
    WdatePicker({onpicking:function () {
        object.focus();
    }, skin:"whyGreen", minDate:""});
}

var LastLeftID='';
function DoMenu(emid) {
	var obj = document.getElementById(emid); 
	obj.className = (obj.className.toLowerCase() == "expanded"?"collapsed":"expanded");
	if(LastLeftID!="" && LastLeftID != emid) {
		document.getElementById(LastLeftID).className = "collapsed";
	}
	LastLeftID = emid;
}


function selectAllCheckBox(id, selectChoiceName, saveValueElementID){
	var choices = $("input[name='" + selectChoiceName + "']");
	var topSelect = $("#" + id).attr("checked");
    for (var i = 0; i < choices.length; i++) {
        choices[i].checked = topSelect;
    }
    setSelectedValues(selectChoiceName, saveValueElementID);
}

function widnowJumpToPage() {
	var jumpToPageIndex = document.getElementById("windowPageIndexInput").value;
	var jumpToPageIndexValue = parseInt(jumpToPageIndex);
	if (!jumpToPageIndexValue) {
		alert("请输入一个正确的数值");
		document.getElementById("windowPageIndexInput").focus();
		return;
	}
	var jumpToPageMaxValue = parseInt(document.getElementById("windowPageCount").value);
	if (jumpToPageIndex < 1 || jumpToPageIndex > jumpToPageMaxValue) {
		alert("请输入一个介于1和" + jumpToPageMaxValue + "的数值");
		document.getElementById("windowPageIndexInput").focus();
		return;
	}
	windowShowPage(jumpToPageIndex);
}
function windowShowPage(pageIndex) {
	document.getElementById("windowPageIndex").value = pageIndex;
	var beforeAjaxAction = document.getElementById('ajaxAction').value;
	//openInfoWindow('waitingWindow');
	openInfoWindow(beforeAjaxAction);
}

function getSelectValue(selecteid) {
	var selectionObject = document.getElementById(selecteid);
    for (var i = 0; i < selectionObject.options.length; i++) {
        if (selectionObject.options[i].selected == true) {
            return selectionObject.options[i].value;
        }
    }
    
    return "";
}