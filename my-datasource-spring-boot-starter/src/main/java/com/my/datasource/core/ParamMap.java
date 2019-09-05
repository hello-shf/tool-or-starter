package com.my.datasource.core;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@SuppressWarnings("rawtypes")
public class ParamMap extends HashMap implements Map {
	private static final long serialVersionUID = 1L;

	Map map = null;
	HttpServletRequest request;

	/**
	 * @param request
	 */
	public ParamMap(HttpServletRequest request) {
		this.request = request;
		Map properties = request.getParameterMap();
		Map returnMap = new HashMap();
		Iterator entries = properties.entrySet().iterator();
		Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			if(!"".equals(value)){
				returnMap.put(name, value);
			}
		}
		map = returnMap;
	}

	public ParamMap() {
		map = new HashMap();
	}

	@Override
	public Object get(Object key) {
		Object obj = null;
		if (map.get(key) instanceof Object[]) {
			Object[] arr = (Object[]) map.get(key);
			obj = request == null ? arr
					: (request.getParameter((String) key) == null ? arr
							: arr[0]);
		} else {
			obj = map.get(key);
		}
		return obj;
	}

	public String getString(Object key) {
		return (String) get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set keySet() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map t) {
		map.putAll(t);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection values() {
		return map.values();
	}
}
