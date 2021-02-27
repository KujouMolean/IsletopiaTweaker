package com.molean.isletopia.message.obj;

public class VisitRequest {
    private String sourcePlayer;
    private String targetPlayer;

    public VisitRequest() {
    }

    public VisitRequest(String sourcePlayer, String targetPlayer) {
        this.sourcePlayer = sourcePlayer;
        this.targetPlayer = targetPlayer;
    }

    public String getSourcePlayer() {
        return sourcePlayer;
    }

    public void setSourcePlayer(String sourcePlayer) {
        this.sourcePlayer = sourcePlayer;
    }

    public String getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(String targetPlayer) {
        this.targetPlayer = targetPlayer;
    }
}
