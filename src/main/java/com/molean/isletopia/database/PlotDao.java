package com.molean.isletopia.database;

import com.molean.isletopia.utils.UUIDUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class PlotDao {

    public static UUID getAllUUID() {
        return UUID.fromString("00000001-0001-0003-0003-000000000007");
    }
}