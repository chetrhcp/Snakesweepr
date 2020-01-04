var nm = require('nodemailer');

var mod = {};

module.exports = class Emailer{
	constructor(service, user, pass){
		this.user = user;
		
		this.trans = nm.createTransport({
			service: service,
			auth:{
				user:user,
				pass:pass
			}
		});
	}

	sendEmail(to, sub, content, alt=undefined){
		var opt = {
			from:this.user,
			to:to,
			subject:sub,
			text:content //can be switched out for 'html' to send markdown.
		};

		console.log("Sending Email: "+to);
		this.trans.sendMail(opt, alt);
	}
}