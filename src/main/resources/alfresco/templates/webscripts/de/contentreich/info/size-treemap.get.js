// logger.log("xxx");
function appendNode(folder)
{
	logger.log("Processing " + folder.name + ", " + folder.nodeRef.id);
	var folderSum = 0;
	var contentSum = 0;
	var childFolders = folder.childFileFolders(false, true);
	var children = [];
	
	for (var i=0; i < childFolders.length; i++) {
		var child = appendNode(childFolders[i]);
		children.push(child);
		folderSum += child.folderSum; 
		folderSum += child.contentSum; 
	}

	var childFiles = folder.childFileFolders(true, false);
	
	for (var i=0; i < childFiles.length; i++) {
		contentSum += childFiles[i].size;
	}
	// Special child
	/*
	children.push({
		sum: contentSum,
		name: "",
		id:"",
		children:[]
	});
	*/
	logger.log("Done " + folder.name + ", " + folder.nodeRef.id);
	return {
			id:folder.nodeRef.id,
			name:folder.name,
			children:children,
			folderSum : folderSum,
			contentSum : contentSum
	};
}

// var node = space;
var node = search.findNode("workspace://SpacesStore/" + url.extension);
// status.setCode(status.STATUS_BAD_REQUEST, "Invalid");
model.node = appendNode(node);

// return tree;
// vom letzten Node parentId, parentName null setzen - s. google doku
/*
for (var j=0; j<results.length; j++) {
	print(results[j].parent + " " + results[j].node + " " + results[j].size);
}
*/
// return results;