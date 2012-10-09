/*
solr.backup.alfresco.cronExpression=0 0 2 * * ?
solr.backup.archive.cronExpression=0 0 4 * * ?
solr.backup.alfresco.remoteBackupLocation=
solr.backup.archive.remoteBackupLocation=

// http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
solr.backup.alfresco.cronExpression=0 0 0 * * ? 2050
solr.backup.archive.cronExpression=0 0 0 * * ? 2050

lucene

index.backup.cronExpression=0 0 0 * * ? 2050
*/

var SOLR_INDICES = [ "alfresco", "archive" ];
var LUCENE_COMPONENT = "search.luceneIndexBackupComponent";
var LUCENE_BACKUP_DIR = "backup-lucene-indexes";

function backupSolrIndex(targetRootDir) {
	logger.log("Executing solr backup");
	for (var i=0; i<SOLR_INDICES.length; i++) {
		var loc = targetRootDir + "/" + SOLR_INDICES[i];
		var client = searchCtx.getBean("search." + SOLR_INDICES[i] + "CoreSolrBackupClient");
		logger.log("Executing backup for core " + SOLR_INDICES[i] + " to " + loc);
		client.remoteBackupLocation = targetRootDir + "/" + SOLR_INDICES[i];// default is empty 
		client.execute();
	}
}

function backupLuceneIndex(targetRootDir) {
	var loc = targetRootDir + "/" + LUCENE_BACKUP_DIR;
	logger.log("Executing lucene backup to " + loc);
	var component = searchCtx.getBean(LUCENE_COMPONENT);
	component.targetLocation = loc;
	component.backup();
}

function isLucene(searchCtx) {
	var is = false;	
	try {
		searchCtx.getBean(LUCENE_COMPONENT);
		is = true;
	} catch (ex) {}
	return is;
}

var searchCtx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getBean("Search").getApplicationContext();

var targetDirectory = json.get("targetDirectory");

if (targetDirectory) {
	if (isLucene(searchCtx)) {
		backupLuceneIndex(targetDirectory);
	} else {
		backupSolrIndex(targetDirectory);
	}	
} else {
	status.code = 404;
	status.message = "Missing parameter " + targetDirectory;
	status.redirect = true;
	
}
