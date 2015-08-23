package fra.unit.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.resource.Singleton;
import fra.unit.runner.JUnitJarRunner;

import java.util.List;

@Singleton
@Path("hello")
public class SampleRESTWebService {

	private class JenkinsBuildResult {
		public String result;
	}

    private class Task {
        String name, url;
    }

    private class Executable {
        public String url;
        public int number;
    }

    private class JenkinsQueuedItem {
        public int id;
        public Executable executable;
        public Task task;
    }

	@Inject
	@Named("hello.world.string")
	private String helloWorldString;

    @Inject
    @Named("jenkins.server")
    private String jenkins;
	
	@GET
	@Path("/runttest")
    @Produces("application/json")
    public Response runtTest(@QueryParam("jar") String jarFile){
		try {
			JUnitJarRunner.runTest(jarFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().build();
	}

	@GET
	@Path("/jenkins")
	@Produces("application/json")
	public Response run(@QueryParam("project") String project, @QueryParam("test") String test){
		try {
            //FIXME http status check before parse jenkins json response
            Gson gson = new Gson();
			Client client = Client.create();
			WebResource resource = client.resource(jenkins + "job/" + project + "/build");
			ClientResponse resp = resource.get(ClientResponse.class);
            if (resp.getStatus() != 201) {
                return Response.status(resp.getStatus()).entity("{message:'eroro starting build'}").build();
            }
            String buildItemUrl = resp.getHeaders().getFirst("Location");
            String result = client.resource(buildItemUrl + "api/json").get(String.class);
            System.out.println(result);
            JenkinsQueuedItem queuedItem = gson.fromJson(result, JenkinsQueuedItem.class);
            JenkinsBuildResult jenkinsBuildResult = null;

            do {
                resource = client.resource(queuedItem.task.url + queuedItem.id + "/api/json");
                resp = resource.get(ClientResponse.class);
                Thread.sleep(1000);
                System.out.println("wait for quit period.");
            } while (resp.getStatus() == 404);

			do {
                resource = client.resource(queuedItem.task.url + queuedItem.id  + "/api/json");
                resp = resource.get(ClientResponse.class);
				result = resp.getEntity(String.class);
                System.out.println(result);
				jenkinsBuildResult = gson.fromJson(result, JenkinsBuildResult.class);
                Thread.sleep(1000);
			} while(jenkinsBuildResult != null && jenkinsBuildResult.result == null);

            resource = client.resource(jenkins + "job/" + project + "/lastBuild/testReport/api/json");
            return Response.status(resp.getStatus()).entity(resource.get(String.class)).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().build();
	}
}
