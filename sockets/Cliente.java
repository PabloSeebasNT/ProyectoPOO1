import javax.swing.*;

import java.awt.Window;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;

public class Cliente {

	public static void main(String[] args) {
		
		MarcoCliente mimarco=new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}


class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,280,350);
				
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		
		add(milamina);
		
		setVisible(true);

		addWindowListener(new EnvioIp());
		}	
	
}

class EnvioIp extends WindowAdapter{
	public void windowOpened(WindowEvent e){
		try {
			Socket otroSocket = new Socket("192.168.0.241", 9999);
			paqueteDatos data = new paqueteDatos();
			data.setMensaje(" online");
			ObjectOutputStream paquete_enviar = new ObjectOutputStream(otroSocket.getOutputStream());
			paquete_enviar.writeObject(data);
			otroSocket.close();

		} catch (Exception e2) {
		}
	}
}
class LaminaMarcoCliente extends JPanel implements Runnable{
	
	public LaminaMarcoCliente(){
		String nick_usuario = JOptionPane.showInputDialog("Nick: ");
		JLabel n_nick = new JLabel("Nick: ");
		add(n_nick);
		nick = new JLabel();
		nick.setText(nick_usuario);
		add(nick);
		JLabel texto=new JLabel(" Online: ");
		
		add(texto);
		ip = new JComboBox();

		add(ip);
		campochat = new JTextArea(12,20);

		add(campochat);
	
		campo1=new JTextField(20);
	
		add(campo1);		
	
		miboton=new JButton("Enviar");

		EnviarTexto mievento = new EnviarTexto();
		miboton.addActionListener(mievento);
		
		add(miboton);
		
		Thread miHilo = new Thread(this);
		miHilo.start();
		
	}
	
	
	private class EnviarTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			// Aqui construiremos el socket
			campochat.append("\nTú: "+ campo1.getText());
			try{
				Socket misocket = new Socket("192.168.0.241",9999); //host = ip del servidor
				
				paqueteDatos data = new paqueteDatos();
				data.setNick(nick.getText());
				data.setIp(ip.getSelectedItem().toString());
				data.setMensaje(campo1.getText());

				ObjectOutputStream enviar_paquete = new ObjectOutputStream(misocket.getOutputStream());

				enviar_paquete.writeObject(data);
				campo1.setText("");
				//DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream());
				//flujo_salida.writeUTF(campo1.getText());
				//flujo_salida.close();
			} catch(UnknownHostException e1){
				e1.printStackTrace();
			} catch(IOException e1){
				System.out.println(e1.getMessage());
			}
			
			// Verificamos que funciona el boton:
			//System.out.println(campo1.getText());
		}
		
	}
		
		
		
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	private JTextArea campochat;
	private JButton miboton;
	@Override
	public void run() {
		try{

			ServerSocket servidor_cliente = new ServerSocket(9090);
			Socket cliente;
			paqueteDatos paquete_recibido;

			while (true) {
				cliente = servidor_cliente.accept();
				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
				paquete_recibido = (paqueteDatos) flujoentrada.readObject();

				if (!paquete_recibido.getMensaje().equals(" online")) {
					campochat.append("\n" + paquete_recibido.getNick() + " : " + paquete_recibido.getMensaje());
				}else{
					ArrayList <String> ipLista = new ArrayList<String>();
					ipLista = paquete_recibido.getIps();
					ip.removeAllItems();
					for(String i:ipLista){
						ip.addItem(i);
					}
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}

class paqueteDatos implements Serializable{
	private String nick, ip, mensaje;

	private ArrayList <String> Ips;
	

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public ArrayList<String> getIps() {
		return Ips;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}