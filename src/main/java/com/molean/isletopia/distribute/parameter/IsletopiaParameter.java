package com.molean.isletopia.distribute.parameter;

import com.molean.isletopia.shared.database.ParameterDao;

public class IsletopiaParameter {
    public IsletopiaParameter() {
        new ParameterCommand();
        ParameterDao.checkTable();
    }
}
