package mrabezreb.ezrebbot.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mrabezreb.ezrebbot.EzrebBot;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ControlPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 926358433879043929L;
	private JPanel contentPane;
	private static ControlPanel frame;
	private JTextField textField;
	public int preH = 60;
	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ControlPanel();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void close() {
		frame.dispose();
	}
	
	public ControlPanel() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				EzrebBot.bot.disconnect();
				EzrebBot.bot.dispose();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JButton btnStopBot = new JButton("Stop Bot");
		btnStopBot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EzrebBot.bot.disconnect();
				EzrebBot.bot.dispose();
				System.exit(0);
			}
		});
		contentPane.add(btnStopBot, BorderLayout.WEST);
		
		JButton btnSettings = new JButton("Settings");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationWindow.main();
			}
		});
		contentPane.add(btnSettings, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(25);
		
		JButton btnNewButton = new JButton("Send Message");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EzrebBot.bot.sendMessage(EzrebBot.bot.settings.getProperty("channel"), textField.getText());
				textField.setText("");
			}
		});
		panel.add(btnNewButton);
	}
}
