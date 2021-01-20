package com.molean.isletopia.utils;

import com.jcraft.jsch.*;
import com.molean.isletopia.database.DataSourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Sftp {
    public static boolean uploadFile(String src, String dst) {
        JSch jSch = new JSch();
        Properties properties = null;
        try {
            InputStream inputStream = DataSourceUtils.class.getClassLoader().getResourceAsStream("sftp.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            return false;
        }
        String host = properties.getProperty("host");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        try {
            Session session = jSch.getSession(user, host);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(3000);

            session.connect();
            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            sftp.put(new FileInputStream(src), dst);
            sftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
