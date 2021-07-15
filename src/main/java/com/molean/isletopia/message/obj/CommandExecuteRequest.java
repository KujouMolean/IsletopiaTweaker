package com.molean.isletopia.message.obj;

public class CommandExecuteRequest {
    private String command;

    public CommandExecuteRequest() {
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public CommandExecuteRequest(String command) {
        this.command = command;
    }
}
