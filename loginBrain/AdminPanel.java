import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AdminPanel
 */
@WebServlet("/AdminPanel")
public class AdminPanel<HttpServletRequest> extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminPanel() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("adminPanel.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = ((ServletRequest) request).getParameter("action");
		int actionInteger = Integer.parseInt(action);
		HttpSession login = ((javax.servlet.http.HttpServletRequest) request).getSession(false);
		String myIdentificationString;
		if(login != null )
			myIdentificationString = login.getAttribute("access").toString();	
		else 
			myIdentificationString = "Failure";
		if( myIdentificationString.equals("AdminSuccess") ){
			if( actionInteger == 1 ){
				PrintWriter print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>Events Submitted</title>\n<body>Event file gives: <p>"+
						printFile() +" </p>You are logged out automatically. Login again <a href=\"index.html\">here</a> to take other action\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else if ( actionInteger == 2 ){
				maintainEvents("events.xml");
				PrintWriter print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>DONE!</title>\n<body>The process was completed successfully!"
						+ " No outdated events exist in database. You are logged out automatically. Login again <a href=\"index.html\">here</a> to take other action\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else if ( actionInteger == 3 ){
				response.sendRedirect("loginAccepted.html");
			}
			else if ( actionInteger == 4 ){
				PrintWriter print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>User DB!</title>\n<body>User DB file gives: <p>"+
					grabDB() +" </p>\n<br>\nYou are logged out automatically. Login again <a href=\"index.html\">here</a> to take other action\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else if ( actionInteger == 5 ){
				//Delete an event
				//Event number and name
				//a list and a submit button and stuff
				login.invalidate();
			}
			else if ( actionInteger == 6 ){
				//Add a user
				login.invalidate();
			}
			else if ( actionInteger == 7 ){
				PrintWriter print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>User DB!</title>\n<body>User DB file gives: <p>"+
					grabLog() +" </p>\n<br>\nYou are logged out automatically. Login again <a href=\"index.html\">here</a> to take other action\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else if ( actionInteger == 8 ){
				File output = new File("log.txt");
				if( !output.exists() )
					output.createNewFile();
				output.setExecutable(true);
				output.setReadable(true);
				output.setWritable(true);
				output.delete();
				output.createNewFile();
				output.setExecutable(false);
				output.setReadable(false);
				output.setWritable(false);
				PrintWriter print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>Done!</title>\n<body>Logs are all cleared"+
					" successfully. \n<br>\nYou are logged out automatically. Login again <a href=\"index.html\">here</a> to take other action\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else if ( actionInteger == 9 ){
				PrintWriter print;
				print = response.getWriter();
				print.println("<!DOCTYPE html>\n<html><title>Logged Out!</title>\n<body>You "+
						"Logged Out successfully."
						+ "\n Thank you for being a part of our staff."
						+ "\n</body>\n</html>");
				print.close();
				login.invalidate();
			}
			else
				login.invalidate();
		}
		else{
			PrintWriter print;
			print = response.getWriter();
			print.println("<!DOCTYPE html>\n<html><title>Not authenticated!</title>\n<body>We are "+
					"experiencing a problem trying to authenticate you."
					+ " Please try to login again <a href=\"index.html\">here</a>. \n We appologise"
					+ " for any inconvenience.\n</body>\n</html>");
			print.close();
		}

	}
	//takes stuff from events.xml file
	private String printFile(){
		int counter = 0;
		String file = "";
		File output = new File("events.xml");
		try {
			for (String line:Files.readAllLines(Paths.get("events.xml"))){
				if(line.indexOf("<event>") >=0 ){
					counter++;
					file += "Event " + counter +".\n<br>";
				}
				if( line.indexOf("<place>")>=0 )
					line += "<br>";
				int start = line.indexOf(">")+1;
				if (line.indexOf("<event>")>=0 )
					continue;
				else if (line.indexOf("</event>")>=0)
					line="<br>";
				else
					line = line.substring(1,start-1)+ ":   " +line.substring( start , line.indexOf("</"));
				line += "<br>";
				file += line+"\n";
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			try {
				output.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return file;
	}
	
	//deletes outdated events
	private void maintainEvents(String toRead) throws IOException{
		File secret = new File(toRead);
		String file = "";
		int day = 0;
		int month = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		String date = sdf.format(new Date());
		try {
			for (String line:Files.readAllLines(Paths.get(toRead))){
				
				if( line.indexOf("<day>") >=0 )
					day = Integer.parseInt(line.substring(5,line.indexOf("</")));
				if( line.indexOf("<month>") >=0 )
					month = Integer.parseInt(line.substring(7,line.indexOf("</")));
				
				if( line.indexOf("</event>") >=0 ){
					if( month < Integer.parseInt(date.substring(3,5)) )
						file = file.substring(0, file.lastIndexOf("</event>"));
					else if (month == Integer.parseInt(date.substring(3,5)) && day < Integer.parseInt(date.substring(0,2)))
						file = file.substring(0, file.lastIndexOf("</event>"));
					day = 0;
					month = 0;
				}
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
		secret.delete();
		secret.createNewFile();
		PrintWriter print = new PrintWriter(secret);
		print.print(file);
		print.flush();
		print.close();
	}
	//grabs user DB
	private String grabDB() throws IOException{
		String file = "<br>";
		File secret = new File("secretData.txt");
		secret.setExecutable(true);
		secret.setReadable(true);
		secret.setWritable(true);
		int stuff = 0;
		//reads the lines for the user's database and dumps it in a String variable
		//called "file"
		for (String line:Files.readAllLines(Paths.get("secretData.txt"))){
			if ( stuff == 0 )
				file += "Username: ";
			else if ( stuff == 1 )
				file += "Password: ";
			else
				file += "Role: ";
			file += line+"<br>";
			if( stuff == 2 )
				file += "<br>";
			stuff++;
			stuff %= 3;
		}
		secret.setExecutable(false);
		secret.setReadable(false);
		secret.setWritable(false);
		return file;
	}
	
	//This method is able to display the logs to the admin requesting it
	private String grabLog() throws IOException{
		String stuff = "<br>";
		File output = new File("log.txt");
		if( !output.exists() )
			output.createNewFile();
		output.setExecutable(true);
		output.setReadable(true);
		output.setWritable(true);
		for (String line:Files.readAllLines(Paths.get("log.txt"))){
			stuff += line+"<br>";
		}
		output.setExecutable(false);
		output.setReadable(false);
		output.setWritable(false);
		return stuff;
		
	}

}