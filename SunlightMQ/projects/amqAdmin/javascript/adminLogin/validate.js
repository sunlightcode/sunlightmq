(function autoValidate( )
{
    //if (window.addEventListener) window.addEventListener("load", init, false);
    //else if (window.attachEvent) window.attachEvent("onload", init);

	Group={
		exist:false,
		member:[],//[{id:1,element:[obj1,obj2,...]},{...}]
		add:function(obj){
			this.exist=true;
			var id=obj.getAttribute('group');
			for(var i=0;i<this.member.length;i++){
				if(this.member[i].id==id){
					this.member[i]['element'].push(obj)
					return;
				}
			}
			this.member.push({"id":id,"element":[obj]})
		},
		check:function(){
			if(!this.exist)	return true;
			var ret=false;
			for(var i=0;i<this.member.length;i++){
				var _ret=true;
				var elem=this.member[i]['element'];
				for(var j=0;j<this.member[i]['element'].length;j++){
					if(this.member[i]['element'][j].className.indexOf(" valid-text")==-1){
						_ret=false
					}
				}
				ret=ret||_ret
			}
			return ret;
		},
		showMsg:function(){
			if(Group.member.length>0){
				
				var textfield=Group.member[0]['element'][0]
				var msg=getMsgCont(textfield);
				msg.className = msg._className+" invalid-msg";
				textfield.className=textfield.className.replace("valid-text","");
				if(textfield.className.indexOf("invalid-text")==-1)textfield.className += ' invalid-text';
				msg.innerHTML = textfield.getAttribute('groupMsg')||'请选填此项或其他项';
				textfield.focus();
				addEvent(document,'keydown',Group.clearMsg)
			}
		},
		clearMsg:function(){
			if(Group.member.length>0){
				var textfield=Group.member[0]['element'][0]
				var msg=getMsgCont(textfield);
				if(msg&&!msg._className)msg._className=msg.className.replace('invalid-msg','')
				msg.className = msg._className;

				textfield.className=textfield.className.replace('invalid-text','');
				//if(textfield.className.indexOf("valid-text")==-1)textfield.className += ' valid-text';
				msg.innerHTML = textfield.getAttribute('alt');
			}
			try{
			removeEvent(document,'keydown',Group.clearMsg)
			}catch(e){
			}
		}
	}

	addEvent(window,'load',init);
	function addEvent(obj, type, fn)
	{
		if (obj.attachEvent)
		{ 
			obj['e'+type+fn] = fn;
			obj[type+fn] = function(){obj['e'+type+fn]( window.event );}
			obj.attachEvent('on'+type, obj[type+fn]);
		}
		else
			obj.addEventListener(type, fn, false);
	}
	function FireEvent(elem, eventName)
	{
		if (document.all)
		{
			elem.fireEvent(eventName);
		}
		else
		{
			 var evt = document.createEvent('HTMLEvents');
			 evt.initEvent('change',true,true);
			 elem.dispatchEvent(evt);
		}
	}
	function removeEvent(obj, type, fn)
	{
		if (obj.detachEvent)
		{
			obj.detachEvent('on'+type, obj[type+fn]);
			obj[type+fn] = null;
		}
		else
			obj.removeEventListener(type, fn, false);
	}
	function getMsgCont(textfield){
		var msg=textfield.nextSibling,_msg;
		while(msg && msg.nodeType==3){_msg=msg;msg=msg.nextSibling}
		if(msg && (msg.tagName=='LABEL' || msg.tagName=='SPAN')){
			return msg;
		}else{
			msg=getMsgCont(_msg.parentNode);
			return msg;
		}
	}
    function init( ) {
        for(var i = 0; i < document.forms.length; i++) {
            var f = document.forms[i];
			f.setAttribute('submiting','false')
            var needsValidation = false;
            for(j = 0; j < f.elements.length; j++) {
                var e = f.elements[j];
				if(e.getAttribute('group')){
					Group.add(e)
				}
                if (e.type != "text" && e.type!="password" && e.type!='select-one' && e.type!='textarea') continue;
                var pattern = e.getAttribute("pattern");
                var required = e.getAttribute("required") != null;
                if (required && !pattern) {
                    pattern = "\\S";
                    e.setAttribute("pattern", pattern);
                }
                if (pattern)
                {
					addEvent(e,'blur',function(){validateOnChange.call(this,f)});
                    needsValidation = true;
                }
            }
            if (needsValidation){f.onsubmit = validateOnSubmit;f.setAttribute('novalidate','true');}
        }
    }
    function validateOnChange(form)
    {
		////console.log(form)
		var f=form;
        var textfield = this;
		if(textfield.className.indexOf('chkIng-text')!=-1) return;
        var pattern = textfield.getAttribute("pattern");
		textfield._type=pattern;
		switch(pattern)
		{
			case 'required': pattern = /\S+/i;break;
			case 'email': pattern = /^\w+([-+.]\w+)*@\w+([-.]\w+)+$/i;break;
			case 'email_ajax': pattern = /^\w+([-+.]\w+)*@\w+([-.]\w+)+$/i;break;
			case 'realname': pattern = /^[\w\u0391-\uFFE5]{2,20}$/;break;
			case 'username_ajax': pattern = /^[\w\u0391-\uFFE5]{2,20}$/;break;
			case 'qq':  pattern = /^[1-9][0-9]{4,}$/i;break;
			case 'id': pattern = /^\d{15}(\d{2}[0-9x])?$/i;break;
			case 'ip': pattern = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/i;break;
			case 'zip': pattern = /^\d{6}$/i;break;
			case 'phone': pattern = /^((\d{3,4})|\d{3,4}-)?\d{7,8}(-\d+)*$/i;break;
			case 'mobi': pattern = /^1[3584]\d{9}$/i;break;
			case 'url': pattern = /^[a-zA-z]+:\/\/(\w+(-\w+)*)(\.(\w+(-\w+)*))+(\/?\S*)?$/i;break;
			case 'date': pattern = /^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$/i;break;
			case 'datetime': pattern = /^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29) (?:(?:[0-1][0-9])|(?:2[0-3])):(?:[0-5][0-9]):(?:[0-5][0-9])$/i;break;
			case 'int':	pattern = /^\d+$/i;break;
			case 'float': pattern = /^\d+\.?\d*$/i;break;
		}
        var value = this.value;
        var alt = textfield.getAttribute("alt");
		var empty = textfield.getAttribute("empty");
		var msg=getMsgCont(textfield);
		
		if(msg&&!msg._className)msg._className=msg.className
		//console.log(textfield,(empty==null && value=='') || (value!='' && value.search(pattern) == -1))
        if ((empty==null && value=='') || (value!='' && value.search(pattern) == -1))
        {
			
			//验证不通过
        	textfield.className=textfield.className.replace("invalid-text","");
			textfield.className=textfield.className.replace("valid-text","");
        	 if(textfield.className.indexOf("invalid-text")==-1)textfield.className += " invalid-text";
			 //console.log(textfield.className)
        	 if(msg && (msg.tagName=='LABEL' || msg.tagName=='SPAN'))
        	 {
        	 	msg.className = msg._className+" invalid-msg";
				if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', msg.innerHTML);
        	 	msg.innerHTML=alt;
        	 }
        	 else
        	 {
        	 	 var new_msg=document.createElement("LABEL");
        	 	 new_msg.className = " invalid-msg";
				 if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', '');
        	 	 new_msg.innerHTML=alt;
				 textfield.parentNode.insertBefore(new_msg,msg);
        	 }
        }
        else
        {
			
        	textfield.className=textfield.className.replace("invalid-text","");
			textfield.className=textfield.className.replace("valid-text","");
			if(empty!=null && value=='');
			else
        	if(textfield.className.indexOf("valid-text")==-1)textfield.className +=" valid-text";
        	if(msg && (msg.tagName=='LABEL' || msg.tagName=='SPAN' ))
        	 {
				if(empty!=null && value=='')
					msg.className = msg._className;
				else
					msg.className = msg._className+" valid-msg";
				if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', msg.innerHTML);
				msg.innerHTML=textfield.getAttribute('initmsg');
        	 }
        	  else
        	 {
        	 	 var new_msg=document.createElement("LABEL");
				 if(empty!=null && value=='')
					new_msg.className = "";
				else
					new_msg.className = " valid-msg";
				 if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', '');
				 new_msg.innerHTML=textfield.getAttribute('initmsg');
				 textfield.parentNode.insertBefore(new_msg,msg);
                msg = new_msg;
        	 }
			if(this.type == 'password')
	        {
	        	var bind = textfield.getAttribute("bind");
		        var bind_flag = true;
		        var bind_arr = document.getElementsByName(bind);
		        var bind_arr_len = bind_arr.length;
		        for(var i=0; i<bind_arr_len; i++)
			    {
					if(bind_arr[i].name == bind && bind_arr[i].value != this.value && bind_arr[i].value != '')
			    	{
			    		bind_flag = false;
			    	}
			    }
			    if(!bind_flag)
			    {
			    	msg.className = msg._className+" invalid-msg";
			    	textfield.className=textfield.className.replace("valid-text","");
			    	if(textfield.className.indexOf("invalid-text")==-1)this.className += ' invalid-text';
			    	msg.innerHTML = '两次输入密码不一致';
			    }
			    else
			    {
			    	msg.className = msg._className+" valid-msg";
			    	textfield.className=textfield.className.replace("invalid-text","");
			    	if(textfield.className.indexOf("valid-text")==-1)this.className += ' valid-text';
			    	msg.innerHTML = textfield.getAttribute('initmsg');
					 for(var i=0; i<bind_arr_len; i++)
					{
						var _msg=getMsgCont(bind_arr[i]);
						if(!_msg._className) _msg._className=_msg.className;
						_msg.className = _msg._className+" valid-msg";
						bind_arr[i].className=bind_arr[i].className.replace("invalid-text","");
						if(bind_arr[i].className.indexOf("valid-text")==-1)this.className += ' valid-text';
						_msg.innerHTML = bind_arr[i].getAttribute('initmsg');
						
					}
			    }
			}
			//ajax验证email 
			if((this._type == 'email_ajax' || this._type == 'username_ajax') && textfield.getAttribute('chkText')!=value && textfield.className.indexOf('chkIng-text') == -1)
			{
				//console.log('AJAX check email')
				textfield.className=textfield.className.replace("invalid-text","");
				textfield.className=textfield.className.replace("valid-text","");
				if(textfield.className.indexOf("chkIng-text")==-1){
					textfield.className += " chkIng-text";
					textfield.setAttribute("chkText",value);//记录这次验证的value供下次区分
				}
				var requestStr='';
				switch(this._type){
					case 'email_ajax':
						requestStr='/index.php?controller=ajax&action=chk_email&email='+value;break;
					case 'username_ajax':
						requestStr='/index.php?controller=ajax&action=chk_username&username='+value;break;
					default:
						throw new Error('Input\'s type is undefined.');
				}
				$.get(requestStr,{},function(data){
					var data=$.parseJSON(decodeURIComponent(data));
					if(!data.error){
						//console.log('email_ajax check pass')
						textfield.className=textfield.className.replace("chkIng-text","");//删除正在验证标识
						if(textfield.className.indexOf("valid-text")==-1)textfield.className += " valid-text";
						
						//检查其他提交项，有某项验证不通过则不提交
						var invalid = false;
						for(var i = 0; i < f.elements.length; i++)
						{
							var e = f.elements[i];
							if ((e.type == "text" || e.type == "password" || e.type == "select-one" || e.type == "textarea") && e.getAttribute("pattern") && e.style.display!='none') {
								if (e.className.indexOf(" valid-text")==-1)
								{
									invalid = true;
									break;
								}
							}
						}
						if(!invalid){
							validateOnSubmit.call(f)
						}
					}else{
						textfield.className=textfield.className.replace("chkIng-text","");
						if(textfield.className.indexOf("invalid-text")==-1)textfield.className += " invalid-text";
						 if(msg && (msg.tagName=='LABEL' || msg.tagName=='SPAN'))
						 {
							msg.className = msg._className+" invalid-msg";
							if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', msg.innerHTML);
							msg.innerHTML=data['message'];
						 }
						 else
						 {
							 var new_msg=document.createElement("LABEL");
							 new_msg.className = "invalid-msg";
							 if(textfield.getAttribute('initmsg')==null) textfield.setAttribute('initmsg', '');
							 new_msg.innerHTML=data['message'];
							 textfield.parentNode.insertBefore(new_msg,msg);
						 }
					}
				})
			}
        }
    }
    function validateOnSubmit() {
		//console.log('start')
		Group.clearMsg();
        var invalid = false;
		this.setAttribute('submiting','true')
        for(var i = this.elements.length-1; i >=0; i--)
        {
            var e = this.elements[i];
			if(e.getAttribute('vhide')=='true') continue;
            if ((e.type == "text" || e.type == "password" || e.type == "select-one" || e.type == "textarea") && e.getAttribute("pattern") && e.style.display!='none') {
				addEvent(e,'change',validateOnChange);

				if (e.className.indexOf(" invalid-text")!=-1)
				{
					invalid = true;
					if(e.offsetHeight > 0 || e.client > 0)
					{
						e.focus();
					}

				}
				else
				{
					//e.onchange( );
					//FireEvent(e,'onchange');
					validateOnChange.call(e)
					if (e.className.indexOf(" invalid-text")!=-1)
					{
						invalid = true;
						if(e.offsetHeight > 0 || e.client > 0)
						{
							e.focus();
						}

					}else if(e.className.indexOf('chkIng-text') != -1){
						invalid = true;
					}
				}
            }
        }
		//console.log(Group)
		if(!invalid){
			if(!Group.check()){
				Group.showMsg();
				return false;
			}
		}
        var callback = this.getAttribute('callback');
        var result = true;
        if(callback !=null) {result = eval(callback);}
        result = !(result==undefined?true:result);
        if (invalid || result) {
            //alert("    你填写的字段格式不正确！\n" +
            //      "纠正后，再试一次！");
			this.setAttribute('submiting','false')
            return false;
        }
		//console.log('submit')
		//return false
    }
})( );
