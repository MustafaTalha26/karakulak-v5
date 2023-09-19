import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean( name="update" )
@SessionScoped
public class update {
    private String email = "";
    private String oldPassword = "";
    private String newPassword = "";
    
    CachedRowSet rowSet=null;
    DataSource dataSource;
    
    public update() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup("jdbc/sample");
            
        } 
        catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    
    public String changePassword() throws SQLException, ClassNotFoundException{
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );

        
        try{
            PreparedStatement ps = connection.prepareStatement( "UPDATE APP.USERSTABLE SET PASSWORD= ? WHERE EMAIL= ? AND PASSWORD= ?");
            ps.setString(1, newPassword);
            ps.setString(2, email);
            ps.setString(3, oldPassword);
            ps.executeUpdate();
        }
        finally {
            connection.close();
        }
        return "Bilgilerim.xhtml";
    }
}
