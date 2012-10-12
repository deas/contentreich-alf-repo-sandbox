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
package de.contentreich.instrumentation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractRefreshableApplicationContext;

public abstract class SpringBeansHelper implements ApplicationContextAware {
	private static final Logger logger = Logger
			.getLogger(SpringBeansHelper.class);
	private ApplicationContext context;
	// private ChildApplicationContextHelper childApplicationContextHelper;
	// private DefaultListableBeanFactory beanFactory;
	private static final char UNESCAPED = '.';
	private static final char ESCAPED = '$';

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

	/*
	 * public List<String[]> getSpringBeans() { return
	 * getSpringBeans(this.context, "/"); }
	 */

	public List<String[]> getSpringBeans(ApplicationContext appContext,
			String parentId) {
		logger.debug("Get spring beans for context " + parentId);
		ArrayList<String[]> beans = new ArrayList<String[]>();
		String[] names = appContext.getBeanDefinitionNames();
		for (int i = 0; i < names.length; i++) {
			String beanName = names[i];
			DefaultListableBeanFactory beanFactory = getBeanFactory(appContext);
			Class clazz = getBeanClass(beanName, beanFactory);
			// DefaultListableBeanFactory beanFactory = (appContext instanceof DefaultListableBeanFactory) ? (DefaultListableBeanFactory) appContext : null;
			if (clazz != null && beanFactory != null) { // Not abstract
				BeanDefinition def = beanFactory.getBeanDefinition(beanName);
				if (!(def.isPrototype() || def.isLazyInit())) {
					Object bean = appContext.getBean(beanName);
					if (bean != null){
						if (holdsChildApplicationContext(bean)) {
							// Semantically not perfect but ok ;)
							clazz = getApplicationContext(bean).getClass();
						}
					} else {
						logger.warn("Bean for name " + beanName + " is null - skipping it");
						continue;
					}
				}
				String[] beanEntry = new String[] { beanName, clazz.getName(), parentId };
				beans.add(beanEntry);
			}
		}
		Collections.sort(beans, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				String s1 = ((String[]) o1)[0];
				String s2 = ((String[]) o2)[0];
				return s1.compareTo(s2);
			}

		});
		logger.debug("Got " + beans.size() + " spring beans");
		return beans;
	}

	public String escapeBeanName(String name) {
		return name.replace(UNESCAPED, ESCAPED);
	}

	public String unscapeBeanName(String name) {
		return name.replace(ESCAPED, UNESCAPED);
	}

	public Object getBean(String[] path) {
		Object bean = null;
		ApplicationContext ctx = context;
		if (path == null || path.length == 0) {
			bean = this.context;
		} else {
			for (int i = 0; i < path.length; i++) {
				Object o = ctx.getBean(path[i]);
				if (i + 1 == path.length) {
					bean = o;
				} else if (holdsChildApplicationContext(o)) {
					ctx = getApplicationContext(o);
				}
			}
			if (holdsChildApplicationContext(bean)) { // unwrap
				bean = getApplicationContext(bean);
			}
		}
		return bean;
	}

	private DefaultListableBeanFactory getBeanFactory(ApplicationContext appContext) {
		ConfigurableListableBeanFactory clfb = (appContext instanceof AbstractRefreshableApplicationContext) ? ((AbstractRefreshableApplicationContext) appContext).getBeanFactory() : null;
		return (clfb instanceof DefaultListableBeanFactory) ? (DefaultListableBeanFactory) clfb : null;
	}
	private Class getBeanClass(String beanName, DefaultListableBeanFactory beanFactory) {
		Class clazz = null;
		if (beanFactory != null) {
			BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
			if (!definition.isAbstract()) {
				clazz = beanFactory.getType(beanName);
			}
		}
		return clazz;
	}

	public List<String[]> getPublicMethodSignatures(String className) {
		logger.debug("Get public method signatures for class " + className);
		ArrayList<String[]> methodSignatures = new ArrayList<String[]>();
		try {
			Class clazz = Class.forName(className);
			List<Method> methods = (List<Method>) Arrays.asList(clazz
					.getMethods());
			// Filtering - a pita in java
			for (Iterator<Method> iterator = methods.iterator(); iterator
					.hasNext();) {
				Method method = iterator.next();
				if (!method.getDeclaringClass().getName().startsWith("java")
						&& Modifier.isPublic(method.getModifiers())) {
					methodSignatures.add(new String[] { method.toString(),
							method.getDeclaringClass().getName() });
				}
			}
			Collections.sort(methodSignatures, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					String s1 = ((String[]) o1)[0];
					String s2 = ((String[]) o2)[0];
					return s1.compareTo(s2);
				}

			});
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException(cnfe);
		}
		return methodSignatures;
	}

	public abstract ApplicationContext getApplicationContext(Object bean);
	
	public abstract boolean holdsChildApplicationContext(Object object);
}