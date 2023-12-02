

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Servidor  {

	public static void main(String[] args) {
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}

class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		Thread mihilo = new Thread(this);
		mihilo.start();
		}
	
	private	JTextArea areatexto;

	@Override
	public void run() {
		//System.out.println("Listening...");

		try {
			ServerSocket servidor = new ServerSocket(9999);

			String nick, ip, mensaje;
			
			paqueteDatos paquete_recibido;
			ArrayList <String> listaIp = new ArrayList<String>();

			while (true) {
				
				Socket misocket = servidor.accept();

				ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());

				paquete_recibido = (paqueteDatos) paquete_datos.readObject();

				nick = paquete_recibido.getNick();

				ip = paquete_recibido.getIp();

				mensaje = paquete_recibido.getMensaje();

				if (!mensaje.equals(" online")) {
				
					areatexto.append("\n" + nick + " : " + mensaje + " Para: " + ip);

					Socket enviarDataDestinatario = new Socket(ip,  9090);
					ObjectOutputStream paquete_reenviar = new ObjectOutputStream(enviarDataDestinatario.getOutputStream());
					
					paquete_reenviar.writeObject(paquete_recibido);
					paquete_reenviar.close();
					enviarDataDestinatario.close();
					misocket.close();
				}else{
					InetAddress getIp = misocket.getInetAddress();
					String IpRemota = getIp.getHostAddress();
					listaIp.add(IpRemota);
					paquete_recibido.setIps(listaIp);
					for(String z:listaIp){
						Socket enviarDataDestinatario = new Socket(z,  9090);
						ObjectOutputStream paquete_reenviar = new ObjectOutputStream(enviarDataDestinatario.getOutputStream());
						
						paquete_reenviar.writeObject(paquete_recibido);
						paquete_reenviar.close();
						enviarDataDestinatario.close();
						misocket.close();
					}
				}
				
			}
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
