//Link to base level database functions.
var DatabaseConnection = require('./database.js'); //Double check
var db;

//Links to other higher level database functions.
var user = require("./userdb.js");
var lobby = require("./lobbydb.js");
var util = require("./dbutilities.js");

//Requirements for mod specific file.
var fs = require('fs');

//Settings for this specific file.
const chatDir = "chats/";

//Output for functions.
var mod = {};
module.exports = mod;  

mod.init = function(host,database){
	db = new DatabaseConnection(host,database);
}




//---- Modifiers ---- \\


//CK: Makes chatstore file. Only in charge of checking existance and making.
mod.create_chat = mod.createChat = function(name){
	if(!mod.chat_exists(name))
		fs.writeFile(chatDir+name+".lobbychat","[]",(err)=>{});
	else console.log("Chat file ["+name+"] already exists.");
	
	return true;
}

//CK: Appends message to chat file
mod.store_message = mod.storeMessage = function(name,username,message){
	var chatfile;
	if(!(chatfile = mod.chat_exists(name))){
		mod.create_chat(name);
	}

	chatfile = mod.chat_exists(name);

	console.log("LOG FILE: "+chatfile)

	var chat = JSON.parse(fs.readFileSync(chatDir+chatfile));

	console.log(chat);

	chat[chat.length] = username + ": " + message;
	
	console.log(JSON.stringify(chat));

	fs.writeFileSync(chatDir+chatfile,JSON.stringify(chat));
}



//---- Getters ---- \\


mod.get_chat_file = mod.getChatFile = function(name){
	var lobbies = fs.readdirSync(chatDir);

	for(var l in lobbies){
		if(lobbies[l] == (name+".lobbychat")) return lobbies[l];
	}
	return undefined;
}

//CK: Returns array of messages, if length is larger than number of messages, then return everything.
mod.get_messages = mod.getMessages = function(name,num){
	
	var chatfile;
	if(chatfile = mod.chat_exists(name)){
		var chat = JSON.parse(fs.readFileSync(chatDir+chatfile));

		if((chat.length - num) < 0) num = chat.length;

		var chunk = [];
		for(var i = (chat.length-num); i < chat.length; i++){
			chunk[i] = chat[i];
		}

		return chunk;
	}
}



//---- Checkers ---- \\


//CK: Checks to see if chat exists.
mod.chat_exists = mod.chatExists = function(name){
	var chat;
	if((chat = mod.get_chat_file(name)) != undefined){
		return chat;
	}
	return false;
}