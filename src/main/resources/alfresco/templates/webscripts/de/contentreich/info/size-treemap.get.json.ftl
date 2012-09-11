<#--
https://github.com/mbostock/d3/wiki/Treemap-Layout#wiki-treemap
-->
<#escape x as jsonUtils.encodeJSONString(x)>

<#macro process_entry entry>                                                                                                                                                       
{
"name" : "${entry.name}",
"id" : "${entry.id}",
"folderSum" : ${entry.folderSum?string("#")},
"contentSum" : ${entry.contentSum?string("#")},
<#if entry.children?has_content>
"children" : [
<#list entry.children as child>
<@process_entry entry=child/>
<#if child_has_next>,
</#if>
</#list>
]
<#else>
"children" : []        
</#if>
}
</#macro>
                                                                                                                                                                              
<@process_entry entry=node/>

</#escape>
