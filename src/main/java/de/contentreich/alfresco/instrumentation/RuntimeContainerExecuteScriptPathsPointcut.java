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
package de.contentreich.alfresco.instrumentation;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class RuntimeContainerExecuteScriptPathsPointcut extends DynamicMethodMatcherPointcut {
	private static final String METHOD_NAME = "executeScript";
	private static final Logger logger = Logger.getLogger(RuntimeContainerExecuteScriptPathsPointcut.class);
	
	List<String> paths;
	/*
	public interface RuntimeContainer extends Container
	    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
	        throws IOException;
   */

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
 		boolean matches = false;
		String path = null;
		if (method.getName().equals(METHOD_NAME)) {
			if (paths != null) {
				path = ((WebScriptRequest) args[0]).getServiceMatch().getPath();
				matches = paths.contains(path);
			}
			logger.debug("Path = " + path + ", matches = " + matches);
		}
		return matches;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	
}
