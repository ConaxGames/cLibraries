package com.conaxgames.libraries.task;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.License;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoUpdaterTask extends BukkitRunnable {
    private static final Logger logger = Bukkit.getLogger();

    public void run() {
        try {
            // Create a HTTP Client
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Let's say to http we want to get something
            HttpRequestBase httpRequestMethod = new HttpGet();
            httpRequestMethod.setURI(new URI("http://cdn.conaxgames.com/cLibraries.jar")); // URL where it will download the file

            // Make response to web and donwload the file in memory
            HttpResponse httpResponse = httpClient.execute(httpRequestMethod, new BasicHttpContext());

            // Get the file to variable, where this jar is running
            File jarFileOnPluginFolder = new File(LibraryPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (LibraryPlugin.getInstance().getSettings().debug) {
                logger.log(Level.INFO, "[cLibraries] Local file has size: " + jarFileOnPluginFolder.length() + " bytes");
                logger.log(Level.INFO, "[cLibraries] Received file with size: " + httpResponse.getEntity().getContentLength() + " bytes");
            }

            if (httpResponse.getEntity().getContentLength() == -1) {
                if(new License(LibraryPlugin.getInstance().getSettings().license, "https://cdn.conaxgames.com/license/verify.php", LibraryPlugin.getInstance()).register()) {
                    logger.log(Level.INFO, "[cLibraries] Unable to connect to updater, Please contact our team.");
                    return;
                }
            }

            // Let's verify if the length of version on web is different than original on folder
            if (jarFileOnPluginFolder.length() != httpResponse.getEntity().getContentLength()) {
                FileOutputStream fileOutputStream = new FileOutputStream(jarFileOnPluginFolder);
                BufferedInputStream bufferedInputStream = null;

                try {
                    logger.log(Level.INFO, "[cLibraries] Found a new version and downloading the new update!");

                    bufferedInputStream = new BufferedInputStream(httpResponse.getEntity().getContent());

                    final byte[] bytes = new byte[1024];
                    int bytesCounter;

                    while ((bytesCounter = bufferedInputStream.read(bytes, 0, 1024)) != -1)
                        fileOutputStream.write(bytes, 0, bytesCounter);

                    logger.log(Level.INFO, "[cLibraries] Updated to latest version successfully.");
                } finally { // Let's close if everything is done to free the memory
                    httpResponse.getEntity().getContent().close();
                    fileOutputStream.close();

                    if (bufferedInputStream != null)
                        bufferedInputStream.close();
                }
            } else
                logger.log(Level.INFO, "[cLibraries] The plugin is already up-to-date.");
        } catch (IOException | URISyntaxException ex) {
            System.out.println("[cLibraries] Unable to download the new update!");
            ex.printStackTrace();
        }
    }
}