package assignment1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.jfree.ui.RefineryUtilities;

public class SnmpTrapProgram {
	
	private static final String GET_QUERY_BASE = "snmpget -v 2c -c ttm4128 localhost ";
	private static final String GET_RECEIVED_DATAGRAMS_QUERY = "ipInReceives.0";
	private static final String GET_DELIVERED_DATAGRAMS_QUERY = "ipInDelivers.0";
	private static final String GET_IPFORWARDING_INDICATION_QUERY = "ipForwarding.0";
	
	private static final String TRAP_SETUP_QUERY = "sudo snmptrapd -f -Lf /etc/snmp/trapLog.txt";
	private static final String TRAP_QUERY_BASE = "snmptrap -v 2c -c ttm4128 localhost \"\" NTNU-NOTIFICATION-MIB::anotif IP-MIB::";
	private static final String IP_IN_DELIVERS_QUERY = "ipInDelivers.0 c ";
	private static final String IP_IN_RECEIVES_QUERY = "ipInReceives.0 c ";
	private static final String IP_FORWARDING_QUERY = "ipForwarding.0 i ";
	
	private static final String RESTART_QUERY = "sudo /etc/init.d/snmpd restart";
	
	private static final int THRESHOLD = 2200; // can be anything based on manager's request
	private static final int SLEEPTIME_IN_MILLISECONDS = 60000; //should be set to 60000 (equals 60 seconds)
	private static final int TIME_OF_MONITORING_IN_MINUTES = 60;
	
	private Process m_snmpd;
	
	public SnmpTrapProgram()
	{
		try {
			m_snmpd = Runtime.getRuntime().exec(RESTART_QUERY );
			m_snmpd.waitFor();
			Runtime.getRuntime().exec(TRAP_SETUP_QUERY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		SnmpTrapProgram program = new SnmpTrapProgram();
		program.monitorNetwork();
		program.printGraphOfTrafficIfEnoughDataInTrapLog();
	}
	
	public String executeCommand(String query)
	{
		// Code for executing commands in terminal
		StringBuffer output = new StringBuffer();
		Process process;
		try
		{
			process = Runtime.getRuntime().exec(query);
			process.waitFor();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null)
			{
				output.append(line + "\n");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return output.toString();
	}
	
	private void monitorNetwork()
	{
		int tick = 0; // value to indicate how long the program will run. Can be replaced by a 'boolean online = true' and while(online) to create 24/7 availability
		int previousReceivedDatagram = 0;
		
		while(tick < TIME_OF_MONITORING_IN_MINUTES) //to simulate 24/7-online replace with boolean online = true;
		{
			int receivedDatagrams = getSpecifiedNumberOfDatagrams(GET_QUERY_BASE + GET_RECEIVED_DATAGRAMS_QUERY);
			System.out.println(String.format("Previous: %d, Current: %d, Traffic: %d", previousReceivedDatagram, receivedDatagrams, receivedDatagrams-previousReceivedDatagram));
			if(receivedDatagrams - previousReceivedDatagram > THRESHOLD && tick != 0)
			{
				System.out.println("Wops. Too much traffic --> Sending traps to manager.");
				int deliveredDatagrams = getSpecifiedNumberOfDatagrams(GET_QUERY_BASE + GET_DELIVERED_DATAGRAMS_QUERY);
				sendTrap(receivedDatagrams, deliveredDatagrams);
			}
			try
			{
				Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			tick ++;
			previousReceivedDatagram = receivedDatagrams;
		}
	}
	
	private void sendTrap(int receivedDatagrams, int deliveredDatagrams)
	{
		Process process;
		
		try
		{
			// This could've been done in one trap command, but after reviewing the trapLog we decided that
			// it was more conveniently sending it in three commands regarding readability in the log.
			process = Runtime.getRuntime().exec(TRAP_QUERY_BASE + IP_IN_RECEIVES_QUERY + receivedDatagrams);
			process.waitFor();
			process = Runtime.getRuntime().exec(TRAP_QUERY_BASE + IP_IN_DELIVERS_QUERY + deliveredDatagrams);
			process.waitFor();
			process = Runtime.getRuntime().exec(TRAP_QUERY_BASE + IP_FORWARDING_QUERY + EntityActsAsIpGateway());
			process.waitFor();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private int EntityActsAsIpGateway() throws Exception
	{
		String ipForwarding = executeCommand(GET_QUERY_BASE + GET_IPFORWARDING_INDICATION_QUERY);
		
		if(ipForwarding.contains("forwarding(1)"))
		{
			return 1;
		}
		else if(ipForwarding.contains("notForwarding(2)"))
		{
			return 2;
		}
		else
		{
			throw new Exception(String.format("The return message from '%s' didn't contain \"forwarding(1) or \"notForwarding(2)", GET_QUERY_BASE + GET_IPFORWARDING_INDICATION_QUERY));
		}
	}
	
	private int getSpecifiedNumberOfDatagrams(String specifiedQuery)
	{
		String datagrams = executeCommand(specifiedQuery);
		int pos = datagrams.indexOf("Counter32: ");
		return Integer.parseInt(datagrams.substring(pos+11).trim());
	}

	private void printGraphOfTrafficIfEnoughDataInTrapLog()
	{
		LogFileReader logFileReader = new LogFileReader();
		ArrayList<Integer> samples = logFileReader.getM_samples();
		if(samples.size() > 0)
		{
			Printer printer = new Printer("Use Case 1.4 - Helping Adam the Admin", logFileReader.getM_samples());
		    printer.pack();
		    RefineryUtilities.centerFrameOnScreen(printer);
		    printer.setVisible(true);
		}
	}
}
