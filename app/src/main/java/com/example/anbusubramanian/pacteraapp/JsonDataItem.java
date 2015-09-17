package com.example.anbusubramanian.pacteraapp;

/**
 * Created by anbu.subramanian on 16/09/15.
 * Model for JSON Data
 */
public class JsonDataItem
{
    private String itemTitle;
    private String itemThumbnail;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    private String description;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemThumbnail() {
        return itemThumbnail;
    }

    public void setItemThumbnail(String itemThumbnail) {
        this.itemThumbnail = itemThumbnail;
    }
}


