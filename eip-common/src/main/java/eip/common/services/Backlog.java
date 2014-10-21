package eip.common.services;

import java.util.Collections;
import java.util.List;

import eip.common.entities.BacklogItem;

public class Backlog {
    private List<BacklogItem> items;

    public Backlog(BacklogItem item) {
        this(Collections.singletonList(item));
    }

    public Backlog(List<BacklogItem> items) {
        this.items = items;
    }

    public List<BacklogItem> getItems() {
        return items;
    }

}
