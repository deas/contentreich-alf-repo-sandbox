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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.perf4j.chart.StatisticsChartGenerator;
import org.perf4j.log4j.GraphingStatisticsAppender;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class Perf4JGraphWebScript extends DeclarativeWebScript {
	private static final Logger logger = Logger.getLogger(Perf4JGraphWebScript.class);
	protected StatisticsChartGenerator getGraphByName(String name) {
        GraphingStatisticsAppender appender = GraphingStatisticsAppender.getAppenderByName(name);
        return (appender == null) ? null : appender.getChartGenerator();
    }

    protected List<String> getAllKnownGraphNames() {
        List<String> retVal = new ArrayList<String>();
        for (GraphingStatisticsAppender appender : GraphingStatisticsAppender.getAllGraphingStatisticsAppenders()) {
            retVal.add(appender.getName());
        }
        return retVal;
    }

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest request, Status status) {
        String graphName = request.getServiceMatch().getTemplateVars().get("graph_name");
        logger.debug("executeImpl - graphName = " + graphName);
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, StatisticsChartGenerator> chartsByName = getChartGeneratorsToDisplay();
        if (graphName != null) {
            model.put("graph", new Object[] { graphName, chartsByName.get(graphName) });;
        } 
        model.put("graph_names", chartsByName.keySet());
        return model;
    }


    protected Map<String, StatisticsChartGenerator> getChartGeneratorsToDisplay() {
        List<String> graphsToDisplay = getAllKnownGraphNames();
        Map<String, StatisticsChartGenerator> retVal = new LinkedHashMap<String, StatisticsChartGenerator>();
        for (String graphName : graphsToDisplay) {
            retVal.put(graphName, getGraphByName(graphName));
        }
        return retVal;
    }

}
