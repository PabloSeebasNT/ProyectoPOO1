

import javax.swing.*;

import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
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

				// ENcriptar mensaje
				mensajeRSA = data.encriptarRSA( mensajeRSA );
				
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

				// Desencriptar mensaje
				String mensajeDesencriptado = paquete_recibido.desencriptarRSA( paquete_recibido.getMensaje() );
				paquete_recibido.setMensaje( mensajeDesencriptado );

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

	public String encriptarRSA( String mensaje ) {
		String ps = "16756117517205980867544763324295280310411274310459993152675868924963927551767548732547665939122992963486078559045320376243777761769635640051615327370284800614629165831136938138649123738739163466462814508692591334820331134509380435671589107528848240242101871963834202221933337962773920481111578588518762083073843657474815679077563658680309267560887584408871652886995653901349092939037407613429521869060777860657128168053723049936289247521608861993642256744463926478172455601206618416084937019014793486305691536381234880028356769605001235316867557154268777927169683322609252877686071052633196915224365052772868764017049";
		String qs = "28184725913601841951096447805694189105228105329731657558208404011468569722679730042794368534251814190382574595303785255409100780924226875215722620348109560064643520452727203991172637715824388680221219323073741664405943276362549987504032892216196683736364932230569111126613957747249402654769903376499591320284342125861159015398326667711252522953066909942192041286516447072572982893481460180670619651738651992479722628709785491965942299628475415879729576887933162635229054172149312109708365066275947107598663496851767049709385526242824422980699860041732680247401286497438469815102444079930738734634832997858998372577613";

		BigInteger p = new BigInteger( ps );
		BigInteger q = new BigInteger( qs );

		// Calcular n = p * q
        BigInteger n = p.multiply(q);

		// Clave pública
        BigInteger e = BigInteger.valueOf(65537);

		// Convertir el mensaje a un número (BigInteger)
        BigInteger mensajeNumero = new BigInteger( mensaje.getBytes() );

        // Encriptación del mensaje
        BigInteger mensajeEncriptado = mensajeNumero.modPow(e, n);

		return mensajeEncriptado.toString();
	}

	public String desencriptarRSA( String mensaje ) {
		String ps = "16756117517205980867544763324295280310411274310459993152675868924963927551767548732547665939122992963486078559045320376243777761769635640051615327370284800614629165831136938138649123738739163466462814508692591334820331134509380435671589107528848240242101871963834202221933337962773920481111578588518762083073843657474815679077563658680309267560887584408871652886995653901349092939037407613429521869060777860657128168053723049936289247521608861993642256744463926478172455601206618416084937019014793486305691536381234880028356769605001235316867557154268777927169683322609252877686071052633196915224365052772868764017049";
		String qs = "28184725913601841951096447805694189105228105329731657558208404011468569722679730042794368534251814190382574595303785255409100780924226875215722620348109560064643520452727203991172637715824388680221219323073741664405943276362549987504032892216196683736364932230569111126613957747249402654769903376499591320284342125861159015398326667711252522953066909942192041286516447072572982893481460180670619651738651992479722628709785491965942299628475415879729576887933162635229054172149312109708365066275947107598663496851767049709385526242824422980699860041732680247401286497438469815102444079930738734634832997858998372577613";

		BigInteger p = new BigInteger( ps );
		BigInteger q = new BigInteger( qs );
		BigInteger mensajeEncriptado = new BigInteger( mensaje );

		// Calcular n = p * q
        BigInteger n = p.multiply(q);

		// Clave pública
        BigInteger e = BigInteger.valueOf(65537);
		BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

		// Calcular clave privada d usando el algoritmo extendido de Euclides
        BigInteger d = e.modInverse(phiN);

        // Desencriptación del mensaje
        BigInteger mensajeDesencriptado = mensajeEncriptado.modPow(d, n);

		return mensajeDesencriptado.toString();
	}
}