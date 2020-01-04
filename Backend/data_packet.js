/* NOTES

Possibly move the dataTypes definitions to other file or as argument. (Modular Programming and Such)

*/


//Defines what must be in the DataPacket, regardless of the type.
const absReqTypes = ['type','platform','username'];



////The Incoming/Outgoing nature of this DataPacket definition is a bit restrictive to this project.
////Similar implementations of DataPacket may be used more generally if only one option is provided for types.

//Defines what must be in the DataPacket, dependent on the type. INCOMING
const reqDataTypesIncoming = {
	'login':['password'],
	'new_user':['firstname','lastname','password','bdate','email'],
	'forgot_password':[],
	'reset_password':['newpassword','recpin'],

	'new_lobby':['lobby','isprivate'],  //Private is boolean. True is private. False is public.
	'join_lobby':['lobby'],
	'leave_lobby':['lobby'],

	'get_user_lobbies':[],
	'get_private_lobbies':[],
	'get_public_lobbies':[],

	'lobby_chat':['lobby','message'],

	'new_game':['lobby_name'],
	'update_game':['lobby_id','data']

};

//Defines what must be in the DataPacket, dependent on the type. OUTGOING
const reqDataTypesOutgoing = {
	'login':['firstname','lastname','success'],
	'new_user':['success'],
	'forgot_password':['success'],
	'reset_password':['success'],

	'new_lobby':['success'],
	'join_lobby':['success','chats'],
	'leave_lobby':['success'],

	'get_user_lobbies':['lobbies'],
	'get_private_lobbies':['lobbies'],
	'get_public_lobbies':['lobbies'],

	'lobby_chat':['lobby','message'],

	'new_game':['data'],
	'update_game':['data']
};



//Add the optionals back for servmsg and others



//CK - Will allow definition of specific fields for data packaging. Keeping things neat.
module.exports = class DataPacket{
	constructor(data,addons=undefined,flow=undefined){ //When flow is undefined, DataPacket is formatted as Incoming. Anything else is Outgoing.
		assoc_attr(this,absReqTypes,data);

		if(!validate_attr(this,absReqTypes)){
			return;
		}

		//Required attributes of the DataPacket, dependent on the type of DataPacket it is.
		var reqAttr = reqDataTypesIncoming[this.type]; //[this.action];

		if(flow != undefined) reqAttr = reqDataTypesOutgoing[this.type];

		if(typeof reqAttr === 'undefined'){
			handle_type_error(this.type,this);
			return;
		}

		assoc_attr(this,reqAttr,data);

		if(addons != undefined) assoc_attr(this,reqAttr,addons);

		if(!validate_attr(this,reqAttr)){
			return;
		}
	}
	
	add(attribute, value=undefined){
		for(var attr in reqDataTypes[type]){ //[action]
			if(attribute == attr){
				this[attribute] = value;
				return;
			}
		}
		console.log("[" + attribute + "] is not a valid attribute for this DataType.")
	}
}

//CK - Validates that the passed in object has all the required attributes.
function validate_attr(obj,attr){
	if(Array.isArray(attr)){
		for(var a in attr){
			if(!obj.hasOwnProperty(attr[a])){
				handle_attr_error(attr[a],obj); 
				return false;
			}
		}
	}else{
		if(obj.hasOwnProperty(attr)) return true;
		handle_attr_error(attr,obj); 
		return false;
	}
	return true;
}

//CK - Associates all needed attributes to the current DataPacket. Pretty Neat.
function assoc_attr(obj,list,haystack){
	for(var el in list){
		if(haystack && haystack.hasOwnProperty(list[el])){
			obj[list[el]] = haystack[list[el]];
		}
	}

	for(var attr in haystack){
		if((typeof haystack[attr] === 'object') && !obj.hasOwnProperty(attr)){
			var a;

			if((a = assoc_attr(obj,list,haystack[attr])) != undefined) return a;
			
		}
	}

	return obj;
}


//CK - Recursively iterates through data object to find the specified field.
////This is nice and all but only useful in some cases. That's why we use it's bigger, badder, brother assoc_attr().
function find_attribute(needle, haystack){ //Has issues with self referencing objects.
	for(var attr in haystack){
		if(attr == needle) return haystack[attr];

		if(typeof haystack[attr] === 'object'){
			var a;

			if((a = find_attribute(needle, haystack[attr])) != undefined) return a;
			
		}	
	}

	return undefined;
}

//CK - Sets error flag for attribute error. In function because of possibly future development.
function handle_attr_error(attr,obj){
	obj.error = "Attribute ["+attr+"] not found in submitted object.";
}

//CK - Sets error flag for type error. In function because of possibly future development.
function handle_type_error(type,obj){
	obj.error = "Type ["+type+"] not valid type for this object.";
}



//// EXAMPLE \\\\
/*

var DataPacket = require('./data_packet.js');

var loginData = new DataPacket({
	type:'login',
	content:{
		red:{
			user:"Nope",
			pass:"NERP"
		},
		blue:{
			user:"Nope",
			pass:"NERP"
		},
		hidden:{
			loginData:{
				username:"TestUser",
				password:"TestPassword"
			}
		}
	},
	socket:"socketOBJ"
});

console.log(loginData);

loginData.add("test",{"yeet":"lol"});

console.log(loginData);

*/