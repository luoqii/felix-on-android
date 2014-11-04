package org.bangbang.song.felix.util;

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
    
    public static String getName (org.osgi.framework.Bundle b) {
    	String name = "";
    	name = b.getSymbolicName();
    	if (TextUtils.isEmpty(name)) {
    		name = b.getLocation();
    	}
    	
    	return name;
    }
}
