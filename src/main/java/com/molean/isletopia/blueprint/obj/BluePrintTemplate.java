package com.molean.isletopia.blueprint.obj;

import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.UUID;

public class BluePrintTemplate {
    private int id;
    private UUID owner;
    private String name;
    private Timestamp creation;
    private int island;
    private BluePrintData bluePrintData;
}
