package com.qa.file.window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Window extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Window.class);

    private String currentPath;
    private LinkedList<String> backList = new LinkedList<>();
    private LinkedList<String> frontList = new LinkedList<>();
    private JTree tree;
    private JTextField directoryField;
    private JTable table;
    private JPopupMenu popup;
    private File file;
    private List<File> listOfFiles = new ArrayList<>();
    private final FileSystemView fileSystemView;
    private final DisplayModel model = new DisplayModel();
    private final DirListener dirListener = new DirListener();
    private final DirButtons buttons = new DirButtons();
    private final MenuButtons menuButtons = new MenuButtons();

    private Window() {
        fileSystemView = FileSystemView.getFileSystemView();

        setTitle("File Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        setJMenuBar(createMenuBar());

        final JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));

        contentPane.add(createTopPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(createQuickAccessPanel()), createMainPanel());

        contentPane.add(splitPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        createPopup();
    }

    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        JMenu mnFile = new JMenu("File");
        fileMenu(mnFile);
        menuBar.add(mnFile);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);

        JMenu mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);

        return menuBar;
    }

    private void fileMenu(JMenu mnFile) {
        final JMenuItem newWindow = new JMenuItem("New Window");
        newWindow.addActionListener(actionEvent -> startProgram());
        final JMenuItem openCmd = new JMenuItem("Open Command Prompt Here");
        openCmd.addActionListener(actionEvent -> {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", "cd", currentPath});
            } catch (IOException io) {
                LOG.error("Failed to open CMD", io);
            }
        });
        final JMenuItem close = new JMenuItem("Close");
        close.addActionListener(actionEvent -> this.dispose());

        mnFile.add(newWindow);
        mnFile.add(openCmd);
        mnFile.addSeparator();
        mnFile.add(close);
    }

    private JPanel createTopPanel() {
        final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final BasicArrowButton leftButton = new BasicArrowButton(BasicArrowButton.WEST);
        leftButton.setActionCommand("BACK");
        leftButton.addActionListener(buttons);
        final BasicArrowButton rightButton = new BasicArrowButton(BasicArrowButton.EAST);
        rightButton.setActionCommand("FORWARD");
        rightButton.addActionListener(buttons);

        LOG.info("{}", System.getProperty("user.home"));
        currentPath = System.getProperty("user.home");
        directoryField = new JTextField(58);
        directoryField.setText(System.getProperty("user.home"));
        directoryField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                LOG.info("code: {}", keyEvent.getKeyChar(), keyEvent.getKeyCode());
                if (keyEvent.getKeyCode() == 10) {
                    lf(directoryField.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        final JButton btnGo = new JButton("Go");
        btnGo.setActionCommand("GO");
        btnGo.addActionListener(buttons);
        final BasicArrowButton upButton = new BasicArrowButton(BasicArrowButton.NORTH);
        upButton.setActionCommand("UP");
        upButton.addActionListener(buttons);

        topPanel.add(leftButton);
        topPanel.add(rightButton);
        topPanel.add(directoryField);
        topPanel.add(btnGo);
        topPanel.add(upButton);

        return topPanel;
    }

    private JPanel createQuickAccessPanel() {
        final JPanel quickAccessPanel = new JPanel();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        TreeSelectionListener treeSelectionListener = tse -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
            showChildren(node);
        };

        // show the file system roots.
        File[] roots = fileSystemView.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add(node);
            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
        }

        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(treeSelectionListener);
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.expandRow(0);
        JScrollPane treeScroll = new JScrollPane(tree);

        tree.setVisibleRowCount(15);

        Dimension preferredSize = treeScroll.getPreferredSize();
        Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
        treeScroll.setPreferredSize(widePreferred);

        quickAccessPanel.add(new JScrollPane(tree));

        return quickAccessPanel;
    }

    private void showChildren(final DefaultMutableTreeNode node) {
        JProgressBar progressBar = new JProgressBar();
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    private JPanel createMainPanel() {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        table = new JTable(model);
        table.addMouseListener(dirListener);
        getDefaultFiles();

        mainPanel.add(new JScrollPane(table));

        return mainPanel;
    }

    private void createPopup() {
        popup = new JPopupMenu();
        final JMenuItem open = new JMenuItem("Open");
        open.setActionCommand("OPEN");
        open.addActionListener(menuButtons);
        final JMenuItem copy = new JMenuItem("Copy");
        copy.setActionCommand("COPY");
        copy.addActionListener(menuButtons);
        final JMenuItem cut = new JMenuItem("Cut");
        cut.setActionCommand("CUT");
        cut.addActionListener(menuButtons);
        final JMenuItem paste = new JMenuItem("Paste");
        paste.setActionCommand("PASTE");
        paste.addActionListener(menuButtons);
        final JMenuItem delete = new JMenuItem("Delete");
        delete.setActionCommand("DELETE");
        delete.addActionListener(menuButtons);

        popup.add(open);
        popup.addSeparator();
        popup.add(copy);
        popup.add(cut);
        popup.add(paste);
        popup.addSeparator();
        popup.add(delete);
    }

    private void getDefaultFiles() {
        try {
            Files.list(Paths.get(currentPath))
                    .filter(path -> !path.toFile().isHidden())
                    .forEach(path -> model.addRow(path.toFile()));
        } catch (IOException io) {
            LOG.error("Could not read files!", io);
        }
    }

    private void lf(String dir) {
        backList.push(currentPath);
        final Path path = Paths.get(dir);
        currentPath = path.toString();
        model.clearRow();
        directoryField.setText(currentPath);
        getDefaultFiles();
    }

    private void openFile(File file) {
        try {
            if (file.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }
        } catch (IOException io) {
            LOG.error("Could not open file!", io);
        }
    }

    class DirListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                final int row = table.rowAtPoint(e.getPoint());
                final int col = table.columnAtPoint(e.getPoint());
                if (!table.isRowSelected(row)) table.changeSelection(row, col, false, false);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            model.getValueAt(table.rowAtPoint(e.getPoint()), 0);
            file = model.getFile();
            LOG.info(file.getName());
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (e.getClickCount() == 2) {
                    if (file.isDirectory()) {
                        backList.push(currentPath);
                        LOG.info("Clicked dir");
                        LOG.info("{} || {}", table.rowAtPoint(e.getPoint()));
                        currentPath += "\\" + table.getValueAt(table.rowAtPoint(e.getPoint()), 0);
                        model.clearRow();
                        directoryField.setText(currentPath);
                        getDefaultFiles();
                    } else {
                        openFile(file);
                    }
                }
            }
        }
    }

    class DirButtons implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.info("Action Listener");
            LOG.info("{}", backList);

            switch (e.getActionCommand()) {
                case "BACK":
                    if (!backList.isEmpty()) {
                        frontList.push(currentPath);
                        currentPath = backList.get(0);
                        backList.remove(0);
                        LOG.info("CurrentPath: " + currentPath);
                        model.clearRow();
                        directoryField.setText(currentPath);
                        getDefaultFiles();
                    }
                    break;
                case "FORWARD":
                    if (!frontList.isEmpty()) {
                        backList.push(currentPath);
                        currentPath = frontList.get(0);
                        frontList.remove(0);
                        LOG.info(currentPath);
                        model.clearRow();
                        directoryField.setText(currentPath);
                        getDefaultFiles();
                    }
                    break;
                case "UP":
                    lf(currentPath);
                    break;
                case "GO":
                    lf(directoryField.getText());
                    break;
            }
        }
    }

    class MenuButtons implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.debug(e.getActionCommand());
            switch (e.getActionCommand()) {
                case "OPEN":
                    if (!file.isDirectory()) {
                        LOG.info(file.getName());
                        openFile(file);
                    }
                    break;
                case "COPY":
                    listOfFiles.clear();
                    listOfFiles.add(file);
                    final FileTransferable ft = new FileTransferable(listOfFiles);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, transferable) -> LOG.debug("Clipboard: {}, Transferable: {}", clipboard, transferable));
                    break;
            }
        }
    }

    public static void startProgram() {
        try {
            Window frame = new Window();
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}