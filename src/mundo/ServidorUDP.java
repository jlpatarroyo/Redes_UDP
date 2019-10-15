package mundo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServidorUDP
{
	private boolean corriendo;
	private int numeroConexiones;
	private Logger logger;
	private ServerSocket socketServidor;
	private int contadorConexiones;
	private ServidorUDPThread[] threads;

	private static final int NUM_MAX_CONEXIONES = 25;
	private static final String RUTA_LOG_SERVIDOR = "./data/logs/servidor/log_servidor.txt";

	public ServidorUDP(int numeroConexiones, int puerto, String direccionIP)
	{
		logger = new Logger();
		contadorConexiones = 0;
		if(numeroConexiones > NUM_MAX_CONEXIONES)
		{
			logger.log("El numero de conexiones solicitadas excede la capacidad", RUTA_LOG_SERVIDOR);
			logger.log("Se ha comenzado el servicio para " + NUM_MAX_CONEXIONES + " conexiones", RUTA_LOG_SERVIDOR);
			this.numeroConexiones = NUM_MAX_CONEXIONES;
		}
		else
		{
			this.numeroConexiones = numeroConexiones;
			logger.log("Se comenzara el servicio para " + this.numeroConexiones + " conexiones", RUTA_LOG_SERVIDOR);
		}

		//Se crea el ServerSocket
		try 
		{
			socketServidor = new ServerSocket(puerto, 1, InetAddress.getByName(direccionIP));
			threads = new ServidorUDPThread[this.numeroConexiones];
			escuchar();
		} 
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			logger.log("La dirección IP es incorrecta", RUTA_LOG_SERVIDOR);
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			logger.log("Error al crear el Socket de Servidor", RUTA_LOG_SERVIDOR);
			e.printStackTrace();
		}
	}

	public void escuchar()
	{
		corriendo = true;
		try
		{
			contadorConexiones = 0;
			while(corriendo)
			{
				System.out.println("INFO: Esperando conexion de cliente...");
				//Se esperan las n conexiones
				if(contadorConexiones < numeroConexiones)
				{
					Socket socketCliente = socketServidor.accept();
					contadorConexiones++;
					ServidorUDPThread thread = new ServidorUDPThread(socketCliente);
					threads[contadorConexiones] = thread;
					logger.log("Se agregó una conexión a la espera", RUTA_LOG_SERVIDOR);
					logger.log("Esperando " + (numeroConexiones-contadorConexiones) + "+ conexiones", RUTA_LOG_SERVIDOR);
				}
				//ya se llenaron las conexiones
				else
				{
					for (int i = 0; i < threads.length; i++) {
						threads[i].start();
						logger.log("Se desplegó el servidor #" + i, RUTA_LOG_SERVIDOR);
					}
				}
				
			}
		}
		catch (Exception e) 
		{
			System.out.println("ERROR: No se pudo crear el servidor");
		}
	}
}
