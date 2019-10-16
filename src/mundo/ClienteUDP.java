package mundo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClienteUDP extends Thread
{
	private static final String PREPARADO = "Preparado";
	private static final String NOMBRE_CLIENTE = "NombreCliente:";
	private static final String TAMANIO_BLOQUE = "TamanioBloque";
	private static final String TAMANIO_ARCHIVO = "TamanioArchivo:";
	private static final String CHECKSUM = "Checksum:";
	private static final String CORRECTO = "Correcto:";
	private static final String INCORRECTO = "Incorrecto:";
	private static final long TIEMPO_MAXIMO_DESCARGA = 20000;

	private Socket socketCliente;
	private Logger logger;
	private String nombreCliente;
	private int tamanioBloque;
	private int tamanioArchivo;
	private String miRutaFinal;

	private static final String RUTA_LOG_CLIENTE = "./data/logs/cliente/log_cliente.txt";
	private static final String RUTA_FINAL_ARCHIVO = "C:\\Users\\viejo\\Documents\\Universidad\\Redes\\RepoArchivos\\Descargado";

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
			log("Error al crear el socket de conexion");
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
			long tFinal = 0;
			while(corriendo)
			{
				String data = in.readLine();
				if(data != null)
				{
					if(data.contains(NOMBRE_CLIENTE))
					{
						nombreCliente = data.split(":")[1];
						miRutaFinal = RUTA_FINAL_ARCHIVO + "_" + nombreCliente + ".txt";
						log("Estado de la conexión exitoso");
						log("Nombre asignado por el servidor: " + nombreCliente);
						log("La ruta final del archivo es = " + miRutaFinal);
						out.println(PREPARADO);
					}
					if(data.contains(TAMANIO_BLOQUE))
					{
						tamanioBloque = Integer.parseInt(data.split(":")[1]);
					}
					if(data.contains(TAMANIO_ARCHIVO))
					{
						tamanioArchivo = Integer.parseInt(data.split(":")[1]);
						tFinal = descargarArchivo(socketCliente, tamanioArchivo);
					}
					if(data.contains(CHECKSUM))
					{
						String miChecksum = checkSum(miRutaFinal);
						String elChecksum = data.split(":")[1];
						if(miChecksum.equals(elChecksum))
						{
							out.println(CORRECTO + tFinal);
						}
						else
						{
							out.println(INCORRECTO + tFinal);
						}
						corriendo = false;
					}
				}
			}

		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

	private synchronized long descargarArchivo(Socket socketCliente, int tamanioArchivo) throws IOException 
	{
		System.out.println("INFO: Descargando Archivo..." );
		DataInputStream dis = new DataInputStream(socketCliente.getInputStream());
		FileOutputStream fos = new FileOutputStream(miRutaFinal);
		byte[] buffer = new byte[tamanioBloque];

		//		int filesize = 3307868; // Send file size in separate msg
		int leer = 0;
		int totalLeido = 0;
		int remaining = tamanioArchivo;
		long cronoInicio = System.currentTimeMillis();
		boolean error = false;
		while((leer = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0 && !error) {
			//System.out.println(nombreCliente);
			long cronoActual = System.currentTimeMillis();
			totalLeido += leer;
			remaining -= leer;
			//System.out.println("read " + totalLeido + " bytes.");
			fos.write(buffer, 0, leer);
			if(cronoActual-cronoInicio > TIEMPO_MAXIMO_DESCARGA)
			{
				error = true;
			}
		}
		if(!error)
		{
			long tFinal = System.currentTimeMillis();
			fos.close();
			return tFinal;
		}
		else
		{
			fos.close();
			return -1;		
		}
		//dis.close();
	}

	private synchronized String checkSum(String path)
	{
		String checksum = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			MessageDigest md = MessageDigest.getInstance("MD5");

			//Using MessageDigest update() method to provide input
			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			while( (numOfBytesRead = fis.read(buffer)) > 0){
				//System.out.println(nombreCliente);
				md.update(buffer, 0, numOfBytesRead);
			}
			byte[] hash = md.digest();
			checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}

		return checksum;
	}

}
