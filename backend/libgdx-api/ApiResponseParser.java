package com.isthereanyone.api;

import com.badlogic.gdx.utils.JsonValue;
import com.isthereanyone.api.model.SlotInfo;
import com.isthereanyone.api.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class untuk parse JSON response dari API
 */
public class ApiResponseParser {

    /**
     * Parse User dari JsonValue
     */
    public static User parseUser(JsonValue userJson) {
        if (userJson == null) return null;

        User user = new User();
        user.setId(userJson.getLong("id", 0));
        user.setUsername(userJson.getString("username", ""));
        user.setEmail(userJson.getString("email", ""));
        user.setDisplayName(userJson.getString("displayName", null));
        user.setCreatedAt(userJson.getString("createdAt", null));
        return user;
    }

    /**
     * Parse User dari auth response (signup/signin)
     */
    public static User parseUserFromAuthResponse(JsonValue response) {
        JsonValue data = response.get("data");
        if (data == null) return null;

        JsonValue userJson = data.get("user");
        return parseUser(userJson);
    }

    /**
     * Parse SlotInfo dari JsonValue
     */
    public static SlotInfo parseSlotInfo(JsonValue slotJson) {
        if (slotJson == null) return null;

        SlotInfo slot = new SlotInfo();
        slot.setSlotId(slotJson.getInt("slotId", 0));
        slot.setEmpty(slotJson.getBoolean("isEmpty", true));
        slot.setLastUpdated(slotJson.getString("lastUpdated", null));
        slot.setCurrentMap(slotJson.getString("currentMap", null));

        if (slotJson.has("allTimeDeathCount") && !slotJson.get("allTimeDeathCount").isNull()) {
            slot.setAllTimeDeathCount(slotJson.getInt("allTimeDeathCount"));
        }
        if (slotJson.has("allTimeCompletedTask") && !slotJson.get("allTimeCompletedTask").isNull()) {
            slot.setAllTimeCompletedTask(slotJson.getInt("allTimeCompletedTask"));
        }

        return slot;
    }

    /**
     * Parse list of SlotInfo dari getAllSlots response
     */
    public static List<SlotInfo> parseSlotList(JsonValue response) {
        List<SlotInfo> slots = new ArrayList<>();

        JsonValue data = response.get("data");
        if (data == null || !data.isArray()) return slots;

        for (JsonValue slotJson : data) {
            SlotInfo slot = parseSlotInfo(slotJson);
            if (slot != null) {
                slots.add(slot);
            }
        }

        return slots;
    }

    /**
     * Parse saveData dari loadGame response
     */
    public static JsonValue parseSaveData(JsonValue response) {
        JsonValue data = response.get("data");
        if (data == null) return null;
        return data.get("saveData");
    }

    /**
     * Get message dari response
     */
    public static String getMessage(JsonValue response) {
        return response.getString("message", "");
    }

    /**
     * Get boolean data dari response (untuk checkUsername, checkEmail, checkSlotExists)
     */
    public static boolean getBooleanData(JsonValue response) {
        return response.getBoolean("data", false);
    }
}

