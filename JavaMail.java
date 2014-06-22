package assigment1;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMail
{
	public JavaMail()
	{
	}
	
   public void CreateAndSendMail(String to, String from, String report)
   {
	   try{
		   String host = "localhost";
		   String subject = "TTM4128: Use Case 1.5 - Report to Administrator Ruth from her Monitoring System.";
		   Properties properties = System.getProperties();
		   properties.setProperty("smtp.gmail.com", host);
		   Session session = Session.getDefaultInstance(properties);

		   MimeMessage message = new MimeMessage(session);
		   message.setFrom(new InternetAddress(from));
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		   message.setSubject(subject);
		   message.setText(report);

		   Transport.send(message);
		   System.out.println(String.format("Report has been sent successfully to %s.", to));
	      }
	   catch (MessagingException mex)
	   {
		   mex.printStackTrace();
	   }
   }
}
