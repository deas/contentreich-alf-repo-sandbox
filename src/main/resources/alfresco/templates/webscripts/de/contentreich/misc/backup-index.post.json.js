// WEB-INF/classes/alfresco/subsystems/Search/solr/solr-backup-context.xml
// WEB-INF/classes/alfresco/subsystems/Search/solr/solr-backup.properties
// solr.backup.alfresco.cronExpression
/*
solr.backup.alfresco.cronExpression=0 0 2 * * ?
solr.backup.archive.cronExpression=0 0 4 * * ?
solr.backup.alfresco.remoteBackupLocation=
solr.backup.archive.remoteBackupLocation=

// http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
solr.backup.alfresco.cronExpression=0 0 0 * * ? 2050
solr.backup.archive.cronExpression=0 0 0 * * ? 2050

*/

var location = json.get("location");
var coreClient = json.get("coreClient");
// search.alfrescoCoreSolrBackupClient|search.archiveCoreSolrBackupClient

logger.log("About to execute index backup with core client = " + coreClient + ", location = " + location);
var client = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getBean("Search").getApplicationContext().getBean(coreClient);

if (!client) {
	status.code = 404;
	status.message = "No client for core " + core + " not found";
	status.redirect = true;
} else {
	if (location) {
		client.remoteBackupLocation = location;// default is empty
	}
	logger.log("Executing index backup");
	client.execute();
}
