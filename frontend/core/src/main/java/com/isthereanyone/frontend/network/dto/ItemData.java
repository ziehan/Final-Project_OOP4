package com.isthereanyone.frontend.network.dto;

/**
 * Model class untuk data Item/Barang
 * Digunakan untuk save/load inventory
 */
public class ItemData {
    private String itemId;
    private String itemName;
    private String itemType; // "key", "tool", "consumable", "quest_item"
    private int quantity;
    private boolean isEquipped;
    private String slotPosition; // "hand", "bag_1", "bag_2", etc

    public ItemData() {
        this.quantity = 1;
        this.isEquipped = false;
    }

    public ItemData(String itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = 1;
        this.isEquipped = false;
    }

    public ItemData(String itemId, String itemName, String itemType, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.isEquipped = false;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }

    public String getSlotPosition() { return slotPosition; }
    public void setSlotPosition(String slotPosition) { this.slotPosition = slotPosition; }
}

