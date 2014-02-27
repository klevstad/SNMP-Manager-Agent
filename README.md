SNMP-Manager-Agent
==================

Part of an assignment given in the course 'Network and Service Management' at the Norwegian University of Science and Technology (NTNU). 


This contribution consists of five classes:

<b>SnmpTrapProgram.java</b>

- A program to analyze the incoming traffic on the ethernet card of the host of the program and send a trap to the manager if the traffic exceeds a certain threshold.
- NB: Does not work without a snmp-config file and the NTNU-NOTIFICATION-MIB. The program logs the traps to a local file, and then uses it to create a graph of the traffic that has been logged.

<b>SnmpMailProgram.java</b>

- A program to analyze incoming traffic on the ethernet card of all agents added to the managers list of known agents. The program logs the ip address of the agent, the traffic and a timestamp of the observation. The program sends an email to the manager of the system every hour with the information that has been collected over the past hour.
- NB: Does not work without snmp-config files on the agents allowing the manager to reach them, and specifing which properties of the agent that can be reach from the manager.

<b>JavaMail.java</b>

- A small class using the javax.* library to setup a mail.

<b>LogFileReader.java</b>

- A small class to read from file and create the dataset to be printed.

<b>Printer.java</b>

- Prints the dataset.

