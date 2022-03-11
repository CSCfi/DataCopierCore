package fi.csc.data;

import fi.csc.data.model.CopyRequest;
import fi.csc.data.model.Palvelu;
import io.agroal.api.AgroalDataSource;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.extended.run.RunConfigBuilder;
import io.fabric8.kubernetes.client.extended.run.RunOperations;
import io.fabric8.openshift.client.OpenShiftClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
//import org.jboss.resteasy.annotations.jaxrs.HeaderParam;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static fi.csc.data.model.Palvelu.PalveluID.ALLAS;
import static fi.csc.data.model.Palvelu.PalveluID.ALLASPUBLIC;
import static fi.csc.data.model.Palvelu.PalveluID.B2DROP;
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATACLOSED;
import static fi.csc.data.model.Palvelu.PalveluID.FAIRDATAOPEN;
import static fi.csc.data.model.Palvelu.PalveluID.IDASTAGING;


@Path("/v1/copy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoreResource {

    private final static String PODNAME = "dc-engine"; //Should have numbering extra?
    private final static String APP = "app"; //Does content matter?
    private final static String SECRETSNAME = "datacopiersecrets";
    private final static int OK = 200;
    public final static int AD = 403; //Forbidden
    private final static String KEYERROR = "API key was INVALID";
    public final static Response ACCESSDENIED = Response.status(AD, KEYERROR).build();


    @ConfigProperty(name = "dc.apikey")
    String apikey;

    @Inject
    private OpenShiftClient openshiftClient;

    @Inject
    AgroalDataSource defaultDataSource;

    @Inject
    Logger log;

    @Transactional
    @POST
    public Response copy( CopyRequest ft, @HeaderParam("Apikey") String apikeytocheck) {
        if (!apikey.equals(apikeytocheck)) {
            log.error("Invalid Apikey: "+ apikeytocheck);
            return ACCESSDENIED;
        }


        int code = validate(ft);
        if (OK == code) {
            try  {
                Connection connection = defaultDataSource.getConnection();
                if (ft.tallenna(connection)) {
                    connection.close();
                    try {
                        Deployment deployment = new DeploymentBuilder().withNewMetadata()
                                .withName("datacopier-engine-deployment")
                                .addToLabels(APP, PODNAME).endMetadata()
                                .withNewSpec()
                                .withReplicas(1)
                                .withNewSelector()
                                .addToMatchLabels(APP, PODNAME)
                                .endSelector()
                                .withNewTemplate()
                                .withNewMetadata()
                                .addToLabels(APP, PODNAME)
                                .endMetadata()
                                .withNewSpec()
                                .addNewContainer()
                                .withName(PODNAME)
                                .withImage("docker-registry.default.svc:5000/datacopier/engine")
                                .withEnv(new EnvVarBuilder()
                                                .withName("READ_PASSWORD")
                                                .withNewValueFrom()
                                                .withNewSecretKeyRef()
                                                .withName(SECRETSNAME)
                                                .withKey("READ_PASSWORD")
                                                .endSecretKeyRef()
                                                .endValueFrom()
                                                .build(),
                                        new EnvVarBuilder()
                                                .withName("WRITE_PASSWORD")
                                                .withNewValueFrom()
                                                .withNewSecretKeyRef()
                                                .withName(SECRETSNAME)
                                                .withKey("WRITE_PASSWORD")
                                                .endSecretKeyRef()
                                                .endValueFrom()
                                                .build()
                                )
                                .endContainer()
                                //.withRestartPolicy("Never") Not supported
                                .endSpec()
                                .endTemplate()
                                .endSpec()
                                .build();
                        /*RunOperations r = openshiftClient.run().withRunConfig(new RunConfigBuilder()
                                .withImage("docker-registry.default.svc:5000/datacopier/engine")
                                .withName(PODNAME)
                                .addToLabels(APP, PODNAME)
                                .build());*/

                        Deployment d = openshiftClient.apps().deployments()
                                .inNamespace("datacopier").createOrReplace(deployment);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        DeploymentStatus ds = d.getStatus();
                        log.info(ds.toString());
                        //List li =openshiftClient.images();
                    } catch (io.fabric8.kubernetes.client.KubernetesClientException kce) {
                        log.error(kce.getMessage());
                        return Response.status(202, "Suottipa onnistua tahi ei").build();
                    }
                    return Response.ok("Pyyntö lähetetty\n").build();
                }
                else {
                    connection.close();
                    return Response.status(500, "Pyynnön tallennus epäonnistui\n").build();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Response.status(500, "Tietokantayhteysongelma\n").build();
            }

        } else
            return Response.status(code).build();
    }


    private int validate(CopyRequest ft) {
        if (null == ft.requester)
            return 403;
        if (null == ft.source)
            return 400;
        if (null == ft.destination)
            return 400;
        if (ft.source.type.equals(ft.destination.type))
            return 409;
        int code = validatePalvelu(ft.source, true);
        if (OK != code)
            return code;
        return validatePalvelu(ft.destination, false);
    }

    private int validatePalvelu(Palvelu service, boolean source) {
        if (null == service.auth)
            if (!source && !((service.type.equals(ALLASPUBLIC) || service.type.equals(FAIRDATAOPEN)))) {
                log.error("Mandatory Auth object missing");
                return 403;
            }
        if (null == service.param)
            return 400;
        if (null == service.param.polku)
            return 400;
        if (!(service.type.equals(FAIRDATAOPEN) || service.type.equals(FAIRDATACLOSED)))
                if (null == service.param.omistaja)
                    return 400;
        if (service.type.equals(ALLASPUBLIC) || service.type.equals(ALLAS))
            if (null == service.protocol)
                return 402; //missig protocol even code is payment
        if (!source && (!(service.type.equals(IDASTAGING)
                || service.type.equals(ALLAS)
                || service.type.equals(B2DROP))))
            return 418; // teapot is not a valid Destination
        return 200;
    }

}