package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;
import org.apache.commons.lang3.RandomStringUtils;
import services.EmailService;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Email {

    @JsonProperty
    String emailId;
    @JsonProperty
    String firstName;
    @JsonProperty
    String lastName;
    @JsonProperty
    String department;
    @JsonProperty
    private int mailboxCapacity;
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

    public Email() {
    }

    public Email(String firstName, String lastName, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.email = this.firstName.toLowerCase() + "." + this.lastName.toLowerCase() +
                "@" + this.department.toLowerCase() + "." + companyName + ".com";
        this.password = generatePassword();
    }


    private String generatePassword() {

        return RandomStringUtils.randomAlphanumeric(10);

    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public int getMailboxCapacity() {
        return mailboxCapacity;
    }

    public void setMailboxCapacity(int mailboxCapacity) {
        this.mailboxCapacity = mailboxCapacity;
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

    public static void changePassword(String email_id, String oldPassword, String newPassword) throws ValidationException, ClassNotFoundException,
            SQLException, MessagingException {
        if (!validatePassword(newPassword)) throw new ValidationException("Password is not in required format.");
        Email email = getEmail(email_id);
        if (oldPassword == email.getPassword()) {
            email.setPassword(newPassword);
            updateEmail(email_id, newPassword);
            EmailService.sendChangedPasswordEmail(newPassword, email.getEmail());
        } else throw new ValidationException("Please enter correct old password!");
    }

    public static boolean validatePassword(String password) {
        return password.length() >= 10;
    }

    public static String displayInfo(String emailId) throws ClassNotFoundException, SQLException {

        Email email = getEmail(emailId);
        return "First name: " + email.getFirstName() + " Last name: " + email.getLastName() + " email: " + email.getEmail() +
                " mailbox capacity : " + email.getMailboxCapacity();
    }

    public static Email addEmail(Email email) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("INSERT INTO exercise.email (email_id,first_name,last_name,department,email,alternate,capacity,password,created,modified)" +
                "VALUES(?,?,?,?,?,?,?,?,?)");
        String id = Database.generatorId(5);
        ps.setString(1, id);
        ps.setString(2, email.getFirstName());
        ps.setString(3, email.getLastName());
        ps.setString(4, email.getDepartment());
        ps.setString(5, email.getEmail());
        ps.setString(6, email.getAlternateEmail());
        ps.setInt(7, email.getMailboxCapacity());
        ps.setString(8, email.getPassword());
        ps.setLong(9, System.currentTimeMillis());
        ps.setLong(10, System.currentTimeMillis());
        ps.executeUpdate();
        return email;
    }

    public static void updateEmail(String email_id, String newPassword) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("UPDATE exercise.email SET password = ? WHERE" +
                "email_id = ?");
        ps.setString(1, newPassword);
        ps.setString(2, email_id);
        ps.executeUpdate();

    }

    public static Email getEmail(String email_id) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise.email WHERE email_id = ?");
        ps.setString(1, email_id);
        ResultSet rs = ps.executeQuery();
        Email email = new Email();
        while (rs.next()) {
            email.setEmailId(rs.getString("email_id"));
            email.setFirstName(rs.getString("firstName"));
            email.setLastName(rs.getString("lastName"));
            email.setEmail(rs.getString("email"));
            email.setPassword(rs.getString("password"));
            email.setAlternateEmail(rs.getString("alternateEmail"));
            email.setMailboxCapacity(rs.getInt("mailbox_capacity"));
            email.setDepartment(rs.getString("department"));
            email.setCreated(rs.getLong("created"));
            email.setModified(rs.getLong("modified"));
        }
        return email;
    }

    public static void defineAlternateEmailAdress(String emailId, String alternateEmail) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("UPDATE exercise.email SET alternateEmail = ?" +
                " WHERE email_id = ?");
        ps.setString(1, alternateEmail);
        ps.setString(2, emailId);
        ps.executeUpdate();

    }

}
