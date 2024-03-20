package com.example.walletmanager.Models;

public class SettingsListModel {
    String buttonName;
    String desc;
    boolean isToggleNeed;

    public SettingsListModel(String buttonName, String desc, boolean isToggleNeed){
        this.buttonName = buttonName;
        this.desc = desc;
        this.isToggleNeed = isToggleNeed;
    }
    public String getDesc() {
        return desc;
    }

    public String getButtonName() {
        return buttonName;
    }


    public boolean getIsToggleNeed() {
        return isToggleNeed;
    }
}

