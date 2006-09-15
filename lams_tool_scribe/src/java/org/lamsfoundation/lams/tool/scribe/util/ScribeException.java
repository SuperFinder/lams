/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
 * ****************************************************************
 */
/* $$Id$ */

package org.lamsfoundation.lams.tool.scribe.util;

/**
 * 
 * @author Anthony Sukkar
 *
 */
public class ScribeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5518806968051758859L;

	public ScribeException(String message) {
		super(message);
	}

	public ScribeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScribeException() {
		super();

	}

	public ScribeException(Throwable cause) {
		super(cause);

	}

}
