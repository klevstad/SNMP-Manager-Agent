package assignment1;

import java.util.Date;
import java.util.HashMap;

public class SnmpMailProgram extends SnmpTrapProgram{
	
	private static final String[] AGENT_IP_ADDRESSES = {"129.241.209.3", "129.241.209.32"};
	private static final String GET_QUERY_BASE = "snmpget -v 2c -c ttm4128 ";
	private static final String GET_RECEIVED_DATAGRAMS_QUERY = " ipInReceives.0";
	private static final String RESTART_QUERY = "sudo /etc/init.d/snmpd restart";
	
	private static final Integer SLEEPTIME_IN_MILLISECONDS = 60000; // Should be 60000 (equals 60 seconds = 1 minute)
	private static final Integer MINUTES_BEFORE_NOTIFYING_MANAGER = 60; // Should be 60 (equals 60 seconds * 60 times = 3600 seconds = 1 hour)
	
	private static final String REPORT_HEADER = "IP address\tTime (UTC)\t\t\tTraffic\n";
	private static final String MAIL_TO = "vladimir@putin.ru";
	private static final String MAIL_FROM = "barack@obama.gov";
	
	private String m_stringWithDataBelongingToIpAddress;
	private HashMap<String, String> map;
	private Process m_snmpd;
	private Integer m_previousRecievedDatagramsAtAgentA;
	private Integer m_previousRecievedDatagramsAtAgentB;
	
	public SnmpMailProgram()
	{
		map = new HashMap<String, String>();
		setM_stringWithDataBelongingToIpAddress("");
		m_previousRecievedDatagramsAtAgentA = 0;
		m_previousRecievedDatagramsAtAgentB = 0;
		
		for(String ip : AGENT_IP_ADDRESSES)
		{
			map.put(ip, m_stringWithDataBelongingToIpAddress);
		}
		try {
			m_snmpd = Runtime.getRuntime().exec(RESTART_QUERY);
			m_snmpd.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		Boolean online = true;
		while(online)
		{
			SnmpMailProgram program = new SnmpMailProgram();
			program.monitorNetwork();
			online = false; // This line can be removed to simulate a 24/7 online system. Replace with for-loop to have more than one iteration.
		}
	}
	
	private void monitorNetwork()
	{
		String report = "";
		int tick = 0;
		while(tick < MINUTES_BEFORE_NOTIFYING_MANAGER)
		{
			try {
				updateMapWithIpAddressTimestampAndReceivedDatagrams();
				Thread.sleep(SLEEPTIME_IN_MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tick ++;
			System.out.println(String.format("Ticks: %d/%d", tick, MINUTES_BEFORE_NOTIFYING_MANAGER));
		}
		report = createReport();
		createAndSendMail(report);
	}
	
	private void updateMapWithIpAddressTimestampAndReceivedDatagrams()
	{	
		for(String ipAddress : AGENT_IP_ADDRESSES)
		{
			String datagrams = executeCommand(GET_QUERY_BASE + ipAddress + GET_RECEIVED_DATAGRAMS_QUERY);
			int pos = datagrams.indexOf("Counter32: ");
			try{
				
				int received = Integer.parseInt(datagrams.substring(pos+11).trim());
				int previous = getPreviousReceivedDatagramSizeOfIpAddress(ipAddress);
				int traffic = received - previous;
				
				String data = map.get(ipAddress);
				
				// Creating a neat printing layout for the data in the report.
				if(data.isEmpty())
				{
					setM_stringWithDataBelongingToIpAddress(String.format("%s%s\t%s\tno sample\n\t", ipAddress, data, new Date().toString()));
				}
				else
				{
					setM_stringWithDataBelongingToIpAddress(String.format("%s\t%s\t%d\n\t", data, new Date().toString(), traffic));
				}
				map.put(ipAddress, m_stringWithDataBelongingToIpAddress);
				
				setPreviousReceivedDatagramSizeOfIpAddress(ipAddress, received);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				System.out.println("ErrorMessage: Could not query your agents. Please make sure they are running the right configurations.");
				e.printStackTrace();
			}
		}
	}
	
	private Integer getPreviousReceivedDatagramSizeOfIpAddress(String ipAddress)
	{
		if(ipAddress.equals(AGENT_IP_ADDRESSES[0]))
		{
			return m_previousRecievedDatagramsAtAgentA;
		}
		else if(ipAddress.equals(AGENT_IP_ADDRESSES[1]))
		{
			return m_previousRecievedDatagramsAtAgentB;
		}
		else
		{
			return -1;
		}
	}
	
	private void setPreviousReceivedDatagramSizeOfIpAddress(String ipAddress, Integer newDatagramSize)
	{
		if(ipAddress.equals(AGENT_IP_ADDRESSES[0]))
		{
			m_previousRecievedDatagramsAtAgentA = newDatagramSize;
		}
		if(ipAddress.equals(AGENT_IP_ADDRESSES[1]))
		{
			m_previousRecievedDatagramsAtAgentB = newDatagramSize;
		}
	}

	private String createReport()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(REPORT_HEADER);

		for(String key : getMap().keySet())
		{
			builder.append(map.get(key) + "\n");
		}
		return builder.toString();
	}
	
	private void createAndSendMail(String reportToManager)
	{
		JavaMail mail = new JavaMail();
		mail.CreateAndSendMail(MAIL_TO, MAIL_FROM, reportToManager);
		System.out.println(reportToManager);
	}
	
	private HashMap<String, String> getMap() {
		return map;
	}
	
	private void setM_stringWithDataBelongingToIpAddress(String m_data) {
		this.m_stringWithDataBelongingToIpAddress = m_data;
	}
}
