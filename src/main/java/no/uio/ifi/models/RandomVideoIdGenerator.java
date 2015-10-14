package no.uio.ifi.models;

import java.util.Random;

public class RandomVideoIdGenerator {
	
	
	public String getNextRandom() {
		String alfabet = "0123456789_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		String randomValue = "";
		for (int i = 0; i < 4; i++) {
			randomValue += alfabet.charAt(random.nextInt(alfabet.length()));
		}
		return randomValue;
	}
}
