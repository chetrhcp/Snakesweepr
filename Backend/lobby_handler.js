var DataPacket = require('./data_packet.js');

var Game = require('./game.js');

var database = require("./ss_database.js");
var comsdb = new database("localhost","kevin");

var socketHandler = require("./socket_handler.js");

var users = {};
var lobbies = {};

var mod = {};
module.exports = mod;



class User{
	constructor(username){
		this.username = username;
		this.socketid = undefined;
	}

	set_socket(socketid){
		this.socketid = socketid;
	}
}

class Lobby{
	constructor(name,owner,isprivate,lobbyid=undefined){
		if(lobbyid != undefined)
			this.id = lobbyid;
		else
			this.id = comsdb.generateID();

		this.name = name;
		this.owner = owner;
		this.users = [];
		this.isprivate = isprivate;


		this.add_user(owner);
	}

	add_user(username,socketid=undefined){
		var u;
		if(u = create_user(username))
			this.users.push(u.username);

		return this.in_lobby(username);
	}

	remove_user(username){
		if(this.in_lobby(username)){
			delete this.users[username];
		}

		return this.in_lobby(username);
	}

	update_socketid(username,socketid){
		this.users[username].set_socket(socketid);
	}

	broadcast_message(message){ //DataPacket
		for(var user in this.users){
			if(this.users[user] != message.username){
				socketHandler.send_socket(users[this.users[user]].socketid,JSON.stringify(message));
			}
		}
	}

	in_lobby(username){
		for(var user in this.users){
			if(username == this.users[user]) return true;
		}

		return false;
	}
}


function create_user(username){
	if(!users[username]){
		users[username] = new User(username);
	}
	return users[username];
}


//Lobby

mod.init = function(){
	var usrs = comsdb.get_users();

	usrs.forEach(function(user){
		if(!users[user.username])
			create_user(user.username);
	});

	//Retrieve all lobbies
	var ls = comsdb.get_lobbies();
	
	//Make each into object
	ls.forEach(function(lobby){
		var l = new Lobby(lobby.name,lobby.creator,lobby.isprivate,lobby.lobbyid);

		var lu = comsdb.get_users_in_lobby(l.id);

		lu.forEach(function(user){
			l.add_user(user.username);
		});

		lobbies[l.name] = l;

		var gameData = new DataPacket({
			type:"new_game",
			platform:"app",
			username:lobby.creator,
			lobby_name:l.name
		});

		mod.newGame(gameData);

		comsdb.create_chat(l.name);
	});

}


//MAKE THESE EDIT THE DATABASE THEN SYNC BACK TO LOCAL


//Handlers for messages incoming. Local handling will be in other functions.
mod.new_lobby = function(lobbyData){

	//Check database and the list to see if the lobby exists.

	var l = new Lobby(lobbyData.lobby,lobbyData.username,lobbyData.isprivate);

	lobbies[lobbyData.lobby] = l;

	lobbies[lobbyData.lobby].update_socket(lobbyData.username,lobbyData.conn);

	return new DataPacket({
		type:"new_lobby",
		platform:"app",
		username:lobbyData.username,
		success:mod.lobby_exists(lobbyData.lobby)
	},undefined,"outgoing");
}

mod.join_lobby = function(lobbyData){
	var jldp = new DataPacket({
		type:"join_lobby",
		platform:"app",
		username:lobbyData.username,
		success:false,
		chats:[]
	},undefined,"outgoing");

	if(mod.lobby_exists(lobbyData.lobby)){
		if(!lobbies[lobbyData.lobby].in_lobby(lobbyData.username)){
			lobbies[lobbyData.lobby].add_user(lobbyData.username);
		}
		jldp.success = lobbies[lobbyData.lobby].in_lobby(lobbyData.username);

		jldp.chats = comsdb.get_messages(lobbyData.lobby,15);
	}

	return jldp;
}

mod.leave_lobby = function(lobbyData){
	var lldp = new DataPacket({
		type:"leave_lobby",
		platform:"app",
		username:lobbyData.username,
		success:false
	},undefined,"outgoing");

	if(mod.lobby_exists(lobbyData.lobby)){
		lobbies[lobbyData.lobby].remove_user(lobbyData.username);
		lldp.success = !lobbies[lobbyData.lobby].in_lobby(lobbyData.username);
	}

	console.log("Lobbies.");
	console.log(lobbies);

	return lldp;
}

mod.get_lobby = function(name){
	if(mod.lobby_exists(name))
		return lobbies[name];
	return undefined;
}

mod.get_lobby_by_id = function(id){
	for(var lobby in lobbies){
		console.log(lobby);
		if(lobbies[lobby].id == id) return lobbies[lobby];
	}
	return undefined;
}


mod.update_socket = function(username,socketid){
	if(users[username])	
		users[username].set_socket(socketid);
}


//Lobby Functions
mod.get_all_lobbies = function(){
	return lobbies;
}

mod.get_public_lobbies = function(lobbyData){
	var ls = comsdb.get_public_lobbies(15);

	return new DataPacket(lobbyData,{lobbies:ls},"outgoing");
}

mod.get_private_lobbies = function(lobbyData){
	var ls = comsdb.get_private_lobbies(lobbyData.username,15);

	return new DataPacket(lobbyData,{lobbies:ls},"outgoing");
}

mod.get_user_lobbies = function(lobbyData){
	var ls = comsdb.get_user_lobbies(lobbyData.username,15);

	return new DataPacket(lobbyData,{lobbies:ls},"outgoing");
}

mod.lobby_exists = function(name){
	if(lobbies[name]) return true;
	return false;
}

mod.user_in_lobby = function(lobby,username){
	var users = comsdb.get_users_in_lobby(lobby);

	console.log(users);

	users.forEach(function(user){
		console.log("USER");
		console.log(user);
		if(user.username == username) return true;
	});

	return false;
}

//Chat
mod.handle_lobby_chat = function(chatData){
	delete chatData.conn;
	var lobby;
	if(lobby = mod.get_lobby(chatData.lobby)){
		comsdb.store_message(lobby.name,chatData.username,chatData.message);
		
		lobby.broadcast_message(chatData);

		socketHandler.update_web("chat",{chat:chatData});

	}

	return chatData;
}



//GAMES
var all_games = {};

//starts a new snakesweeper game
mod.newGame = function(game_data) {

 var lobby = mod.get_lobby(game_data.lobby_name);

  var snake_username = lobby.owner;

  var ms_username;
  for(var i in lobby.users){
    if(lobby.users[i].username != snake_username){
      ms_username = lobby.users[i].username;
    }
  }

  //create game
  var game = new Game(snake_username, ms_username);
  all_games[lobby.id] = game;

  var returnObject = all_games[lobby.id].gameInit();
  var packet =  new DataPacket({
		type:game_data.type,
		platform:game_data.platform,
    	username:game_data.username,
		data:returnObject
	},undefined,"outgoing");

  //send game data

  lobby.broadcast_message(packet);

  console.log(all_games);

  return packet;

}

mod.updateGame = function(game_data) {

  var packet =  new DataPacket({
      type:game_data.type,
      platform:game_data.platform,
      username:game_data.username,
      data:{}
    },undefined,"outgoing");

  var game = all_games[game_data.lobby_id];

  console.log(game);

  if(game != undefined){

    //update game
    var returnObject = game.update(game_data.data);

    //send game data
    var lobby = mod.get_lobby_by_id(game_data.lobby_id);
    var lobbyUsers = lobby.users;

    packet.data = returnObject;

    console.log(lobby);

    lobby.broadcast_message(packet);
  }


  return packet;
}

mod.viewGames = function() {
  return all_games;
}



