
firstClickInput=true;

function clearInput(obj){
	if(firstClickInput){
		obj.value="";
		firstClickInput=false;
	}
}

function checkForm(id,element){
	if(firstClickInput){
		alert("Bad value");
		return;
	}
	form = document.getElementByTagName(id);
	form.getElement(element);
	if(element.value==""){
		alert("Bad value");
		return;
	}
}