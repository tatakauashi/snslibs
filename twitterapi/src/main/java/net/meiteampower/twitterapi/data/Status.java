/**
 *
 */
package net.meiteampower.twitterapi.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author tatak
 *
 */
public class Status implements Serializable {

    // Tue Apr 11 06:02:56 +0000 2017
    private static final SimpleDateFormat CreatedAtFormat =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    private Date createdAt;
    private String createdAtStr;
    private String id;
    private String text;
//    private String mediaUrlHttps;
    private List<String> mediaUrlHttpsList;

    /**
     * @return createdAt
     */
    public final Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt セットする createdAt
     */
    public final void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return createdAtStr
     */
    public final String getCreatedAtStr() {
        return createdAtStr;
    }

    /**
     * @param createdAtStr セットする createdAtStr
     */
    public final void setCreatedAtStr(String createdAtStr) {
        this.createdAtStr = createdAtStr;
    }

    /**
     * @return id
     */
    public final String getId() {
        return id;
    }

    /**
     * @param id セットする id
     */
    public final void setId(String id) {
        this.id = id;
    }

    /**
     * @return text
     */
    public final String getText() {
        return text;
    }

    /**
     * @param text セットする text
     */
    public final void setText(String text) {
        this.text = text;
    }

//    /**
//     * @return mediaUrlHttps
//     */
//    public final String getMediaUrlHttps() {
//        return mediaUrlHttps;
//    }
//
//    /**
//     * @param mediaUrlHttps セットする mediaUrlHttps
//     */
//    public final void setMediaUrlHttps(String mediaUrlHttps) {
//        this.mediaUrlHttps = mediaUrlHttps;
//    }

    /**
     * @return mediaUrlHttpsList
     */
    public final List<String> getMediaUrlHttpsList() {
        return mediaUrlHttpsList;
    }

    /**
     * @param mediaUrlHttpsList セットする mediaUrlHttpsList
     */
    public final void setMediaUrlHttpsList(List<String> mediaUrlHttpsList) {
        this.mediaUrlHttpsList = mediaUrlHttpsList;
    }

    /**
     * @return mediaUrlHttpsList
     */
    public final boolean hasMediaUrl() {
        return (mediaUrlHttpsList != null && mediaUrlHttpsList.size() > 0);
    }

    public Status(JsonObject jsonObject) {
        // created_at
        createdAtStr = get(jsonObject, "created_at");
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(CreatedAtFormat.parse(createdAtStr));
            createdAt = cal.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // id
        id = get(jsonObject, "id");

        // text
        text = get(jsonObject, "text");

//        // mediaUrlHttps
//        text = get(jsonObject, "entities.media");
//        JsonArray jArray = getArray(jsonObject, "extended_entities.media");
        JsonArray jArray = null;
        if (((jArray = getArray(jsonObject, "extended_entities.media")) != null)
                || ((jArray = getArray(jsonObject, "entities.media")) != null)) {
            mediaUrlHttpsList = new ArrayList<String>();
            for (JsonElement elem : jArray) {
                if (((JsonObject)elem).has("type")
                        && ((JsonObject)elem).get("type").getAsString().equals("video")) {
                    JsonArray jArray2 = getArray(((JsonObject)elem), "video_info.variants");
                    int bitrate = -1;
                    String videoUrl = "";
                    for (JsonElement elem2 : jArray2) {
                        JsonObject variant = ((JsonObject)elem2);
                        if (variant.has("bitrate")) {
                            int bitrate2 = variant.get("bitrate").getAsJsonPrimitive().getAsInt();
                            if (bitrate < bitrate2) {
                                bitrate = bitrate2;
                                videoUrl = variant.get("url").getAsString();
                            }
                        }
                    }
                    mediaUrlHttpsList.add(videoUrl);
                } else {
                    String url = get((JsonObject)elem, "media_url_https");
                    if (url != null) {
                        mediaUrlHttpsList.add(url);
                    }
                }
            }
        }
    }

    private String get(JsonObject jsonObject, String memberName) {
        String value = null;
        int index = memberName.indexOf(".");
        if (index >= 0) {
            String thisMemberName = memberName.substring(0, index);
            if (jsonObject.has(thisMemberName)) {
                JsonElement elem = jsonObject.get(thisMemberName);
                if (elem.isJsonObject()) {
                    value = get((JsonObject)elem, memberName.substring(index + 1));
                }
            }
        } else {
            if (jsonObject.has(memberName)) {
                value = jsonObject.get(memberName).getAsString();
            }
        }
        return value;
    }

    private JsonArray getArray(JsonObject jsonObject, String memberName) {
        JsonArray value = null;
        int index = memberName.indexOf(".");
        if (index >= 0) {
            String thisMemberName = memberName.substring(0, index);
            if (jsonObject.has(thisMemberName)) {
                JsonElement elem = jsonObject.get(thisMemberName);
                if (elem.isJsonObject()) {
                    value = getArray((JsonObject)elem, memberName.substring(index + 1));
                }
            }
        } else {
            if (jsonObject.has(memberName)) {
                value = jsonObject.get(memberName).getAsJsonArray();
            }
        }
        return value;
    }

}