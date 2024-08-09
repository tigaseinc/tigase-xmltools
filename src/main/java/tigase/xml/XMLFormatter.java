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

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This is temporary code used for testing purposes only. It is subject to change or remove at any time of server
 * development. It has been created to format <em>XML</em> files to make them easier to read and modify by a human. With
 * current <code>XMLDB</code> implementation however it is not necessary to use this formatter for configuration files
 * and user repositories as they are saved in proper format. <p> Created: Thu Oct 21 14:49:41 2004 </p>
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class XMLFormatter
		implements SimpleHandler {

	private boolean cdataWritten = false;
	private int indent = 0;
	private boolean openedElement = false;
	private PrintStream output = null;
	private Object parserData = null;

	public static void main(final String[] args) throws Exception {

		if (args.length < 1) {
			System.err.println("You must give file name as parameter.");
			System.exit(1);
		}

		FileReader file = new FileReader(args[0]);
		char[] buff = new char[1];
		SimpleParser parser = new SimpleParser();
		XMLFormatter formatter = null;
		if (args.length == 2) {
			formatter = new XMLFormatter(new FileOutputStream(args[1]));
		} else {
			formatter = new XMLFormatter(System.out);
		}
		int result = -1;
		while ((result = file.read(buff)) != -1) {
			parser.parse(formatter, buff, 0, result);
		}
		file.close();
		formatter.outputExtraData("");
		formatter = null;
	}

	public XMLFormatter(OutputStream out) {
		output = new PrintStream(out);
	}

	@Override
	public void error(String errorMessage) {
		System.err.println(errorMessage);
	}

	@Override
	public void startElement(final StringBuilder name, final StringBuilder[] att_names,
							 final StringBuilder[] att_values) {
		if (openedElement) {
			output.println(">");
		} else {
			output.println("");
		}
		for (int idx = 0; idx < indent; idx++) {
			output.print(" ");
		}
		output.print("<" + name);
		if (att_names != null) {
			for (int i = 0; i < att_names.length; i++) {
				if (att_names[i] != null) {
					output.print(" " + att_names[i] + "='" + att_values[i] + "'");
				}
			}
		}
		indent += 2;
		cdataWritten = false;
		openedElement = true;
	}

	@Override
	public void elementCData(final StringBuilder cdata) {
		output.print(">");
		openedElement = false;
		output.print(cdata);
		cdataWritten = true;
	}

	@Override
	public boolean endElement(final StringBuilder name) {
		if (cdataWritten) {
			output.println("");
			for (int idx = 0; idx < (indent - 2); idx++) {
				output.print(" ");
			}
			output.print("</" + name + ">");
		} else {
			output.print("/>");
		}
		indent -= 2;
		cdataWritten = true;
		openedElement = false;
		return true;
	}

	@Override
	public void otherXML(final StringBuilder other) {
		output.println("<" + other + ">");
	}

	@Override
	public void saveParserState(final Object object) {
		parserData = object;
	}

	@Override
	public Object restoreParserState() {
		return parserData;
	}

	public void outputExtraData(String extra) {
		output.println(extra);
	}

}
