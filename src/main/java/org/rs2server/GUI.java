package org.rs2server;

import org.rs2server.rs2.RS2Server;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.util.TextAreaOutputStream;
import org.rs2server.tools.BanActionListener;
import org.rs2server.tools.InformationActionListener;
import org.rs2server.tools.KickActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI implements Runnable {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());

	static JList<String> players = new JList<>();

	static DefaultListModel<String> listModel = new DefaultListModel<String>();

	static String[] playerList;
	static JList<String> list = new JList<String>(listModel);

	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	public JList<String> getList() {
		return list;
	}

	public GUI() {
		JFrame frame = new JFrame("Lost-Isle Console");
		frame.getContentPane().setLayout(null);

		list.setForeground(Color.GREEN);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setVisibleRowCount(5);

		JPopupMenu menu = new JPopupMenu();

		JMenuItem info = new JMenuItem("Information");
		JMenuItem kick = new JMenuItem("Kick");
		JMenuItem ban = new JMenuItem("Ban");
		JMenuItem swagHammer = new JMenuItem("SwagHammer");

		info.addActionListener(new InformationActionListener(this));
		kick.addActionListener(new KickActionListener(this));
		ban.addActionListener(new BanActionListener(this));

		menu.add(info);
		menu.add(kick);
		menu.add(ban);
		menu.add(swagHammer);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {// get pressed too i forget
													// which
				if (SwingUtilities.isRightMouseButton(e) && list.locationToIndex(e.getPoint()) != -1) {
					int row = list.locationToIndex(e.getPoint());
					list.setSelectedIndex(row);
					menu.show(list, e.getX(), e.getY());
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(10, 55, 72, 182);
		frame.getContentPane().add(scrollPane);

		JLabel lblPlayers = new JLabel("Players");
		lblPlayers.setForeground(Color.BLACK);
		lblPlayers.setBounds(10, 39, 55, 14);
		frame.getContentPane().add(lblPlayers);

		JPanel panel = new JPanel();
		panel.setBounds(92, 55, 382, 182);
		JTextArea textArea = new JTextArea(15, 30);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.GREEN);
		TextAreaOutputStream out = new TextAreaOutputStream(textArea, "Console");
		panel.setLayout(new BorderLayout());
		panel.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		System.setOut(new PrintStream(out));

		frame.getContentPane().add(panel);

		frame.setSize(500, 300);

		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		logger.info("Starting Salve GUI Mode...");
		try {
			new RS2Server().bind(43594).start();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error starting Salve GUI.", ex);
			System.exit(1);
		}
		World.getWorld();
	}

	public static void main(String[] args) {
		(new Thread(new GUI())).start();
	}

	@Override
	public void run() {
		while (true) {
			int index = list.getSelectedIndex();// try this
			if (World.getWorld().getPlayers().size() == 0 && listModel.size() != 0) {
				listModel.clear();
			}
			for (int i = 0; i < listModel.size(); i++) {
				if (!World.getWorld().isPlayerOnline(listModel.getElementAt(i))) {
					listModel.removeElement(listModel.getElementAt(i));
				}
			}
			World.getWorld().getPlayers().stream().filter(Objects::nonNull).filter(p -> p.isActive()).forEach(p -> {

				if (!listModel.contains(p.getName())) {
					listModel.addElement(p.getName());
				}
			});

			list.setModel(listModel);
			list.setSelectedIndex(index);
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
