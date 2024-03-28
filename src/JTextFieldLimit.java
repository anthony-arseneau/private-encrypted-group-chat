package src;

import javax.swing.text.*;

/**
 * Class that enforces a text field to not exceed the given character limit
 * @author Anthony Arseneau
 * @version March 28, 2024
 * Networks project
 */
class JTextFieldLimit extends PlainDocument {
   // Instance variable
    private int limit;

    /**
     * Constructor
     * @param limit the limit of characters to enforce on the text/password field
     */
    JTextFieldLimit(int limit) {
       super();
       this.limit = limit;
    }

    /**
     * Constructor
     * @param limit the limit of characters to enforce on the text/password field
     * @param upper the option to enforce characters to be uppercase 
     */
    JTextFieldLimit(int limit, boolean upper) {
       super();
       this.limit = limit;
    }

    /**
     * Method that enforces the rule when a user inputs characters
     */
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
       if (str == null)
          return;
       if ((getLength() + str.length()) <= limit) {
          super.insertString(offset, str, attr);
       }
    }
 }