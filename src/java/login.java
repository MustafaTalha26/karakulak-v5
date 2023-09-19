
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean( name="val" )
@SessionScoped
public class login {
    private String name = "";
    private String password = "";    
    private int sepetTutar =  0;
    private int silenecek_order_id;
    private int eklenecek_ürün;
    private int eklenecek_miktar;
    private String odemeUyarı;
    private int yuklenecek_bakiye;

    public int getYuklenecek_bakiye() {
        return yuklenecek_bakiye;
    }

    public void setYuklenecek_bakiye(int yuklenecek_bakiye) {
        this.yuklenecek_bakiye = yuklenecek_bakiye;
    }

    public String getOdemeUyarı() {
        return odemeUyarı;
    }

    public void setOdemeUyarı(String odemeUyarı) {
        this.odemeUyarı = odemeUyarı;
    }

    public int getEklenecek_ürün() {
        return eklenecek_ürün;
    }

    public void setEklenecek_ürün(int eklenecek_ürün) {
        this.eklenecek_ürün = eklenecek_ürün;
    }

    public int getEklenecek_miktar() {
        return eklenecek_miktar;
    }

    public void setEklenecek_miktar(int eklenecek_miktar) {
        this.eklenecek_miktar = eklenecek_miktar;
    }
    
    
    
    private int ID = 0;
    private String firstName = "";
    private String lastName = "";
    private String address = "";
    private String email = "";
    private String mobil = "";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
    
    

    public void setSepetTutar(int sepetTutar) {
        this.sepetTutar = sepetTutar;
    }
    public int getSepetTutar() {
        return sepetTutar;
    }

    public int getSilenecek_order_id() {
        return silenecek_order_id;
    }
    public void setSilenecek_order_id(int silenecek_order_id) {
        this.silenecek_order_id = silenecek_order_id;
    }
    
    CachedRowSet rowSet=null;
    DataSource dataSource;
    public login() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup("jdbc/sample");
            
        } 
        catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return this.password;
    }
    public String response() throws SQLException{
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "select EMAIL,PASSWORD" +
            " from APP.USERSTABLE " +
            "where EMAIL=?" +
            "and PASSWORD=?");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                if ( name.equals("")  && password.equals("") ){
                    return"";
                }
                if(rs.getString(1).equals(name) && rs.getString(2).equals(password)) {
                    return "i2.xhtml";
                }
                else {
                    return "";
                }
            }
        }
        finally {
            connection.close();
        }
        return "";
    }
    
    public ResultSet sepetGoster() throws SQLException {
        ResultSet rs;
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "SELECT ORDERS.ORDER_ID,PRODUCTS.PRODUCT_ID as URUN_KODU,ORDERS.AMOUNT as Miktar,PRODUCTS.PER_COST*ORDERS.AMOUNT as COST" +
            " FROM ORDERS,USERSTABLE,PRODUCTS " +
            " WHERE ORDERS.USER_ID = USERSTABLE.ID " +
            " AND ORDERS.PRODUCT_ID = PRODUCTS.PRODUCT_ID " + 
            " AND ORDERS.STATUS = 'SEPET' " +       
            " AND USERSTABLE.EMAIL = ? ");
            ps.setString(1, name);
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            rs = ps.executeQuery();
            
            return rowSet;
        }
        finally {
            connection.close();
        }
    }
    
    public String sil() throws SQLException {
    if ( dataSource == null ){ 
        throw new SQLException( "Unable to obtain DataSource" );
    }
    Connection connection = dataSource.getConnection();
    if ( connection == null )
        throw new SQLException( "Unable to connect to DataSource" );
    try {
        PreparedStatement myObject = connection.prepareStatement( "delete from ORDERS where ORDER_ID=?");
        myObject.setInt( 1, getSilenecek_order_id() );
        myObject.executeUpdate(); 
        sepetToplam();
        return "Sepet.xhtml";
    }
    finally
    {
        connection.close(); 
    } 
} 
    
    public String sepetToplam() throws SQLException{
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "SELECT sum(PRODUCTS.PER_COST*ORDERS.AMOUNT)" +
            " FROM ORDERS,USERSTABLE,PRODUCTS " +
            " WHERE ORDERS.USER_ID = USERSTABLE.ID " +
            " AND ORDERS.PRODUCT_ID = PRODUCTS.PRODUCT_ID " + 
            " AND ORDERS.STATUS = 'SEPET' " +       
            " AND USERSTABLE.EMAIL = ? ");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                sepetTutar = rs.getInt(1);
            }
            odemeUyarı = "";
            return "Sepet.xhtml";
        }
        finally {
            connection.close();
        }
    }
    
    public String bilgiGoster() throws SQLException {
        ResultSet rs;
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "SELECT *" +
            " FROM USERSTABLE " + 
            " WHERE USERSTABLE.EMAIL = ? ");
            ps.setString(1, name);
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            rs = ps.executeQuery();
            while(rs.next()) {
                this.setID(rs.getInt(1));
                this.setFirstName(rs.getString(2));
                this.setLastName(rs.getString(3));
                rs.getInt(4);
                this.setAddress(rs.getString(5));
                this.setEmail(rs.getString(6));
                this.setMobil(rs.getString(7));
            }
            return "Bilgilerim";
        }
        finally {
            connection.close();
        }
    }
    
    public void siparişEkle() throws SQLException {
        ResultSet rs;
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            if(eklenecek_miktar != 0) {
                PreparedStatement ps = connection.prepareStatement( "INSERT INTO APP.ORDERS (PRODUCT_ID, USER_ID, AMOUNT, STATUS) " +
                " VALUES (?, (SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?), ?, 'SEPET')");
                ps.setInt(1, eklenecek_ürün);
                ps.setString(2, name);
                ps.setInt(3, eklenecek_miktar);
                ps.executeUpdate();
                sepetToplam();
            }
        }
        finally {
            connection.close();
        }
    }
    
    public String siparişÖde() throws SQLException {
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "UPDATE ORDERS SET STATUS = 'GONDERI' WHERE USER_ID =  " +
            " (SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?) ");
            ps.setString(1, name);
            ps.executeUpdate();
            odemeUyarı = "Ödendi";
            return "Sepet";
        }
        finally {
            connection.close();
        }
    }
    
    public ResultSet gecmisSiparis() throws SQLException {
        ResultSet rs;
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "SELECT ORDERS.ORDER_ID,PRODUCTS.PRODUCT_ID as URUN_KODU,ORDERS.AMOUNT as Miktar,PRODUCTS.PER_COST*ORDERS.AMOUNT as COST" +
            " FROM ORDERS,USERSTABLE,PRODUCTS " +
            " WHERE ORDERS.USER_ID = USERSTABLE.ID " +
            " AND ORDERS.PRODUCT_ID = PRODUCTS.PRODUCT_ID " + 
            " AND ORDERS.STATUS = 'GONDERI' " +       
            " AND USERSTABLE.EMAIL = ? ");
            ps.setString(1, name);
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            rs = ps.executeQuery();
            return rowSet;
        }
        finally {
            connection.close();
        }
    }
    
    public ResultSet avgPrice() throws SQLException {
        ResultSet rs;
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            PreparedStatement ps = connection.prepareStatement( "SELECT MANUF.ID as ID,MANUF.\"NAME\" as ISIM,MANUF.EMAIL as EMAIL,MANUF.ADDRESS as ADDRESS,AVG(PER_COST) as ORT "
            + " FROM MANUF,PRODUCTS WHERE PRODUCTS.MANUFACTURER_ID = MANUF.ID "
            + " GROUP BY MANUF.ID,MANUF.\"NAME\",MANUF.EMAIL,MANUF.ADDRESS ");
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            rs = ps.executeQuery();
            return rowSet;
        }
        finally {
            connection.close();
        }
    }
    public int bakiye() throws SQLException {
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            int x = 0;
            PreparedStatement ps = connection.prepareStatement( "SELECT AMOUNT FROM CREDITS WHERE USER_ID ="
            + "(SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?)");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                x = rs.getInt(1);
            }
            return x;
        }
        finally {
            connection.close();
        }
    }
    
    public String cüzdanileÖde() throws SQLException {
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            if(bakiye()-sepetTutar>0) {
                PreparedStatement ps = connection.prepareStatement("UPDATE CREDITS SET AMOUNT = ? WHERE USER_ID = " +
                " (SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?) ");
                ps.setInt(1, bakiye()-sepetTutar);
                ps.setString(2, name);
                ps.executeUpdate();
                siparişÖde();
                odemeUyarı = "Ödendi";
                return "Sepet";
            }
            else {
                odemeUyarı = "Yeterli Bakiye Yok";
                return "Sepet";
            }
        }
        finally {
            connection.close();
        }
    }
    public String bakiyeyükle() throws SQLException {
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource");
            Connection connection = dataSource.getConnection();
        if ( connection == null ) 
            throw new SQLException( "Unable to connect to DataSource" );
        try{
            if(yuklenecek_bakiye>0) {
                PreparedStatement ps = connection.prepareStatement("UPDATE CREDITS SET AMOUNT = ? WHERE USER_ID = " +
                " (SELECT USERSTABLE.ID FROM USERSTABLE WHERE USERSTABLE.EMAIL = ?) ");
                ps.setInt(1, (bakiye()+yuklenecek_bakiye));
                ps.setString(2, name);
                ps.executeUpdate();
            }
        }
        finally {
            connection.close();
        }
        return "Sepet";
    }
}
