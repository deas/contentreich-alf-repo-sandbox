<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Contentreich Spring Beans Instrumentation</@wsLib.head>
   <body>
     <div>
            <table>
               <tr>
               	<td <#if bean_class?exists>colspan="2"</#if>>
            		<#if path?size &gt; 0><a href="${url.serviceContext}/contentreich/instrumentation/beans"><strong>Back</strong></a><br/></#if>
               	<#list path as p>
                  <#if p_has_next>
	               	<a href="${url.serviceContext}/contentreich/instrumentation/beans${path_helper.createPath(path, p_index)}"><strong>Back to ${p}</strong></a><br/>
	              </#if>
               	</#list>
               	</td>
               	<#--${url.match}-->
               	</tr>
               <#if bean_class?exists>
                <tr><td colspan="2"><strong>${bean_name} - ${bean_class} <#if !bean_class?starts_with("$")><a target="_blank" href="http://www.google.com/?q=${bean_class}+api">Google for API</a></#if></strong></td></tr>
               	<#list bean_methods as bean_method>
   	            	<tr><td>${bean_method[0]}</td><td><#if !bean_method[1]?starts_with("$")><a target="_blank" href="http://www.google.com/?q=${bean_method[1]}+api">Google for API</a><#else>&nbsp;</#if></td></tr>
               	</#list>
               <#else>
               	<#list beans as bean>
               	<tr>
               	    <#assign l=path?size>
               	    <td><a href="${url.serviceContext}/contentreich/instrumentation/beans${path_helper.createPath(path, l-1)}<#if l &gt; 0>/</#if>${beans_helper.escapeBeanName(bean[0])}">${bean[0]}</a></td>
               		<td>${bean[1]} <#if !bean[1]?starts_with("$")><a target="_blank" href="http://www.google.com/?q=${bean[1]}+api">Google for API</a></#if></td>
               	</tr>
               	</#list>
               </#if>
      </div>
   </body>
</html>