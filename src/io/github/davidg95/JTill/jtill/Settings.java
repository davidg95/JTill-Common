/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Class which holds all server configurations.
 *
 * @author David
 */
public class Settings implements Serializable {

    private static Settings settings;
    private final Properties properties;

    /**
     * The default port number of 52341.
     */
    public static final int DEFAULT_PORT = 52341;
    /**
     * The default maximum connections of 10.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    /**
     * The default maximum queued connections of 10.
     */
    public static final int DEFAULT_MAX_QUEUE = 10;
    /**
     * The default database address.
     */
    public static final String DEFAULT_ADDRESS = "jdbc:derby:TillEmbedded;";
    /**
     * The default database username.
     */
    public static final String DEFAULT_USERNAME = "APP";
    /**
     * The default database password.
     */
    public static final String DEFAULT_PASSWORD = "App";

    public Settings() {
        properties = new Properties();
    }

    /**
     * Returns an instance of the Settings. If an instance has not been created
     * already, it will create one.
     *
     * @return Settings object.
     */
    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * Method to get a setting.
     *
     * @param key the setting to get.
     * @return the value associated with the setting.
     */
    public String getSetting(String key) {
        return properties.getProperty(key);
    }

    /**
     * Method to get a setting.
     *
     * @param key the settings to get.
     * @param defaultValue the value to assign to the setting if it does not
     * already exist.
     * @return the value assigned to the setting.
     */
    public String getSetting(String key, String defaultValue) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            setSetting(key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Method to set a setting.
     *
     * @param key the setting to set.
     * @param value the value to assign to it.
     */
    public void setSetting(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Method to remove a setting.
     *
     * @param key the setting to remove.
     */
    public void removeSetting(String key) {
        properties.remove(key);
    }

    /**
     * Gets the properties object.
     *
     * @return the Properties object.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Method to load the settings from the server.properties file.
     */
    public void loadProperties() {
        InputStream in;

        try {
            in = new FileInputStream("server.properties");

            properties.load(in);

            in.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
            initProperties();
        } catch (IOException ex) {
        }
    }

    /**
     * Method to save the settings to the server.properties file.
     */
    public void saveProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");
            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Fills the settings file with default values.
     */
    private void initProperties() {
        OutputStream out;

        try {
            out = new FileOutputStream("server.properties");

            setSetting("db_address", DEFAULT_ADDRESS);
            setSetting("db_username", DEFAULT_USERNAME);
            setSetting("db_password", DEFAULT_PASSWORD);
            setSetting("max_conn", Integer.toString(DEFAULT_MAX_CONNECTIONS));
            setSetting("max_queue", Integer.toString(DEFAULT_MAX_QUEUE));
            setSetting("port", Integer.toString(DEFAULT_PORT));
            setSetting("AUTO_LOGOUT", "FALSE");
            setSetting("LOGOUT_TIMEOUT", "-1");

            properties.store(out, null);
            out.close();
        } catch (FileNotFoundException | UnknownHostException ex) {
        } catch (IOException ex) {
        }
    }
}