package com.molean.isletopia.mail;

import java.util.List;

public class InboxObject {
    private List<MailObject> mailObjectList;

    public InboxObject(List<MailObject> mailObjectList) {
        this.mailObjectList = mailObjectList;
    }

    public InboxObject() {
    }

    public List<MailObject> getMailObjectList() {
        return mailObjectList;
    }

    public void setMailObjectList(List<MailObject> mailObjectList) {
        this.mailObjectList = mailObjectList;
    }
}
