function verifFormLog(form){
	var login = form.elements[0].value;
	var pass = form.elements[1].value;
	if(login=="" || pass==""){
		alert("Veillez remplir les champs.");
		return false;
	}
	return true;
}

function verifFormAccount(form){
	var partnerID = form.elements[0].value;
	var serviceType = form.elements[1].value;
	var serviceAddress = form.elements[2].value;
	var login = form.elements[3].value;
	var pass = form.elements[4].value;
	var passwdrepete = form.elements[5].value;
	
	if(partnerID=="" || partnerID.length<6){
		alert("Partner ID must contains six characters !");
		return false;
	}
	if(serviceType==""){
		alert("specify service type !");
		return false;
	}
	if(serviceAddress==""){
		alert("specify service address !");
		return false;
	}
	if(login=="" || login.length<6){
		alert("login must contains 6 characters !");
		return false;
	}
	if(pass=="" || pass.length<6){
		alert("password must contains 6 characters !");
		return false;
	}
	if(passwdrepete==""){
		alert("repete your password !");
		return false;
	}
	return true;
}

function verifFormUserModify(form){
	var login = form.elements[0].value;
	var pass = form.elements[1].value;
	var passwdrepete = form.elements[2].value;
	if(login=="" || login.length<6){
		alert("login must contains 6 characters !");
		return false;
	}
	if(pass=="" || pass.length<6){
		alert("password must contains 6 characters !");
		return false;
	}
	if(passwdrepete==""){
		alert("repete your password !");
		return false;
	}
	return true;
}

function verifFormPartnerModify(form){
	var partnerID = form.elements[0].value;
	var serviceType = form.elements[1].value;
	var serviceAddress = form.elements[2].value;
	
	if(partnerID=="" || partnerID.length<6){
		alert("Partner ID must contains six characters !");
		return false;
	}
	if(serviceType==""){
		alert("specify service type !");
		return false;
	}
	if(serviceAddress==""){
		alert("specify service address !");
		return false;
	}
	return true;
}

function checkFormAssos(form){
	 for (var j=0; j<form.getElementsByTagName("select").length; j++ ) {
		liste = form.getElementsByTagName("select")[j];
		for ( var k=0; k<liste.options.length; k++) {
			if ( liste.options[k].selected == true && liste.options[k].value == "--------------" ){
			 	alert("bad entry for user !");
				return false;
			}
		}
	}
	return true;
	
}

