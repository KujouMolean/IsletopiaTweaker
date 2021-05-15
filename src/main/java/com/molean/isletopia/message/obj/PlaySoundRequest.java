package com.molean.isletopia.message.obj;

public class PlaySoundRequest {
    private String targetPlayer;
    private String soundName;

    public PlaySoundRequest() {
    }

    public PlaySoundRequest(String targetPlayer, String soundName) {
        this.targetPlayer = targetPlayer;
        this.soundName = soundName;
    }

    public String getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(String targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }
}
