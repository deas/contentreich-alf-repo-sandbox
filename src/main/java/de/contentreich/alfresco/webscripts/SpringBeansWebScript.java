/*
 * deas - http://www.contentreich.de
 *
 * Created on Nov 7, 2011
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
package de.contentreich.alfresco.webscripts;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import de.contentreich.instrumentation.PathHelper;
import de.contentreich.instrumentation.SpringBeansHelper;

/*
 * Depends 
 */
public class SpringBeansWebScript extends DeclarativeWebScript {
	private static final Logger logger = Logger
			.getLogger(SpringBeansWebScript.class);
	private SpringBeansHelper springBeansHelper;
	private PathHelper pathHelper = new PathHelper();

	public void setSpringBeansHelper(SpringBeansHelper springBeansHelper) {
		this.springBeansHelper = springBeansHelper;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req,
			Status status) {
		String beanPath = req.getServiceMatch().getTemplateVars()
				.get("bean_path");
		String[] path = null;
		logger.debug("executeImpl - bean_path = " + beanPath);
		if (beanPath != null && beanPath != "/") {
			path = springBeansHelper.unscapeBeanName(beanPath).split("/");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		Object bean = springBeansHelper.getBean(path);
		if (bean != null) {
			String beanName = (path != null) ? path[path.length - 1] : "";
			model.put("bean_name", beanName);
			if (bean instanceof ApplicationContext) {
				List<String[]> beanDescs = springBeansHelper.getSpringBeans(
						(ApplicationContext) bean, beanName);
				model.put("beans", beanDescs);
				model.put("beans_helper", springBeansHelper);
			} else {
				// No prototypes and no abstract beans and no application contexts !
				String beanClass = bean.getClass().getName();
				model.put("bean_class", beanClass);
				model.put("bean_methods",
						springBeansHelper.getPublicMethodSignatures(beanClass));
			}
		}
		model.put("path_helper", pathHelper);
		model.put("path", path == null ? Collections.EMPTY_LIST : Arrays.asList(path));
		return model;

	}

}
