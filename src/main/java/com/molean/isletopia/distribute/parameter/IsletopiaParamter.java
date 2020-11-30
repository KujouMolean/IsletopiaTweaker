package com.molean.isletopia.distribute.parameter;

import com.molean.isletopia.database.ParameterDao;

public class IsletopiaParamter {
    public IsletopiaParamter() {
        new ParameterCommand();
        ParameterDao.checkTable();
    }
}
