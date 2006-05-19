/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;

/**
 * @author Ozgur Demirtas
 * generates JFreeCharts 
 */
public class VoteChartGenerator extends HttpServlet implements VoteAppConstants {
    static Logger logger = Logger.getLogger(VoteChartGenerator.class.getName());
    
    public VoteChartGenerator(){
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("dispatching doGet for VoteChartGenerator...");
        
        logger.debug("test6: MAP_STANDARD_NOMINATIONS_CONTENT: " + request.getSession().getAttribute(MAP_STANDARD_NOMINATIONS_CONTENT));
		logger.debug("test6: MAP_STANDARD_RATES_CONTENT: " + request.getSession().getAttribute(MAP_STANDARD_RATES_CONTENT));
        OutputStream out= response.getOutputStream(); 
        
        try{
            String type=request.getParameter("type");
            logger.debug("type: " + type);
            JFreeChart chart=null;
            
            logger.debug("creating pie chart" + type);
            chart=createChart(request, type);
            
            logger.debug("chart:" + chart);
            if (chart != null)
            {
                response.setContentType("image/png");
                ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
            }
                    
        }
        catch(Exception e)
        {
            logger.error("error occurred generating chart: " + e);
        }
        finally
        {
            out.close();
        }
    }
    
    
    private JFreeChart createChart(HttpServletRequest request, String type)
    {
        logger.debug("chartType: " + type);
        
    	if (type.equals("pie"))
    	{
    	    return createPieChart (request, type);    
    	}
    	else if (type.equals("bar"))
    	{
    	    return createBarChart (request, type);    
    	}
    	return null;
    }

    private JFreeChart createPieChart(HttpServletRequest request, String chartType)
    {
        logger.debug("chartType: " + chartType);
        
        logger.debug("starting createPieChart...");
        DefaultPieDataset data= new DefaultPieDataset();
        
        Map mapNominationsContent=(Map)request.getSession().getAttribute(MAP_STANDARD_NOMINATIONS_CONTENT);
        logger.error("mapNominationsContent: " + mapNominationsContent);
        
        Map mapVoteRatesContent=(Map)request.getSession().getAttribute(MAP_STANDARD_RATES_CONTENT);
        logger.error("mapVoteRatesContent: " + mapVoteRatesContent);

        Iterator itMap = mapNominationsContent.entrySet().iterator();
    	while (itMap.hasNext()) 
    	{
        	Map.Entry pairs = (Map.Entry)itMap.next();
            logger.debug("using the  nomination content pair: " +  pairs.getKey() + " = " + pairs.getValue());
            
            String voteRate=(String) mapVoteRatesContent.get(pairs.getKey());
            logger.debug("voteRate:" + voteRate);
            data.setValue(pairs.getValue().toString(), new Double(voteRate));
		}
        
    	JFreeChart chart=null;
   	    chart=ChartFactory.createPieChart3D("Session Votes Chart", data, true, true, false);
   	    logger.debug("chart: " + chart) ;

   	    return chart;
    }
    
    private JFreeChart createBarChart(HttpServletRequest request, String chartType)
    {
        logger.debug("chartType: " + chartType);
        
        logger.debug("starting createBarChart...");
        DefaultCategoryDataset data= new DefaultCategoryDataset();
        
        Map mapNominationsContent=(Map)request.getSession().getAttribute(MAP_STANDARD_NOMINATIONS_CONTENT);
        logger.error("mapNominationsContent: " + mapNominationsContent);
        
        Map mapVoteRatesContent=(Map)request.getSession().getAttribute(MAP_STANDARD_RATES_CONTENT);
        logger.error("mapVoteRatesContent: " + mapVoteRatesContent);

        Iterator itMap = mapNominationsContent.entrySet().iterator();
    	while (itMap.hasNext()) 
    	{
        	Map.Entry pairs = (Map.Entry)itMap.next();
            logger.debug("using the  nomination content pair: " +  pairs.getKey() + " = " + pairs.getValue());
            
            String voteRate=(String) mapVoteRatesContent.get(pairs.getKey());
            logger.debug("voteRate:" + voteRate);
            data.setValue(new Double(voteRate), pairs.getValue().toString(), pairs.getValue().toString());
		}
        
    	JFreeChart chart=null;
   	    chart=ChartFactory.createBarChart3D("Session Votes Chart", "Open Vote", "Percentage", 
   	    									data, PlotOrientation.VERTICAL, true, true, false);
   	    logger.debug("chart: " + chart) ;
   	    return chart;
    }
    
}
