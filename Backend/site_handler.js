var express = require('express');

var socketHandler = require("./socket_handler.js");

var database = require("./ss_database.js");
var comsdb = new database("localhost","kevin");

var app, infoRouter;
var mod = {};


module.exports = mod;


mod.init = function(a,i){
	if(app != undefined){
		console.log("Site Handler Already Initialized!");
		return;
	}

	app = a;
	infoRouter = i;

	app.use(express.static(__dirname+'/sites'));


	app.use(function(req, res, next){
		if(req.session.authed == undefined){
			req.session.authed = false;
		}

		if(!req.session.authed){
			if(req.url != "/login")
				res.redirect("/login");
			else next();
		}else next();
		
	});


	//Home Page
	app.get('/', function(req, res){

		var lobbies = comsdb.getLobbies();


		for(var lobby in lobbies){
			lobbies[lobby].users = comsdb.getUsersInLobby(lobbies[lobby].lobbyid);
			lobbies[lobby].chats = comsdb.get_messages(lobbies[lobby].name,15);
		}

		console.log(lobbies);

		res.render("home",{
			name:"home",
			username:req.session.authed,
			users: comsdb.getUsers(['firstname','lastname','username','level','online','lastseen']),
			lastlogin: comsdb.getLastLogin(),
			lobbies: lobbies
		});

		if(req.session.authed){
			comsdb.set_online(req.session.authed);
			socketHandler.update_web("user");

		}
	});


	//Login Page
	app.get('/login', function(req, res){
		res.render("login",{
			name:"login"
		});
	});

	app.post('/login', function(req,res){
		var resp = infoRouter.handle_data(req.body);

		if(resp.success){
			req.session.authed = resp.username;

			res.redirect("/");
		}else{
			res.render("login",{
				name:"login",
				error: "Username or Password Incorrect!"
			});
		}
	});


	app.get('/logout', function(req,res){

		if(req.session.authed)
			comsdb.set_offline(req.session.authed);

		//Gross but it'll do for now.

		socketHandler.update_web("user");

		req.session.destroy();
		res.redirect("/login");
	});


	//Server Funtionality
	app.post('/forgot', function(req, res){
		var resp = infoRouter.handle_data(req.body);

		res.send(JSON.stringify(resp));
		
	});

	app.post('/new', function(req, res){
		var resp = infoRouter.handle_data(req.body);

		res.send(JSON.stringify(resp));
		
	});


	app.post('/lobbies', function(req, res){ //Make, Join, Leave
		var resp = infoRouter.handle_data(req.body);

		res.send(JSON.stringify(resp));
		
	});

	app.post('/chat', function(req, res){
		var resp = infoRouter.handle_data(req.body);

		res.send(JSON.stringify(resp));
		
	});

	return mod;
}
