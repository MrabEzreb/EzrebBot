package mrabezreb.ezrebbot.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import mrabezreb.ezrebbot.EzrebBot;

public class ConfigurationWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -327062529466591601L;
	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigurationWindow frame = new ConfigurationWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConfigurationWindow() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.NORTH);
		
		JPanel connectionTab = new JPanel();
		FlowLayout flowLayout = (FlowLayout) connectionTab.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		tabbedPane.addTab("Connection", null, connectionTab, "Connection Settings");
		
		JSplitPane channel = new JSplitPane();
		channel.setBorder(null);
		connectionTab.add(channel);
		
		textField = new JTextField();
		channel.setRightComponent(textField);
		textField.setText(EzrebBot.bot.settings.getProperty("channel"));
		textField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				EzrebBot.bot.settings.setProperty("channel", textField.getText());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				EzrebBot.bot.settings.setProperty("channel", textField.getText());
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				EzrebBot.bot.settings.setProperty("channel", textField.getText());
			}
		});
		textField.setColumns(20);
		
		JLabel lblChannel = new JLabel("Channel");
		channel.setLeftComponent(lblChannel);
		
		JButton btnSaveSettings = new JButton("Save Settings");
		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(new File("EzrebBot.properties"));
					EzrebBot.bot.settings.store(fos,"");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fos.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		contentPane.add(btnSaveSettings, BorderLayout.SOUTH);
	}

}
