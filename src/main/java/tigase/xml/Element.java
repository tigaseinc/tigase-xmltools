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

import tigase.xml.annotations.TODO;

import java.io.FileReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

//import java.util.StringTokenizer;

/**
 * <code>Element</code> - basic document tree node implementation. Supports Java 5.0 generic feature to make it easier
 * to extend this class and still preserve some useful functionality. Sufficient for simple cases but probably in the
 * most more advanced cases should be extended with additional features. Look in API documentation for more details and
 * information about existing extensions. The most important features apart from abvious tree implementation are: <ul>
 * <li><code>toString()</code> implementation so it can generate valid <em>XML</em> content from this element and all
 * children.</li> <li><code>addChild(...)</code>, <code>getChild(childName)</code> supporting generic types.</li>
 * <li><code>findChild(childPath)</code> finding child in subtree by given path to element.</li>
 * <li><code>getChildCData(childPath)</code>, <code>getAttribute(childPath, attName)</code> returning element CData from
 * child in subtree by given path to element.</li> </ul> <p> Created: Mon Oct 4 17:55:16 2004 </p>
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
@TODO(note = "Make it a bit lighter.")
public class Element
		implements XMLNodeIfc<Element> {

	protected XMLIdentityHashMap<String, String> attributes = null;

	protected LinkedList<XMLNodeIfc> children = null;

	// protected String cdata = null;

	protected String defxmlns = null;

	protected String name = null;

	protected String xmlns = null;

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("You must give file name as parameter.");
			System.exit(1);
		}

		FileReader file = new FileReader(args[0]);
		char[] buff = new char[1];
		SimpleParser parser = new SimpleParser();
		DomBuilderHandler dom = new DomBuilderHandler();
		int result = -1;

		while ((result = file.read(buff)) != -1) {
			parser.parse(dom, buff, 0, result);
		}
		file.close();

		Queue<Element> elems = dom.getParsedElements();

		for (Element elem : elems) {
			elem.clone();
			System.out.println(elem.toString());
		}
	}

	public Element(Element element) {
		Element src = element.clone();

		this.attributes = src.attributes;
		this.name = src.name;

		// this.cdata = src.cdata;
		this.defxmlns = src.defxmlns;
		this.xmlns = src.xmlns;
		this.children = src.children;
	}

	public Element(String argName) {
		setName(argName);
	}

	public Element(String argName, String argCData) {
		setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
	}

	public Element(String argName, String[] att_names, String[] att_values) {
		setName(argName);
		if (att_names != null) {
			setAttributes(att_names, att_values);
		}
	}

	public Element(String argName, Element[] children, String[] att_names, String[] att_values) {
		setName(argName);
		if (att_names != null) {
			setAttributes(att_names, att_values);
		}
		addChildren(Arrays.asList(children));
	}

	public Element(String argName, String argCData, String[] att_names, String[] att_values) {
		setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
		if (att_names != null) {
			setAttributes(att_names, att_values);
		}
	}

	public Element(String argName, String argCData, StringBuilder[] att_names, StringBuilder[] att_values) {
		setName(argName);
		if (argCData != null) {
			setCData(argCData);
		}
		if (att_names != null) {
			setAttributes(att_names, att_values);
		}
	}

	public void addAttribute(String attName, String attValue) {
		setAttribute(attName, attValue);
	}

	public void addAttributes(Map<String, String> attrs) {
		if (attributes == null) {
			attributes = new XMLIdentityHashMap<String, String>(attrs.size());
		}
		for (Map.Entry<String, String> entry : attrs.entrySet()) {
			attributes.put(entry.getKey().intern(), entry.getValue());
		}
	}

	public void addCData(String argCData) {
		addChild(new CData(argCData));
	}

	public void addChild(XMLNodeIfc child) {
		if (child == null) {
			throw new NullPointerException("Element child can not be null.");
		}
		if (children == null) {
			children = new LinkedList<XMLNodeIfc>();
		}
		children.add(child);

		// Collections.sort(children);
	}

	public void addChildren(List<Element> children) {
		if (children == null) {
			return;
		}
		if (this.children == null) {
			this.children = new LinkedList<XMLNodeIfc>();
		}
		for (XMLNodeIfc child : children) {
			this.children.add(child.clone());
		}

		// this.children.addAll(children);
		// Collections.sort(children);
	}

	public String childrenToString() {
		StringBuilder result = new StringBuilder();
		childrenToString(result);

		return (result.length() > 0) ? result.toString() : null;
	}

	public void childrenToString(StringBuilder result) {
		if (children != null) {
			for (XMLNodeIfc child : children) {

				// This is weird but if there is a bug in some other component
				// it may add null children to the element, let's be save here.
				if (child != null) {
					if (child instanceof Element) {
						((Element) child).toString(result);
					} else {
						result.append(child.toString());
					}
				}
			}
		}
	}

	public String childrenToStringPretty() {
		StringBuilder result = new StringBuilder();

		if (children != null) {
			for (XMLNodeIfc child : children) {

				// This is weird but if there is a bug in some other component
				// it may add null children to the element, let's be save here.
				if (child != null) {
					result.append(child.toStringPretty());
				}
			}
		}

		return (result.length() > 0) ? result.toString() : null;
	}

	public String childrenToStringSecure() {
		StringBuilder result = new StringBuilder();
		childrenToStringSecure(result);

		return (result.length() > 0) ? result.toString() : null;
	}

	public void childrenToStringSecure(StringBuilder result) {
		if (children != null) {
			for (XMLNodeIfc child : children) {

				// This is weird but if there is a bug in some other component
				// it may add null children to the element, let's be save here.
				if (child != null) {
					if (child instanceof Element) {
						((Element) child).toStringSecure(result);
					} else {
						result.append(child.toStringSecure());
					}
				}
			}
		}
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public Element clone() {
		Element result = null;

		try {
			result = (Element) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		if (attributes != null) {
			result.attributes = (XMLIdentityHashMap<String, String>) attributes.clone();
		} else {
			result.attributes = null;
		}
		if (children != null) {
			result.setChildren(children);
		} else {
			result.children = null;
		}

		return result;
	}

	@Override
	public int compareTo(Element elem) {
		return toStringNoChildren().compareTo(elem.toStringNoChildren());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Element) {
			Element elem = (Element) obj;

			return toStringNoChildren().equals(elem.toStringNoChildren());
		}

		return false;
	}

	public Element findChildStaticStr(String[] elementPath) {
		if (elementPath[0] != getName()) {
			return null;
		}

		Element child = this;

		// we must start with 1 not 0 as 0 is name of parent element
		for (int i = 1; (i < elementPath.length) && (child != null); i++) {
			String str = elementPath[i];

			child = child.getChildStaticStr(str);
		}

		return child;
	}

	public Element findChild(String[] elemPath) {
		if (elemPath[0].isEmpty()) {
			elemPath = Arrays.copyOfRange(elemPath, 1, elemPath.length);
		}
		if (!elemPath[0].equals(getName())) {
			return null;
		}

		Element child = this;

		// we must start with 1 not 0 as 0 is name of parent element
		for (int i = 1; (i < elemPath.length) && (child != null); i++) {
			String str = elemPath[i];

			child = child.getChild(str);
		}

		return child;
	}

	/**
	 * @deprecated use {@link #findChild(java.lang.String[])} instead.
	 */
	@Deprecated
	public Element findChild(String elementPath) {

		// For performance reasons, replace StringTokenizer with split
		return findChild(elementPath.split("/"));
	}

	public Element findChild(Matcher<Element> matcher) {
		if (children != null) {
			for (XMLNodeIfc node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				if (matcher.match(el)) {
					return el;
				}
			}
		}

		return null;
	}

	public List<Element> findChildren(Matcher<Element> matcher) {
		if (children != null) {
			LinkedList<Element> result = new LinkedList<Element>();

			for (XMLNodeIfc node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				if (matcher.match(el)) {
					result.add(el);
				}
			}

			return result;
		}

		return null;
	}

	public <R> List<R> flatMapChildren(Function<Element, Collection<? extends R>> mapper) {
		if (children != null) {
			LinkedList<R> result = new LinkedList<R>();

			for (XMLNodeIfc node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				result.addAll(mapper.apply(el));
			}

			return result;
		}

		return null;
	}

	public void forEachChild(Consumer<Element> consumer) {
		if (children != null) {
			for (XMLNodeIfc node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				consumer.accept(el);
			}
		}
	}

	/**
	 * @deprecated use {@link #getAttributeStaticStr(java.lang.String) } instead.
	 */
	@Deprecated
	public String getAttribute(String attName) {
		if (attributes != null) {
			return attributes.get(attName.intern());
		}

		return null;
	}

	/**
	 * @deprecated use {@link #getChildAttributeStaticStr(java.lang.String, java.lang.String) } instead.
	 */
	@Deprecated
	public String getChildAttribute(String childName, String attName) {
		String result = null;
		Element child = getChild(childName);

		if (child != null) {
			result = child.getAttribute(attName);
		}

		return result;
	}

	public String getChildAttributeStaticStr(String childName, String attName) {
		String result = null;
		Element child = getChild(childName);

		if (child != null) {
			result = child.getAttributeStaticStr(attName);
		}

		return result;
	}

	public String getAttributeStaticStr(String attName) {
		if (attributes != null) {
			return attributes.get(attName);
		}

		return null;
	}

	/**
	 * @deprecated use  {@link #getAttributeStaticStr(java.lang.String[], java.lang.String) } instead.
	 */
	@Deprecated
	public String getAttribute(String elementPath, String att_name) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getAttribute(att_name) : null;
	}

	/**
	 * @deprecated {@link #getAttributeStaticStr(java.lang.String[], java.lang.String) } instead.
	 */
	@Deprecated
	public String getAttribute(String[] elementPath, String att_name) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getAttribute(att_name) : null;
	}

	public String getAttributeStaticStr(String[] elementPath, String att_name) {
		Element child = findChildStaticStr(elementPath);

		return (child != null) ? child.getAttributeStaticStr(att_name) : null;
	}

	public Map<String, String> getAttributes() {
		return ((attributes != null) ? new LinkedHashMap<String, String>(attributes) : null);
	}

	public void setAttributes(Map<String, String> newAttributes) {
		attributes = new XMLIdentityHashMap<String, String>(newAttributes.size());
		for (Map.Entry<String, String> entry : newAttributes.entrySet()) {
			setAttribute(entry.getKey(), entry.getValue());

			// attributes.put(entry.getKey().intern(), entry.getValue());
		}
	}

	/**
	 * @deprecated use {@link #getCData(java.lang.String[]) } instead.
	 */
	@Deprecated
	public String getCData(String elementPath) {
		return getChildCData(elementPath);
	}

	public String getCData(String[] elementPath) {
		return getChildCData(elementPath);
	}

	public String getCDataStaticStr(String[] elementPath) {
		return getChildCDataStaticStr(elementPath);
	}

	public String getCData() {
		return cdataToString();
	}

	public void setCData(String argCData) {

		if (children != null) {
			for (XMLNodeIfc child : children) {

				// This is weird but if there is a bug in some other component
				// it may add null children to the element, let's be save here.
				if ((child != null) && (child instanceof CData)) {
					((CData) child).setCdata(argCData);
					return;
				}
			}
		}

		addChild(new CData(argCData));
	}

	public Element getChild(String name) {
		if (children != null) {
			for (XMLNodeIfc el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;

					if (elem.getName().equals(name)) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public Element getChildStaticStr(String name) {
		if (children != null) {
			for (XMLNodeIfc el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;

					if (elem.getName() == name) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public Element getChild(String name, String child_xmlns) {
		if (child_xmlns == null) {
			return getChild(name);
		}
		if (children != null) {
			for (XMLNodeIfc el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;

					if (elem.getName().equals(name) &&
							((elem.getXMLNS() == child_xmlns) || child_xmlns.equals(elem.getXMLNS()))) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public Element getChildStaticStr(String name, String child_xmlns) {
		if (child_xmlns == null) {
			return getChildStaticStr(name);
		}
		if (children != null) {
			for (XMLNodeIfc el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;

					if (elem.getName() == name && elem.getXMLNS() == child_xmlns) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	/**
	 * @deprecated use {@link #getCData(java.lang.String[]) } instead.
	 */
	@Deprecated
	public String getChildCData(String elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getCData() : null;
	}

	public String getChildCData(String[] elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getCData() : null;
	}

	public String getChildCDataStaticStr(String[] elementPath) {
		Element child = findChildStaticStr(elementPath);

		return (child != null) ? child.getCData() : null;
	}

	public String getChildCData(Matcher<Element> matcher) {
		Element child = findChild(matcher);

		return (child != null) ? child.getCData() : null;
	}

	public List<Element> getChildren() {
		if (children != null) {
			LinkedList<Element> result = new LinkedList<Element>();

			for (XMLNodeIfc node : children) {
				if (node instanceof Element) {
					result.add((Element) node);
				}
			}

			return result;
		}

		return null;
	}

	public void setChildren(List<XMLNodeIfc> children) {
		this.children = new LinkedList<XMLNodeIfc>();
		for (XMLNodeIfc child : children) {
			this.children.add(child.clone());
		}

		// Collections.sort(children);
	}

	/**
	 * @deprecated use {@link #getChildren(java.lang.String[]) } instead.
	 */
	@Deprecated
	public List<Element> getChildren(String elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getChildren() : null;
	}

	public List<Element> getChildren(String[] elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getChildren() : null;
	}

	public List<Element> getChildrenStaticStr(String[] elementPath) {
		Element child = findChildStaticStr(elementPath);

		return (child != null) ? child.getChildren() : null;
	}

	public List<Element> getChildren(Matcher<Element> matcher) {
		Element child = findChild(matcher);

		return (child != null) ? child.getChildren() : null;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String argName) {
		this.name = argName.intern();
	}

	public String getXMLNS() {
		if (xmlns == null) {
			xmlns = getAttributeStaticStr("xmlns");
			xmlns = ((xmlns != null) ? xmlns.intern() : null);
		}

		return (xmlns != null) ? xmlns : defxmlns;
	}

	public void setXMLNS(String ns) {
		if (ns == null) {
			xmlns = null;
			removeAttribute("xmlns");
		} else {
			xmlns = ns.intern();
			setAttribute("xmlns", xmlns);
		}
	}

	/**
	 * @deprecated use {@link #getXMLNS(java.lang.String[]) } instead.
	 */
	@Deprecated
	public String getXMLNS(String elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getXMLNS() : null;
	}

	public String getXMLNS(String[] elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getXMLNS() : null;
	}

	public String getXMLNSStaticStr(String[] elementPath) {
		Element child = findChildStaticStr(elementPath);

		return (child != null) ? child.getXMLNS() : null;
	}

	@Override
	public int hashCode() {
		return toStringNoChildren().hashCode();
	}

	public <R> R map(Function<Element, ? extends R> mapper) {
		return mapper.apply(this);
	}

	public <R> List<R> mapChildren(Function<Element, ? extends R> mapper) {
		return mapChildren(null, mapper);
	}

	public <R> List<R> mapChildren(Matcher<Element> matcher, Function<Element, ? extends R> mapper) {
		if (children != null) {
			LinkedList<R> result = new LinkedList<R>();

			for (XMLNodeIfc node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				if (matcher == null || matcher.match(el)) {
					result.add(mapper.apply(el));
				}
			}

			return result;
		}

		return null;
	}

	public boolean matches(Matcher<Element> matcher) {
		return matcher.match(this);
	}

	public void removeAttribute(String key) {
		if (attributes != null) {
			attributes.remove(key.intern());
		}
	}

	public boolean removeChild(Element child) {
		boolean res = false;

		if (children != null) {
			res = children.remove(child);
		}

		return res;
	}

	public void setAttributeStaticStr(String elementPath[], String att_name, String att_value) {
		Element child = findChildStaticStr(elementPath);

		if (child != null) {
			child.setAttribute(att_name, att_value);
		}
	}

	public void setAttribute(String key, String value) {
		if (attributes == null) {
			attributes = new XMLIdentityHashMap<String, String>(5);
		}
		String k = key.intern();
		String v = value;

		if (k == "xmlns") {
			xmlns = value.intern();
			v = xmlns;
		}
		attributes.put(k, v);
	}

	public void setAttributes(StringBuilder[] names, StringBuilder[] values) {
		attributes = new XMLIdentityHashMap<String, String>(names.length);
		for (int i = 0; i < names.length; i++) {
			if (names[i] != null) {
				setAttribute(names[i].toString(), values[i].toString());

				// attributes.put(names[i].toString().intern(), values[i].toString());
			}
		}
	}

	public void setAttributes(String[] names, String[] values) {
		attributes = new XMLIdentityHashMap<String, String>(names.length);
		for (int i = 0; i < names.length; i++) {
			if (names[i] != null) {
				setAttribute(names[i], values[i]);

				// attributes.put(names[i].intern(), values[i]);
			}
		}
	}

	public void setDefXMLNS(String ns) {
		defxmlns = ns.intern();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		toString(result);

		return result.toString();
	}

	public void toString(StringBuilder result) {
		result.append("<").append(name);
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
			}
		}

		if (children != null && !children.isEmpty()) {
			result.append(">");
			childrenToString(result);
			result.append("</").append(name).append(">");
		} else {
			result.append("/>");
		}
	}

	@Override
	public String toStringPretty() {
		StringBuilder result = new StringBuilder();

		result.append("<").append(name);
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
			}
		}

		String childrenStr = childrenToStringPretty();

		if ((childrenStr != null) && (childrenStr.length() > 0)) {
			result.append(">");
			result.append("\n");
			result.append(childrenStr);
			result.append("</").append(name).append(">");
			result.append("\n");
		} else {
			result.append("/>");
			result.append("\n");
		}

		return result.toString();
	}

	public String toStringNoChildren() {
		StringBuilder result = new StringBuilder();

		result.append("<").append(name);
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
			}
		}

		String cdata = cdataToString();

		if (cdata != null) {
			result.append(">");
			if (cdata != null) {
				result.append(cdata);
			}
			result.append("</").append(name).append(">");
		} else {
			result.append("/>");
		}

		return result.toString();
	}

	@Override
	public String toStringSecure() {
		StringBuilder result = new StringBuilder();
		toStringSecure(result);

		return result.toString();
	}

	public void toStringSecure(StringBuilder result) {
		result.append("<").append(name);
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
			}
		}

		if (children != null && !children.isEmpty()) {
			result.append(">");
			childrenToStringSecure(result);
			result.append("</").append(name).append(">");
		} else {
			result.append("/>");
		}
	}

	public Element withAttribute(String attName, String attValue) {
		setAttribute(attName, attValue);
		return this;
	}

	public Element withElement(String name, Consumer<Element> consumer) {
		return withElement(name, null, consumer);
	}

	public Element withElement(String name, String xmlns, String cdata) {
		Element el = new Element(name);
		if (xmlns != null) {
			el.setXMLNS(xmlns);
		}
		if (cdata != null) {
			el.addCData(cdata);
		}
		addChild(el);
		return this;
	}

	public Element withElement(String name, String xmlns, Consumer<Element> consumer) {
		Element el = new Element(name);
		if (xmlns != null) {
			el.setXMLNS(xmlns);
		}
		consumer.accept(el);
		addChild(el);
		return this;
	}

	protected String cdataToString() {
		StringBuilder result = new StringBuilder();

		if (children != null) {
			for (XMLNodeIfc child : children) {

				// This is weird but if there is a bug in some other component
				// it may add null children to the element, let's be save here.
				if ((child != null) && (child instanceof CData)) {
					result.append(child.toString());
				}
			}
		}

		return (result.length() > 0) ? result.toString() : null;
	}

	public static interface Matcher<T> {

		boolean match(T item);

	}

	protected class XMLIdentityHashMap<K, V>
			extends IdentityHashMap<K, V> {

		private static final long serialVersionUID = 1L;

		private XMLIdentityHashMap(int size) {
			super(size);
		}

		@Override
		public V put(K key, V value) {
			if ((key == null) || (value == null)) {
				throw new NullPointerException(
						"Neither attribute key or value can be set to null. Attribute: " + key + ", value: " + value);
			}

			return super.put(key, value);
		}
	}
}

