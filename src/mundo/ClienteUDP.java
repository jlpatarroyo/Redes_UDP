package mundo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteUDP extends Thread
{
	private static final String PREPARADO = "Preparado";
	private static final String NOMBRE_CLIENTE = "NombreCliente:";
	private static final String TAMANIO_BLOQUE = "TamanioBloque";
	private static final String TAMANIO_ARCHIVO = "TamanioArchivo:";
	
	private Socket socketCliente;
	private Logger logger;
	private String nombreCliente;
	private int tamanioBloque;
	private int tamanioArchivo;

	private static final String RUTA_LOG_CLIENTE = "./data/logs/cliente/log_cliente.txt";
	private static final String RUTA_FINAL_ARCHIVO = "./data/archivos/sample_descargado";

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
						out.println(PREPARADO);
					}
					if(data.contains(TAMANIO_BLOQUE))
					{
						tamanioBloque = Integer.parseInt(data.split(":")[1]);
					}
					if(data.contains(TAMANIO_ARCHIVO))
					{
						tamanioArchivo = Integer.parseInt(data.split(":")[1]);
						long tFinal = descargarArchivo(socketCliente, tamanioArchivo);
					}
				}
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private long descargarArchivo(Socket socketCliente, int tamanioArchivo) throws IOException 
	{
		System.out.println("INFO: Descargando Archivo..." );
		DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
		FileOutputStream fos = new FileOutputStream(RUTA_FINAL_ARCHIVO +"_" +nombreCliente + ".txt");
		byte[] buffer = new byte[4096];

		//		int filesize = 3307868; // Send file size in separate msg
		int leer = 0;
		int totalLeido = 0;
		int remaining = tamanioArchivo;
		while((leer = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalLeido += leer;
			remaining -= leer;
			//System.out.println("read " + totalLeido + " bytes.");
			fos.write(buffer, 0, leer);
		}
		long tFinal = System.currentTimeMillis();
		fos.close();
		return tFinal;
		//dis.close();
	}

}
