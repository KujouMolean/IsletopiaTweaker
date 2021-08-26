package com.molean.isletopia.mail;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class MailObject {
    private UUID uuid;
    private String from;
    private String title;
    private String message;
    private List<String> itemStackList;
    private long time;

    public MailObject() {
    }


    public MailObject(String from, String title, String message, List<ItemStack> itemStackList) {
        this.from = from;
        uuid = UUID.randomUUID();
        this.title = title;
        this.message = message;
        this.itemStackList = new ArrayList<>();
        for (ItemStack itemStack : itemStackList) {
            String s = Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
            this.itemStackList.add(s);
        }
        time = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getItemStackList() {
        return itemStackList;
    }

    public void setItemStackList(List<String> itemStackList) {
        this.itemStackList = itemStackList;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
