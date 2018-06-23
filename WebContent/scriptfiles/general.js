
var onAnswer = function(answer, status) {
	if (status != "success") {
		alert("the mistakes appeared while data transmitting!")
		return;
	}
	console.log("status=" + status);
	// console.log("data= " + answer);
	var answerObject = JSON.parse(answer);
	switch (answerObject.command) {
	case "continue":
		break;
	case "newpage":
		console.log("newpage =" + answerObject.newpage);
		location.replace(answerObject.newpage);
		break;
	} // End of switch
	return answerObject;
} // End of onAnswer


var arrayToString = function(array) {
	var result = "";
	var N = array.length;
	for (var m = 0; m < N - 1; m++)
		result += array[m] + ",";
	if (N > 0)
		result += array[N - 1];
	return result;
} // End of arrayToString

var stringToArray = function(string) {
	var result = new Array();
	if (!string || 0 === string.length)
		return result;
	result = string.split(',');
	return result;
}


var stringArrayToUnorderedList = function(id,array,names) {
	for (var m = 0; m < array.length; m++) {
		var NLI = document.createElement('li');
		NLI.innerHTML = names[m] +":  "+ array[m];
		document.getElementById(id).appendChild(NLI);
	} // End of for
} // End of stringArrayToUnorderedList


