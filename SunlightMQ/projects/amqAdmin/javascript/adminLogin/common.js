C={};
C.qtipCfg=function(type,opt){
	var json;
	switch(type){
		case 1:
			json={
				position: {
					my: 'left center',
					at: 'right center'
				},
				hide:{
					delay:2000
				},
				show: {
					event:'click'
			   }
			};break;
		case 2:
			json={
				show:{
					event:'click',
					solo:true
				},
				style: {
					classes: 'ui-tooltip-auto ui-tooltip-shadow'
				},
				
				hide:{
					event:'unfocus'
				},
				position: {
					viewport: $(window),
					my: 'right center',
					at: 'left center'
				}
			}
			break;
		default:
			json={
				position: {
					my: 'left center',
					at: 'right center'
				},
				hide:{
					event:'unfocus'
				},
				show: {
					event:'click'
			   }
			};break;
	}
	return $.extend(json,opt);
}
try{
	console.log();
}catch(e){
	console={}
	console['log']=function(){return null}
}

