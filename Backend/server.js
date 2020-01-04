var express = require('express');
var app = express();

var cookieParser = require('cookie-parser');
var session = require('express-session');

var ews = require('express-ws')(app,null,{
	wsOptions:{
		clientTracking:true
		//verifyClients:function //Implement Later.
	}
	
});

var router = express.Router();


app.ews = ews.getWss('/');
app.web = ews.getWss('/web');

////SERVER CONFIGURATION\\\\

//Server Variables
const port = 5000;


//Usage and Engines
app.set('views', './sites');
app.set('view engine', 'pug');


//Middleware
app.use(function (req, res, next) {
	console.log(req.url);
	next();
});


app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(cookieParser());

app.use(session({
	secret: 'th3Qu1ckBr0wnF0xJump3d0verthelazyd0g',
	resave: false,
	saveUninitialized: true,
	maxAge:600000,
	unset: 'destroy'
}));


//Can attach middleware that would effect both webserver and socket server.


var infoRouter 		= require('./info_router.js');
var lobbyHandler 	= require('./lobby_handler.js');
var siteHandler 	= require('./site_handler.js');
var socketHandler	= require('./socket_handler');


//Server Essentials Inits
infoRouter.init();
socketHandler.init(app,infoRouter);
siteHandler.init(app,infoRouter);


//Server Function Inits
lobbyHandler.init();




//Server Start
app.listen(port);



//Testing

// LHandler = require("./lobby_handler.js");

// console.log(LHandler.get_public_lobbies({username:"atobrien",num:15}));
// // console.log(LHandler.get_private_lobbies({username:"atobrien",num:15}));
// console.log(LHandler.get_user_lobbies({username:"atobrien",num:15}));



