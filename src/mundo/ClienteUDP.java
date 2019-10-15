package mundo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteUDP extends Thread
{
	private static final String SALUDO = "Hola";
	
	
	private Socket socketCliente;
	private Logger logger;

	private static final String RUTA_LOG_CLIENTE = "./data/logs/cliente/log_cliente.txt";

	public ClienteUDP(int puerto, String ip)
	{
		logger = new Logger();
		try 
		{
			socketCliente = new Socket(InetAddress.getByName(ip), puerto);
		} 
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			logger.log("No fue posible establecer la conexión con el servidor", RUTA_LOG_CLIENTE);
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			logger.log("Error al crear el socket de conexión", RUTA_LOG_CLIENTE);
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			out.println(SALUDO);
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

}
