package Pruebas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.Socket;

public class Cliente {
    
    public static void main(String[] args) {
        
        try {
            Socket cliente = new Socket("localhost", 4444);

            BufferedReader in = new BufferedReader( new InputStreamReader( cliente.getInputStream() ) );
            DataOutputStream out = new DataOutputStream( cliente.getOutputStream() );

            BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

            // Hilo para recibir mensajes del servidor
            Thread recibirHilo = new Thread(() -> {
                try {
                    while (true) {
                        String mensajeRecibido = in.readLine();
                        System.out.println("Mensaje recibido: " + mensajeRecibido);
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recibirHilo.start();

            // Hilo para enviar mensajes al servidor
            Thread enviarHilo = new Thread(() -> {
                try {
                    while (true) {
                        String mensaje = br.readLine();
                        out.writeUTF(mensaje);
                        System.out.println("Mensaje enviado: " + mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            enviarHilo.start();

            //cliente.close();

        } 
        catch ( IOException e) {
            System.out.println( e.getMessage() );
        }
    }
}
