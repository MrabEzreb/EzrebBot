package mrabezreb.ezrebbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import mrabezreb.ezrebbot.data.Person;
import mrabezreb.ezrebbot.gui.ControlPanel;

public class EzrebBot extends PircBot {

	public static EzrebBot bot;
	public Map<String, Person> people = new HashMap<>();
	public Properties settings;
	
	public static Thread fanGiver = new Thread(new Runnable() {
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(600000);
					EzrebBot.bot.addFans();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	});
	public static Thread peopleAdder = new Thread(new Runnable() {
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(10000);
					EzrebBot.bot.checkPeople();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	});
	
	@SuppressWarnings("unchecked")
	public EzrebBot() {
		setName("ezrathebot");
		ObjectInputStream oos = null;
		try {
			if(!new File("People.people").createNewFile()){
				try {
					oos = new ObjectInputStream(new FileInputStream(new File("People.people")));
					try {
						people = (Map<String, Person>) oos.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	protected void onConnect() {
		sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
		joinChannel(settings.getProperty("channel"));
		sendMessage(settings.getProperty("channel"), "Heyo everybody! Wassup? I'm EzraTheBot, a custom made bot by MrabEzreb.");
		User[] currentUsers = getUsers(settings.getProperty("channel"));
		for (User user : currentUsers) {
			String sender2 = user.getNick();
			if(!people.containsKey(sender2)) {
				Person newPerson = new Person();
				newPerson.nick = sender2;
				newPerson.fans = 0;
				people.put(sender2, newPerson);
			}
		}
	}
	
	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		if(!people.containsKey(sender)) {
			Person newPerson = new Person();
			newPerson.nick = sender;
			newPerson.fans = 0;
			people.put(sender, newPerson);
		}
		User[] currentUsers = getUsers(settings.getProperty("channel"));
		for (User user : currentUsers) {
			String sender2 = user.getNick();
			if(!people.containsKey(sender2)) {
				Person newPerson = new Person();
				newPerson.nick = sender2;
				newPerson.fans = 0;
				people.put(sender2, newPerson);
			}
		}
	}
	
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(message.startsWith("!")) {
			System.out.println(sender+": "+message);
			onCommand(channel, sender, login, hostname, message);
		} else {
			System.out.println(sender+": "+message);
		}
	}
	
	protected void onCommand(String channel, String sender, String login, String hostname, String command) {
		if(!sender.toLowerCase().equals("mrabezreb")) {
			return;
		}
		if(command.equals("!leave")) {
			disconnect();
			dispose();
			fanGiver.interrupt();
			peopleAdder.interrupt();
			ControlPanel.close();
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(new File("People.people")));
				oos.writeObject(people);
				oos.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.exit(0);
		} else if(command.equals("!hi")) {		
			sendMessage(channel, "Hi there, "+sender);
		} else if(command.equals("!fans")) {
			sendMessage(channel, sender+"\'s non-existant fanbase is "+people.get(sender).fans+" fans strong!");
		} else if(command.equals("!restart")) {
			disconnect();
			dispose();
			main(new String[0]);
		} else if(command.startsWith("!roll")) {
			sendMessage(channel, ""+new Random().nextInt(new Integer(command.substring(6))+1));
		} else if(command.equals("!yomamma")) {
			sendMessage(channel, getYoMamma());
		} else if(command.startsWith("!here")) {
			String usern = command.substring(6);
			sendMessage(channel, "User "+usern+" is currently "+(people.get(usern).isWatching() ? "online" : "offline"));
		} else if(command.equals("!debugPeople")) {
			Collection<Person> peeps = people.values();
			System.err.println(peeps.size());
			for (Person person : peeps) {
				System.err.println("Person: "+person.nick+"/"+person.isWatching());
			}
		}
	}
	
	protected String getYoMamma() {
		URL yoMamma = null;
		try {
			yoMamma = new URL("http://api.yomomma.info/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(yoMamma.openConnection().getInputStream()));
			String json = br.readLine();
			String yoMamaJoke = json.substring(9, json.indexOf("\"}"));
			return yoMamaJoke;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	protected void onUserList(String channel, User[] users) {
		super.onUserList(channel, users);
		checkPeople();
	}
	
	public void addFans() {
		Collection<Person> peeps = people.values();
		for (Person person : peeps) {
			if(person.isWatching()) {
				person.fans += 5;
			}
		}
	}
	
	public void checkPeople() {
		User[] currentUsers = getUsers(settings.getProperty("channel"));
		for (User user : currentUsers) {
			String sender2 = user.getNick();
			if(!people.containsKey(sender2)) {
				Person newPerson = new Person();
				newPerson.nick = sender2;
				newPerson.fans = 0;
				people.put(sender2, newPerson);
			}
		}
	}
	
	public static void main(String[] args) {
		bot = new EzrebBot();
		if(args.length == 1) {
			//bot.channel = args[0];
		}
		try {
			new File("EzrebBot.properties").createNewFile();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Properties prop = new Properties();
		prop.setProperty("channel", "#mrabezreb");
		bot.settings = new Properties(prop);
		try {
			bot.settings.load(new FileInputStream(new File("EzrebBot.properties")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ControlPanel.main();
		try {
			bot.connect("localhost", 6667, "oauth:8nd7xf50x1wqhuwl02ylgal27jwxzl");
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
		fanGiver.start();
		peopleAdder.start();
	}
}
