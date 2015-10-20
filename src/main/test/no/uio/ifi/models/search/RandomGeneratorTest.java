package no.uio.ifi.models.search;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomGeneratorTest {
	@Test
	public void testUniqueValues(){
		RandomVideoIdGenerator rvig  = new RandomVideoIdGenerator();
		String unique = rvig.getNextRandom();
		assertNotEquals(unique, rvig.getNextRandom());
	}
	
	@Test
	public void testNotSameValueInTwoRun(){
		RandomVideoIdGenerator rvig1  = new RandomVideoIdGenerator();
		RandomVideoIdGenerator rvig2  = new RandomVideoIdGenerator();
		
		assertNotEquals(rvig1.getNextRandom(), rvig2.getNextRandom());
	}
	
	@Test
	public void testMultipleValues(){
		RandomVideoIdGenerator rvig  = new RandomVideoIdGenerator();
		String unique = rvig.getNextRandom();
		for(int i = 0; i< 100; i++){
			assertNotEquals(unique, rvig.getNextRandom());
		}
	}
}
