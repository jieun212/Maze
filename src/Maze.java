/* TCSS 342 - Spring 2016
 * Assignment 5 - Maze Generator
 * Jieun Lee
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * The Maze class represents graph G(n,m) using a randomized method to create a
 * spanning tree. It displays the final maze to the console with the solution
 * path highlighted with special characters.
 * 
 * @author Jieun Lee
 * @version 05-28-2016
 */
public class Maze {

	/**
	 * A depth(row) of a maze.
	 */
	private int myRow;
	
	/**
	 * A width(column) of a maze.
	 */
	private int myColumn;
	
	/**
	 * A debugging mode.
	 */
	private boolean myDebugMode;
	
	/**
	 * A 2-D maze.
	 */
	private char[][] myMaze;
	
	/**
	 * A current vertex.
	 */
	private Vertex myCurrentVertex;
	
	/**
	 * A stack of maze for tracking.
	 */
	private Stack<Vertex> myMazeStack;
	
	/**
	 * A stack of maze for solution.
	 */
	private Stack<Vertex> mySolutionStack;
	
	/**
	 * A list of all vertexes of a maze
	 */
	private List<Vertex> myVertexList;
	
	/**
	 * A list of visited vertexes for testing.
	 */
	private List<Vertex> myVisitedList;

	/**
	 * Constructs a new Maze with given maze size and debugging flag.
	 * 
	 * @param width The width.
	 * @param depth THe depth.
	 * @param debug The debug flag.
	 */
	public Maze(int width, int depth, boolean debug) {
		myColumn = width;
		myRow = depth;
		myDebugMode = debug;
		myMaze = new char[(myRow * 2) + 1][(myColumn * 2) + 1];
		myCurrentVertex = new Vertex(1, 1);
		myMazeStack = new Stack<Vertex>();
		mySolutionStack = new Stack<Vertex>();
		myVertexList = new ArrayList<Vertex>();
		myVisitedList = new ArrayList<Vertex>();
	}

	/**
	 * Displays the maze using 'X' and ' ' character.
	 * If debugging mode is on (true), displays all steps with character 'V' 
	 * and solution path with character '+'. Otherwise, displays solution
	 */
	public void display() {

		// 1. draws initial maze.
		drawInitialMaze();
		
		// 2. sets start vertex.
		setStartEndVertex();

		// 3. visits all vertexes in the graph
		while (myVertexList.size() > 0) {

			// how to get one of four directions randomly in java
			// http://codereview.stackexchange.com/questions/84311/get-random-direction-based-off-of-location-and-previous-direction
			// how to get an random element in a list in java
			// http://stackoverflow.com/questions/12487592/randomly-select-an-item-from-a-list
			List<Character> directionList = new ArrayList<Character>();
			
			// checks all directions of current vertex and
			// gets the option list to choose random available direction
			if (checkSouth()) { directionList.add('S'); }
			if (checkNorth()) { directionList.add('N'); }
			if (checkEast()) { directionList.add('E'); }
			if (checkWest()) { directionList.add('W'); }

			// if there is no available direction to go,
			// flags found is false to find other vertex that has available direction.
			boolean found = true;
			if (directionList.size() < 1) { found = false; }


			// if there is at least one available direction of the current vertex,
			// then get the direction randomly and move to the chosen direction.
			if (found) {
				// gets random direction
				Random random = new Random();
				int chooseDir = directionList.get(random.nextInt(directionList.size()));

				// deletes the available direction list to use for next current vertex
				directionList.removeAll(directionList);

				// marks previous vertex with 'V' and open the wall
				// moves current vertex to the chosen direction
				if (chooseDir == 'S') {
					myMaze[myCurrentVertex.getR() + 1][myCurrentVertex.getC()] = ' ';
					myMaze[myCurrentVertex.getR() + 2][myCurrentVertex.getC()] = 'V';
					myCurrentVertex.setVertex(myCurrentVertex.getR() + 2, myCurrentVertex.getC());
				} else if (chooseDir == 'N') {
					myMaze[myCurrentVertex.getR() - 1][myCurrentVertex.getC()] = ' ';
					myMaze[myCurrentVertex.getR() - 2][myCurrentVertex.getC()] = 'V';
					myCurrentVertex.setVertex(myCurrentVertex.getR() - 2, myCurrentVertex.getC());
				} else if (chooseDir == 'E') {
					myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() + 1] = ' ';
					myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() + 2] = 'V';
					myCurrentVertex.setVertex(myCurrentVertex.getR(), myCurrentVertex.getC() + 2);
				} else if (chooseDir == 'W') {
					myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() - 1] = ' ';
					myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() - 2] = 'V';
					myCurrentVertex.setVertex(myCurrentVertex.getR(), myCurrentVertex.getC() - 2);
				}

				// removes the visited vertex from the vertex list
				for (int i = 0; i < myVertexList.size(); i++) {
					if (myVertexList.get(i).getR() == myCurrentVertex.getR()
							&& myVertexList.get(i).getC() == myCurrentVertex.getC()) {
						myVertexList.remove(myVertexList.get(i));
					}
				}

				// pushes the current vertex to the maze stack
				myMazeStack.push(myCurrentVertex.getVertex());
				
				// adds the current vertex for testing
				myVisitedList.add(myCurrentVertex.getVertex());

				// if current vertex is the end vertex of this maze, then copy maze stack to the solution stack
				if (myCurrentVertex.getR() == (myRow * 2) - 1 && myCurrentVertex.getC() == (myColumn * 2) - 1) {
					Stack<Vertex> temp = new Stack<Vertex>();
					while (!myMazeStack.isEmpty()) {
						temp.push(myMazeStack.pop());
					}
					while (!temp.isEmpty()) {
						Vertex v = temp.pop();
						myMazeStack.push(v);
						mySolutionStack.push(v);
					}
				}
				
				// if debug mode is true, then prints whenever the current vertex moves
				if (myDebugMode == true) {
					System.out.println(printMaze());
				}
				
			} else { 
				// if there is no available direction to go, 
				// back tracks until finding the vertex that has available direction
				Vertex popV = myMazeStack.peek();
				while (!(checkSouth() || checkNorth() || checkEast() || checkWest())) {
					popV = myMazeStack.peek();
					myCurrentVertex.setVertex(popV.myR, popV.myC);
					myMazeStack.pop();
				}

				// pushes last pop vertex into myMazeStack
				myMazeStack.push(popV);
			}
		}

		
		// for testing the solution stack
		//System.out.println("testing solution stack: " + mySolutionStack.toString());
		

		// marks the solution vertex with  '+' 
		// and prints this maze
		while (!mySolutionStack.isEmpty()) {
			Vertex temp = mySolutionStack.pop();
			myMaze[temp.getR()][temp.getC()] = '+';
		}
		
		// displays the maze with solution path.
		System.out.println(printMaze());
	}
	
	/**
	 * Creates initial maze and display the maze if debugging mode is true
	 */
	public void drawInitialMaze() {

		// marks the maze with 'X' & ' '
		for (int r = 0; r <= (myRow * 2); r++) {
			for (int c = 0; c <= (myColumn * 2); c++) {
				if (r % 2 != 0 && c % 2 != 0) {
					// stores all vertexes of this maze into the my vertex list 
					// to check all vertexes are visited.
					myVertexList.add(new Vertex(r, c));
					myMaze[r][c] = ' ';
				} else {
					if ((r == 0 && c == 1) || (r == (myRow * 2) && c == (myColumn * 2) - 1)) {
						myMaze[r][c] = ' '; // for start and end
					} else {
						myMaze[r][c] = 'X';
					}
				}
			}
		}

		// if debugging mode is on, draws the initial maze.
		if (myDebugMode == true) {
			System.out.println(printMaze());
		}
	}
	
	/**
	 * Sets the start and end vertex.
	 */
	public void setStartEndVertex() {
		// sets current vertex at(1,1) and marks with 'V'
		myCurrentVertex.setVertex(1, 1);
		myMaze[myCurrentVertex.getR()][myCurrentVertex.getC()] = 'V';
		
		// pushes the initial current vertex to the maze stack
		myMazeStack.push(myCurrentVertex.getVertex());
		// adds the current vertex for testing
		myVisitedList.add(myCurrentVertex.getVertex());
		
		// removes the visited vertex from the vertex list of this maze
		for (int i = 0; i < myVertexList.size(); i++) {
			if (myVertexList.get(i).getR() == myCurrentVertex.getR()
					&& myVertexList.get(i).getC() == myCurrentVertex.getC()) {
				myVertexList.remove(myVertexList.get(i));
			}
		}
		
		// if debugging mode is on, draws the maze.
		if (myDebugMode == true) {
			System.out.println(printMaze());
		}
	}
	

	/**
	 * Displays the maze.
	 * 
	 * @return The String of the maze.
	 */
	private String printMaze() {
		String result = "";
		for (int r = 0; r <= (myRow * 2); r++) {
			for (int c = 0; c <= (myColumn * 2); c++) {
				result += myMaze[r][c];
				result += ' ';
			}
			result += '\n';
		}
		return result;
	}
	
	
/* checks available directions */
	
	/**
	 * Returns true if the current vertex can move to south direction.
	 * 
	 * @return True if the current vertex can move to south direction.
	 */
	private boolean checkSouth() {
		// if south vertex is not the last row of this maze && south == ' ',
		// return true
		return myCurrentVertex.getR() < (myRow * 2) - 1
				&& myMaze[myCurrentVertex.getR() + 2][myCurrentVertex.getC()] == ' ';
	}

	/**
	 * Returns true if the current vertex can move to north direction.
	 * 
	 * @return True if the current vertex can move to north direction.
	 */
	private boolean checkNorth() {
		// if north vertex is not the first row or this maze && north == ' ',
		// return true
		return myCurrentVertex.getR() > 2 && myMaze[myCurrentVertex.getR() - 2][myCurrentVertex.getC()] == ' ';
	}

	/**
	 * Returns true if the current vertex can move to east direction.
	 * 
	 * @return True if the current vertex can move to east direction.
	 */
	private boolean checkEast() {
		// if east vertex is not the last column or this maze && east == ' ',
		// return true
		return myCurrentVertex.getC() < (myColumn * 2) - 1
				&& myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() + 2] == ' ';
	}

	/**
	 * Returns true if the current vertex can move to west direction.
	 * 
	 * @return True if the current vertex can move to west direction.
	 */
	private boolean checkWest() {
		// if west vertex is not the first column or this maze && west == ' ',
		// return true
		return myCurrentVertex.getC() > 2 && myMaze[myCurrentVertex.getR()][myCurrentVertex.getC() - 2] == ' ';
	}

	
/* Tests method */
	/**
	 * Tests the Maze class.
	 */
	public void testMaze(Maze test) {
		
		System.out.println("===================== testMaze() ==================");
		// creates a small maze with debugging mode off to check each step.
		//Maze test = new Maze(5, 5, false);
		
		// the size of this maze
		// debugging = true, expects to print the maze.
		test.drawInitialMaze();
		test.myDebugMode = true;
		test.drawInitialMaze();
		
		// resets the debugging mode
		test.myDebugMode = false;
		
		// the initial current vertex
		test.setStartEndVertex();

		System.out.print("Current vertex:");
		System.out.println("expected (1, 1), actual " + test.myCurrentVertex.getVertex().toString());
		System.out.println();
		
		// the track stack of test maze
		System.out.print("maze tracking stack: ");
		System.out.println("expected [(1, 1)], actual " + test.myMazeStack.toString());
		System.out.println();
		
		// the available directions to go
		List<Character> testDir = new ArrayList<Character>();
		System.out.print("if current Vertex is " + test.myCurrentVertex.getVertex().toString());
		System.out.print(" expected available direction: [S, E], ");
		if (test.checkSouth()) { testDir.add('S'); }
		if (test.checkNorth()) { testDir.add('N'); }
		if (test.checkEast()) { testDir.add('E'); }
		if (test.checkWest()) { testDir.add('W'); }
		System.out.print("actual : " + testDir.toString());
		testDir.removeAll(testDir);
		System.out.println();

		
		// if the test's current vertex moves to east,
		// check the available direction of the updated current vertex
		test.myCurrentVertex.setVertex(1, 3);
		System.out.print("if current Vertex is " + test.myCurrentVertex.getVertex().toString());
		System.out.print(" expected available direction: [S, E], ");
		if (test.checkSouth()) { testDir.add('S'); }
		if (test.checkNorth()) { testDir.add('N'); }
		if (test.checkEast()) { testDir.add('E'); }
		if (test.checkWest()) { testDir.add('W'); }
		System.out.print("actual : " + testDir.toString());
		testDir.removeAll(testDir);
		System.out.println();
		test.myCurrentVertex.setVertex(1, 1);
		
		
		System.out.println("==== Test debugging components ===");
		Maze test2 = new Maze(5, 5, true);
		
		// tests display() with debugging mode
		// to check the visited vertex and stack of this maze
		test2.display();
		
		System.out.println("size of visted vertices: expected 25, actual: " + test2.myVisitedList.size());
		System.out.println("visited vertieces: " + test2.myVisitedList.toString());
		System.out.println("tracking stack: " + test2.myMazeStack.toString());
		System.out.println("=============== End testMaze() ==============");
	}
	
	
	
	
/* inner class */

	/**
	 * The Vertex inner class to generate 2D maze with row and column.
	 * 
	 * @author Jieun Lee
	 *@version 05-28-2016
	 */
	private class Vertex {
		/**
		 * A row number.
		 */
		public int myR;
		
		/**
		 * A coulmn number.
		 */
		public int myC;

		/**
		 * Constructs new vertex with given row number and column number.
		 * 
		 * @param r The row.
		 * @param c The column.
		 */
		public Vertex(int r, int c) {
			myR = r;
			myC = c;
		}

		/**
		 * Returns the vertex.
		 * 
		 * @return The vertex.
		 */
		public Vertex getVertex() {
			final Vertex result = new Vertex(myR, myC);
			return result;
		}

		/**
		 * Returns the column.
		 * @return The column.
		 */
		public int getC() {
			return myC;
		}

		/**
		 * Retunrs the row.
		 * @return The row.
		 */
		public int getR() {
			return myR;
		}

		/**
		 * Sets the vertex with given row number and column number.
		 * @param r
		 * @param c
		 */
		public void setVertex(int r, int c) {
			myR = r;
			myC = c;
		}

		/**
		 * Displays the row and column of the vertex.
		 */
		@Override
		public String toString() {
			return "(" + myR + ", " + myC + ")";
		}

	}

}
