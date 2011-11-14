/*
 * deas - http://www.contentreich.de
 *
 * Created on Nov 4, 2011
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
package de.contentreich.instrumentation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.perf4j.LoggingStopWatch;
import org.perf4j.aop.AbstractJoinPoint;
import org.perf4j.aop.DefaultProfiled;
import org.perf4j.aop.Profiled;
import org.perf4j.log4j.Log4JStopWatch;

/**
 * @author deas
 */
public class ProfileMethodInterceptor implements MethodInterceptor {
	private static final Logger logger = Logger
			.getLogger(ProfileMethodInterceptor.class);

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		// We just delegate to the super class, wrapping the AspectJ-specific
		// ProceedingJoinPoint as an AbstractJoinPoint
		Profiled profiled = DefaultProfiled.INSTANCE;
		return runProfiledMethod(new AbstractJoinPoint() {
			public Object proceed() throws Throwable {
				return invocation.proceed();
			}

			public Object getExecutingObject() {
				return invocation.getThis();
			}

			public Object[] getParameters() {
				return invocation.getArguments();
			}

			public String getMethodName() {
				return invocation.getMethod().getName();
			}
		}, profiled, newStopWatch(profiled.logger() + "", profiled.level()));
	}

	public Object runProfiledMethod(AbstractJoinPoint joinPoint,
			Profiled profiled, LoggingStopWatch stopWatch) throws Throwable {
    	logger.info("Executing " + joinPoint.getExecutingObject().getClass().getName() + "." + joinPoint.getMethodName() +"(...)");
		// if we're not going to end up logging the stopwatch, just run the
		// wrapped method
		if (!stopWatch.isLogging()) {
			return joinPoint.proceed();
		}

		stopWatch.setTimeThreshold(profiled.timeThreshold());
		stopWatch.setNormalAndSlowSuffixesEnabled(profiled
				.normalAndSlowSuffixesEnabled());

		Object retVal = null;
		Throwable exceptionThrown = null;
		try {
			return retVal = joinPoint.proceed();
		} catch (Throwable t) {
			throw exceptionThrown = t;
		} finally {
			String tag = getStopWatchTag(profiled, joinPoint, retVal,
					exceptionThrown);
			String message = getStopWatchMessage(profiled, joinPoint, retVal,
					exceptionThrown);

			if (profiled.logFailuresSeparately()) {
				tag = (exceptionThrown == null) ? tag + ".success" : tag
						+ ".failure";
			}

			stopWatch.stop(tag, message);
		}
	}

	protected String getStopWatchTag(Profiled profiled,
			AbstractJoinPoint joinPoint, Object returnValue,
			Throwable exceptionThrown) {
		String tag;
		if (Profiled.DEFAULT_TAG_NAME.equals(profiled.tag())) {
			// if the tag name is not explicitly set on the Profiled annotation,
			// use the name of the method being annotated.
			tag = joinPoint.getMethodName();
		} else {
			tag = profiled.tag();
		}
		return tag;
	}

	/**
	 * Helper method get the message to use for StopWatch logging. Performs JEXL
	 * evaluation if necessary.
	 * 
	 * @param profiled
	 *            The profiled annotation that was attached to the method.
	 * @param joinPoint
	 *            The AbstractJoinPoint encapulates the method around which this
	 *            aspect advice runs.
	 * @param returnValue
	 *            The value returned from the execution of the profiled method,
	 *            or null if the method returned void or an exception was
	 *            thrown.
	 * @param exceptionThrown
	 *            The exception thrown, if any, by the profiled method. Will be
	 *            null if the method completed normally.
	 * @return The value to use as the StopWatch message.
	 */
	protected String getStopWatchMessage(Profiled profiled,
			AbstractJoinPoint joinPoint, Object returnValue,
			Throwable exceptionThrown) {
		return "".equals(profiled.message()) ? null : profiled.message();
	}

	protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
		return new Log4JStopWatch(loggerName, levelName);
		// return new LoggingStopWatch(loggerName, levelName);
	}
}
