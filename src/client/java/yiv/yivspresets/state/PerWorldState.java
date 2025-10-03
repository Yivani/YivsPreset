package yiv.yivspresets.state;

import java.util.HashMap;
import java.util.Map;

public class PerWorldState {
	private static final Map<String, String> LAST_PRESET_BY_CONTEXT = new HashMap<>();

	public static String get(String contextKey) {
		return LAST_PRESET_BY_CONTEXT.get(contextKey);
	}

	public static void put(String contextKey, String presetId) {
		LAST_PRESET_BY_CONTEXT.put(contextKey, presetId);
	}
}

