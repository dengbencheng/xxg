//获取这个Cookie的值
function getCookie(name) {
    var strCookie = document.cookie;
    console.log("获取到的cookie：" + strCookie)
    var arrCookie = strCookie.split(";");

    for (var i = 0; i < arrCookie.length; i++) {
        var arr = arrCookie[i].split("=");
        if (arr[0] == name) {
            return arr[1];
        }
    }
    return "";
}

/**
 * 通过key获取数据 转换成JSON对象
 * @param key
 */
function getCookieInfo(key) {
    //首先要获取 用户信息
    var strJSON=getCookie("userInfo");
    //第二步:转换成JSON对象
    var jsonObjCookie=eval("("+strJSON+")");
    return jsonObjCookie;
}