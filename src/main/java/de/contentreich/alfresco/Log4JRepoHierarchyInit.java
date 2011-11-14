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

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class Log4JRepoHierarchyInit extends org.alfresco.repo.admin.Log4JHierarchyInit {
    private static Log logger = LogFactory.getLog(Log4JRepoHierarchyInit.class);
    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private Log4JInit log4jInit = new Log4JInit();
    private List<String> extraLog4jXmlUrls = Collections.EMPTY_LIST;
	
	@Override
	public void init() {
		super.init();
		try {
			log4jInit.init(extraLog4jXmlUrls, resolver);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setExtraLog4jXmlUrls(List<String> log4jXmlUrls) {
		this.extraLog4jXmlUrls = log4jXmlUrls;
	}
}
