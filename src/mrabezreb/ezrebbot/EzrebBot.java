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
		checkPeople();
	}
	
	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		if(!people.containsKey(sender)) {
			Person newPerson = new Person();
			newPerson.nick = sender;
			newPerson.fans = 0;
			people.put(sender, newPerson);
		}
		checkPeople();
	}
	
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(message.startsWith("!")) {
			System.out.println(sender+": "+message);
			onCommand(channel, sender, login, hostname, message);
		} else {
			processMessage(channel, sender, message);
			System.out.println(sender+": "+message);
		}
	}
	
	protected void processMessage(String channel, String sender, String message) {
		if(message.contains("fuck")) {
			sendMessage(channel, "/timeout "+sender+" 1");
			sendMessage(channel, "Please do not use profanity!");
		}
		if(message.contains("shit")) {
			sendMessage(channel, "/timeout "+sender+" 1");
			sendMessage(channel, "Please do not use profanity!");
		}
		if(message.contains("ass")) {
			sendMessage(channel, "/timeout "+sender+" 1");
			sendMessage(channel, "Please do not use profanity!");
		}
		if(message.contains("damn")) {
			sendMessage(channel, "/timeout "+sender+" 1");
			sendMessage(channel, "Please do not use profanity!");
		}
		if(message.contains("bitch")) {
			sendMessage(channel, "/timeout "+sender+" 1");
			sendMessage(channel, "Please do not use profanity!");
		}
	}
	
	protected void onCommand(String channel, String sender, String login, String hostname, String command) {
		if(command.equals("!leave")) {
			if(!sender.toLowerCase().equals("mrabezreb")) {
				return;
			}
			fanGiver.interrupt();
			peopleAdder.interrupt();
			if(noGUI == false) {
				ControlPanel.close();
			}
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
					e.printStackTrace();
				}
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File("EzrebBot.properties"));
				settings.store(fos, "");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			disconnect();
			dispose();
			System.exit(0);
		} else if(command.equals("!hi")) {		
			sendMessage(channel, "Hi there, "+sender);
		} else if(command.equals("!fans")) {
			sendMessage(channel, sender+"\'s non-existant fanbase is "+people.get(sender.toLowerCase()).fans+" fans strong!");
		} else if(command.startsWith("!roll")) {
			rollFormulas(command.substring(6));
		} else if(command.equals("!yomamma")) {
			sendMessage(channel, getYoMamma());
		} else if(command.startsWith("!here")) {
			String usern = command.substring(6);
			sendMessage(channel, "User "+usern+" is currently "+(people.get(usern.toLowerCase()).isWatching() ? "online" : "offline"));
		} else if(command.equals("!debugPeople")) {
			if(!sender.toLowerCase().equals("mrabezreb")) {
				return;
			}
			Collection<Person> peeps = people.values();	
			for (Person person : peeps) {
				System.out.println("Person: "+person.nick+"/"+person.isWatching());
			}
		} else if(command.equals("!resetPeople")) {
			if(!sender.toLowerCase().equals("mrabezreb")) {
				return;
			}
			people.clear();
			checkPeople();
		} else if(command.startsWith("!addFans")) {
			if(!sender.toLowerCase().equals("mrabezreb")) {
				return;
			}
			addFans(command.substring(9));
		} else if(command.equals("!meow")) {
			sendMessage(channel, "Meow meow meow meow meow meow meow meow! :D");
		}
	}
	
	protected void addFans(String args) {
		String name = args.substring(0, args.indexOf(" "));
		int points = new Integer(args.substring(args.indexOf(" ")+1));
		name = name.toLowerCase();
		if(people.containsKey(name)) {
			people.get(name).fans += points;
			sendMessage(settings.getProperty("channel"), "Added "+points+" points to "+name);
		} else {
			sendMessage(settings.getProperty("channel"), name+" has never been here. :P");
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
	
	protected void addFans() {
		Collection<Person> peeps = people.values();
		for (Person person : peeps) {
			if(person.isWatching()) {
				person.fans += 5;
			}
		}
	}
	
	protected void checkPeople() {
		User[] currentUsers = getUsers(settings.getProperty("channel"));
		for (User user : currentUsers) {
			String sender2 = user.getNick().toLowerCase();
			if(!people.containsKey(sender2)) {
				Person newPerson = new Person();
				newPerson.nick = sender2;
				newPerson.fans = 0;
				people.put(sender2, newPerson);
			}
		}
	}
	
	protected void rollFormulas(String formulas) {
		int roll = 0;
		boolean hasMore = true;
		String nextMod = "+";
		String cutForms = formulas;
		while(hasMore) {
			int plus = cutForms.indexOf("+");
			int minus = cutForms.indexOf("-");
			hasMore = !((cutForms.indexOf("+") == -1) && (cutForms.indexOf("-") == -1));
			if(hasMore) {
				int nextLoc = 0;
				if(plus == -1) {
					nextLoc = minus;
				} else if(minus > -1 && plus > -1) {
					nextLoc = ((plus < minus)) ? plus : minus;
				} else if(minus == -1) {
					nextLoc = plus;
				}
				String nextForm = cutForms.substring(0, nextLoc);
				int dieRolls = rollDice(nextForm);
				dieRolls = new Integer(nextMod+dieRolls);
				roll += dieRolls;
				if(plus == -1) {
					nextMod = "-";
				} else if(minus > -1 && plus > -1) {
					nextMod = ((plus < minus)) ? "+" : "-";
				} else if(minus == -1) {
					nextMod = "+";
				}
				cutForms = cutForms.substring(nextLoc+1);
			} else {
				int dieRolls = rollDice(cutForms);
				dieRolls = new Integer(nextMod+dieRolls);
				roll += dieRolls;
			}
		}
		System.out	.println("Rolled "+formulas+" and got "+roll);
		sendMessage(settings.getProperty("channel"), ""+roll);
	}
	
	protected int rollDice(String dieFormula) {
		if(dieFormula.indexOf("d") == -1) {
			return new Integer(dieFormula);
		} else {
			int num = new Integer(dieFormula.substring(0, dieFormula.indexOf("d")));
			int sides = new Integer(dieFormula.substring(dieFormula.indexOf("d")+1));
			int roll = 0;
			for (int i = 1; i <= num; i++) {
				roll += rollDie(sides);
			}
			return roll;
		}
	}
	
	protected Random random = new Random();
	
	protected int rollDie(int sides) {
		//return sides;
		return random.nextInt(sides)+1;
	}
	
	private static boolean noGUI = false;
	
	public static void main(String[] args) {
		bot = new EzrebBot();
		if(args.length == 1) {
			if(args[0].equals("--nogui")) {
				noGUI = true;
			}
		}
		try {
			new File("EzrebBot.properties").createNewFile();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Properties prop = new Properties();
		prop.setProperty("server", "irc.twitch.tv");
		prop.setProperty("channel", "#mrabezreb");
		bot.settings = new Properties(prop);
		try {
			bot.settings.load(new FileInputStream(new File("EzrebBot.properties")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(noGUI == false) {
			//ControlPanel.main();
		}
		try {
			bot.connect(bot.settings.getProperty("server"), 6667, "oauth:8nd7xf50x1wqhuwl02ylgal27jwxzl");
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
