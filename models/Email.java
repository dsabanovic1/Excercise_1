package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;
import org.apache.commons.lang3.RandomStringUtils;
import services.EmailService;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;
import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Email {

    @JsonProperty
    String email_id;
    @JsonProperty
    String firstName;
    @JsonProperty
    String lastName;
    @JsonProperty
    String department;
    @JsonProperty
    private int mailbox_capacity;
    @JsonProperty
    String alternateEmail;
    @JsonProperty
    String password;
    @JsonProperty
    String email;
    private final String companyName = "company";
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public Email(){}

    public Email(String firstName,String lastName,String department ){
        this.firstName=firstName;
        this.lastName=lastName;
        this.department= department;
        this.email = this.firstName.toLowerCase()+"."+this.lastName.toLowerCase()+
                "@"+this.department.toLowerCase()+"."+companyName+".com";
        this.password = generatePassword();
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    private String generatePassword() {

        return RandomStringUtils.randomAlphanumeric(10);

    }

    public int getMailbox_capacity() {
        return mailbox_capacity;
    }

    public void setMailbox_capacity(int mailbox_capacity) {
        this.mailbox_capacity = mailbox_capacity;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getPassword() {
        return password;
    }

    public static void changePassword(String email_id,String oldPassword,String newPassword) throws ValidationException,ClassNotFoundException,
            SQLException, MessagingException {
        if(!validatePassword(newPassword)) throw new ValidationException("Password is not in required format.");
        Email email = getEmail(email_id);
        if(oldPassword == email.getPassword())
        {email.setPassword(newPassword);
            updateEmail(email_id,newPassword);
            EmailService.sendChangedPasswordEmail(newPassword,email.getEmail());
        }
        else throw new ValidationException("Please enter correct old password!");
    }
    public static boolean validatePassword(String password){
        return password.length()>=10;
    }

    public String displayInfo(){

        return "First name: "+this.firstName+" Last name: "+this.lastName+" email: "+this.email+
                " mailbox capacity : "+this.mailbox_capacity;
    }

    public static Email addEmail(Email email) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("INSERT INTO excercise.email (email_id,first_name,last_name,department,email,alternate,capacity,password,created,modified)" +
                "VALUES(?,?,?,?,?,?,?,?,?)");
        String id = Database.generatorId(5);
        ps.setString(1,id);
        ps.setString(2,email.getFirstName());
        ps.setString(3,email.getLastName());
        ps.setString(4,email.getDepartment());
        ps.setString(5,email.getEmail());
        ps.setString(6,email.getAlternateEmail());
        ps.setInt(7,email.getMailbox_capacity());
        ps.setString(8,email.getPassword());
        ps.setLong(9,System.currentTimeMillis());
        ps.setLong(10,System.currentTimeMillis());
        ps.executeUpdate();
        return email;
    }

    public static void updateEmail(String email_id,String newPassword) throws ClassNotFoundException,SQLException{
        PreparedStatement ps = Database.prepareStatement("UPDATE excercise.email SET password = ? WHERE" +
                "email_id = ?");
        ps.setString(1,newPassword);
        ps.setString(2,email_id);
        ps.executeUpdate();

    }
    public static Email getEmail(String email_id) throws ClassNotFoundException,SQLException{
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise.email WHERE email_id = ?");
        ps.setString(1,email_id);
        ResultSet rs = ps.executeQuery();
        Email email = new Email();
        while(rs.next()){
        email.setEmail_id(rs.getString("email_id"));
        email.setFirstName(rs.getString("firstName"));
        email.setLastName(rs.getString("lastName"));
        email.setEmail(rs.getString("email"));
        email.setPassword(rs.getString("password"));
        email.setAlternateEmail(rs.getString("alternateEmail"));
        email.setMailbox_capacity(rs.getInt("mailbox_capacity"));
        email.setDepartment(rs.getString("department"));
        email.setCreated(rs.getLong("created"));
        email.setModified(rs.getLong("modified"));
        }
        return email;
    }
}
