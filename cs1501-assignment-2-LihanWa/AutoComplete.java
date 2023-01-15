
/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root;
  private StringBuilder currentPrefix;
  private DLBNode currentNode;
  private DLBNode waitForRetreat;
  private DLBNode back;
  private DLBNode tempNode;
  private DLBNode up;
  private StringBuilder temp;
  private int countNotFound;
  private DLBNode addNode;
  // : Add more instance variables if you need to

  public AutoComplete() {
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
    back = null;
    up = null;
    temp = new StringBuilder();
    tempNode = null;
    waitForRetreat=null;//used in advance and retreat
    countNotFound = 0;// used in advance and retreat
  }

  /**
   * Adds a word to the dictionary in O(word.length()) time
   * 
   * @param word the String to be added to the dictionary
   * @return true if add is successful, false if word already exists
   * @throws IllegalArgumentException if word is the empty string
   */
  public boolean add(String word) {
   
    addNode=root;
    if (word.length() == 0)
      throw new IllegalArgumentException();
    // find if already exists
    for (int i = 0; i < word.length(); i++) {
      char now = word.charAt(i);
      // look for the char in the level
      while (addNode != null && now != addNode.data) {
        addNode = addNode.nextSibling;
      }
      // didn't find it
      if (addNode == null) {
        break;
      }
      // when the last char was found
      if (i == word.length() - 1 && addNode.isWord)
        return false;
      // go to next level
      addNode = addNode.child;

    }
    // if didn't find the word
    addNode = root;

    for (int i = 0; i < word.length(); i++) {
      char now = word.charAt(i);
      // if the dlb is empty; root needs to be initialized
      if (addNode == null && root == null) {
        root = new DLBNode(now);
        root.size++;
        addNode = root;
      }
      // if not empty

      else {
        // if there is no node in that lavel
        if (addNode == null) {
          addNode = new DLBNode(now);
          addNode.size++;
          up.child = addNode;
          addNode.parent = up;
        }
        // if there is at least a node in that level
        else {
          // look for char in that level
          while (addNode != null && now != addNode.data) {
            back = addNode;
            addNode = addNode.nextSibling;

          }
          // if didn't find it
          if (addNode == null) {
            addNode = new DLBNode(now);
            addNode.size++;
            if (back != null)
              back.nextSibling = addNode;
            addNode.previousSibling = back;
          }
          // if find it
          else {
            addNode.size++;
          }
        }

      }
      // the last one is inserted
      if (i == word.length() - 1) {
        if(addNode.isWord==true) return false;
        addNode.isWord = true;
        break;
      }
      // go to next level
      up = addNode;

      addNode = addNode.child;

    }
    addNode = up;
    return true;
  }

  /**
   * appends the character c to the current prefix in O(1) time. This method
   * doesn't modify the dictionary.
   * 
   * @param c: the character to append
   * @return true if the current prefix after appending c is a prefix to a word
   *         in the dictionary and false otherwise
   */
  public boolean advance(char c) {
    // : implement this method
    currentPrefix.append(c);
    
    // if is already no prediction
    if (countNotFound >= 1){
      countNotFound++;
      return false;
    }
     
  
    // else if there is prediction now;
    else {
      // if currentprefix only has one char
      if (currentPrefix.length() == 1)
        currentNode = root;
      // go to next level
      else{
        
        // if there is no node in the level
        if(currentNode.child==null){
          waitForRetreat=currentNode;
          currentNode=new DLBNode(c);
          countNotFound++;
          return false;
        }
        else currentNode = currentNode.child;
      }
       
      // look for char in that level
      while (currentNode.nextSibling != null && c != currentNode.data) {
        currentNode = currentNode.nextSibling;
      }
      // if didn't find it,return false;find the parent waiting for retreat in the
      // future(when countNotFound is 1 and thrown to retreat method);set currentnode to a
      // empty node(size=0 not word;)
      if (currentNode.data != c) {
        countNotFound++;
        //find the parent
        if (currentPrefix.length()==1)
          currentNode = null;
        else {
          while (currentNode.previousSibling != null) {
          // go to the left most node
            currentNode = currentNode.previousSibling;
          }
        // go up
          currentNode = currentNode.parent;
         
        }
        waitForRetreat=currentNode;
        currentNode=new DLBNode(c);
        return false;
      }
      // if found it return true
      else
        return true;

    }

  }

  /**
   * removes the last character from the current prefix in O(1) time. This
   * method doesn't modify the dictionary.
   * 
   * @throws IllegalStateException if the current prefix is the empty string
   */
  public void retreat() {
    // : implement this method
    if (currentPrefix==null||currentPrefix.length()==0) throw new IllegalStateException();
    currentPrefix.deleteCharAt(currentPrefix.length() - 1);
    // if retreat from a empty node(in advance method, no prediction;
    // for example word is ab,but input is abc or abc..., and abc is not a prefix of
    // a word)
    if (countNotFound == 1) {
      currentNode = waitForRetreat;
      countNotFound--;
    } else if (countNotFound > 1)
      countNotFound--;
    // if there is no upper level, the current prefix is null;

    else {
      if (currentPrefix == null)
        currentNode = null;
      else {
        while (currentNode.previousSibling != null) {
          // go to the left most node
          currentNode = currentNode.previousSibling;
        }
        // go up
        //if already at root
        if(currentNode==root) currentNode=root;
        else currentNode = currentNode.parent;
      }
    }
  }

  /**
   * resets the current prefix to the empty string in O(1) time
   */
  public void reset() {
    // : implement this method
    currentPrefix= new StringBuilder();
    currentNode = root;
    back = null;
    up = null;
    temp = new StringBuilder();
    tempNode = null;
    waitForRetreat=null;//used in advance and retreat
    countNotFound = 0;// used in advance and retreat
  }

  /**
   * @return true if the current prefix is a word in the dictionary and false
   *         otherwise
   */
  public boolean isWord() {
    // : implement this method

    
    return currentNode.isWord;

  }

  /**
   * adds the current prefix as a word to the dictionary (if not already a word)
   * The running time is O(length of the current prefix).
   */
  public void add() {
    // : implement this method
    add(currentPrefix.toString());
    if(countNotFound>=1) {
      currentNode=addNode;
      currentNode.isWord=true;
      countNotFound=0;
    }
    
    
    //if before add, there is no this prediction.
  }

  /**
   * @return the number of words in the dictionary that start with the current
   *         prefix (including the current prefix if it is a word). The running
   *         time is
   *         O(1).
   */
  public int getNumberOfPredictions() {
    // : implement this method
    return currentNode.size;

  }

  /**
   * retrieves one word prediction for the current prefix. The running time is
   * O(prediction.length()-current prefix.length())
   * 
   * @return a String or null if no predictions exist for the current prefix
   */
  public String retrievePrediction() {
    // : implement this method
    temp = new StringBuilder(currentPrefix);
    tempNode = currentNode;
    // if it's not word now, keep going down;find the nearest word, which doesn't
    // have a child
    
    if(countNotFound>=1) return null;
    while (!tempNode.isWord) {
      tempNode = tempNode.child;
      temp.append(tempNode.data);
    }
    return temp.toString();
  }

  /*
   * ==============================
   * Helper methods for debugging.
   * ==============================
   */

  // print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start) {
    System.out.println("==================== START: DLB Trie Starting from \"" + start + "\" ====================");
    if (start.equals("")) {
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if (startNode != null) {
        printTrie(startNode.child, 0);
      }
    }

    System.out.println("==================== END: DLB Trie Starting from \"" + start + "\" ====================");
  }

  // a helper method for printTrie
  private void printTrie(DLBNode node, int depth) {
    if (node != null) {
      for (int i = 0; i < depth; i++) {
        System.out.print(" ");
      }
      System.out.print(node.data);
      if (node.isWord) {
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth + 1);
      printTrie(node.nextSibling, depth);
    }
  }

  // return a pointer to the node at the end of the start String.
  private DLBNode getNode(DLBNode node, String start, int index) {
    if (start.length() == 0) {
      return node;
    }
    DLBNode result = node;
    if (node != null) {
      if ((index < start.length() - 1) && (node.data == start.charAt(index))) {
        result = getNode(node.child, start, index + 1);
      } else if ((index == start.length() - 1) && (node.data == start.charAt(index))) {
        result = node;
      } else {
        result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  }

  // The DLB node class
  private class DLBNode {
    private char data;
    private int size;
    private boolean isWord;
    private DLBNode nextSibling;
    private DLBNode previousSibling;
    private DLBNode child;
    private DLBNode parent;

    private DLBNode(char data) {
      this.data = data;
      size = 0;
      isWord = false;
      nextSibling = previousSibling = child = parent = null;
    }
  }
}
