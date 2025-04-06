package com.zvicraft;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerLicenseChecker {
    public static boolean isLicenseValid() {
        String apiUrl = ServerConfig.getServerApiUrl();
        String licenseKey = ServerConfig.getLicenseKey();
        String serverIp = "your-server-ip";

        try {
            URL url = new URL(apiUrl + "?licenseKey=" + licenseKey + "&serverIP=" + serverIp);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
