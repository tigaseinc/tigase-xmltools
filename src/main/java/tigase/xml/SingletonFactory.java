/*
 * Tigase XML Tools - Tigase XML Tools
 * Copyright (C) 2004 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.xml;

/**
 * <code>SingletonFactory</code> provides a way to use only one instance of <code>SimpleParser</code> in all your code.
 * Since <code>SimpleParser</code> if fully thread safe implementation there is no sense to use multiple instances of
 * this class. This in particular useful when processing a lot of network connections sending <em>XML</em> streams and
 * using one instance for all connections can save some resources.<br> Of course it is still possible to create as many
 * instances of <code>SimpleParser</code> you like in normal way using public constructor. <p> Created: Sat Oct  2
 * 22:12:21 2004 </p>
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */

public class SingletonFactory {

	private static SimpleParser parser = null;

	public static SimpleParser getParserInstance() {
		if (parser == null) {
			parser = new SimpleParser();
		}
		return parser;
	}

}
