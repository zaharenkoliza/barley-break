import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
public class Game extends JFrame {
    //контейнер в виде таблицы
    private JPanel panel = new JPanel(new GridLayout(4, 4, 2, 2));
    private int[][] numbers = new int[4][4];

    public Game() {
        super("Pyatnashki");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        //размеры окна
        setBounds(200, 200, 300, 300);
        //положение на экране
        setLocation(screenSize.width/4 + screenSize.width/8, screenSize.height/2 - screenSize.height/4);
        //пользователь не может менять размер окна
        setResizable(false);
        //задаём то, как окно закрыть
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //создаем меню
        createMenu();

        Container container = getContentPane();
        panel.setDoubleBuffered(true);
        container.add(panel);
        panel.setFocusable(true);
        panel.grabFocus(); //фокус на панели
        panel.addKeyListener(new MyKeyAdapter());

        generate();
        repaintField();
    }
    //метод, генерирующую матрицу
    private void generate() {
        Random generator = new Random();
        int[] invariants = new int[16];

        do {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    //заполняем матрицу нулями
                    numbers[i][j] = 0;
                    invariants[i * 4 + j] = 0;
                }
            }

            for (int i = 1; i < 16; i++) {
                int k, l;
                //находим пустую ячейку и не крайнюю в нижнем правом углу
                do {
                    k = generator.nextInt(4);
                    l = generator.nextInt(4);
                }
                while (numbers[k][l] != 0 | (k == 3 && l == 3));
                numbers[k][l] = i;
                invariants[k * 4 + l] = i;
            }

        }
        while (!canBeSolved(invariants));
    }
    //метод, который проверяет решаема ли данная комбинация, на входе массив из матрицы
    private boolean canBeSolved(int[] invariants) {
        int sum = 3;
        for (int i = 0; i < 16; i++) {
            for (int j = i + 1; j < 16; j++) {
                if (invariants[j] < invariants[i])
                    sum ++;
            }
        }

        return sum % 2 == 0;
    }
    //метод, добавляющий кнопки в панель
    private void repaintField() {
        panel.removeAll();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                //создаём кнопку для цифры
                JButton button = new JButton(Integer.toString(numbers[i][j]));
                button.setFont(new Font("Arial", Font.PLAIN, 20)); //работаем со шрифтом
                button.setFocusable(false);
                panel.add(button);
                panel.setFocusable(true);
                panel.grabFocus();
                if (numbers[i][j] == 0) {
                    button.setVisible(false);
                } else
                    button.addActionListener(new ClickListener());
                button.addKeyListener(new MyKeyAdapter());
            }
        }

        panel.validate();
        panel.repaint();
        panel.grabFocus();
    }
    //метод, проверяющий на победу
    private boolean checkWin() {
        boolean status = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 && j > 2)
                    break;
                if (numbers[i][j] != i * 4 + j + 1) {
                    status = false;
                }
            }
        }
        return status;
    }
    //метод, создающий меню
    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem about = new JMenuItem("About",KeyEvent.VK_A);
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        about.setActionCommand("About".toLowerCase());
        about.addActionListener(new NewMenuListener());

        for (String fileItem : new String [] { "New", "Exit" }) {
            JMenuItem item;
            if (fileItem.equals("New")) {
                item = new JMenuItem(fileItem, KeyEvent.VK_R);
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));}
            else {
                item = new JMenuItem(fileItem, KeyEvent.VK_E);
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_DOWN_MASK)); }
            item.setActionCommand(fileItem.toLowerCase());
            item.addActionListener(new NewMenuListener());
            fileMenu.add(item);
        }
        fileMenu.insertSeparator(1); //добавляем разделить между new и exit

        menu.add(fileMenu);
        menu.add(helpMenu);
        helpMenu.add(about);
        setJMenuBar(menu);
    }

    //класс для обработки событий меню
    private class NewMenuListener implements ActionListener {
        //рассматриваем все кнопки меню
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if ("exit".equals(command)) {
                System.exit(0);
            }
            if ("new".equals(command)) {
                generate();
                repaintField();
            }
            if ("about".equals(command)) {
                JOptionPane.showMessageDialog(null, "Created by Zaharenko Liza P3167 2022", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    //класс для обработки кликов мышки по кнопкам
    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource(); //кнопка, на которую нажали
            button.setVisible(false);
            String name = button.getText();
            change(Integer.parseInt(name));
        }
    }
    //класс для обработки событий клавиатуры
    private class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            Coordinate c = findEmpty();


            switch (e.getKeyCode()) {
                //рассматриваем все варианты нажатия стрелочек на клавиатуре
                case KeyEvent.VK_UP:
                    try {
                        change(numbers[c.row+1][c.column]);
                        break;
                    } catch (IndexOutOfBoundsException ignored) {
                        return;
                    }
                case KeyEvent.VK_DOWN:
                    try {
                        change(numbers[c.row-1][c.column]);
                        break;
                    } catch (IndexOutOfBoundsException ignored) {
                        return;
                    }
                case KeyEvent.VK_RIGHT:
                    try {
                        change(numbers[c.row][c.column-1]);
                        break;
                    } catch (IndexOutOfBoundsException ignored) {
                        return;
                    }
                case KeyEvent.VK_LEFT:
                    try {
                        change(numbers[c.row][c.column+1]);
                        break;
                    } catch (IndexOutOfBoundsException ignored) {
                        return;
                    }
            }
        }
    }
    //класс для системы коррдинат
    private class Coordinate {
        private int row;
        private int column;
        private Coordinate(int i, int j) {
            row = i;
            column = j;
        }
    }
    //возвращает координаты пустой клетки
    private Coordinate findEmpty()
    {
        int k; int l;
        for (k = 0; k < 4; k++) {
            for (l = 0; l < 4; l++) {
                if (numbers[k][l] == 0) {
                    return (new Coordinate(k, l));
                }
            }
        }
        return null;
    }
    //метод меняет положение пятнашки и проверяет на победу
    private void change(int num) {
        int i = 0, j = 0;
        for (int k = 0; k < 4; k++) { //находим необходимую клетку
            for (int l = 0; l < 4; l++) {
                if (numbers[k][l] == num) {
                    i = k;
                    j = l;
                }
            }
        }
        //находим пустую клетку рядом, чтобы переместить туда значение
        if (i > 0) {
            if (numbers[i - 1][j] == 0) {
                numbers[i - 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (i < 3) {
            if (numbers[i + 1][j] == 0) {
                numbers[i + 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (j > 0) {
            if (numbers[i][j - 1] == 0) {
                numbers[i][j - 1] = num;
                numbers[i][j] = 0;
            }
        }
        if (j < 3) {
            if (numbers[i][j + 1] == 0) {
                numbers[i][j + 1] = num;
                numbers[i][j] = 0;
            }
        }

        repaintField(); //перерисовываем поле
        //победа игрока
        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "YOU WIN!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            generate();
            repaintField();
        }
    }

    //запускаем игру
    public static void main(String[] args) {
        JFrame app = new Game();
        app.setVisible(true);
    }
}

