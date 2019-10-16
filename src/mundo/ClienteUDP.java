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
	private static final String NOMBRE_CLIENTE = "NombreCliente:";
	
	private Socket socketCliente;
	private Logger logger;
	private String nombreCliente;

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
			log("No fue posible establecer la conexion con el servidor");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			log("Error al crear el socket de conexión");
			e.printStackTrace();
		}
	}
	
	private void log(String mensaje)
	{
		logger.log(mensaje, RUTA_LOG_CLIENTE);
	}

	@Override
	public void run()
	{
		try
		{
			PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			boolean corriendo = true;
			while(corriendo)
			{
				String data = in.readLine();
				if(data != null)
				{
					if(data.contains(NOMBRE_CLIENTE))
					{
						nombreCliente = data.split(":")[1];
						log("Estado de la conexión exitoso\n");
						log("Nombre asignado por el servidor: " + nombreCliente);
					}
				}
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

}
