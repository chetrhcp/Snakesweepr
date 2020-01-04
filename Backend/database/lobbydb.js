//Link to base level database functions.
var DatabaseConnection = require('./database.js'); //Double check
var db;

//Links to other higher level database functions.
var user = require("./userdb.js");
var chat = require("./chatdb.js");
var util = require("./dbutilities.js");

//Output for functions.
var mod = {};
module.exports = mod;  

mod.init = function(host,database){
	db = new DatabaseConnection(host,database);
}



//---- Modifiers ---- \\

//CK: Stores lobby and owner assoc.
mod.store_lobby = mod.storeLobby = function(id,name,creator,occupancy){
	if(lobby_exists(id)) return;


	db.storeData(
		'lobbies',
		['lobbyid','name','creator','occupancy','chatstore','isprivate'],
		[id,name,creator,occupancy,]
	);
}

//CK: Removes lobby.
mod.remove_lobby = mod.removeLobby = function(id){

}


//CK: Add user to lobby
mod.add_to_lobby = mod.addToLobby = function(lobbyid,username){
	
}



//---- Getters ---- \\


//CK: Gets lobby by id;
mod.get_lobby = mod.getLobby = function(lobbyid){
	return db.get_data('lobbies',undefined,["lobbyid",lobbyid]);
}

//CK: Gets all lobbies
mod.get_lobbies = mod.getLobbies = function(){
	return db.get_data('lobbies');
}

//CK: Gets a certain number of public lobbies.
mod.get_public_lobbies = mod.getPublicLobbies = function(num){ //Look into LIMIT
	var pl = db.get_data('lobbies',undefined,['isprivate',0]);

	if(pl.length < num) num = pl.length;

	var rpl = [];

	for(var i = 0; i < num; i++){
		rpl[i] = pl[i];
	}

	return rpl;
}

// CK: Gets a certain number of private lobbies.
//NEEDS MORE INDEPTH... LOOK FOR FRIENDS ASSOCIATIONS
mod.get_private_lobbies = mod.getPrivateLobbies = function(username,num){ //Look into LIMIT //Complex relationship with owners as friends.
	var pl = db.get_data('lobbies',undefined,['isprivate',1]);

	if(pl.length < num) num = pl.length;

	var rpl = [];

	for(var i = 0; i < num; i++){
		rpl[i] = pl[i];
	}

	return rpl;
}

// CK: Gets a certain number of lobbies associated with a user.
mod.get_user_lobbies = mod.getUserLobbies = function(username,num){ //Look into LIMIT //Complex relationship with owners as friends.
	var pl = db.get_data('lobby_users',undefined,["username",username]);

	if(pl.length < num) num = pl.length;

	var rpl = [];

	for(var i = 0; i < num; i++){
		rpl[i] = mod.get_lobby(pl[i].lobbyid);
	}

	return rpl;
}

mod.get_users_in_lobby = mod.getUsersInLobby = function(lobbyid){
	var lu = db.get_data('lobby_users','username',["lobbyid",lobbyid]);

	return lu;
}

//---- Checkers ---- \\


//CK: Checks to see if lobby exists in database.
mod.lobby_exists = mod.lobbyExists = function(lobbyid){
	var lobby = mod.get_lobby(lobbyid);
	if(lobby != undefined) return lobby;
	return false;
}