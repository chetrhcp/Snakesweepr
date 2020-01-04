var url = "ws://cs309-sd-1.misc.iastate.edu:5000/web";



var ws = new WebSocket(url);

var connected = false;

ws.onopen = function(){
	document.getElementById("server-state").innerHTML = "<span style='color:green'>Connected.</span>";

	connected = true;
}

ws.onclose = function(){
	//Attempt reconnect. Display Message about reconnection.
	
	document.getElementById("server-state").innerHTML = "<span style='color:red'>Disconnected.</span>";

	connected = false;

	ws = new WebSocket(url);

}

ws.onmessage = function(e){
	var data = JSON.parse(e.data);

	console.log(data);

	if(data.type == "user"){
		if(data.lastlogin != undefined)
			document.getElementById("last-login").innerHTML = data.lastlogin;
		
		var update = "<tr><th>Name</th><th>Username</th><th>Level</th><th>Online</th><th>Last Seen</th></tr>";

		var users = data.users;

		for(var user in users){
			update += "<tr>";
			update += "	<td>"+users[user].firstname + " " +users[user].lastname+"</td>";
			update += "	<td>"+users[user].username+"</td>";
			update += "	<td>"+users[user].level+"</td>";
			update += "	<td>"+users[user].online+"</td>";
			update += "	<td>"+users[user].lastseen+"</td>";
			update += "</tr>";
		}

		document.getElementById("users-table").innerHTML = update;
	}

	else if(data.type == "lobby"){ //convert to divs
		var update = "<tr><th>Lobby Name</th><th>Owner</th><th>Level</th><th>Online</th><th>Last Seen</th></tr>";

		var users = data.users;

		for(var user in users){
			update += "<tr>";
			update += "	<td>"+users[user].firstname + " " +users[user].lastname+"</td>";
			update += "	<td>"+users[user].username+"</td>";
			update += "	<td>"+users[user].level+"</td>";
			update += "	<td>"+users[user].online+"</td>";
			update += "	<td>"+users[user].lastseen+"</td>";
			update += "</tr>";
		}

		document.getElementById("lobbies-table").innerHTML = update;
	}

	else if(data.type == "chat"){
		var chat = data.chat;


		var p = document.createElement("P");
		var t = document.createTextNode(chat.username + ": " + chat.message);
		p.appendChild(t);   

		document.getElementById(chat.lobby).appendChild(p);

		var chatDiv = document.getElementById(chat.lobby);
		chatDiv.scrollTop = chatDiv.scrollHeight;
	}

	//Based on type of update, update specific parts of site.

}

function chat_message(e,lobby){
	e.preventDefault();

	var chat = document.getElementById("chat_"+lobby);

	if(chat == "" || chat == undefined || chat == null) return;

	var msgPacket = {
		type:"lobby_chat",
		platform:"web",
		username:"admin",
		lobby:lobby,
		message:chat.value
	};

	ws.send(JSON.stringify(msgPacket));

	chat.value = "";
	
}

function set_chat_state(){
	var divs = document.getElementsByClassName("lobby-chat");

	for(var i = 0; i < divs.length; i++){
		divs[i].scrollTop = divs[i].scrollHeight;
	}
}

