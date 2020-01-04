//Link to base level database functions.
var DatabaseConnection = require('./database.js'); //Double check
var db;

//Output for functions.
var mod = {};
module.exports = mod;


mod.init = function(host,database){
  db = new DatabaseConnection(host,database);
}


//CK - Makes sure all local data is updated to the permenant database.
mod.sync_to_db = mod.syncToDB = function(table, columns, values){
  //Get Values from table
  var data = db.get_data(table,columns);

  //Compare to submitted columns and values
  

  //Save to DB
}



//---- Modifiers ---- \\





//---- Getters ---- \\


//GP: generates a 6 digit id
//CK: Modified to make sure that id is unique. 
mod.generate_id = mod.generateID = function(){
  var id = Math.floor(100000 + Math.random() * 900000);

  while(this.id_exists(id)) id = Math.floor(100000 + Math.random() * 900000);

  return id;
}




//---- Checkers ---- \\
  

//CK: checks that the id generated is not being used in either the deviceIDs or userIDs.
mod.id_exists = mod.idExists = function(id){
  var users = undefined;
  var lobbies = undefined;

  users = db.get_data("users","recpin",["recpin",id]);
  lobbies = db.get_data("lobbies","lobbyid",["lobbyid",id]);

  if((users != undefined) && (lobbies != undefined)) return false;

  return true;
}