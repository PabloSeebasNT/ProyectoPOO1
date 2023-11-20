package Pruebas;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    
    public static void main(String[] args) throws IOException {
        
        final int PORT = 4444;

        ServerSocket serverSocket;

        Socket cliente1;
        Socket cliente2;

        try {

            serverSocket = new ServerSocket( PORT );
            System.out.println("Servidor iniciado, esperando conexiones...");

            cliente1 = serverSocket.accept();
            System.out.println("Cliente 1 conectado");

            cliente2 = serverSocket.accept();
            System.out.println("Cliente 2 conectado");

            // Entradas:
            BufferedReader inCliente1 = new BufferedReader( new InputStreamReader( cliente1.getInputStream() ) );
            BufferedReader inCliente2 = new BufferedReader( new InputStreamReader( cliente2.getInputStream() ) );

            // Salidas:
            DataOutputStream outCliente1 = new DataOutputStream( cliente1.getOutputStream() );
            outCliente1.writeUTF("Conectado con el cliente 1");

            DataOutputStream outCliente2 = new DataOutputStream( cliente2.getOutputStream() );
            outCliente2.writeUTF("Conectado con el cliente 2");

             // Aquí se realiza la comunicación entre los clientes
             Thread cl1 = new Thread(() -> {

                try {

                    String mensaje;

                    while ( ( mensaje = inCliente1.readLine() ) != null ) {
                        System.out.println("Cliente 1: " + mensaje);
                        outCliente2.writeUTF("Cliente 1: " + mensaje);
                    }

                } 
                catch (IOException e) {
                    System.out.println( e.getMessage() );
                }

            });
            
            cl1.start();
            
            Thread cl2 = new Thread(() -> {

                try {

                    String mensaje;

                    while ( ( mensaje = inCliente2.readLine() ) != null ) {
                        System.out.println("Cliente 2: " + mensaje);
                        outCliente1.writeUTF("Cliente 2: " + mensaje);
                    }

                } 
                catch (IOException e) {
                    e.printStackTrace();
                }

            });
            cl2.start();

        } 
        catch ( IOException e) {
            System.out.println( e.getMessage() );
        }
        
    }

}
