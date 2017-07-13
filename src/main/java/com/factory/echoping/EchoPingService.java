package com.factory.echoping;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by yuboski on 12/07/17.
 * from https://docs.jboss.org/author/display/WFLY10/Java+API+for+RESTful+Web+Services+%28JAX-RS%29
 */

@Path("modules")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class EchoPingService {
    @GET
    @Path("/info")
    public Collection<EchoInfo> getInfo() {
        ArrayList<EchoInfo> infos = new ArrayList<EchoInfo>();
        FolderScanner folderScanner = new FolderScanner();
        infos.add(new EchoInfo("home folder", Arrays.asList(folderScanner.getHomeFolder())));
        infos.addAll(folderScanner.getDeploymentsList());
        infos.addAll(folderScanner.getDependenciesList());
        return infos;
    }
}
