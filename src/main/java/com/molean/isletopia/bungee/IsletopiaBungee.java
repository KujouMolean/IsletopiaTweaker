package com.molean.isletopia.bungee;

import com.molean.isletopia.bungee.individual.PlayerMessageHandler;
import com.molean.isletopia.bungee.individual.UniversalVisitHandler;
import com.molean.isletopia.bungee.individual.VisitNotificationHandler;

public class IsletopiaBungee {
    public IsletopiaBungee() {
        new PlayerMessageHandler();
        new UniversalVisitHandler();
        new VisitNotificationHandler();
    }
}
