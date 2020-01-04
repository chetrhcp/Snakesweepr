var database = require("./ss_database.js");
var comsdb = new database("localhost","kevin");

var app, infoRouter;

var mod = {};

var sockets = {};


module.exports = mod;


function server(ws, req) {
	var socketid = comsdb.generate_id();
	ws.id = socketid;
	sockets[socketid] = ws;

	ws.on('message', function(data) {

		console.log(data);

		var rdp = infoRouter.handle_data(data,socketid);

		if(rdp.error){
			ws.send("{\"error\":\"" + rdp.error + "\"}");
		}else{
			if(typeof rdp === "object"){
				console.log("[OUT] DataPacket");
				console.log(rdp);

				ws.send(JSON.stringify(rdp));
			}
		}
	});

	ws.on('close',function(){
		console.log("[SERVER] Closed.");
	});

	ws.on('error',function(err){
		console.log("[SERVER] Server Error: ");
		console.log(err);
	});
}

function wws(ws, req){
	ws.type = "web";

	ws.on('message', function(data){
		var rdp = infoRouter.handle_data(data);
	});
}

//Module Functions
mod.init = function(a,i){
	if(app != undefined){
		console.log("[SERVER] Socket Handler Already Initialized!");
		return;
	}

	app = a;
	infoRouter = i;

	//Socket Server	
	app.ws('/web',wws);			
	app.ws('/', server);
}


//Outside Functions
mod.send_socket = function(socketid,data){
	if(sockets[socketid])
		sockets[socketid].send(data);
}

mod.send_socket_bundle = function(socketids, data){
	for(var s in socketids){
		if(sockets[socketids[s]])
			sockets[socketids[s]].send(data);
	}
}

//CK: Deletes socket from local storage
mod.delete_socket = function(socketid){
	if(sockets[socketid])
		delete sockets[socketid];
}



mod.update_web = function(type,extra=undefined){ //Make update_web, specify default actions with input and have options as extra attributes
	
	if(app){
		var data = {};

		switch(type){
			case "user":
				data.type = "user";
				data.users = comsdb.getUsers(['firstname','lastname','username','level','online','lastseen']);
		
				break;
			case "lobby":
				data.type = "lobby";

				var lobbies = comsdb.getLobbies();


				for(var lobby in lobbies){
					lobbies[lobby].users = comsdb.getUsersInLobby(lobbies[lobby].lobbyid);
				}

				data.lobbies = lobbies;
				break;
			case "chat":
				data.type = "chat";

				break;
			default: 	//Send everything
				data.type = "all";
				data.users = comsdb.getUsers(['firstname','lastname','username','level','online','lastseen']);

				var lobbies = comsdb.getLobbies();


				for(var lobby in lobbies){
					lobbies[lobby].users = comsdb.getUsersInLobby(lobbies[lobby].lobbyid);
				}

				data.lobbies = lobbies;

				break;
		}

		//Assumes extra is an object
		for(var attr in extra){
			data[attr] = extra[attr];
		}

		app.web.clients.forEach(function(client){
			if(client.type == "web")
		    	client.send(JSON.stringify(data));
		});
	}
	
}



















