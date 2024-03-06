import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


/**
 * Calculator Class
 * */
public class Calculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorApp app = new CalculatorApp();

            // Load the logo image
            ImageIcon icon = new ImageIcon("Calculator/resources/logo.png");
            Image logo = icon.getImage();

            // Set the icon for the JFrame
            app.setIconImage(logo);

            app.setPreferredSize(new Dimension(500, 600));
            app.setSize(500, 600);
            app.setMinimumSize(new Dimension(500, 500));
            app.setVisible(true);
            app.setBackground(Color.WHITE);
        });
    }
}

/** Calculator App - extends JFrame and implements the ActionListener classes */
class CalculatorApp extends JFrame implements ActionListener {
    public JButton numberButtons[]; // Declare JButton array of Number buttons
    private JButton operatorButtons[]; // Declare JButton array of Operator buttons
    private JButton equalButton; // Equal button
    private JButton clrButton; // Clear Screen button
    private JTextField displayFieldText; // Display the user input fields
    private JTextField calculatedFieldText; // Display the calculated formula
    private JTextField invisibleField;
    private boolean starting_or_clear = true; // For removing 0 when calculator starts or is cleared
    private char operationEnvoked = ' '; // Track the operation envoked "+ - / x"
    private char prevoperationEnvoked = ' '; // Track the previous operation envoked for history
    private boolean requestedOperation = false;
    private float pnum1 = 0; // Previous calculated numbers. Start from 0
    private float pnum2 = 0; // Previous calculated numbers. Start from 0
    private float ans = 0; // Update for answers after calculation is performed

    /** Constructor - Calculator App. Takes no arguments */
    public CalculatorApp() {
        // Set application context
        setTitle("Simple Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Settings for color, font and background
        Font calculatedFieldFont = new Font ("Arial", Font.PLAIN, 15);
        Color calculatorBackground = new Color(235, 235, 235);
        Color calculatedFieldFontColor = new Color(130, 130, 130);
        Font displayFieldFont = new Font ("Arial", Font.PLAIN, 40);

        // Enter Invisible Field - This is to give some top border for the app
        invisibleField =new JTextField();
        invisibleField.setPreferredSize(new Dimension(400, 20));
        invisibleField.setEditable(false);
        invisibleField.setFocusable(false);
        invisibleField.setBackground(calculatorBackground);
        invisibleField.setCaretColor(invisibleField.getBackground());
        Border emptyBorder = BorderFactory.createEmptyBorder();
        invisibleField.setBorder(emptyBorder);

        // Set Calculation Field Panel
        calculatedFieldText = new JTextField();
        calculatedFieldText.setMinimumSize(new Dimension(400,25));
        calculatedFieldText.setPreferredSize(new Dimension(400, 40));
        calculatedFieldText.setBorder(new EmptyBorder(15, 15, 0, 15));
        calculatedFieldText.setEditable(false);
        calculatedFieldText.setHorizontalAlignment(SwingConstants.RIGHT);
        calculatedFieldText.setFocusable(false);
        calculatedFieldText.setCaretColor(calculatedFieldText.getBackground());
        calculatedFieldText.setFont(calculatedFieldFont);
        calculatedFieldText.setBackground(calculatorBackground);
        calculatedFieldText.setForeground(calculatedFieldFontColor);
        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 215, 215));
        calculatedFieldText.setBorder(bottomBorder);

        // Set Main Display Field Panel
        displayFieldText = new JTextField();
        displayFieldText.setMinimumSize(new Dimension(400, 50));
        displayFieldText.setPreferredSize(new Dimension(400, 100));
        displayFieldText.setBorder(new EmptyBorder(0, 15, 5, 15));
        displayFieldText.setEditable(false);
        displayFieldText.setText("0");
        displayFieldText.requestFocus();
        displayFieldText.setCaretColor(displayFieldText.getBackground());
        displayFieldText.setHorizontalAlignment(SwingConstants.RIGHT);
        displayFieldText.setFont(displayFieldFont);
        displayFieldText.setBackground(calculatorBackground);

        // Create BoxLayout for vertical expansion
        JPanel displayFieldPanel = new JPanel();
        displayFieldPanel.setLayout(new BoxLayout(displayFieldPanel, BoxLayout.Y_AXIS));
        displayFieldPanel.add(Box.createVerticalGlue());
        displayFieldPanel.add(invisibleField);
        displayFieldPanel.add(calculatedFieldText);
        displayFieldPanel.add(displayFieldText);
        add(displayFieldPanel, BorderLayout.NORTH);

        // Set Button Panel
        JPanel btnPanel = new JPanel(new GridLayout(4,3, 10, 10));
        btnPanel.setPreferredSize(new Dimension(400, 500));
        btnPanel.setBorder(new EmptyBorder(5, 5 , 5,5 ));
        btnPanel.setBackground(calculatorBackground);
        initNumBtns(btnPanel);

        // Set Operator Panel
        JPanel opPanel = new JPanel(new GridLayout(5, 1, 5,5));
        opPanel.setPreferredSize(new Dimension(100, 500));
        opPanel.setBorder(new EmptyBorder(5, 5 , 5,5 ));
        opPanel.setBackground(calculatorBackground);
        initOpBtns(opPanel);

        // Enable Keypad and KeyBoard entry
        displayFieldText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Ignore
            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Ignore
            }
        });
        // Add components to main layout
        add(btnPanel, BorderLayout.CENTER);
        add(opPanel, BorderLayout.EAST);
    }

    // --- METHODS --- //
    /**
    * This method checks for whether decimal exists in user's input
    * @params decimalExists
    * returns 'true' if decimal exists
    * */
    public boolean decimalExists() {
        boolean decimalExists = false;
        String currentText = displayFieldText.getText();
        for (int i = 0; i < currentText.length(); i++) {
            if (currentText.charAt(i) == '.') {
                decimalExists = true;
                break;
            }
        }
        return decimalExists;
    }

    /**
     * This method is used to detect back space entered and removes a character from
     * the input
     * @params Returns an updated String value
     **/
    public String backSpaceEntered() {
        String currentText = displayFieldText.getText();
        return currentText.substring(0, currentText.length() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        // Grabs the Button object and checks the text on the button for operations
        if (source instanceof JButton){
            JButton clickedButton = (JButton) source;
            String buttonOperation = clickedButton.getText();

            // If button is a number, then enter a number in the display field
            if (buttonOperation.matches("[0-9]")) {
                int number = Integer.parseInt(buttonOperation);
                enterNumber(number);
            }
            // If button is Clear (CLR), then clear the display field
            else if (buttonOperation.equals("CLR")) {
                displayFieldText.setText("0");
                calculatedFieldText.setText("");
                displayFieldText.requestFocus();
                starting_or_clear = true;
            }
            // If button is a decimal, then add a decimal point in the display field
            else if (buttonOperation.equals(".")) {
                enterDecimal();
            }
            // If button is an "=", then perform calculation by invoking the enterEquals() function
            else if (buttonOperation.equals("=")) {
                enterEquals();
            }
            // If button is "+", then perform add
            else if (buttonOperation.equals("+")) {
                add();
            }
            // If button is "-", then perform subtract
            else if (buttonOperation.equals("-")) {
                subtract();
            }
            // If button is "x", then perform multiplication
            else if (buttonOperation.equals("x")) {
                multiply();
            }
            // If button is "/", then perform division
            else if (buttonOperation.equals("/")) {
                divide();
            }
        }
        // Request refocus on the displayFieldText to ensure capturing of events
        displayFieldText.requestFocus();
    }

    /**
     * This method checks for the key presses on keyboard and keypads
     * */
    public void handleKeyPress(KeyEvent e) {
        int numCode = e.getKeyCode();

        // KeyPad Entry for Number
        if (numCode >= KeyEvent.VK_NUMPAD0 && numCode <= KeyEvent.VK_NUMPAD9) {
            int number = numCode - KeyEvent.VK_NUMPAD0;
            enterNumber(number);
        }

        // Keyboard Entry for Number
        else if (numCode >= KeyEvent.VK_0 && numCode <= KeyEvent.VK_9) {
            int number = numCode = numCode - KeyEvent.VK_0;
            enterNumber(number);
        }

        // Key Entry for Decimal
        else if (numCode == KeyEvent.VK_DECIMAL) {
            enterDecimal();
        }

        // Key Entry for Backspace
        else if (numCode == KeyEvent.VK_BACK_SPACE) {
            enterBackSpace();
        }

        // Key Entry for Enter (equals)
        else if (numCode == KeyEvent.VK_ENTER) {
            enterEquals();
        }

        // Key Entry for ADD (plus)
        else if (numCode == KeyEvent.VK_ADD || numCode == KeyEvent.VK_PLUS) {
            add();
        }

        // Key Entry for SUBTRACT (minus)
        else if (numCode == KeyEvent.VK_MINUS || numCode == KeyEvent.VK_SUBTRACT) {
            subtract();
        }
        
        // Key Entry for DIVIDE
        else if (numCode == KeyEvent.VK_DIVIDE) {
            divide();
        }
        
        // Key Entry for MULTIPLY
        else if (numCode == KeyEvent.VK_MULTIPLY) {
            multiply();
        }
    }

    /**
     * Initliaze the number buttons
     * @params
     * btnPanel - type JPanel
     */
    public void initNumBtns(JPanel btnPanel) {
        // Initialize numberButtons with 10 JButton objects
        numberButtons = new JButton[12];
        // For all buttons, updated text and add action listener
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            btnPanel.add(numberButtons[i]);
        }
        // Set '.' and '=' buttons separately
        numberButtons[10] = new JButton(".");
        numberButtons[10].addActionListener(this);
        numberButtons[11] = new JButton("=");
        numberButtons[11].addActionListener(this);

        // Settings for color, font and background
        Border borderColor = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(215, 215, 215));
        Font buttonFontNumbers = new Font("Arial", Font.PLAIN, 24);
        Color buttonBGNumbers = Color.white;
        for (JButton button : numberButtons) {
            // Loop through all buttons and set the display
            button.setFont(buttonFontNumbers);
            button.setBackground(buttonBGNumbers);
            button.setBorder(borderColor);

            // Add mouse listener and update the colors
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    button.setBackground(new Color(245, 245, 245));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    button.setBackground(buttonBGNumbers);
                }
            });
        }

        // Set the "=" button display different
        numberButtons[11].setFont(new Font("Arial", Font.BOLD, 30));
        numberButtons[11].setBackground(new Color(77, 167, 196));
        numberButtons[11].setForeground(Color.WHITE);
        numberButtons[11].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(77, 167, 196)));
        numberButtons[11].addMouseListener(new MouseAdapter() {
            // Add mouse listener and update the colors
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                numberButtons[11].setBackground(new Color(52, 147, 176));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                numberButtons[11].setBackground(new Color(77, 167, 196));
            }
        });

        // Add buttons to the btnPanel
        btnPanel.add(numberButtons[10]);
        btnPanel.add(numberButtons[11]);
    }

    /**
     * Initliaze the operations buttons (+ - x / CLR)
     * @params
     * btnPanel - type JPanel
     */
    public void initOpBtns(JPanel opPanel) {
        // Initilize the operatorButtons with 4 operators
        operatorButtons = new JButton[4];
        operatorButtons[0] = new JButton("+");
        operatorButtons[1] = new JButton("-");
        operatorButtons[2] = new JButton("x");
        operatorButtons[3] = new JButton("/");
        operatorButtons[0].addActionListener(this);
        operatorButtons[1].addActionListener(this);
        operatorButtons[2].addActionListener(this);
        operatorButtons[3].addActionListener(this);

        // Add Clear Button
        clrButton = new JButton("CLR");
        clrButton.addActionListener(this);

        // Settings for color, font and background
        Font buttonFontNumbers = new Font("Arial", Font.PLAIN, 24);
        Color buttonBGNumbers = new Color(240, 240, 240);
        for (JButton button : operatorButtons) {
            // Loop through all buttons and set the display
            button.setFont(buttonFontNumbers);
            button.setBackground(buttonBGNumbers);
            button.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(215, 215, 215)));

            // Add mouse listener and update colors
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    button.setBackground(new Color(225, 225, 225));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    button.setBackground(new Color(240, 240, 240));
                }
            });
        }

        // Set the "CLR" button display different
        clrButton.setFont(new Font("Arial", Font.PLAIN, 24));
        clrButton.setBackground(new Color(224, 71, 49));
        clrButton.setForeground(Color.WHITE);
        clrButton.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(224, 71, 49)));
        // Add mouse listener and update colors
        clrButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                clrButton.setBackground(new Color(204, 51, 39));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                clrButton.setBackground(new Color(224, 71, 49));
            }
        });
        // Add buttons to opPanel
        opPanel.add(operatorButtons[0]);
        opPanel.add(operatorButtons[1]);
        opPanel.add(operatorButtons[2]);
        opPanel.add(operatorButtons[3]);
        opPanel.add(clrButton);
    }

    /**
     * This method enters a number to the display of the calculator
     * @params
     * int number
     * */
    public void enterNumber(int number) {
        if (starting_or_clear) {
            displayFieldText.setText("");
            displayFieldText.setText(String.valueOf(number));
            starting_or_clear = false;
        } else {
            displayFieldText.setText(displayFieldText.getText() + number);
        }
    }

    /**
     * This method enters a decimal in the display of the calculator
     * */
    public void enterDecimal() {
        boolean decimalExists = decimalExists();
        if (!decimalExists) {
            displayFieldText.setText(displayFieldText.getText() + ".");
        }
    }

    /**
     * This method removes a character from the display of the calculator
     * */
    public void enterBackSpace() {
        String updatedText = backSpaceEntered();
        displayFieldText.setText(updatedText);
    }

    /**
     * This method detects an ENTER from keyboard or "=" on the calculator button and
     * performs a calculation
     * */
    public void enterEquals() {
        // Track floating numbers num1 and num2
        float num1;
        float num2;

        // If there is a requested operation (+ - / x):
        if (requestedOperation) {
            // Track the previous expression from the calculated field (top display)
            String previousExpression = calculatedFieldText.getText();
            // Check if the first character in the top display has a negative sign
            char firstChar = calculatedFieldText.getText().charAt(0);
            if (firstChar == '-') {
                // Remove the '-' from the display. We have captured the negative sign in firstChar
                previousExpression = previousExpression.substring(1);
            }
            // Update Top Field to show '='
            calculatedFieldText.setText(previousExpression + displayFieldText.getText() + " = ");
            // Remove any non-numeric characters to extract numbers only
            String numericExpression = calculatedFieldText.getText().replaceAll("[^0-9.]", " ");
            String elements[] = numericExpression.split("\\s+");

            // If the first number was a negative, appropriately update the num1
            if (firstChar == '-') {
                num1 = -Float.parseFloat(elements[0]);
            } else {
                num1 = Float.parseFloat(elements[0]);
            }

            num2 = Float.parseFloat(elements[1]);
            // Update Input Field to show '='
            calculatedFieldText.setText(String.valueOf(num1) + " " + operationEnvoked + " " + String.valueOf(num2) + " = ");

        }
        // If no operation was requested and an ENTER or "=" was detected,
        // then we simply perform calculation of the result and the num2 with the appropriate previous operation
        // For example if 3-2 = 1, then we pressed enter, then we would have 1-2 = -1. Where result was 1 and num2 was 2
        // and the previous operation was a subtraction
        else {
            // Update num1 and num2
            num1 = Float.parseFloat(displayFieldText.getText());
            num2 = pnum2;
            // Update the calculated field text (Top Display) of the calculator
            calculatedFieldText.setText(String.valueOf(num1) + " " + prevoperationEnvoked + " " + String.valueOf(num2) + " = ");
        }

        // The below statements check which operation was detected
        // It updates the previous pnum1 and pnum2 with the current num1 and num2 respectively
        // Performs a calculation
        // Updated the previous operation envoked with the current operation envoked
        // Resets the requestedOperation to false
        if (operationEnvoked == '+') {
            pnum1 = num1;
            pnum2 = num2;
            ans = num1 + num2;
            prevoperationEnvoked = operationEnvoked;
            operationEnvoked = ' ';
            requestedOperation = false;
        }
        else if (operationEnvoked == '-') {
            pnum1 = num1;
            pnum2 = num2;
            ans = num1 - num2;
            prevoperationEnvoked = operationEnvoked;
            operationEnvoked = ' ';
            requestedOperation = false;
        }
        else if (operationEnvoked == 'x') {
            pnum1 = num1;
            pnum2 = num2;
            ans = num1 * num2;
            prevoperationEnvoked = operationEnvoked;
            operationEnvoked = ' ';
            requestedOperation = false;
        }
        else if (operationEnvoked == '/') {
            pnum1 = num1;
            pnum2 = num2;
            ans = num1 / num2;
            prevoperationEnvoked = operationEnvoked;
            operationEnvoked = ' ';
            requestedOperation = false;
        }

        // If no operation was envoked and an ENTER or "=" was detected
        // We proceed to performing the operation previously envoked
        else {

            if (!requestedOperation && prevoperationEnvoked == '+') {
                ans = num1 + num2;
            } else if (!requestedOperation && prevoperationEnvoked == '-') {
                ans = num1 - num2;
            } else if (!requestedOperation && prevoperationEnvoked == 'x') {
                ans = num1 * num2;
            } else if (!requestedOperation && prevoperationEnvoked == '/') {
                ans = num1 / num2;
            } else {
                calculatedFieldText.setText("");
                ans = num1;
            }
        }
        // Update the display with the final answer
        displayFieldText.setText(String.valueOf(ans));
    }

    /**
     * This method performs an add operation
     * It only works when there is no previous-requested operation
     * Sets the display fields appropriately, requestedOperation to true and operationEnvoked
     * */
    public void add() {
        if (!requestedOperation) {
            // Update Input Field to show '+'
            calculatedFieldText.setText(displayFieldText.getText() + " + ");
            //ans = Float.parseFloat(displayFieldText.getText());
            displayFieldText.setText("");
            operationEnvoked = '+';
            requestedOperation = true;
        }
    }

    /**
     * This method performs a subtraction operation
     * It only works when there is no previous-requested operation
     * Sets the display fields appropriately, requestedOperation to true and operationEnvoked
     * */
    public void subtract() {
        if (!requestedOperation) {
            // Update Input Field to show '-'
            calculatedFieldText.setText(displayFieldText.getText() + " - ");
            ans = Float.parseFloat(displayFieldText.getText());
            displayFieldText.setText("");
            operationEnvoked = '-';
            requestedOperation = true;
        }
    }

    /**
     * This method performs a multiplication operation
     * It only works when there is no previous-requested operation
     * Sets the display fields appropriately, requestedOperation to true and operationEnvoked
     * */
    public void multiply() {
        if (!requestedOperation) {
            // Update Input Field to show 'x'
            calculatedFieldText.setText(displayFieldText.getText() + " x ");
            ans = Float.parseFloat(displayFieldText.getText());
            displayFieldText.setText("");
            operationEnvoked = 'x';
            requestedOperation = true;
        }
    }

    /**
     * This method performs a division operation
     * It only works when there is no previous-requested operation
     * Sets the display fields appropriately, requestedOperation to true and operationEnvoked
     * */
    public void divide() {
        if (!requestedOperation) {
            // Update Input Field to show '/'
            calculatedFieldText.setText(displayFieldText.getText() + " / ");
            ans = Float.parseFloat(displayFieldText.getText());
            displayFieldText.setText("");
            operationEnvoked = '/';
            requestedOperation = true;
        }
    }
}