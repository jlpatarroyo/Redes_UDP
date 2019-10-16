package mundo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServidorUDPThread extends Thread
{
	private static final String PREPARADO = "Preparado";
	private static final String SALUDO = "Hola";
	private static final String NOMBRE_CLIENTE = "NombreCliente:";
	private static final String TAMANIO_BLOQUE = "TamanioBloque:4096";
	private static final int BLOQUE = 4096;
	private static final String TAMANIO_ARCHIVO = "TamanioArchivo:";
	private static final String CHECKSUM = "Checksum:";
	private static final String CORRECTO = "Correcto:";
	private static final String INCORRECTO = "Incorrecto:";

	

	private Socket socketCliente;
	private String nombreCliente;
	private Logger logger;
	private File archivo;
	private String rutaArchivo;

	public ServidorUDPThread(Socket socketCliente, String nombreCliente, String rutaArchivo)
	{
		this.rutaArchivo = rutaArchivo;
		this.socketCliente = socketCliente;
		this.nombreCliente = nombreCliente;
		this.logger = new Logger();
		archivo = new File(rutaArchivo);
		log("Se desplego el servidor para: " + nombreCliente);
	}

	private void log(String mensaje)
	{
		logger.log(nombreCliente + ": " + mensaje, ServidorUDP.RUTA_LOG_SERVIDOR);
	}

	@Override
	public void run() {
		try
		{
			PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			boolean corriendo = true;
			long tInicial = System.currentTimeMillis();
			long tFinal = 0;
			//Se envía la notificación de conexión exitosa
			out.println(NOMBRE_CLIENTE + nombreCliente);
			while(corriendo)
			{
				String data = in.readLine();
				if(data != null)
				{
					if(data.equals(PREPARADO))
					{
						log("El cliente se encuentra preparado");
						out.println(TAMANIO_BLOQUE);
						log("El tamanio del bloque es: " + BLOQUE);
						int tamanioArchivo = (int) archivo.length();
						out.println(TAMANIO_ARCHIVO + tamanioArchivo);
						enviarArchivo(archivo);
						sleep(1000);
						out.println(CHECKSUM + checkSum(rutaArchivo));
					}
					if(data.contains(CORRECTO))
					{
						tFinal = Long.parseLong(data.split(":")[1]);
						double deltaT = (tFinal - tInicial)/1000;
						log("El archivo fue descargado correctamente");
						log("Tiempo de transferencia= " + deltaT);
						corriendo = false;
					}
					if(data.contains(INCORRECTO))
					{
						tFinal = Long.parseLong(data.split(":")[1]);
						double deltaT = (tFinal - tInicial)/1000;
						log("El archivo fue descargado incorrectamente");
						log("Tiempo de transferencia= " + deltaT + "s");
						corriendo = false;
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

	private synchronized long enviarArchivo(File archivo) throws IOException
	{
		long tInicial = System.currentTimeMillis();
		DataOutputStream dos = new DataOutputStream(socketCliente.getOutputStream());
		FileInputStream fis = new FileInputStream(archivo);
		byte[] buffer = new byte[BLOQUE];

		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		fis.close();
		return tInicial;
		//dos.close();	
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
