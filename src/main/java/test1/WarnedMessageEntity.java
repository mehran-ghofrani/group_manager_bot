package test1;

import java.util.LinkedList;

public class WarnedMessageEntity {
    private long chatId;
    private long messageId;
    private LinkedList<Integer> warnerIds = new LinkedList<Integer>();

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public LinkedList<Integer> getWarnerIds() {
        return warnerIds;
    }

    public void setWarnerIds(LinkedList<Integer> warnerIds) {
        this.warnerIds = warnerIds;
    }
}
