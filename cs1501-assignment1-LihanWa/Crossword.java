public final class Crossword implements WordPuzzleInterface {

  /*
   * fills out a word puzzle defined by an empty board.
   * The characters in the empty board can be:
   * '+': any letter can go here
   * '-': no letter is allowed to go here
   * a letter: this letter has to remain in the filled puzzle
   * 
   * @param board is a 2-d array representing the empty board to be filled
   * 
   * @param dictionary is the dictinary to be used for filling out the puzzle
   * 
   * @return a 2-d array representing the filled out puzzle
   */
  public char[][] fillPuzzle(char[][] board, DictInterface dictionary) {
    boolean minus = false;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board.length; j++) {
        if (board[i][j] == '-')
          minus = true;

      }
    }//If there is more than one '-' in the board, the minus should be checked in "accept" method.
    return fillPuzzle2(board, dictionary, minus);
  }

  public char[][] fillPuzzle2(char[][] board, DictInterface dictionary, boolean minus) {
// This frame is what I learned in cs445 from Prof Garrison. It's similar to that in this course.
    if (!accept(board, dictionary, minus))//test if it doesn't break the rule
      return null;
    if (satisfied(board, dictionary)) {//if success, print the board

      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board.length; j++) {
          if (board[i][j] <= 'Z' && board[i][j] >= 'A')
            board[i][j] = Character.toLowerCase(board[i][j]);
        }

      }

      return board;
    }
    
    char[][] attempt = extend(board);//try the next place (+)

    while (attempt != null) {
      char[][] solution = fillPuzzle(attempt, dictionary);
      if (solution != null)
        return solution;
      else {
        attempt = next(board);//try the next letter (from a to z)
      }
    }

    return null;
  }

  private char[][] next(char[][] board) {
    int row = board.length - 1;

    int col = row;
    //find the last letter that doesn't exist in the original empty board, and make it the next letter(from A to Z)
    for (int i = row; i >= 0; i--) {
      for (int j = col; j >= 0; j--) {
        if (board[i][j] >= 'A' && board[i][j] < 'Z') {
          board[i][j]++;
          return board;
        }
        if (board[i][j] == 'Z') {

          board[i][j] = '+';//if it Z, it means it should back to the previous location
          return null;
        }
      }
    }
    return null;
  }

  private char[][] extend(char[][] board) {
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == '+') {//try the next place, 
          board[i][j] = 'A';
          return board;
        }
      }
    }
    return null;
  }

  private boolean accept(char[][] board, DictInterface dictionary, boolean minus) {

  // Actually I think my algorithm is better than that shown in the assignment hint.
  // I think it only needs to check the row and column that letter locates in. There is no need to care other rows and column.
    int row = board.length - 1;
    int col = row;
    for (int i = row; i >= 0; i--) {
      for (int j = col; j >= 0; j--) {
        if (board[i][j] >= 'A' && board[i][j] <= 'Z') {

          col = j;//find the column that the last added letter locates
          break;
        }
      }
      if (board[i][col] >= 'A' && board[i][col] <= 'Z') {
        row = i;// find the row
        break;
      }
    }
    //for the first, there is no letter added, so doesn't need to be checked.
    if (!(board[row][col] >= 'A' && board[row][col] <= 'Z'))
      return true;
    //turn to lower case
    char[][] newBoard = new char[board.length][board.length];
    for (int i = 0; i < board.length; i++) {
      
        if (board[i][col] <= 'Z' && board[i][col] >= 'A')
          newBoard[i][col] = Character.toLowerCase(board[i][col]);
        else
          newBoard[i][col] = (board[i][col]);
      
    }
    for (int j= 0; j < board.length; j++) {
      
      if (board[row][j] <= 'Z' && board[row][j] >= 'A')
        newBoard[row][j] = Character.toLowerCase(board[row][j]);
      else
        newBoard[row][j] = (board[row][j]);
    
  }

    // if there is '-' in the board, the first letter may not be in the first location, it's after the '-'
    int colStart = 0;
    int rowStart = 0;
    if (minus) {
      // find '-' in the column before the position;
      for (int i = row - 1; i >= 0; i--) {
        if (newBoard[i][col] == '-') {
          colStart = i + 1;
          break;
        }
      }
      
      // find '-' in the line before the position;
      for (int j = col - 1; j >= 0; j--) {
        if (newBoard[row][j] == '-') {
          rowStart = j + 1;
          break;
        }
      }
    
    }
    

    // find if there are some letters behind the letter in column
    int colEnd = row;
    for (int i = row + 1; i < newBoard.length; i++) {
      if (newBoard[i][col] <= 'z' && newBoard[i][col] >= 'a') {
        colEnd++;
      } else
        break;
    }
 
    // find if there are some letters behind the letter in row
    int lineEnd = col;
    for (int j = col + 1; j < newBoard.length; j++) {
      if (newBoard[row][j] <= 'z' && newBoard[row][j] >= 'a') {
        lineEnd++;
      } else
        break;
    }
    // check if there is '+' after the position before'-' in column; if yes, check prefix bellow;otherwise, check word
    boolean PlusCol = false;
    for (int i = row; i < newBoard.length; i++) {
      if (newBoard[i][col] == '+') {
        PlusCol = true;
        break;
      }
      if (newBoard[i][col] == '-') {

        break;
      }
    }
    // check if there is '+' after the position before'-' in that line; if so, check prefix
    boolean PlusLine = false;
    for (int j = col; j < newBoard.length; j++) {
      if (newBoard[row][j] == '+') {
        PlusLine = true;
        break;
      }
      if (newBoard[row][j] == '-') {

        break;
      }
    }

    // stringBuilder col
    StringBuilder colStr = new StringBuilder();
    for (int i = colStart; i <= colEnd; i++) {
      colStr.append(newBoard[i][col]);

    }
    // stringBuilder row
   
    StringBuilder rowStr = new StringBuilder();
    for (int j = rowStart; j <= lineEnd; j++) {
      rowStr.append(newBoard[row][j]);
      

    }
  
    // 1.if no '+' in the column, check word

    int resCol = 0;
    resCol = dictionary.searchPrefix(colStr);
    if (!PlusCol) {
      if (resCol == 0 || resCol == 1) {
     
        return false;
      }
      
    }

    // 2.if '+' in the column, check prefix
    if (PlusCol) {
      if (resCol == 0) {

        return false;
      }
     
    }
    // 3.if no '+' in the line, check word
    int resLine = 0;
    resLine = dictionary.searchPrefix(rowStr);

    if (!PlusLine) {
      if (resLine == 0 || resLine == 1) {
    
        return false;
      }
    }

    // 4.if '+' in the line, check prefix
    if (PlusLine) {
      if (resLine == 0)
        return false;
    }
    return true;
  }

  private boolean satisfied(char[][] board, DictInterface dictionary) {//check if success, 
    //this method is behind the "accept" method, so if the letter doesn't break the rule, and there is no '+' in the board; it satisfies
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == '+')

          return false;
      }
    }

    return true;
  }

  /*
   * checks if filledBoard is a correct fill for emptyBoard
   * 
   * @param emptyBoard is a 2-d array representing an empty board
   * 
   * @param filledBoard is a 2-d array representing a filled out board
   * 
   * @param dictionary is the dictinary to be used for checking the puzzle
   * 
   * @return true if rules defined in fillPuzzle has been followed and
   * that every row and column is a valid word in the dictionary. If a row
   * a column has one or more '-' in it, then each segment separated by
   * the '-' should be a valid word in the dictionary
   */
  public boolean checkPuzzle(char[][] emptyBoard, char[][] filledBoard, DictInterface dictionary) {

    /* check if there is '+'*/

    for (int i = 0; i < filledBoard.length; i++) {
      for (int j = 0; j < filledBoard[i].length; j++) {
        if (filledBoard[i][j] == '+') {

          return false;
        }

      }
    }
    //check every segment in each line is word
    boolean afterM = true;
    int res = 0;
    StringBuilder w = null;
    for (int i = 0; i < filledBoard.length; i++) {
      // in one line

      for (int j = 0; j < filledBoard[i].length; j++) {
        if (filledBoard[i][j] >= 'a' && filledBoard[i][j] <= 'z' && afterM) {
          w = new StringBuilder();
        }
        if (filledBoard[i][j] >= 'a' && filledBoard[i][j] <= 'z') {

          w.append(filledBoard[i][j]);
          afterM = false;
        } else {
          if (w != null) {
            res = dictionary.searchPrefix(w);
            if (res == 0 || res == 1) {

              return false;
            }
          }

          afterM = true;
        }
      }
      if (w != null) {
        res = dictionary.searchPrefix(w);
        if (res == 0 || res == 1) {

          return false;
        }
      }
      afterM = true;
    }
    


    //check if every segment in every column is word
    for (int i = 0; i < filledBoard.length; i++) {
      // in one col
      for (int j = 0; j < filledBoard[i].length; j++) {
        if (filledBoard[j][i] >= 'a' && filledBoard[j][i] <= 'z' && afterM) {
          w = new StringBuilder();
        }
        if (filledBoard[j][i] >= 'a' && filledBoard[j][i] <= 'z') {

          w.append(filledBoard[j][i]);
          afterM = false;
        } else {
          if (w != null) {
            res = dictionary.searchPrefix(w);
            if (res == 0 || res == 1) {

              return false;
            }

          }
          afterM = true;
        }
      }
      if (w != null) {
        res = dictionary.searchPrefix(w);
        if (res == 0 || res == 1) {

          return false;
        }

      }
      afterM = true;
    }

    /*
     * check if emptyBoard has all of the "-" in filledBoard;
     */

    for (int i = 0; i < filledBoard.length; i++) {
      for (int j = 0; j < filledBoard[i].length; j++) {

        if (filledBoard[i][j] == '-' && emptyBoard[i][j] != '-')

          return false;
      }
    }
    /*
     * check if "-" and letters in empty board can be found in filledboard
     */

    for (int i = 0; i < filledBoard.length; i++) {
      for (int j = 0; j < filledBoard[i].length; j++) {
        if (emptyBoard[i][j] == '-' && filledBoard[i][j] != '-') {

          return false;
        }

        if (emptyBoard[i][j] != '+' && emptyBoard[i][j] != '-' && emptyBoard[i][j] != filledBoard[i][j]) {

          return false;
        }

      }
    }



    return true;

  }

}
