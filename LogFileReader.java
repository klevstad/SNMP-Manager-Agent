package assignment1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class LogFileReader {
	
	private static final String PATH_TO_LOG_FILE = "/etc/snmp/trapLog.txt"; // Set the path to your trapLog.
	private ArrayList<Integer> m_trafficSamples;
	
	public LogFileReader()
	{
		m_trafficSamples = getArrayOfReceivedDatagramsFromFile(PATH_TO_LOG_FILE);
	}
	
	public ArrayList<Integer> getArrayOfReceivedDatagramsFromFile(String path)
	{
		ArrayList<Integer> arrayOfTrafficSamples = new ArrayList<Integer>();
		Scanner inputFile;
		try {
			inputFile = new Scanner(new FileReader(path));
			int previous = 0;
			
			while (inputFile.hasNextLine()) {
	            String line = inputFile.nextLine();
	            if(line.contains("ipInReceives.0"))
	            {
	        		int pos = line.indexOf("Counter32: ");
	        		int received = Integer.parseInt(line.substring(pos+11).trim());
	        		int traffic = received - previous;
	        		//System.out.println(String.format("previous: %d, current: %d, traffic: %d", previous, received, traffic));
	        		arrayOfTrafficSamples.add(traffic);
	        		previous = received;
	            }
	        }
	        inputFile.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		if(arrayOfTrafficSamples.size() > 1)
		{
			arrayOfTrafficSamples.remove(0);
	        return arrayOfTrafficSamples;
		}
        return arrayOfTrafficSamples;
	}

	public ArrayList<Integer> getM_samples() {
		return m_trafficSamples;
	}
}
