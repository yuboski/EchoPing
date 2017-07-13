package com.factory.echoping;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by yuboski on 12/07/17.
 */
public class FolderScanner {

    private String DEPLOYMENTS_FOLDER = "/standalone/deployments";

    public String getHomeFolder() {
        return System.getProperty("jboss.home.dir");
    }

    public Collection<EchoInfo> getDeploymentsList() {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        File folder = new File(this.getHomeFolder() + DEPLOYMENTS_FOLDER);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
        for (File f : listOfFiles) {
            fileNames.add(f.getName());
        }
        out.add(new EchoInfo(folder.getAbsolutePath(), fileNames));
        return out;
    }

    public Collection<EchoInfo> getDependenciesList() {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        for(EchoInfo deployFolder : this.getDeploymentsList()) {
            for (String file : deployFolder.getValues()) {
                if (file.endsWith(".ear")) {
                    out.addAll(this.getModuleDependencies(file));
                } else if (file.endsWith(".war")) {
                    out.addAll(this.getModuleDependencies(file));
                }
            }
        }
        return out;
    }
    private Collection<EchoInfo> getModuleDependencies(String moduleName) {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        ArrayList<String> deps = new ArrayList<String>();
        String filePath = this.getHomeFolder() + DEPLOYMENTS_FOLDER + "/" + moduleName;
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(filePath));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".jar")) {
                    deps.add(entry.getName());
                }
            }
            out.add(new EchoInfo(moduleName, deps));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            out.add(new EchoInfo("FileNotFoundException", Arrays.asList(e.getMessage())));
        } catch (IOException e) {
            e.printStackTrace();
            out.add(new EchoInfo("IOException", Arrays.asList(e.getMessage())));
        }
        return out;
    }
}
