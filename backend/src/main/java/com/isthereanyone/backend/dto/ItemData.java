package com.isthereanyone.backend.dto;

/**
 * DTO untuk data Item/Barang
 */
public class ItemData {
    private String itemId;
    private String itemName;
    private String itemType; // "key", "tool", "consumable", "quest_item"
    private Integer quantity;
    private Boolean isEquipped; // sedang di-equip/dipegang
    private String slotPosition; // posisi di inventory ("hand", "bag_1", "bag_2", etc)

    public ItemData() {}

    public ItemData(String itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = 1;
        this.isEquipped = false;
    }

    public ItemData(String itemId, String itemName, String itemType, Integer quantity) {
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

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Boolean getIsEquipped() { return isEquipped; }
    public void setIsEquipped(Boolean isEquipped) { this.isEquipped = isEquipped; }

    public String getSlotPosition() { return slotPosition; }
    public void setSlotPosition(String slotPosition) { this.slotPosition = slotPosition; }
}

