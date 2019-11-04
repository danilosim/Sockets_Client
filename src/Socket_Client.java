import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Socket_Client {

    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private Scanner teclado = new Scanner(System.in);
    private String COMANDO_TERMINACION = "salir";

    //Inicia la conexion entre el Server y el Client
    private void levantarConexion(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            System.out.println("Conectado a :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            System.out.println("Excepción al levantar conexión: " + e.getMessage());
            System.exit(1);
        }
    }


    //Configuracion de Flujo de Datos
    private void abrirFlujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("Error en la apertura de flujos");
        }
    }

    //Envia los datos al Server
    private void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("IOException on enviar");
        }
    }

    //Termina la conexion existente
    private void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
            System.out.println("Conexión terminada");
        } catch (IOException e) {
            System.out.println("Error de IO: " + e.getMessage());
        } finally{
            System.exit(0);
        }
    }

    ///Thread que se encarga de recibir los datos continuamente
    private void ejecutarConexion(String ip, int puerto) {
        new Thread(() -> {
            try {
                levantarConexion(ip, puerto);
                abrirFlujos();
                recibirDatos();
            } finally {
                cerrarConexion();
            }
        }).start();
    }

    //Recibir Datos desde el servidor
    private void recibirDatos() {
        String st;
        try {
            do {
                st = bufferDeEntrada.readUTF();
                System.out.println(st);
                System.out.println("\nServidor => " + st);
                System.out.print("\nCliente => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException ignored) {
        }
    }

    //Lee datos ingresados por el teclado
    private void escribirDatos() {
        String entrada;
        while (true) {
            System.out.print("Cliente => ");
            entrada = teclado.nextLine();
            if(!entrada.isEmpty()){
                enviar(entrada);
            }
        }
    }

    public static void main(String[] argumentos) {
        Socket_Client cliente = new Socket_Client();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa la IP: (localhost por defecto) ");
        String ip = scanner.nextLine().length() <= 0 ? "localhost" : scanner.nextLine();

        System.out.println("Puerto: (5050 por defecto) ");
        String puerto = scanner.nextLine().length() <= 0 ? "5050" : scanner.nextLine();
        cliente.ejecutarConexion(ip, Integer.parseInt(puerto));
        cliente.escribirDatos();
    }
}