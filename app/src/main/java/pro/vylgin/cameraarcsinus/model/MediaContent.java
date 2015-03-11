package pro.vylgin.cameraarcsinus.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaContent {

    public static List<MediaItem> ITEMS = new ArrayList<>();
    public static Map<Integer, MediaItem> ITEM_MAP = new HashMap<>();

//    static {
//        // Add 3 sample items.
//        addItem(new MediaItem(1, "Item 1"));
//        addItem(new MediaItem(2, "Item 2"));
//        addItem(new MediaItem(3, "Item 3"));
//    }

    public static void addItem(MediaItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class MediaItem {
        public int id;
        public String content;
        public String contentPath;

        public MediaItem(int id, String content, String contentPath) {
            this.id = id;
            this.content = content;
            this.contentPath = contentPath;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
