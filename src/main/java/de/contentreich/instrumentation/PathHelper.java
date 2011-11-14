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
package de.contentreich.instrumentation;

public class PathHelper {
	public String createPath(String[] path, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		for (int i = 0; i <= level; i++) {
			sb.append(path[i]);
			if (i != 0 && i != level) {
				sb.append("/");
			}
		}
		return sb.toString();
	}
	
	public String[] beanPath(String[] p) { 
		String[] path = null;
		if (p.length > 1) {
			path = new String[p.length - 1];
			System.arraycopy(p, 1, path, 0, path.length);
		}
		return path;
	}

}
