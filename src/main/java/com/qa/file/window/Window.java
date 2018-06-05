package com.qa.file.window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class Window extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Window.class);

    private final DisplayModel model = new DisplayModel();

    public Window() {
        setTitle("File Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        setJMenuBar(createMenuBar());

        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout(0, 0));

        container.add(createTopPanel(), BorderLayout.NORTH);
        container.add(createQuickAccessPanel(), BorderLayout.WEST);
        container.add(createMainPanel(), BorderLayout.CENTER);

        contentPane.add(container);
        setContentPane(contentPane);
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        return menuBar;
    }

    private JPanel createTopPanel() {
        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final BasicArrowButton leftButton = new BasicArrowButton(BasicArrowButton.WEST);
        final BasicArrowButton rightButton = new BasicArrowButton(BasicArrowButton.EAST);

        LOG.info("{}", System.getProperty("user.home"));
        final JTextField directoryField = new JTextField(58);
        directoryField.setText(System.getProperty("user.home"));

        final JButton btnGo = new JButton("Go");
        final BasicArrowButton upButton = new BasicArrowButton(BasicArrowButton.NORTH);

        topPanel.add(leftButton);
        topPanel.add(rightButton);
        topPanel.add(directoryField);
        topPanel.add(btnGo);
        topPanel.add(upButton);

        return topPanel;
    }

    private JPanel createQuickAccessPanel() {
        final JPanel quickAccessPanel = new JPanel();

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Quick Access");
        final DefaultMutableTreeNode desktopNode = new DefaultMutableTreeNode("Desktop");
        final DefaultMutableTreeNode documentNode = new DefaultMutableTreeNode("Documents");
        root.add(desktopNode);
        root.add(documentNode);

        final JTree tree = new JTree(root);
        quickAccessPanel.add(tree);

        return quickAccessPanel;
    }

    private JPanel createMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        final JTable table = new JTable(model);

        mainPanel.add(new JScrollPane(table));

        return mainPanel;
    }
}