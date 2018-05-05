
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

public class MainFrame {

    /* Frame components */
    private JFrame frmMain;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTextPane txtResult;
    private JScrollPane scrollPane;
    /* Dimension values */
    private static final int MAIN_FORM_WIDTH = 535;
    private static final int MAIN_FORM_HEIGHT = 500;
    private static final int TXT_SEARCH_X = 10;
    private static final int TXT_SEARCH_Y = 10;
    private static final int TXT_SEARCH_WIDTH = 400;
    private static final int TXT_SEARCH_HEIGHT = 25;
    private static final int BTN_SEARCH_X = TXT_SEARCH_X + TXT_SEARCH_WIDTH + 10;
    private static final int BTN_SEARCH_Y = TXT_SEARCH_Y;
    private static final int BTN_SEARCH_WIDTH = 100;
    private static final int BTN_SEARCH_HEIGHT = 25;
    private static final int TXT_RESULT_X = TXT_SEARCH_X;
    private static final int TXT_RESULT_Y = TXT_SEARCH_X + TXT_SEARCH_HEIGHT + 10;
    private static final int TXT_RESULT_WIDTH = TXT_SEARCH_WIDTH + 10 + BTN_SEARCH_WIDTH;
    private static final int TXT_RESULT_HEIGHT = MAIN_FORM_HEIGHT - 85;

    public static InvertedIndex index;

    public MainFrame() {
        initialize();
    }

    public static void main(String args[]) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.frmMain.setVisible(true);
        index = InvertedIndex.getInstance();

        if (!index.isIndexed("indexed/")) {
            List<File> fileList = index.indexFileList("res/");
            for (int i = 0; i < fileList.size(); i++) {
                index.buildIndex(fileList.get(i));
            }
            index.saveIndex();
        } else {
            index.readIndex("indexed/indexed-20180429.txt");
        }
        index.retrieveIndex("res/1.txt", 50);
    }

    private void initialize() {
        frmMain = new JFrame();
        frmMain.setTitle("Search Engine");
        frmMain.setSize(MAIN_FORM_WIDTH, MAIN_FORM_HEIGHT);
        frmMain.setResizable(false);
        frmMain.getContentPane().setLayout(null);
        frmMain.setLocationRelativeTo(null);
        frmMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        txtSearch = new JTextField();
        txtSearch.setBounds(TXT_SEARCH_X, TXT_SEARCH_Y, TXT_SEARCH_WIDTH, TXT_SEARCH_HEIGHT);
        frmMain.getContentPane().add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(BTN_SEARCH_X, BTN_SEARCH_Y, BTN_SEARCH_WIDTH, BTN_SEARCH_HEIGHT);
        frmMain.getContentPane().add(btnSearch);
        frmMain.getRootPane().setDefaultButton(btnSearch);
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                performSearch();
            }
        });

        btnSearch.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                performSearch();
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });

        txtResult = new JTextPane();
        txtResult.setContentType("text/html");
        txtResult.setEditable(false);
        scrollPane = new JScrollPane(txtResult);
        scrollPane.setBounds(TXT_RESULT_X, TXT_RESULT_Y, TXT_RESULT_WIDTH, TXT_RESULT_HEIGHT);
        frmMain.getContentPane().add(scrollPane);
    }

    private void performSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                txtResult.setText(index.searchResult(txtSearch.getText().toString()));
            }
        }).start();
    }
}
