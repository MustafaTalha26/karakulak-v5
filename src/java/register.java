import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

import java.sql.ResultSet;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean( name="registerBean" )
@SessionScoped
public class register {
    private String firstName = "";
    private String lastName = "";
    private String password = "";
    private String address = "";
    private String email = "";
    private String mobil = "";
    
    CachedRowSet rowSet=null;
    DataSource dataSource;
    
    public register() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup("jdbc/sample");
            
        } 
        catch (NamingException e) {
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }
    
    public String init() throws SQLException, ClassNotFoundException{
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );

        
        try{
           ResultSet rs =null;
        PreparedStatement ps = connection.prepareStatement("INSERT INTO APP.USERSTABLE (FIRST_NAME, LAST_NAME, PASSWORD, ADDRESS, EMAIL, MOBIL)"
                + "VALUES(?,?,?,?,?,?)");
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, password);
        ps.setString(4, address);
        ps.setString(5, email);
        ps.setString(6, mobil);
        ps.executeUpdate();
        ps.close();
        }
        finally {
            connection.close();
        }
        cuzdanOlustur();
        return "login.xhtml";
    }
    public void cuzdanOlustur() throws SQLException{
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "INSERT INTO APP.CREDITS (USER_ID, AMOUNT)" +
            " VALUES ((SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?), 100)" );
            ps.setString(1, email);
            ps.executeUpdate();
        }
        finally {
            connection.close();
        }
    }
}
