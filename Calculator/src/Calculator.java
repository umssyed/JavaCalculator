import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;


/**
 * Calculator Class
 * */
public class Calculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorApp app = new CalculatorApp();
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
    private char operationEnvoked = ' ';
    private char prevoperationEnvoked = ' ';
    private boolean requestedOperation = false;
    private float pnum1 = 0; // Previous calculated numbers. Start from 0
    private float pnum2 = 0; // Previous calculated numbers. Start from 0
    private float ans = 0;
    private int counter = 0;

    /** Constructor - Calculator App. Takes no arguments */
    public CalculatorApp() {
        // Set application context
        setTitle("Java Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Settings for color, font and background
        Font calculatedFieldFont = new Font ("Arial", Font.PLAIN, 15);
        Color calculatorBackground = new Color(235, 235, 235);
        Color calculatedFieldFontColor = new Color(130, 130, 130);
        Font displayFieldFont = new Font ("Arial", Font.PLAIN, 40);

        // Enter Invisible Field
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

            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
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

    public String backSpaceEntered() {
        String currentText = displayFieldText.getText();
        return currentText.substring(0, currentText.length() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton){
            JButton clickedButton = (JButton) source;
            String buttonOperation = clickedButton.getText();
            if (buttonOperation.matches("[0-9]")) {
                int number = Integer.parseInt(buttonOperation);
                enterNumber(number);
            }
            else if (buttonOperation.equals("CLR")) {
                displayFieldText.setText("0");
                calculatedFieldText.setText("");
                displayFieldText.requestFocus();
                starting_or_clear = true;
            }
            else if (buttonOperation.equals(".")) {
                enterDecimal();
            }
            else if (buttonOperation.equals("=")) {
                enterEquals();
            }
            else if (buttonOperation.equals("+")) {
                add();
            }
            else if (buttonOperation.equals("-")) {
                subtract();
            }
            else if (buttonOperation.equals("x")) {
                multiply();
            }
            else if (buttonOperation.equals("/")) {
                divide();
            }
        }
        // Request refocus on the displayFieldText to ensure capturing of events
        displayFieldText.requestFocus();
    }

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

            // Add mouse listener
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

            // Add mouse listener
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

    public void enterNumber(int number) {
        if (starting_or_clear) {
            displayFieldText.setText("");
            displayFieldText.setText(String.valueOf(number));
            starting_or_clear = false;
        } else {
            displayFieldText.setText(displayFieldText.getText() + number);
        }
    }

    public void enterDecimal() {
        boolean decimalExists = decimalExists();
        if (!decimalExists) {
            displayFieldText.setText(displayFieldText.getText() + ".");
        }
    }

    public void enterBackSpace() {
        String updatedText = backSpaceEntered();
        displayFieldText.setText(updatedText);
    }

    public void enterEquals() {
        float num1;
        float num2;

        String previousExpression = calculatedFieldText.getText();

        char firstChar = calculatedFieldText.getText().charAt(0);
        if (firstChar == '-') {
            previousExpression = previousExpression.substring(1);
        }

        if (requestedOperation) {
            // Update Input Field to show '='
            calculatedFieldText.setText(previousExpression + displayFieldText.getText() + " = ");
            // Remove any non-numeric characters to extract numbers only
            String numericExpression = calculatedFieldText.getText().replaceAll("[^0-9.]", " ");
            String elements[] = numericExpression.split("\\s+");

            if (firstChar == '-') {
                num1 = -Float.parseFloat(elements[0]);
            } else {
                num1 = Float.parseFloat(elements[0]);
            }

            if (elements.length > 1) {
                num2 = Float.parseFloat(elements[1]);
            } else {
                num2 = ans;
            }
            // Update Input Field to show '='
            calculatedFieldText.setText(String.valueOf(num1) + " " + operationEnvoked + " " + String.valueOf(num2) + " = ");

        } else {
            String numericPreviousExpression = previousExpression.replaceAll("[^0-9.]", " ");
            String elements[] = numericPreviousExpression.split("\\s+");

            num1 = Float.parseFloat(displayFieldText.getText());
            num2 = pnum2;

            calculatedFieldText.setText(String.valueOf(num1) + " " + prevoperationEnvoked + " " + String.valueOf(num2) + " = ");
        }

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
        else {

            if (!requestedOperation && prevoperationEnvoked == '+') {
                System.out.println("+ PrevOp Envoked, num1 = " + num1 + ", num2 = " + num2);
                ans = num1 + num2;
            } else if (!requestedOperation && prevoperationEnvoked == '-') {
                System.out.println("- PrevOp Envoked, num1 = " + num1 + ", num2 = " + num2);
                ans = num1 - num2;
            } else if (!requestedOperation && prevoperationEnvoked == 'x') {
                System.out.println("x PrevOp Envoked, num1 = " + num1 + ", num2 = " + num2);
                ans = num1 * num2;
                System.out.println("ans = " + ans);
            } else if (!requestedOperation && prevoperationEnvoked == '/') {
                System.out.println("/ PrevOp Envoked, num1 = " + num1 + ", num2 = " + num2);
                ans = num1 / num2;
            } else {
                System.out.println("No Operation Envoked ");
                calculatedFieldText.setText("");
                ans = num1;
            }
        }

        //ans = ans + Float.parseFloat(displayFieldText.getText());
        displayFieldText.setText(String.valueOf(ans));
    }

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