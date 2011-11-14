/*
 * deas - http://www.contentreich.de
 *
 * Created on Nov 8, 2011
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.contentreich.alfresco;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

public class Log4JInit {
    private static Log logger = LogFactory.getLog(Log4JInit.class);
	private static final String XML_CONFIGURATOR_CLASS = "org.apache.log4j.xml.DOMConfigurator";
	private static final String METHOD_NAME = "configure";

	public void init(List<String> log4jXmlUrls,ResourcePatternResolver resolver) throws SecurityException, ClassNotFoundException {
    	Method method = getMethod(XML_CONFIGURATOR_CLASS);
    	for (Iterator<String> iterator = log4jXmlUrls.iterator(); iterator.hasNext();) {
			String url = iterator.next();
        	importLogSettings(method, url, resolver);
		}
	}
	
	private void importLogSettings(Method method, String springUrl, ResourcePatternResolver resolver)
    {
        Resource[] resources = null;
        try
        {
            resources = resolver.getResources(springUrl);
        }
        catch (Exception e)
        {
            logger.warn("Failed to find additional Logger configuration: " + springUrl);
        }
        for (Resource resource : resources)
        {
            try
            {
                URL url = resource.getURL();
                method.invoke(null, url);
            }
            catch (Throwable e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Failed to add extra Logger configuration: \n" + "   URL:   " + springUrl + "\n" + "   Error: " + e.getMessage(), e);
                }
            }
        }
    }

    private Method getMethod(String className) throws SecurityException, ClassNotFoundException {
    	
        try
        {
            return Class.forName(className).getMethod(METHOD_NAME, URL.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Unable to find method 'configure' on class '" + className + "'");
        }
	}
	
}
