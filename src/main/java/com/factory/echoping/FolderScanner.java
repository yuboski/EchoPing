package com.factory.echoping;


import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by yuboski on 12/07/17.
 */
public class FolderScanner {

    private String DEFAULT_DEPLOYMENTS_FOLDER = "standalone/deployments";

    public String getHomeFolder() {
        return System.getProperty("jboss.home.dir");
    }


    private String getDeploymentsFolder(EchoInfoArgument argument) {
        return (argument != null && argument.getDeploymentsFolder() != null && argument.getDeploymentsFolder().length() > 0) ?
                argument.getDeploymentsFolder() :
                DEFAULT_DEPLOYMENTS_FOLDER;
    }

    public Collection<EchoInfo> getDeploymentsList(EchoInfoArgument argument) {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        File folder = new File(this.getHomeFolder() + "/" +
                this.getDeploymentsFolder(argument));

        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                fileNames.add(f.getName());
            }
        }
        out.add(new EchoInfo(folder.getAbsolutePath(), fileNames));
        return out;
    }

    public Collection<EchoInfo> getDependenciesList(EchoInfoArgument argument) {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        for(EchoInfo deployFolder : this.getDeploymentsList(argument)) {
            for (String file : deployFolder.getValues()) {
                if (file.toUpperCase().endsWith(".EAR")) {
                    out.addAll(this.getModuleDependencies(argument, file));
                } else if (file.toUpperCase().endsWith(".WAR")) {
                    out.addAll(this.getModuleDependencies(argument, file));
                }
            }
        }
        return out;
    }
    private Collection<EchoInfo> getModuleDependencies(EchoInfoArgument argument, String moduleName) {
        ArrayList<EchoInfo> out = new ArrayList<EchoInfo>();
        ArrayList<String> deps = new ArrayList<String>();
        String filePath = this.getHomeFolder() +
                "/" + this.getDeploymentsFolder(argument) +
                "/" + moduleName;
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(filePath));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory()) {
                    if (entry.getName().toUpperCase().endsWith(".JAR")) {
                        deps.add(entry.getName());
                    } else if (entry.getName().toUpperCase().endsWith("MANIFEST.MF")) {
                        Scanner s = new Scanner(zip).useDelimiter("\\A");
                        String content = s.hasNext() ? s.next() : "";
                        out.add(new EchoInfo(moduleName + ":MANIFEST.MF", Arrays.asList(content)));
                    }
                }
            }
            out.add(new EchoInfo(moduleName + ":lib", deps));
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
