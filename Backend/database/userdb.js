//Link to base level database functions.
var DatabaseConnection = require('./database.js'); //Double check
var db;

//Links to other higher level database functions.
var lobby = require("./lobbydb.js");
var chat = require("./chatdb.js");
var util = require("./dbutilities.js");

//Output for functions.
var mod = {};
module.exports = mod;  //In the future make a processor that adds the camel case names dynamically, rather than all the hard code.


mod.init = function(host,database){
	db = new DatabaseConnection(host,database);
}

//---- Modifiers ---- \\

//GP: adds a user to database if they arent already in there
mod.register_user = mod.registerUser = function(username, password, firstname, lastname, email, bdate, level){
	if(mod.userExists(username)){return false;}
	else{
		var columns = ['username', 'password', 'firstname', 'lastname', 'email', 'bdate', 'level', 'lastseen', 'online', 'recnum', 'recpin'];

		var data = [username, password, firstname, lastname, email, bdate, level, String(new Date()), 0, 0, util.generate_id()];
		db.store_data('users', columns, data);
		return true;
	}
}

//GP: removes a user from the database
//CK: Modified for username instead of userid.
mod.remove_user = mod.removeUser = function(username){
	db.remove_data('users', ['username', username]);
}

//CK: Resets password and assigns new recovery PIN.
mod.reset_password = mod.resetPassword = function(username,newpassword,recpin){
	if(mod.user_exists(username)){
		if(mod.check_recpin(username,recpin)){
			mod.change_password(username,newpassword);
			mod.assign_new_recpin(username);
			mod.increase_recnum(username);
			return true;
		}
	}	
	return false;
}

//GP: changes a user's password || CK: Edited so that auth is not a thing, can do that in another function.
mod.change_password = mod.changePassword = function(username, new_password){
	db.modify_data('users', 'password', new_password, ['username', username]);
}


//GP: sets the status of a user's activity
//CK: Changed to be all encompassing rather than relying on another source for status and stuff. Basically login or refresh user activity.
mod.set_online = mod.setOnline = function(username){
	db.modify_data('users', ['online', 'lastseen'], [1, String(new Date().getTime())], ['username', username]);
}

//CK: Sets user as offline.
mod.set_offline = mod.setOffline = function(username){
	db.modify_data('users', ['online', 'lastseen'], [0, String(new Date().getTime())], ['username', username]);
}

//CK: Assigns new recovery PIN to user.
mod.assign_new_recpin = mod.assignNewRecpin = function(username){
	db.modify_data('users','recpin',util.generateID(),['username',username]);
}

//CK: Increases recnum by one. 
mod.increase_recnum = mod.increaseRecnum = function(username){
	var user = mod.getUser(username);
	db.modify_data('users','recnum',user.recnum+1,['username',username]);
}

//GP: Turns a normal user into an admin
mod.make_admin = mod.makeAdmin = function(username){
	db.modify_data('users', 'level', 1, ['username', username]);
}




//---- Getters ---- \\


//CK: Returns all users.
mod.get_users = mod.getUsers = function(columns=undefined){
	return db.get_data('users',columns);
}

//CK: Returns user object.
mod.get_user = mod.getUser = function(username){
	return db.get_data('users',undefined,['username',username])[0];
}

//CK: Returns users based on attribute search.
mod.get_users_from_attribute = mod.getUsersFromAttribute = function(attribute,match){
	var users = db.get_data("users",undefined,[attribute,match]);
	return users;
}

//CK: Returns user based on unique attribute search.
mod.get_user_from_attribute = mod.getUserFromAttribute = function(attribute,match){
	return db.get_data('users',undefined,[attribute,match])[0];
}

//GP: returns all users who's username conatins the keyword
mod.search_user = mod.searchUser = function(keyword){
	return db.searchData('users', null, ['username', keyword]);
}


mod.get_last_login = mod.getLastLogin = function(){
	var users = db.get_data('users',['username','lastseen']);

	var max = 0;
	var maxpos = 0;
	
	for(var u in users){
		if(users[u].lastseen > max){
			max = users[u].lastseen;
			maxpos = u;
		}
	}

	return users[maxpos].username;
}


//---- Checkers ---- \\


//CK: Checks if the provided user exists.
mod.user_exists = mod.userExists = function(username){
	var user = mod.get_user(username);

	if(user != undefined) return user;

	return false;
}

//CK: Checks username and password.
mod.user_auth = mod.userAuth = function(username,password){
	var user = mod.user_exists(username);

	if(user){
		if(user.password == password) return true;
	}
	return false;
	
}

//CK: Checks saved recovery PIN against submitted recovery PIN.
mod.check_recpin = mod.checkRecpin = function(username, recpin){
	var user;
	if(user = mod.user_exists(username))
		if(user.recpin == recpin) return true;
	
	return false;
}

//CK: Checks if user is admin.
mod.is_admin = mod.isAdmin = function(username){
	var user;
	if(user = mod.user_exists(username))
		if(user.level == 1) return true;
	
	return false;
}
