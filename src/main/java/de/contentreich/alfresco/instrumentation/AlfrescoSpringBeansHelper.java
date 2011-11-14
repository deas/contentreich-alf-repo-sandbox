/*
 * deas - http://www.contentreich.de
 *
 * Created on Nov 12, 2011
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
package de.contentreich.alfresco.instrumentation;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import de.contentreich.instrumentation.SpringBeansHelper;

public class AlfrescoSpringBeansHelper extends SpringBeansHelper {
	private static final Logger logger = Logger
			.getLogger(AlfrescoSpringBeansHelper.class);
	private static final String ALFRESCO_CTX_FACTORY = "org.alfresco.repo.management.subsystems.ApplicationContextFactory";
	private Class clazz = null;
	private Method method = null;

	public AlfrescoSpringBeansHelper() {
		try {
			clazz = Class.forName(ALFRESCO_CTX_FACTORY);
			method = clazz.getMethod("getApplicationContext", null);
		} catch (ClassNotFoundException e) {
			logger.info("Class " + ALFRESCO_CTX_FACTORY + " not found");
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationContext getApplicationContext(Object object) {
		ApplicationContext context = null;
		if (holdsChildApplicationContext(object)) {
			try {
				context = (ApplicationContext) method.invoke(object, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return context;
	}

	public boolean holdsChildApplicationContext(Object object) {
		boolean holds = false;
		if (clazz != null) {
			holds = clazz.isAssignableFrom(object.getClass());
		}
		return holds;
	}
}
