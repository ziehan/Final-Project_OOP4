package com.isthereanyone.frontend.components;

import com.isthereanyone.frontend.entities.items.ItemType;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<ItemType> items;
    private int capacity = 3;
    private int selectedSlot = 0;

    public Inventory() {
        items = new ArrayList<>();
    }

    public boolean addItem(ItemType item) {
        if (items.size() < capacity) {
            items.add(item);
            System.out.println("Item added: " + item);
            return true;
        }
        return false;
    }

    public boolean hasItem(ItemType type) {
        return items.contains(type);
    }

    public void removeItem(ItemType type) {
        items.remove(type);
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public void setSelectedSlot(int index) {
        if (index >= 0 && index < capacity) {
            this.selectedSlot = index;
            System.out.println("Selected Slot: " + (index + 1));
        }
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public ItemType dropSelectedItem() {
        if (selectedSlot < items.size()) {
            return items.remove(selectedSlot);
        }
        return null;
    }

    public ItemType swapItem(ItemType newItem) {
        if (selectedSlot < items.size()) {
            ItemType oldItem = items.get(selectedSlot);
            items.set(selectedSlot, newItem);
            return oldItem;
        }
        return null;
    }

    public List<ItemType> getItems() {
        return items;
    }
}
