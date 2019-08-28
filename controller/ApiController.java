package controller;

import models.Email;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.ValidationException;
import java.sql.SQLException;

@Path("/")
public class ApiController {

    @POST
    @Path("/email")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addEmail(Email email){
        try{
            return Response.status(200).entity(Email.addEmail(email)).build();
        }
        catch (ClassNotFoundException | SQLException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/email/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response changePassword(@PathParam("id") String email_id, String oldPassword,String newPassword ){
        try{
            Email.changePassword(email_id,oldPassword,newPassword);
            return Response.status(500).build();
        }catch(ClassNotFoundException | SQLException | ValidationException | MessagingException e){
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
