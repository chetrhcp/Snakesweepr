var DataPacket = require('./data_packet.js');

var Emailer = require('./email.js');

var socketHandler = require('./socket_handler.js');


var fpassMailer = new Emailer('gmail','snakesweeper@gmail.com','UnicornRainbow4!');

var database = require("./ss_database.js");
var comsdb = new database("localhost","kevin");

var mod = {};

module.exports = mod;


////Functions\\\\
check_user_status();
setInterval(check_user_status, 60000);

////User Data

//CK: Registers new user.
mod.reg_new_user = function(newUserData){
		//Zero for default user.

		//Build up the database functions to return based on the success/failure of the query.
		comsdb.registerUser(newUserData.username,newUserData.password,newUserData.firstname,newUserData.lastname,newUserData.emailnewUserData.bdate,0);

		var res = {
			type:"new_user",
			success:true
		};

		return res;
}
	
//CK: Sends recpin to user for them to use with reset password.
mod.forgot_password = function(forgotPasswordData){

	if(comsdb.userExists(forgotPasswordData.username)){
			console.log("Sending recovery email to: " + forgotPasswordData.username);

			var user = comsdb.get_user(forgotPasswordData.username);

			var content = "Hello " + user.firstname + 

			"!\n\nIt seems you want to reset your password. But that's not totally implemented yet. (Probably won't reset, but hey here's to trying)\n"+
			"\nSo here's a PIN I guess. Go ahead and slap it in that app of yours and we'll get this shindig figured out.\n\n"+
			"\nPIN: "+user.recpin + "\n" +
			"\n\nSincerely,\nThe SnakeSweeper Sqwad"+
			"\n\nP.S: Don't forget your password! We like to let everyone know >:)";
			
			
			

			fpassMailer.sendEmail(user.email,"[SnakeSweeper] Forgot Password",content,function(err){
				console.log("Sent.");
			}); //This is neat. Async callback nesting.

			var fpdp = new DataPacket(forgotPasswordData,{success:true},"outgoing"); //Somehow find a way to get the error from the emailer syncronously.

			return fpdp;

	}else{
		console.log("User Does Not Exist.");  //Implement servmsg and optional attrs in data_packet to send error messages.
	}
}

//CK: Compares submitted PIN and resets password to the submitted new password.
mod.reset_password = function(resetPasswordData){
	if(comsdb.userExists(resetPasswordData.username)){
		var rpdp = new DataPacket(resetPasswordData,{success:false},"outgoing");
			
		if(comsdb.reset_password(resetPasswordData.username,resetPasswordData.newpassword,resetPasswordData.recpin)){
			rpdp.success = true;
		}
		return rpdp;
	}
}


////Server Functions

//CK - Logs in.
mod.login = function(loginData){
	var ldp = new DataPacket(loginData,{success:false,sessionid:null,firstname:"",lastname:""},"outgoing");

	if(comsdb.user_auth(loginData.username,loginData.password)){
		ldp.success = true;

		comsdb.set_online(loginData.username);

		var user = comsdb.get_user(loginData.username);

		ldp.firstname = user.firstname;
		ldp.lastname = user.lastname;

	}else{
		//Track incorrect logins? Other data?
	}

	return ldp;
}

//Checks offline for now
function check_user_status(){
	var users = comsdb.getUsers(['username','lastseen','online']);

	var currentTime = new Date().getTime();

	var offlineusers = [];

	for(var user in users){
		if((users[user].online == 1) && ((currentTime - users[user].lastseen) > 5*60000)){
			offlineusers.push(users[user].username);
		}
	}

	console.log("OFFLINE");
	console.log(offlineusers);

	for(var user in offlineusers){
		console.log("Setting Offline: " + offlineusers[user]);
		comsdb.set_offline(offlineusers[user]);
	}

	if(offlineusers.length > 0)
		socketHandler.update_web("user");

}

