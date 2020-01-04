var DataPacket = require('./data_packet.js');

var socketHandler = require('./socket_handler.js');

var lobbyHandler = require("./lobby_handler.js")
var util = require("./util.js");

var database = require("./ss_database.js");
var comsdb = new database("localhost","kevin");

var UHandler, LHandler, GHandler;

var handlers;

var mod = {};

module.exports = mod;


//Module Functions

mod.init = function(){
	UHandler = require("./user_handler.js");
	LHandler = require("./lobby_handler.js");


	//CK - Mapping function tags to respective functions.
	handlers = {
		'login': 				UHandler.login,							
		'new_user': 			UHandler.reg_new_user,
		'forgot_password': 		UHandler.forgot_password,
		'reset_password': 		UHandler.reset_password,

		'new_lobby': 			LHandler.new_lobby,
		'join_lobby': 			LHandler.join_lobby,
		'leave_lobby': 			LHandler.leave_lobby,

		'get_user_lobbies': 	LHandler.get_user_lobbies,
		'get_private_lobbies': 	LHandler.get_private_lobbies,
		'get_public_lobbies': 	LHandler.get_public_lobbies,

		'lobby_chat': 			LHandler.handle_lobby_chat,
	
		'new_game': 			LHandler.newGame,
		'update_game': 			LHandler.updateGame
	};

}


// Outside Functions

//CK - Parses and directs data to specified functions.
mod.handle_data = function(data,conn){
	var dataPacket;
	var ret = {};

	if(util.is_json(data) || (typeof data === "object")){

		var parsedData = data;

		if(typeof data !== "object")
			parsedData = JSON.parse(data);

		ret = new DataPacket(parsedData);
		
		if(conn) ret.conn = conn;


		middleman(ret);


		// console.log("[IN] DataPacket");
		// console.log(ret);

		if(ret.error){
			console.log("[ERROR] " + ret.error);
		}else{
			ret = handlers[ret.type](ret);
		}


		if(ret && ret.conn){
			delete ret.conn;
		}

	}else{
		ret.error = "Data Not of type JSON.";
	}

	return ret;
}


function middleman(dataPacket){

	if(!comsdb.user_exists(dataPacket.username) && dataPacket.type != "new_user" && dataPacket.username != "admin"){
		dataPacket.error = "User ["+dataPacket.username+"] Does Not Exist."
	
	}else{
		if(dataPacket.type != "login"){
			//Online/Connection Metadata
			if(dataPacket.username) comsdb.set_online(dataPacket.username);
			
			
			if(dataPacket.type == "lobby_chat"){

			}
		}

		if(dataPacket.conn){
			lobbyHandler.update_socket(dataPacket.username, dataPacket.conn);
			console.log("[UPDATE SOCKET] " + dataPacket.username + " : " + dataPacket.conn);
		}
	}

	socketHandler.update_web("user",{lastlogin:dataPacket.username});
}
