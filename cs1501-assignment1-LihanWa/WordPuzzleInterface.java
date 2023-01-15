public interface WordPuzzleInterface {
    /*
     * fills out a word puzzle defined by an empty board. 
     * The characters in the empty board can be:
     *    '+': any letter can go here
     *    '-': no letter is allowed to go here
     *     a letter: this letter has to remain in the filled puzzle
     *  @param board is a 2-d array representing the empty board to be filled
     *  @param dictionary is the dictinary to be used for filling out the puzzle
     *  @return a 2-d array representing the filled out puzzle or null if the puzzle has no solution
     */
    public char[][] fillPuzzle(char[][] board, DictInterface dictionary);

    /*
     * checks if filledBoard is a correct fill for emptyBoard
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @return true if rules defined in fillPuzzle has been followed and 
     *  every row and column is a valid word in the dictionary. If a row
     *  or a column includes one or more '-', then each segment should be 
     *  a valid word in the dictionary; the method returns false otherwise
     */
    public boolean checkPuzzle(char[][] emptyBoard, char[][] filledBoard, DictInterface dictionary);
    
}
