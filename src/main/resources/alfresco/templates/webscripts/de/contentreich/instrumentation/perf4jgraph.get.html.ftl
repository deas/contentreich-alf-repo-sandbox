<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Contentreich Perf4j Graphing</@wsLib.head>
   <body>
     <div>
            <#if graph?exists>
            <h2>${graph[0]}</h2>
            <img src="${graph[1].chartUrl}"/>
            </#if>
            <br/>
            <br>
            <form action="#">
            	<select name="graph" id="graph" onchange="javascript:window.location.href='${url.serviceContext}/contentreich/instrumentation/perf4jgraph/' + document.getElementById('graph').value;">
            		<option value="-">-</option>
            		<#list graph_names as graph_name>  
	                  <option <#if graph?exists && graph[0] == graph_name>selected</#if> value="${graph_name}">${graph_name}</option>
            		</#list>
            	</select>
            </form>
      </div>
   </body>
</html>