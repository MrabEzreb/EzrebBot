package mrabezreb.ezrebbot.data;

import java.io.Serializable;

import org.jibble.pircbot.User;

import mrabezreb.ezrebbot.EzrebBot;

public class Person implements Serializable {

	private static final long serialVersionUID = -1024462199393196212L;
	
	public String nick;
	public int fans;
	
	public boolean isWatching() {
		User[] users = EzrebBot.bot.getUsers(EzrebBot.bot.settings.getProperty("channel"));
		for (User user : users) {
			if(user.getNick().equalsIgnoreCase(nick)) {
				return true;
			}
		}
		return false;
	}
}