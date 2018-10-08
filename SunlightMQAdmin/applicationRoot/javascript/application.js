function doAction(action) {
    try {
        document.getElementById("ajaxAction").value = action;
        refreshItem("hiddenSpan");
    }
    catch(e) {
        alert("提交数据出错");
    }
}

function changeSearchText(obj){
	obj.value='';
}
function changeSearchText2(obj){
	if(obj.value==''){
		obj.value='News Search';
	}
}
function showMenuList(id){
	var li_id = 'li_';
		if (id == 7) {
			$('#' + li_id + id).attr('class','on about');
		}
		else {
			$('#' + li_id + id).attr('class','on');
		}
	$('#' + li_id + id + ' ' + '.liListDiv').css('display','block');
}
function hiddenMenuList(id){
	var li_id = 'li_';
		if (id == 7) {
			$('#' + li_id + id).attr('class','about');
		}
		else {
			$('#' + li_id + id).attr('class','');
		}
	$('#' + li_id + id + ' ' + '.liListDiv').css('display','none');
}
function showSubMenuList(id){
	$("#subLi_" + id).attr('class','listdivOn');
	$("#subLi_" + id + " .secondDiv").css("display","block");
}
function hiddenSubMenuList(id){
	$("#subLi_" + id).attr('class','listdiv');
	$("#subLi_" + id + " .secondDiv").css("display","none");
}


function doSearch() {
    var keyword = document.getElementById("keywordInput").value;
    keyword = keyword.replace(">", "").replace("<", "").replace("\"", "").replace("'", "").replace("-", "");
    var url = encodeURI("/articles/search-" + keyword +  "-1.html");
    location.href = url;
}