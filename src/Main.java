/* TCSS 342 - Spring 2016
 * Assignment 5 - Maze Generator
 * Jieun Lee
 */



public class Main {
	


	public static void main(String[] args) {
		
		
		// Generates a 5x5 maze with debugging on
		Maze teston = new Maze(5, 5, true);
		teston.display(); // on, prints all steps and solution
		
		
		
		// Generates a 5x5 maze with debugging off
		Maze testoff = new Maze(5, 5, false);
		testoff.display(); // off, prints only solution

		
		
		// Generates a larger maze with debugging off
		Maze testlarge = new Maze(10, 15, false);
		testlarge.display();
		
		
		
		// tests Maze class
		testAndDebug();

	}
	

	/**
	 * Tests Maze class
	 */
	public static void testAndDebug() {
		
		// creates testMaze method in the Maze class
		// to test the private methods
		Maze test = new Maze(5, 5, false);
		test.testMaze(test);
	}

}
