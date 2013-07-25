var mongojs = require ('mongojs');
var google_api_key = 'AIzaSyC_UydFRANdnnb2vnJ97jxOcNKMhETbWqA';
var url = require('url');
var dbname = "gcm";
var collections = [ "gcm_users" ];
var db = mongojs.connect(dbname,collections);
var http = require('http');
var fs = require('fs');
var qs = require('querystring');

var server = http.createServer(function(req, res){
	var uu = url.parse(req.url, true);
	if(uu.pathname == "/add_user" ){
		var body = '';
		var name = '';
		var email = '';
		var regid = '';
		req.on("data", function (chunk){
			body += chunk;
		});
		req.on("end", function(){
			var json_post = qs.parse(body);
			name = json_post.name;
			email = json_post.email;
			regid = json_post.regid;
		});
			
			
			
			db.gcm_users.find(function(err,docs){
                var jsonobj = JSON.parse(JSON.stringify(docs));
				var existing = false;
				for (var i=0;i<jsonobj.length;i++){
					if(jsonobj[i].name == name && jsonobj[i].email == email){
						console.log("Found an existing user!");
						existing = true;
					}
				}
				if (!existing){
					db.gcm_users.insert({ "name" : name , "email" : email , "regid" : regid , "created_at" : getDateTime()});
					res.writeHead(200, { "Content-Type" : "text/html" });
		            res.end(name + " added to database");
				}else{
					res.writeHead(200, { "Content-Type" : "text/html" });
		            res.end(name + " already exists in the database");
				}
           });		
	}
	if(uu.pathname == "/send_message"){
		var regid = uu.query.regid
		var message = uu.query.message
		db.gcm_users.find(function(err,docs){
            var jsonobj = JSON.parse(JSON.stringify(docs));
			var existing = false;
			for (var i=0;i<jsonobj.length;i++){
				if(jsonobj[i].regid == regid){
					send_notification(regid,message);
					i=jsonobj.length;
					res.writeHead(200, { "Content-Type" : "text/html" });
		            res.end("Successfully sent request to the GCM server");
				}
			}
			
       });
		
	}
	else{
		 db.gcm_users.find(function(err,docs){
                    res.writeHead(200, { "Content-Type" : "application/json" });
                    res.end(JSON.stringify(docs));
         });
	}
	
}).listen(8080);
function send_notification(regid, message){
	var url = 'https://android.googleapis.com/gcm/send';
	var registration_ids = [];
	registration_ids.push(regid);
	var registration_ids_json = JSON.stringify(registration_ids);
	var headers = {
		'Authorization: key=' : google_api_key,
		'Content-Type:' : 'application/json' 
	};
	var message_json = { 'message' : message };
	var fields = {
		'registration_ids' : registration_ids_json,
		'data' : message_j
		
	}
	
}
function getDateTime() {

    var date = new Date();

    var hour = date.getHours();
    hour = (hour < 10 ? "0" : "") + hour;

    var min  = date.getMinutes();
    min = (min < 10 ? "0" : "") + min;

    var sec  = date.getSeconds();
    sec = (sec < 10 ? "0" : "") + sec;

    var year = date.getFullYear();

    var month = date.getMonth() + 1;
    month = (month < 10 ? "0" : "") + month;

    var day  = date.getDate();
    day = (day < 10 ? "0" : "") + day;

    return year + ":" + month + ":" + day + ":" + hour + ":" + min + ":" + sec;

}