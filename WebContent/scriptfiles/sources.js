var addSource = function(path) {
	var table = document.getElementById("sourcesList");
	var tr = document.createElement('tr');
	// ----
	var td = document.createElement('td');
	var txt = document.createElement('input');
	txt.type = "text";
	txt.value = path;
	txt.name = "source";
	// txt.disabled=true;
	td.appendChild(txt);
	tr.appendChild(td);
	// ----
	td = document.createElement('td');
	button = document.createElement('input');
	button.type = "button";
	button.value = "delete";
	button.setAttribute("onclick", "deleteTableRow('sourcesList',this)");
	// alert(button.onclick);
	td.appendChild(button);
	tr.appendChild(td);
	table.appendChild(tr);
} // End of addSource


var deleteTableRow=function(tableId,input){
var table = document.getElementById(tableId);
var tr = input.parentNode.parentNode;
table.deleteRow(tr.rowIndex);
numberOfSourses--;
if(numberOfSourses===0) table.hidden=true;
}

var addTableRow=function(tableId,Id){
var table = document.getElementById(tableId);
var tr=document.createElement('tr');
//----
var td=document.createElement('td');
var leftElement=document.getElementById(Id);
var txt=document.createElement('input');
txt.type="text";
txt.value=leftElement.value;
txt.name="source";
// txt.disabled=true;
td.appendChild(txt);
leftElement.value="";
tr.appendChild(td);
//----
td=document.createElement('td');
button=document.createElement('input');
button.type="button";
button.value="delete";
button.setAttribute("onclick","deleteTableRow('sourcesList',this)");
//alert(button.onclick);
td.appendChild(button);
tr.appendChild(td);
table.appendChild(tr);

table.hidden=false;
numberOfSourses++;
}

var remember=function(name){
var click=document.getElementByName("result");
click.value=name;
}

/*var sendData=function(){
	//var formData = new FormData(document.forms.parameters);
	var formData = new FormData();
	 formData.append("patron", "Vsiliy");
	 formData.append("sasha", "Qwerty");	  
	  var xhr = new XMLHttpRequest();
	  xhr.open("POST", "setparameters");
	  xhr.send(formData);
	}
*/

