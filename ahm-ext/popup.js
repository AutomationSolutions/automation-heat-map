$(document).ready(function() {
  var radios = document.getElementsByName("selectGroup");
  var val = localStorage.getItem('selectGroup');
  for (var i = 0; i < radios.length; i++) {
    if (radios[i].value == val) {
      radios[i].checked = true;
    }
  }
  $('input[name="selectGroup"]').on('change', function() {
    localStorage.setItem('selectGroup', $(this).val());
  });
});

function onAhmRadioClick() {
	var classList = {
		allClasses: ahmShortNames,
		activeClass: this.value		
};
	
  chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
    chrome.tabs.sendMessage(tabs[0].id, {data: classList}, function(response) {
    });
  });
}

function createDOMElement(name) { 

	var labelElement = document.createElement("label");
    var radioElement = document.createElement("input");
    var divElement = document.createElement("div");

    divElement.id = name + "-div";
	
	if(name === "None" || name==="All"){
		labelElement.innerHTML = name;
		if(name ==="None") {
			radioElement.checked = "checked";
		}
 	} else {
		labelElement.innerHTML = name.substring(3, name.length);
	}
	
    labelElement.innerHTML = name;
	labelElement.id = name + "-lbl";
	labelElement.style = "font-size: medium; font-family: 'Comic Sans MS', cursive, sans-serif;";
	
    radioElement.id = name;
	radioElement.name = "selectGroup";
    radioElement.type = "radio";
	radioElement.value = name;
	
    document.getElementById("radio-container").appendChild(divElement);
    document.getElementById(name + "-div").appendChild(radioElement);
    document.getElementById(name + "-div").appendChild(labelElement);
	
	document.getElementById(name).addEventListener('click', onAhmRadioClick);
}

setTimeout(function() {
  for (var i = 0; i < ahmShortNames.length; i++) {
	  if(i===0) {
		createDOMElement("None");  
	  }
	  createDOMElement(ahmShortNames[i]);
	  
	  if(i===ahmShortNames.length-1) {
		createDOMElement("All");
	  }
  }
}, 0);