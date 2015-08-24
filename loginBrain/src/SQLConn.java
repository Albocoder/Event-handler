import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class SQLConn {
	Connection con;
	Statement st;
	ResultSet rs = null;
	
	public SQLConn(String url, String username, String password){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url,username,password);
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ResultSet getData(){
		try{
			
			rs = st.executeQuery("SELECT * FROM users");
		}
		catch(Exception e){
			System.out.println("Error: " + e);
		}
		return rs;
	}
}
