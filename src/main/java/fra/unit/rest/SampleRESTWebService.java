package fra.unit.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.spi.resource.Singleton;
import fra.unit.runner.JUnitJarRunner;

@Singleton
@Path("hello")
public class SampleRESTWebService {

	@Inject
	@Named("hello.world.string")
	private String helloWorldString;
	
	@GET
	@Produces("application/json")
	public Response runtTest(@QueryParam("jar") String jarFile){
		try {
			JUnitJarRunner.runTest(jarFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().build();
	}
}
