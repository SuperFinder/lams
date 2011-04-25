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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
/* $$Id$$ */
package org.lamsfoundation.lams.web.tag;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * Output the base part of the current web app url (e.g. http://server/lams/tool/nb11/)
 * based on the current servlet details. 
 * 
 * @jsp.tag name="WebAppURL"
 * 			body-content="empty"
 * 			display-name="Base URL for the current web app"
 * 			description="Output the basic URL for the current webapp. e.g. http://server/lams/tool/nb11/"
 * 	
 * @author Fiona Malikoff
 */
public class WebAppURLTag extends TagSupport {

	private static final long serialVersionUID = -3773379475085729642L;

	private static final Logger log = Logger.getLogger(WebAppURLTag.class);

	/**
	 * 
	 */
	public WebAppURLTag() {
		super();
	}
	
	public int doStartTag() throws JspException {
		ServletRequest sr = pageContext.getRequest();
		if ( HttpServletRequest.class.isInstance(sr) ) {
			HttpServletRequest request = (HttpServletRequest) sr;
			
			String protocol;
			if(request.isSecure()){
				protocol = "https://";
			}else{
				protocol = "http://";
			}
			
			String path = protocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
			if ( ! path.endsWith("/") )
					path = path + "/";

   			try {
   				JspWriter writer = pageContext.getOut();
   				writer.print(path);
   			} catch ( IOException e ) {
   				log.error("ServerURLTag unable to write out server URL due to IOException. ", e);
   				throw new JspException(e);
   			}

   		} else {
	   		log.warn("ServerURLTag unable to write out server URL as the servlet request is not an HttpServletRequest.");
   		}
    	return SKIP_BODY;	}

	public int doEndTag() {
		return EVAL_PAGE;
	}
	
}
