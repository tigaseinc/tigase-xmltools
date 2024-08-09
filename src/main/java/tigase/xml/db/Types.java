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
package tigase.xml.db;

import java.util.Map;
import java.util.TreeMap;

/**
 * Describe class Types here.
 * <p>
 * Created: Wed Dec 28 21:54:43 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class Types {

	/**
	 * Describe class DataType here.
	 * <p>
	 * Created: Tue Dec  6 17:34:22 2005
	 *
	 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
	 * @version $Rev$
	 */
	public enum DataType {

		INTEGER(Integer.class.getSimpleName()),
		INTEGER_ARR(int[].class.getSimpleName()),
		LONG(Long.class.getSimpleName()),
		LONG_ARR(long[].class.getSimpleName()),
		STRING(String.class.getSimpleName()),
		STRING_ARR(String[].class.getSimpleName()),
		DOUBLE(Double.class.getSimpleName()),
		DOUBLE_ARR(double[].class.getSimpleName()),
		BOOLEAN(Boolean.class.getSimpleName()),
		BOOLEAN_ARR(boolean[].class.getSimpleName()),
		UNDEFINED(null);

		private String javaType = null;

		public static DataType valueof(String javaType) {
			DataType result = UNDEFINED;
			if (javaType != null && !javaType.equals("")) {
				result = dataTypeMap.get(javaType);
			}
			return result == null ? UNDEFINED : result;
		}

		/**
		 * Creates a new <code>DataType</code> instance.
		 */
		private DataType(String java_type) {
			this.javaType = java_type;
			if (java_type != null) {
				dataTypeMap.put(java_type, this);
			}
		}

		public String toString() {
			if (javaType == null) {
				return String.class.getSimpleName();
			}
			else {
				return javaType;
			}
		}

	} // DataType

	public static Map<String, DataType> dataTypeMap = new TreeMap<String, DataType>();

} // Types