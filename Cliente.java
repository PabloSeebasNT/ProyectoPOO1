

import javax.swing.*;

import java.awt.event.*;
//import java.io.DataOutputStream;
import java.io.IOException;
// import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;

public class Cliente {

	public static void main(String[] args) {
		
		MarcoCliente mimarco=new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}


class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
		
		setBounds(600,300,280,350);
				
		LaminaMarcoCliente milamina = new LaminaMarcoCliente();
		
		add(milamina);
		
		setVisible(true);
	}	
	
}

class LaminaMarcoCliente extends JPanel implements Runnable {
	
	public LaminaMarcoCliente(){

		JLabel texto=new JLabel("                             - CHAT -                             ");
		add(texto);
		
		JLabel textoNick=new JLabel("Nick:");
		add(textoNick);
		
		nick = new JTextField(5);
		add(nick);
		
		JLabel textoIP=new JLabel("IP:");
		add(textoIP);

		ip = new JTextField(8);
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
			try{
				Socket misocket = new Socket("10.105.9.220",9999);
				
				paqueteDatos data = new paqueteDatos();
				data.setNick(nick.getText());
				data.setIp(ip.getText());

				String mensajeRSA = campo1.getText();


				
				data.setMensaje( mensajeRSA );

				ObjectOutputStream enviar_paquete = new ObjectOutputStream(misocket.getOutputStream());

				enviar_paquete.writeObject(data);
				//DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream());
				//flujo_salida.writeUTF(campo1.getText());
				//flujo_salida.close();
			} 
			catch(UnknownHostException e1) {
				e1.printStackTrace();
			} 
			catch(IOException e1) {
				System.out.println(e1.getMessage());
			}
			
			// Verificamos que funciona el boton:
			//System.out.println(campo1.getText());
		}
		
	}
		
		
		
	private JTextField campo1, nick, ip;
	private JTextArea campochat;
	private JButton miboton;

	@Override
	public void run() {
		try {
			ServerSocket servidor_cliente = new ServerSocket(9090);
			Socket cliente;
			paqueteDatos paquete_recibido;

			while (true) {
				cliente = servidor_cliente.accept();
				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
				paquete_recibido = (paqueteDatos) flujoentrada.readObject();

				campochat.append("\n" + paquete_recibido.getNick() + " : " + paquete_recibido.getMensaje());
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}

class paqueteDatos implements Serializable {
	private String nick, ip, mensaje;

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

	public void encriptarRSA() {
}