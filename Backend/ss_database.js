

module.exports = class ss_database{
	constructor(host, database){

		var user = require("./database/userdb.js");
		var lobby = require("./database/lobbydb.js");
		var chat = require("./database/chatdb.js");
		var util = require("./database/dbutilities.js");

		user.init(host,database);
		lobby.init(host,database);
		chat.init(host,database);
		util.init(host,database);

		for(var func in user){
			this[func] = user[func];
		}

		for(var func in lobby){
			this[func] = lobby[func];
		}

		for(var func in chat){
			this[func] = chat[func];
		}
		
		for(var func in util){
			this[func] = util[func];
		}
	}
};