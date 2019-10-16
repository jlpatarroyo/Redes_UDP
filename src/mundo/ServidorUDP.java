package mundo;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServidorUDP
{
	public static final String RUTA_ARCHIVO_PEQUEÑO = "C:\\Users\\viejo\\Documents\\Universidad\\Redes\\RepoArchivos\\SampleText.txt";
	public static final String RUTA_ARCHIVO_GRANDE = "C:\\Users\\viejo\\Documents\\Universidad\\Redes\\RepoArchivos\\100MB_Sample.txt";
	public static final String RUTA_VIDEO = "C:\\Users\\viejo\\Documents\\Videos\\SUTRA\\Sebastían Yatra - SUTRA ft. Dalmata JIIRO (Cover).mp4";

	private boolean corriendo;
	private int numeroConexiones;
	private Logger logger;
	private ServerSocket socketServidor;
	private int contadorConexiones;
	private ServidorUDPThread[] threads;
	private TipoArchivo tipoArchivo;
	
	private static final String GRANDE = "g";
	private static final String PEQUEÑO = "p";
	private static final String VIDEO = "v";

	public enum TipoArchivo
	{
		PEQUENIO,
		GRANDE,
		VIDEO
	}

	private static final int NUM_MAX_CONEXIONES = 25;
	public static final String RUTA_LOG_SERVIDOR = "./data/logs/servidor/log_servidor.txt";


	public ServidorUDP(int numeroConexiones, int puerto, String direccionIP, String tipoArchivo)
	{
		if(tipoArchivo.equals(GRANDE))
		{
			this.tipoArchivo = TipoArchivo.GRANDE;
		}
		else if(tipoArchivo.equals(VIDEO))
		{
			this.tipoArchivo = TipoArchivo.VIDEO;
		}
		else
		{
			this.tipoArchivo = TipoArchivo.PEQUENIO;
		}
		
		logger = new Logger();
		logger.log("\n-------------------------------------------------------", RUTA_LOG_SERVIDOR);
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
			logger.log("La direccion IP es incorrecta", RUTA_LOG_SERVIDOR);
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
				//Se esperan las n conexiones
				if(contadorConexiones < numeroConexiones)
				{
					System.out.println("INFO: Esperando conexion de cliente...");
					Socket socketCliente = socketServidor.accept();
					String nombreCliente = "Cliente " + (contadorConexiones+1);
					if(tipoArchivo == TipoArchivo.GRANDE)
					{
						ServidorUDPThread thread = new ServidorUDPThread(socketCliente, nombreCliente, RUTA_ARCHIVO_GRANDE);
						threads[contadorConexiones] = thread;
						contadorConexiones++;
					}
					else if(tipoArchivo == TipoArchivo.VIDEO)
					{
						ServidorUDPThread thread = new ServidorUDPThread(socketCliente, nombreCliente, RUTA_VIDEO);
						threads[contadorConexiones] = thread;
						contadorConexiones++;
					}
					else
					{
						ServidorUDPThread thread = new ServidorUDPThread(socketCliente, nombreCliente, RUTA_ARCHIVO_PEQUEÑO);
						threads[contadorConexiones] = thread;
						contadorConexiones++;
					}
					//					logger.log("Se agregó una conexión a la espera", RUTA_LOG_SERVIDOR);
					//					logger.log("Esperando " + (numeroConexiones-contadorConexiones) + "+ conexiones", RUTA_LOG_SERVIDOR);
				}
				//ya se llenaron las conexiones
				else
				{
					for (int i = 0; i < threads.length; i++) {
						synchronized (this) {
							if(threads[i].getState() == State.NEW)
							{
								threads[i].start();
							}
						}
					}
				}

			}
		}
		catch (Exception e) 
		{
			System.out.println("ERROR: No se pudo crear el servidor");
			e.printStackTrace();
		}
	}
}
