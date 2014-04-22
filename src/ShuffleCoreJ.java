import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ShuffleCoreJ {
	JFrame frame;
	JButton shuffleButton;
	JLabel mainLabel;

	int cardIndex = 0;
	JComboBox<String> deckSelect;
	ArrayList<String> availableDecks;

	ArrayList<String> loadedDeck;
	ArrayList<String> currentDeck;

	boolean shuffling = false;

	final String DECK_PATH = "Decks";
	final String CONFIG_PATH = "ShuffleCoreJ.config";

	int shuffleSpeed = 100;
	int fontSize = 30;

	final int DEFAULT_SHUFFLE_SPEED = 100;
	final int DEFAULT_FONT_SIZE = 30;
	final boolean DEFAULT_REMOVE_CARDS = true;

	ShuffleCoreJOptions optionsWindow = null;
	boolean removeCardsWhenSelected = true;
	
	public void setup() {
		// Initialize the list with a blank element
		availableDecks = new ArrayList<String>();
		availableDecks.add("");
		currentDeck = new ArrayList<String>();
		loadedDeck = new ArrayList<String>();

		// Set up the default values in case the config is corrupt
		shuffleSpeed = DEFAULT_SHUFFLE_SPEED;
		fontSize = DEFAULT_FONT_SIZE;
		removeCardsWhenSelected = DEFAULT_REMOVE_CARDS;

		readConfig();

		fileCheck();
		setupGUI();
	}

	private void readConfig() {
		// Check to see if the file exists
		File f = new File(CONFIG_PATH);
		if(f.exists() && !f.isDirectory()) {
			try {
				BufferedReader br = 
					new BufferedReader(new FileReader(f));
				String s = "";
				while((s = br.readLine()) != null) {
					parseConfigLine(s);
				}

				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Create the default config file
			createDefaultConfig();
			readConfig();
		}
	}

	private void createDefaultConfig() {
		File f = new File(CONFIG_PATH);
		try {
			PrintWriter bw = new PrintWriter(
				new FileWriter(f));
		
			bw.println("#---------------------------");
			bw.println("# SHUFFLECOREJ CONFIGURATION");
			bw.println("#---------------------------");
			bw.println("");
			bw.println("fontSize : " + DEFAULT_FONT_SIZE);
			bw.println("shuffleSpeed: " + DEFAULT_SHUFFLE_SPEED);
			bw.println("removeCards : " + 
				Boolean.toString(DEFAULT_REMOVE_CARDS));

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseConfigLine(String s) {
		// Remove whitespace
		String newS = s.replaceAll("\\s", "");
		// Skip comment lines and blank lines
		if(newS.length() <= 0 || newS.charAt(0) == '#')
			return;

		// Split into two parts on :
		String[] subs = newS.split(":");
		if(subs[0].equals("fontSize")) {
			fontSize = Integer.parseInt(subs[1]);
		}
		if(subs[0].equals("shuffleSpeed")) {
			shuffleSpeed = Integer.parseInt(subs[1]);
		}
		if(subs[0].equals("removeCards")) {
			if(subs[1].equals("true")) {
				removeCardsWhenSelected = true;
			} else {
				removeCardsWhenSelected = false;
			}
		}
	}

	public void setupGUI() {
		frame = new JFrame("ShuffleCoreJ");
		frame.setSize(400,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Setup options button
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());
		JButton optionsButton = new JButton("Options");
		optionsButton.addActionListener(new OptionsButtonListener());
		optionsPanel.add(BorderLayout.EAST, optionsButton);

		JPanel bottomPanel = new JPanel();
		shuffleButton = new JButton("Shuffle");
		shuffleButton.addActionListener(new ShuffleButtonListener());
		bottomPanel.add(shuffleButton);

		mainLabel = new JLabel("ShuffleCoreJ", SwingConstants.CENTER);
		mainLabel.setFont(new Font("sanserif", Font.BOLD, 30));

		JPanel topPanel = new JPanel(new BorderLayout());
		String[] deckArray = new String[availableDecks.size()];
		deckSelect = new JComboBox<String>(availableDecks.toArray(
			deckArray)); 
		deckSelect.addActionListener(new DeckSelectActionListener());
		topPanel.add(optionsPanel, BorderLayout.NORTH);
		topPanel.add(BorderLayout.EAST, deckSelect);

		frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
		frame.getContentPane().add(BorderLayout.NORTH, topPanel);
		frame.getContentPane().add(BorderLayout.CENTER, mainLabel);

		frame.setVisible(true);
	}

	private void fileCheck() {
		// Check if the "Deck" folder exists
		File f = new File(DECK_PATH);
		if(f.isDirectory()) {
			System.out.println(DECK_PATH + " directory found");
		} else {
			System.out.println(DECK_PATH + " directory not found.");
			System.out.println("Creating directory...");

			// Create the default directory and write a default file
			f.mkdirs();
			createDefaultFile();
		}

		// Parse all available files
		parseDeckFiles();
	}

	/// Creates a default file to be read by the program
	private void createDefaultFile() {
		try {
			String[] lines = { 
				"This", "is", "an", "example", "of", 
				"ShuffleCoreJ", "these", "words", "will", 
				"appear", "in", "a", "random", "order", 
				"日本語でもOK"
			};
		
			BufferedWriter writer = new BufferedWriter(
			new FileWriter(DECK_PATH + "/example"));

			for(String s : lines) {
				writer.write(s + "\n");
			}

			System.out.println("Default file created.");
			writer.close();

		} catch(Exception e) { 
			e.printStackTrace();
		}
	}

	private void parseDeckFiles() {
		File f = new File(DECK_PATH);
		String[] fileNames = f.list();	

		// Now add each name to the combobox
		for(String s : fileNames) {
			availableDecks.add(s);
		}
	}

	private void loadDeck(String path) {
		// Load the file, parsing it into the current deck
		currentDeck.clear();
		loadedDeck.clear();

		String filePath = DECK_PATH + "/" + path;
		System.out.println("Load text from " + filePath);
		File f = new File(filePath);

		// Try to parse the deck
		try {
			BufferedReader reader = new BufferedReader(
				new FileReader(f));

			String line = "";
			while((line = reader.readLine()) != null) {
				loadedDeck.add(line);
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resetDeck();
	}

	private void resetDeck() {
		// Make the current deck a copy of the loaded deck
		currentDeck = new ArrayList<String>(loadedDeck);
		System.out.println("Loaded deck: " + loadedDeck.size());
		System.out.println("Current deck: " + currentDeck.size());

		// See if we want to shuffle
		// if(randomizeOrder) { ...
		
		ArrayList<String> temp = new ArrayList<String>();
		while(currentDeck.size() > 0) {
			int index = (int)(Math.random() * currentDeck.size());
			temp.add(currentDeck.get(index));
			currentDeck.remove(index);
		}

		currentDeck = temp;
	}


	private void openOptionsWindow() {
		if(optionsWindow == null) {
			optionsWindow = new ShuffleCoreJOptions(this);
		}
	}

	public void closeOptionsWindow() {
		optionsWindow = null;
	}

	public void setShuffleSpeed(int speed) {
		shuffleSpeed = speed;
	}

	public int getShuffleSpeed() { return shuffleSpeed; }
	public int getFontSize() { return fontSize; }

	public void setRemovingCards(boolean bool) {
		removeCardsWhenSelected = bool;
	}
	public boolean getRemovingCards() { return removeCardsWhenSelected; }

	public void setFontSize(int size) {
		fontSize = size;
		mainLabel.setFont(new Font("sanserif", Font.BOLD, size));
	}

	private void shuffle(ArrayList<String> list) {
		// Shuffle the given list
		ArrayList<String> temp = new ArrayList<String>();
		while(list.size() > 0) {
			// Get a random index
			int i = (int)(Math.random() * list.size());
			temp.add(list.get(i));
			list.remove(i);
		}

		list = temp;
	}

	public static void main(String[] args) {
		ShuffleCoreJ shuffleCore = new ShuffleCoreJ();
		shuffleCore.setup();
	}

	private void startShuffling() {
		mainLabel.setForeground(Color.black);
		if(currentDeck.size() == 0) {
			resetDeck();
			System.out.println("CURRENT DECK IS SIZE 0");
		}

		shuffling = true;
		Thread t = new Thread(new ThreadedShuffle());
		t.start();
	}

	class ThreadedShuffle implements Runnable {
		public void run() {
			int time = 0;
			while(shuffling) {
				try {
					Thread.sleep(shuffleSpeed);
					if(shuffling)
						nextCard();	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void stopShuffling() {
		shuffling = false;
		//nextCard();

		// Set the label color to red!
		mainLabel.setForeground(Color.red);

		// Now remove this card from the deck if we have to
		if(removeCardsWhenSelected) {
			if(currentDeck.size() > 1)
				currentDeck.remove(cardIndex);	
			else
				resetDeck();
		}
	}

	private void nextCard() {
		cardIndex++;
		if(cardIndex >= currentDeck.size())
			cardIndex = 0;

		mainLabel.setText(currentDeck.get(cardIndex));
	}

	// ---Listener classes---
	class ShuffleButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!shuffling) {
				startShuffling();
			} else {
				stopShuffling();
			}
			System.out.println("Shuffle Button Clicked");	
		}
	}

	class DeckSelectActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadDeck((String)deckSelect.getSelectedItem());	
		}
	}

	class OptionsButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Open options window
			openOptionsWindow();
		}
	}
}
