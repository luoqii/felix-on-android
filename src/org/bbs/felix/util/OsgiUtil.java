package org.bbs.felix.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

public class OsgiUtil {
    static Map<Integer, String> sStateMap = new HashMap<Integer, String>();
    static {
        sStateMap.put(org.osgi.framework.Bundle.ACTIVE, "ACTIVE");
        sStateMap.put(org.osgi.framework.Bundle.INSTALLED, "INSTALLED");
        sStateMap.put(org.osgi.framework.Bundle.RESOLVED, "RESOLVED");
        sStateMap.put(org.osgi.framework.Bundle.STARTING, "STARTING");
        sStateMap.put(org.osgi.framework.Bundle.STOPPING, "STOPPING");
        sStateMap.put(org.osgi.framework.Bundle.UNINSTALLED, "UNINSTALLED");
    }
    public static String bundleState2Str(int state){
        String str = state + " [unknown state]";
        if (sStateMap.containsKey(state)){
            str = sStateMap.get(state);
        }
        return str;
    }
    
    public static String getName(org.osgi.framework.Bundle b) {
    	String name = "";
    	name = b.getSymbolicName();
    	if (TextUtils.isEmpty(name)) {
    		name = b.getLocation();
    	}
    	
    	return name;
    }
    
    public static String[] HEADER_ALL = new String[]{};
    public static String[] HEADER_OSGI = new String[]{
    	org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME,
    	org.osgi.framework.Constants.BUNDLE_VERSION,
    	org.osgi.framework.Constants.IMPORT_PACKAGE,
    	org.osgi.framework.Constants.EXPORT_PACKAGE,
    };
    public static String getHeader(org.osgi.framework.Bundle b, String[] headers) {
    	String header = "";
    	for (String h : headers) {
    		Dictionary<String, String> d = b.getHeaders(h);
    		Enumeration<String> keys = d.keys();
    		while (keys.hasMoreElements()) {
    			String key = keys.nextElement();
    			if (key.equals(h)) {
    				header += h + ":" + d.get(key) + "\n";
    				break;
    			}
    			
    		}
    	}
    	if (null == header || header.length() == 0) {
    		header = toString(b.getHeaders());
    	}
    	
    	return header;
    }
	
	public static String toString(Dictionary<String, String> d) {
		String str = "";
		Enumeration<String> e = d.keys();
		while (e != null && e.hasMoreElements()) {
			String key = e.nextElement();
			str += key + ": " + d.get(key) + "\n";
		}
		
		return str;
	}
}
