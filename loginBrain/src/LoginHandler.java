import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FilePermission;

/**
 * Servlet implementation class LoginHandler
 */
@WebServlet("/LoginHandler")
public class LoginHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */

	//instance variables
	String un;
	String pw;
	File secret = new File("secretData.txt");


	PrintWriter print;
	HttpSession session;
	FilePermission permit;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("index.html");
		secret.setExecutable(false);
		secret.setReadable(false);
		secret.setWritable(false);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//getting input from login
		String un = request.getParameter("un");
		String pw = request.getParameter("pw");
		secret.setReadable(true);
		//creating a new session
		session = request.getSession(true);
		session.setMaxInactiveInterval( 15*60 );
		int loginCheck = loginCheck(un, pw);
		if ( loginCheck == 2){
			// set session info
			secret.setReadable(true);
			String dataName = "access";
			String dataValue = "Success";
			session.setAttribute(dataName, dataValue);
			secret.setReadable(false);
			response.sendRedirect("loginAccepted.html");
		}
		else if ( loginCheck == 1){
			secret.setReadable(true);
			String dataName = "access";
			String dataValue = "AdminSuccess";
			session.setAttribute(dataName, dataValue);
			secret.setReadable(false);
			response.sendRedirect("AdminPanel");
		}
		
		else {
			secret.setReadable(true);
			String dataName = "access";
			String dataValue = "Failure";
			session.setAttribute(dataName, dataValue);
			secret.setReadable(false);
			response.sendRedirect("loginDenied.html");
		}
		
		File output = new File("log.txt");
		if( !output.exists() )
			output.createNewFile();
		output.setExecutable(true);
		output.setReadable(true);
		output.setWritable(true);
		boolean returnedLogin = false;
		if( loginCheck == 1 || loginCheck == 2 )
			returnedLogin = true;
		print = new PrintWriter(new FileWriter(output, true));
		SimpleDateFormat sdf = new SimpleDateFormat("{ dd/MM/YYYY -- HH:mm }");
		print.println( sdf.format(new Date()) + " -- Username: " 
						+ un + " tried to login. Login returned: " 
						+ returnedLogin );
		print.flush();
		output.setExecutable(false);
		output.setReadable(false);
		output.setWritable(false);
	}

	private int loginCheck(String user, String pass) {
		String file = "";
		//reads the lines for the user's database and dumps it in a String variable
		//called "file"
		try {
			for (String line:Files.readAllLines(Paths.get("secretData.txt"))){
				file += line+"\n";
			}
		}

		catch (IOException e) {
			e.printStackTrace();
			System.out.print("file doesn't exits");
			try {
				secret.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if ( (file.indexOf(user)>=0) && (file.indexOf(pass)>=0)&& (file.indexOf(pass)>=(file.indexOf(user)+user.length()))){
			int role = file.substring(file.indexOf(user)+user.length()+pass.length()).indexOf("admin");
			if ( role>= 0 && role <= 4 )
				return 1;
			secret.setExecutable(false);
			secret.setReadable(false);
			secret.setWritable(false);
			return 2;
		}
		else{
			secret.setExecutable(false);
			secret.setReadable(false);
			secret.setWritable(false);
			return 0;
		}
	}
}
