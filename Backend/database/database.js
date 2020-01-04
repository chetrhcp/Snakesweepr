//https://github.com/ForbesLindesay/sync-mysql#readme

var mysql = require('sync-mysql');

var mod = {};


//DatabaseConnection Class
module.exports = class DatabaseConnection {


	constructor(host,database){
		//Add a read settings file for connection details.
		this._name = "db:"+database;
		this._host = host;
		this._user = "root";
		this._password = "UnicornSparkles4!"; //temp change for later and keep in config file (not synced with gitlab)

		this._database = database;

		this._connection;

		this._connected = false;
	}


	connect(){
		if(!this._connected){
			this._connection = new mysql({
				host: this._host,
				user: this._user,
				password: this._password,
				database: this._database
			});

			this._connected = true;
		}else{
			console.log("Already Connected!");
		}
	}

	disconnect(){
		if(this._connected){
			this._connection.dispose();
			this._connected = false;
		}else{
			//Handle already disconnected.
			console.log("Already Disconnected!");
		}

	}


	//Base Functions
	////May remove function params in later renditions.
	store_data(table,columns,data,func=null){
		this.connect();

		var stmt = "INSERT INTO " + table + " (";

		var content = "";

		if(Array.isArray(columns)){
			for(var i = 0; i < columns.length; i++){
				content += columns[i];
				if(i<columns.length-1) content += ",";
			}
			// console.log(content);
		}else content = columns;

		stmt += content + ") VALUES (";

		content = "";

		if(Array.isArray(data)){
			var content = "";
			for(i = 0; i < data.length; i++){
				if(Array.isArray(data[i])){
					if(data[i].length > 0){
						// console.log(data[i]);
						content += "'";
						for(x = 0; x < data[i].length; x++){
							content += data[i][x];
							if(x<data[i].length-1) content += "|";
						}
						content += "'";
					}else content += "''";
				}else content += "'" + data[i] + "'";

				if(i<data.length-1) content += ",";
			}
			// console.log(content);
		}else content += data;

		stmt += content + ")";


		console.log("SQL: "+stmt);


		var result = this._connection.query(stmt);


		this.disconnect();

		// return result;
	}

	get_data(table,columns,where,func=null){
		this.connect();

		var stmt = "SELECT ";

		if(columns != undefined){
			if(Array.isArray(columns)){
				var len = columns.length;
				for(var i = 0; i<len; i++){
					stmt += columns[i];
					if(i<len-1){
						stmt += ",";
					}
				}
			}else{
				stmt += columns;
			}
		}else{
			stmt += "*";
		}

		stmt += " FROM " + table;

		if(Array.isArray(where)){
			stmt += " WHERE " + where[0] + "=" + "'" + where[1] + "'";
		}

		// console.log("SQL: "+stmt);


		var result = this._connection.query(stmt);

		this.disconnect();

		return result;
	}
	
	//GP: gets all users that match the search
	search_data(table,columns,where,func=null){
		this.connect();

		var stmt = "SELECT ";

		if(columns != undefined){
			if(Array.isArray(columns)){
				var len = columns.length;
				for(var i = 0; i<len; i++){
					stmt += columns[i];
					if(i<len-1){
						stmt += ",";
					}
				}
			}else{
				stmt += columns;
			}
		}else{
			stmt += "*";
		}

		stmt += " FROM " + table;

		if(Array.isArray(where)){
			stmt += " WHERE " + where[0] + " LIKE " + "'%" + where[1] + "%'";
		}

		// console.log("SQL: "+stmt);


		var result = this._connection.query(stmt);

		this.disconnect();

		return result;
	}

	remove_data(table,where,func=null){
		this.connect();


		var stmt = "DELETE FROM "+table+" ";

		if(Array.isArray(where)){
			stmt += " WHERE "+ where[0] + "=" + "'" + where[1] + "'";
		}

		// console.log("SQL: "+stmt);

		var result = this._connection.query(stmt);


		this.disconnect();

		// return result;
	}

	modify_data(table,columns,data,where,func=null){
		this.connect();

		var stmt =  "UPDATE " + table + " SET ";

		if(Array.isArray(columns)){
		  	if(columns.length == data.length){
		  		var len = columns.length;

		  		for(var i = 0; i < len; i++){
		  			stmt += columns[i]+"='"+data[i]+"'";

		  			if(i < len-1){
		  				stmt +=", ";
		  			}
		  		}
		  	}
		}else{
		  	if(!Array.isArray(data)){
		  		stmt += columns+"='"+data+"'";
		  	}else{
		  		console.log("Error: Columns != Values [Length]");
		  		return;
		  	}
		}

		stmt += " WHERE ";
		if(Array.isArray(where)){
		  	stmt += where[0]+="='"+where[1]+"'";
		}

		// console.log("SQL: "+stmt);

		var result = this._connection.query(stmt);



		this.disconnect();

		// return result;
	}

}
