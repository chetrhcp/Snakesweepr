const sinon = require('sinon');


var ir = require("../info_router.js");
var mm = require("../middleman.js");

var uh = require("../user_handler.js");

var ud = require("../database/userdb.js");

var DataPacket = require("../data_packet.js");


//Test path from info_router, middleman, user_handler.

describe("Testing [info_router]", function(){
	it("Data Handling should call (middleman.process) once with passed through args.", function(){
		
		var testData = {
			type:"login",
			platform:"app",
			username:"ckauth97",
			password:"password"
		};

		var mmmock = sinon.mock(mm);

		mmmock.expects('process').once().withArgs(testData);
		
		ir.handle_data(testData);

		mmmock.verify();

		mmmock.restore();
	});
});

describe("Testing [user_handler]", function(){
	it("Data Handling should call (login) once with passed through args.", function(){
		
		var testData = {
			type:"login",
			platform:"app",
			username:"ckauth97",
			password:"password"
		};

		testData = new DataPacket(testData);

		var uhmock = sinon.mock(uh);

		uhmock.expects('login').once().withArgs(testData);
		
		ir.init();
		ir.handle_data(testData);

		// uh.login(testData);

		uhmock.verify();

		uhmock.restore();
	});
});


describe("Testing [userdb]", function(){
	it("Data Handling should call (user_auth from login) once with passed through args.", function(){
		
		var testData = {
			type:"login",
			platform:"app",
			username:"ckauth97",
			password:"password"
		};

		var udmock = sinon.mock(ud);

		udmock.expects('user_auth').once().withArgs(testData.username,testData.password);
		
		ud.user_auth(testData.username,testData.password);

		udmock.verify();

		udmock.restore();
	});
});

