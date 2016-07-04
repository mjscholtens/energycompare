package org.comparer.webservice;

import java.net.URISyntaxException;
import java.util.Calendar;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.comparer.datamodel.*;

/**
 * Class which will create customers and store energy information from the customers.
 * @author Marijn Scholtens & Bob Reimink
 *
 */
@Path("/energyrecs")
public class EnergyResource {
		
	EnergyDAO e = EnergyDAO.instance;
	UriInfo uriInfo;
	
    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String createCustomer(@PathParam(value="name") String name) {
    	int id = e.getCustomers().size();
    	e.getCustomers().add(new EnergyCustomer(id, name));
    	return Integer.toString(id);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response addRecords(EnergyWrapper ew) throws URISyntaxException{
    	boolean same;
    	int size;
    	
    	if ( ew == null){
          throw new BadRequestException();
        }
   	 	EnergyCustomer ec = e.getCustomer(ew.getId());
   	 	
   	 	Calendar cal1 = Calendar.getInstance();
   	 	Calendar cal2 = Calendar.getInstance();
   	 	size = ec.getEnergyRecords().get(0).size();
   	 	if(size == 0){
   	 		same = false;
   	 	} else {
   	 		cal1.setTime(ec.getEnergyRecords().get(0).get(size-1).getStartDate());
   	 		cal2.setTime(ew.getEr().get(0).getStartDate());
   	 		same = (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && 
   	 				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
   	 	}
   	 	for(int i=0; i<ew.getEr().size(); i++){
   	 		if(same){
   	 			double en = ec.getEnergyRecords().get(i).get(size-1).getEnergy();
   	 			ec.getEnergyRecords().get(i).get(size-1).setEnergy(en + ew.getEr().get(i).getEnergy());
   	 		} else {
   	 			ec.getEnergyRecords().get(i).add(ew.getEr().get(i));
   	 		}
   	 	}
   	    return Response.ok().build();
   	   } 
}
