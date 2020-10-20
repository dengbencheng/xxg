var GLOBAL_URL="http://localhost:8080";//new

function RequestHandle(){
	
}

var RequestURL={
	baseURL:GLOBAL_URL,
	API:{
		'USER':'/user',
		'ROLE':'/role',
		"PERM":'/perm',
		"DELEGATION": "/delegation"
	},
	constructURL:function(_apikey,_param,_data){
		if(!_apikey || !this.API.hasOwnProperty(_apikey)){
			console.log('未传入apikey,或传入的apikey未定义!');
			return;
		}
		return (this.baseURL+this.API[_apikey]+_param);
		
	}
}

RequestHandle.prototype.getRemoteData=function(_apiKey,_param,_data){
	if(arguments.length<2){
		console.log('此函数至少需要两个参数!');
		return ;
	}
	var _url=RequestURL.constructURL(_apiKey,_param,_data);
	return $.ajax({
		dataType: 'JSON',
        url: _url,
		xhrFields: {
			withCredentials: true // 发送Ajax时，Request header中会带上 Cookie 信息。
		},
        headers:{
           "Content-Type":"application/json"
        },
        data:_data,
        type: "GET"
	});
}

RequestHandle.prototype.postRemoteData=function(_apikey,_param,_data){
	if(arguments.length<3){
		console.log('此函数至少需要三个参数!');
		return ;
	}
	var _url=RequestURL.constructURL(_apikey,_param);
	return $.ajax({
		dataType: 'JSON',
        url: _url,
		headers:{
            "Content-Type":"application/json"
		},
		xhrFields: {
			withCredentials: true // 发送Ajax时，Request header中会带上 Cookie 信息。
		},
        data: JSON.stringify(_data),
        type: 'POST'
	});
}

RequestHandle.prototype.checkDataEmpty = function (object) {
	if (!object) {
		console.log("请传入object");
		return false;
	}
	for (var key in object) {
		if (object.hasOwnProperty(key) && (!object[key] || object[key] === "")) {
			return false;
		}
	}
	return true;
}
