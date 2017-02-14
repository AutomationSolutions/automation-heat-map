chrome.runtime.onMessage.addListener( function(request, sender, sendResponse) {
    var data = request.data;
	for(var i=0; i<data.allClasses.length; i++) {
		document.body.classList.remove("ahm-" + data.allClasses[i]);
		document.body.classList.remove("ahm-" + "All");
	}
	if(data.activeClass!=="None" && data.activeClass!=="All") {
		document.body.classList.add("ahm-" + data.activeClass);
	}
	
	if(data.activeClass==="All") {
		document.body.classList.add("ahm-All");
	}
    sendResponse({data: data, success: true});
});