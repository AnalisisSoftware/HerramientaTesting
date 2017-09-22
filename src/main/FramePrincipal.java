package main;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class FramePrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane spMetodos;
	private JTextArea taCodigo;
	private JLabel lblMetodos;
	private JList<String> listaMetodos;
	private HashMap<String,String> codigoMetodos;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FramePrincipal frame = new FramePrincipal();
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
	public FramePrincipal() {
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if((e.getKeyCode() == KeyEvent.VK_E) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					abrirBuscadorCodigo();
				}
			}
		});
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					dispose();
				}
			}
		});
		listaMetodos = new JList<String>();
		listaMetodos.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				seleccionaDobleClickChat(arg0);
			}
		});
		codigoMetodos = new HashMap<String,String>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 784, 21);
		contentPane.add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmElegirCarpeta = new JMenuItem("Elegir Carpeta (Ctrl + E)");
		mntmElegirCarpeta.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				abrirBuscadorCodigo();
			}
		});
		mnFile.add(mntmElegirCarpeta);
		
		JMenuItem mntmSalir = new JMenuItem("Salir (Ctrl + S)");
		mntmSalir.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});
		mnFile.add(mntmSalir);
		
		spMetodos = new JScrollPane();
		spMetodos.setBounds(10, 48, 491, 119);
		contentPane.add(spMetodos);
		spMetodos.setViewportView(listaMetodos);
		
		lblMetodos = new JLabel("Metodos:");
		lblMetodos.setBounds(10, 23, 73, 14);
		contentPane.add(lblMetodos);
	    
		taCodigo = new JTextArea();
		taCodigo.setEditable(false);
		taCodigo.setLineWrap(true);
		JScrollPane scrollCodigo = new JScrollPane(taCodigo);
		scrollCodigo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollCodigo.setBounds(10, 196, 491, 254);
		
		contentPane.add(scrollCodigo);
	}
	
	private void abrirBuscadorCodigo() {
		JFileChooser selectorArchivos = new JFileChooser();
		selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int resultado = selectorArchivos.showOpenDialog(new JFrame());
		
		if(resultado == 0) {
			File archivo = selectorArchivos.getSelectedFile();
			DefaultListModel<String> lista = new DefaultListModel<String>();
			boolean flag = false;
			String codigo = "";
			try {
				Scanner lectura = new Scanner(archivo);
				while(lectura.hasNext()) {
					String linea = lectura.nextLine();
					Pattern pattern = Pattern.compile("(public|private|protected) [a-zA-Z|<|>]* [a-z][a-zA-Z].* ");
					Matcher matcher = pattern.matcher(linea);
					if (matcher.find()) {
					    lista.addElement(matcher.group(0));
					    codigoMetodos.put(matcher.group(0), "");
					    codigo = "{" + System.getProperty("line.separator");
					    flag = true;
					} else if(flag) {
						codigo += linea + System.getProperty("line.separator");
						codigo.replaceAll("\t", "    ");
						codigoMetodos.replace(lista.lastElement(), codigo);
					}
				}
				listaMetodos.setModel(lista);
				lectura.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}		
			lblMetodos.setText("Metodos: " + lista.size());
		}
	}
	
	private void seleccionaDobleClickChat(MouseEvent me) {
		if(me.getClickCount() == 2){
			taCodigo.setText(codigoMetodos.get(listaMetodos.getSelectedValue()));
		}
	}
}
