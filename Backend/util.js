
var mod = {};
module.exports = mod;


mod.is_json = function(data) {
	var json = false;

    try {
        json = JSON.parse(data);
    } catch (e) {
    }

    return json;
}

