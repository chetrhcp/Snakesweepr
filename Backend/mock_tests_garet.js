var sinon = require("sinon");
var game_handler = require("./game_handler.js");
var lobby_handler = require("./lobby_handler.js");
var game = require("./game.js");

var getLobby_stub = sinon.stub(lobby_handler, "get_lobby");
var newLobby_stub = sinon.stub(lobby_handler, "new_lobby");
var joinLobby_stub = sinon.stub(lobby_handler, "join_lobby");

////All the set up

//fake lobby object
var fakeLobby = {
  id: 111111,
  name: "",
  owner: "",
  users: {
    0:{username:""},
    1:{username:""}
  },
  broadcast_message: function(data){
    console.log("not sent to " + data.username);
  }
}

//redirects new lobby to the function
newLobby_stub.callsFake(newFakeLobby);

function newFakeLobby(fakeData){

  fakeLobby.name = fakeData.name;
  fakeLobby.owner = fakeData.username;
  fakeLobby.users[0].username = fakeData.username;
}

//redirects join lobby to this function
joinLobby_stub.callsFake(joinFakeLobby);

function joinFakeLobby(fakeData){

  fakeLobby.users[1].username = fakeData.username;
}

//returns the dummy lobby
getLobby_stub.returns(fakeLobby);


//tests creation and joining using the mocks

lobby_handler.new_lobby({name:"testLobby", username:"gwphelps"});
console.log(fakeLobby);
lobby_handler.join_lobby({username:"ckauth97"});
console.log(fakeLobby);
//console.log(JSON.stringify(fakeLobby));

console.log("tests the creation of a new game");
console.log(JSON.stringify(game_handler.newGame({type:"new_game",username:"gwphelps", platform:"app", lobby_name:"testLobby"})));

console.log("tests the updating of the snake game");
console.log(game_handler.updateGame({
    type:"update_game",
    platform:"app",
    username:"gwphelps",
    lobby_id:fakeLobby.id,
    data:
    {
      username:"gwphelps",
      head_pos:{x:5,y:5}
    }
  }));

  console.log(game_handler.updateGame({
      type:"update_game",
      platform:"app",
      username:"ckauth97",
      lobby_id:fakeLobby.id,
      data:
      {
        username:"ckauth97",
        tile:{x:5,y:5},
        click_type:"normal"
      }
    }));
