package com.example.zyyschedule;

public class RemindBean {
    private String remindtitle;
    private int remindtype;
    private boolean remindisChecked;
    public String getRemindtitle() {
        return remindtitle;
    }

    public void setRemindtitle(String remindtitle) {
        this.remindtitle = remindtitle;
    }

    public void setRemindtype(int remindtype) {
        this.remindtype = remindtype;
    }

    public boolean isRemindisChecked() {
        return remindisChecked;
    }

    public void setRemindisChecked(boolean remindisChecked) {
        this.remindisChecked = remindisChecked;
    }
}
