// logger.log("xxx");

function appendNode(folder, depth, maxdepth)
{
	logger.log("Processing " + folder.name + ", " + folder.nodeRef.id + ", depth = " + depth + ", maxdepth = " + maxdepth);
	var folderSum = 0;
	var contentSum = 0;
	var childFolders = folder.childFileFolders(false, true);
	var children = [];
	
	for (var i=0; i < childFolders.length; i++) {
		var child = appendNode(childFolders[i], depth + 1, maxdepth);
		if (depth <= maxdepth) {
			children.push(child);
		}
		folderSum += child.folderSum; 
		folderSum += child.contentSum; 
	}

	var childFiles = folder.childFileFolders(true, false);
	
	for (var i=0; i < childFiles.length; i++) {
		contentSum += childFiles[i].size;
	}
	logger.log("Done " + folder.name + ", " + folder.nodeRef.id);
	return {
			id:folder.nodeRef.id,
			depth: depth,
			name:folder.name,
			children:children,
			folderSum : folderSum,
			contentSum : contentSum
	};
}

var maxdepth = args['maxdepth'] ? args['maxdepth'] : 20;
var nodeRef = "workspace://SpacesStore/" + url.extension;
var node = search.findNode(nodeRef);

logger.log("Create treemap for node " + nodeRef + ", maxdepth = " + maxdepth);
// status.setCode(status.STATUS_BAD_REQUEST, "Invalid");
model.node = appendNode(node, 1, maxdepth);

// return tree;
// vom letzten Node parentId, parentName null setzen - s. google doku
/*
for (var j=0; j<results.length; j++) {
	print(results[j].parent + " " + results[j].node + " " + results[j].size);
}
*/
// return results;