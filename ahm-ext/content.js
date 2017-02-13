chrome.runtime.onMessage.addListener( function(request, sender, sendResponse) {
    var data = request.data;
	for(var i=0; i<data.allClasses.length; i++) {
		document.body.classList.remove(data.allClasses[i]);
	}
	if(data.activeClass!=="None") {
		document.body.classList.add(data.activeClass);
	}
    sendResponse({data: data, success: true});
});