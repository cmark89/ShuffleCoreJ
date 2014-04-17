import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ShuffleCoreJOptions {
	JFrame thisFrame;

	JTextField msField;
	JTextField sizeField;

	JButton applyButton;
	JButton cancelButton;
	JCheckBox removeCardsBox;

	int shuffleSpeed;
	int fontSize;
	boolean removeCardsWhenSelected;

	ShuffleCoreJ parentFrame;

	public ShuffleCoreJOptions(ShuffleCoreJ parent) {
		this.shuffleSpeed = parent.getShuffleSpeed();
		this.fontSize = parent.getFontSize();
		this.removeCardsWhenSelected = parent.getRemovingCards();
		parentFrame = parent;
		setupGUI();	
	}

	private void setupGUI() {
			thisFrame = new JFrame("Options");
			thisFrame.setDefaultCloseOperation
				(JFrame.DISPOSE_ON_CLOSE);
			thisFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					parentFrame.closeOptionsWindow();
				}
			});

			thisFrame.setSize(300,200);
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, 
						BoxLayout.Y_AXIS));
			thisFrame.add(BorderLayout.CENTER, mainPanel);
		
			msField = new JTextField(
					Integer.toString(shuffleSpeed), 4);	
			sizeField = new JTextField(
					Integer.toString(fontSize), 4);

			JPanel msPanel = new JPanel();
			msPanel.add(new JLabel("Speed (ms): "));
			msPanel.add(msField);

			JPanel sizePanel = new JPanel();
			sizePanel.add(new JLabel("Text size: "));
			sizePanel.add(sizeField);

			removeCardsBox = new JCheckBox("Removed cards when selected", removeCardsWhenSelected);
			mainPanel.add(msPanel);
			mainPanel.add(sizePanel);
			mainPanel.add(removeCardsBox);

			// Setup buttons
			JPanel buttonPanel = new JPanel();
			applyButton = new JButton("Apply");
			applyButton.addActionListener(
				new ApplyButtonListener());
			
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(
				new CancelButtonListener());

			buttonPanel.add(applyButton);
			buttonPanel.add(cancelButton);

			thisFrame.getContentPane().add(BorderLayout.SOUTH,
					buttonPanel);
			thisFrame.setVisible(true);
	}

	private class ApplyButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parentFrame.setShuffleSpeed(
				Integer.parseInt(msField.getText()));
			parentFrame.setFontSize(
				Integer.parseInt(sizeField.getText()));
			parentFrame.setRemovingCards(removeCardsBox.isSelected());

			thisFrame.dispose();
		}
	}

	private class CancelButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			thisFrame.dispose();
		}
	}
}
