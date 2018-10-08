function InitAjax() {
    var ajax = false;
    try {
        ajax = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e) {
        try {
            ajax = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (E) {
            ajax = false;
        }
    }
    if (!ajax && typeof XMLHttpRequest != "undefined") {
        ajax = new XMLHttpRequest();
    }
    return ajax;
}

var ajaxActionFlag = "0";
function refreshItem(item) {
	if (ajaxActionFlag == "1") {
        alert("数据提交中,请耐心等待");
        return;
    }
    
    $("#module").val('ajax');
    $("#action").val('refresh' + item);
    if (document.getElementById("ajaxAction").value == 'exportAccessLog') {
    	showWindow("infoWindow2");
    }
    cursor_wait();
    
    ajaxActionFlag = "1";
    
    $.ajax({
   		type: "post",  
   		url : "/ajax",
   		dataType: 'html',
   		data: getformdata("mainForm"),
   		success: function(html) {
   			ajaxActionFlag = "0";
   			
   			if (item == "hiddenSpan") {
            	eval(decodeURI(html));
            }
            else {
            	document.getElementById(item).innerHTML = html;
            }
            
            if (item == "windowInsideDIV") {
                if (document.getElementById('hiddenSpanInWindowInside')) {
                    eval(decodeURI(document.getElementById('hiddenSpanInWindowInside').innerHTML));
                }
                else {
                    showWindow("infoWindow");
                }
            }
            
             if (document.getElementById("ajaxAction").value == 'exportAccessLog') {
             	closeInfoWindow2('infoWindow2');
             }
             cursor_clear();
   		},
   		error: function() {
   			ajaxActionFlag = "0";
   			alert("提交数据出错，请稍后重试！");
   			if (document.getElementById("ajaxAction").value == 'exportAccessLog') {
             	closeInfoWindow2('infoWindow2');
             }
   			 cursor_clear();
   		}
     });
}

// Changes the cursor to an hourglass
function cursor_wait() {
	document.body.style.cursor = 'wait';
}

// Returns the cursor to the default pointer
function cursor_clear() {
	document.body.style.cursor = 'default';
}

function getformdata(formobj) {
    var dtype=Array("input","select","textarea"); 
    var dtypeNum = 3;
    var d = document.getElementById(formobj);
    var i = 0;
    var j = 0;
    var qstring = "";
    var nodeArray = new Array();
    for(;i<dtypeNum;i++) {
        nodeArray.push(d.getElementsByTagName(dtype[i]));
    }
    var nodenum = 0;
    for(i=0;i<dtypeNum;i++) {
        nodenum = nodeArray[i].length;
        var tmpobj = nodeArray[i];
        for(j=0;j<nodenum;j++) {
            var stype = tmpobj[j].type;
            if (stype == "text"  || stype == "password" || stype == "hidden" || stype == "textarea") {
                qstring+="&" + tmpobj[j].name + "=" + encodeURIComponent(tmpobj[j].value);  
            }
            else if (stype == "checkbox" || stype == "radio") {
                if (tmpobj[j].checked) qstring+="&" + tmpobj[j].name + "=" + encodeURIComponent(tmpobj[j].value);  
            }
            else if (stype == "select-one" || stype == "select-multiple") {
                var selectNum = tmpobj[j].length;
                for(var k =0;k<selectNum;k++) {
                    if (tmpobj[j][k].selected) qstring+="&" + tmpobj[j].name + "=" + encodeURIComponent(tmpobj[j][k].value);  
                }
            }
        }
    }
    return qstring.substring(1,qstring.length);
}