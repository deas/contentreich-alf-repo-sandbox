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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.perf4j.aop.AbstractJoinPoint;
import org.perf4j.aop.DefaultProfiled;
import org.perf4j.aop.Profiled;
import org.springframework.extensions.webscripts.WebScriptRequest;

import de.contentreich.instrumentation.ProfileMethodInterceptor;

public class ProfileScriptMethodInterceptor extends ProfileMethodInterceptor {
	private static final Logger logger = Logger
			.getLogger(ProfileMethodInterceptor.class);

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		// We just delegate to the super class, wrapping the AspectJ-specific
		// ProceedingJoinPoint as an AbstractJoinPoint
		// public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
		final WebScriptRequest request = (WebScriptRequest) invocation.getArguments()[0];
		Profiled profiled = DefaultProfiled.INSTANCE;
		return runProfiledMethod(new AbstractJoinPoint() {
			public Object proceed() throws Throwable {
				return invocation.proceed();
			}

			public Object getExecutingObject() {
				return request.getServiceMatch().getWebScript();// invocation.getThis();
			}

			public Object[] getParameters() {
				return request.getParameterNames();//invocation.getArguments();
			}

			public String getMethodName() {
				return request.getServiceMatch().getPath();// invocation.getMethod().getName();
			}
		}, profiled, newStopWatch(profiled.logger() + "", profiled.level()));
	}
}
