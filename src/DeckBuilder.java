import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class DeckBuilder {
	JFrame frame;
	ArrayList<String> entries;
	JTextField textField;
	DefaultListModel<String> cardListModel;
	JList<String> cardList;

	File currentFile = null;
	boolean changesMade = false;

	// Temporary:
	public static void main(String[] args) {
		new DeckBuilder();
	}

	public DeckBuilder() {
		constructGUI();
	}

	private void constructGUI() {
		frame = new JFrame("ShuffleCoreJ - Deck Builder");
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// Set up the left (card list) panel
		JPanel cardPanel = new JPanel();
		JLabel cardTextLabel = new JLabel("Card Text: ");
		entries = new ArrayList<String>();

		textField = new JTextField(15);

		cardPanel.add(cardTextLabel);
		cardPanel.add(textField);	

		cardListModel = new DefaultListModel<String>();
		cardList = new JList<String>(cardListModel);
		cardList.setVisibleRowCount(15);
		JScrollPane cardScroller = new JScrollPane(cardList);
		cardScroller.setHorizontalScrollBarPolicy(
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cardScroller.setVerticalScrollBarPolicy(
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		cardPanel.add(cardScroller);

		// Now build the right frame
		JPanel buttonPanel = new JPanel(new BorderLayout());
		ImageIcon addIcon = new ImageIcon("resources/add.png");
		ImageIcon removeIcon = new ImageIcon("resources/delete.png");

		Image raw = addIcon.getImage();
		Image resized = raw.getScaledInstance(32,32,Image.SCALE_SMOOTH);
		addIcon = new ImageIcon(resized);

		raw = removeIcon.getImage();
		resized = raw.getScaledInstance(32,32,Image.SCALE_SMOOTH);
		removeIcon = new ImageIcon(resized);

		JButton addButton = new JButton(addIcon);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if(s.length() > 0) {
					textField.setText("");
					entries.add(s);
					cardListModel.addElement(s);
					changesMade = true;
				}
				textField.requestFocus();
			}
		});
		
		JButton removeButton = new JButton(removeIcon);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indexes = cardList.getSelectedIndices();
				int offset = 0;
				for(int i : indexes) {
					cardListModel.remove(i - offset);	
					entries.remove(i - offset);
					offset++;
				}
				changesMade = true;
			}
		});

		buttonPanel.add(BorderLayout.NORTH, addButton);	
		buttonPanel.add(BorderLayout.SOUTH, removeButton);

		frame.getContentPane().add(BorderLayout.CENTER, cardPanel);
		frame.getContentPane().add(BorderLayout.EAST, buttonPanel);

		// Create the menu bar
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewDeck();	
			}
		});

		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openOpenDialogue();
			}
		});

		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentFile != null && changesMade) {
					saveFile(currentFile);
				}
				else if(currentFile == null) {
					openSaveDialogue();
				}
			}
		});

		JMenuItem saveAsMenuItem = new JMenuItem("Save As");
		saveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openSaveDialogue();
			}
		});


		fileMenu.add(newMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);

		menu.add(fileMenu);
		
		frame.setJMenuBar(menu);
		frame.setVisible(true);
	}

	private void createNewDeck() {
		// Check if there are any changes to save?
		clear();
		textField.setText("");
		cardListModel.clear();
		entries.clear();
	}

	private void openSaveDialogue() {
		JFileChooser saver = new JFileChooser();
		saver.showSaveDialog(frame);
		saveFile(saver.getSelectedFile());
	}

	private void openOpenDialogue() {
		JFileChooser opener = new JFileChooser();

		int returnVal = opener.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = opener.getSelectedFile();
			openFile(file);
		}
	}

	private void clear() {
		currentFile = null;

		textField.setText("");
		cardListModel.clear();
		entries.clear();
	}

	// Here we actually write to the target file
	private void saveFile(File targetFile) {
		currentFile = targetFile;
		try {
			BufferedWriter bw = new BufferedWriter(
				new FileWriter(targetFile));

			// First write the deck name
			
			// Now write each card
			for(String s : entries) {
				bw.write(s + "\n");
			}

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openFile(File targetFile) {
		clear();
		currentFile = targetFile;
		try {


			FileReader fr = new FileReader(targetFile);
			BufferedReader br = new BufferedReader(fr);
			
			String line = null;
			while(true) {
				line = br.readLine();
				if(line == null)
					break;
				else {
					entries.add(line);
					cardListModel.addElement(line);
				}
			}
			br.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
