package mundo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServidorUDPThread extends Thread
{
	private static final String SALUDO = "Hola";
	private static final String RUTA_LOG_SERVIDOR = "./data/logs/servidor/log_servidor.txt";
	private static final String NOMBRE_CLIENTE = "NombreCliente:";

	private Socket socketCliente;
	private String nombreCliente;
	private Logger logger;

	public ServidorUDPThread(Socket socketCliente, String nombreCliente)
	{
		this.socketCliente = socketCliente;
		this.nombreCliente = nombreCliente;
		this.logger = new Logger();
		log("Se desplego el servidor para: " + nombreCliente);
	}

	private void log(String mensaje)
	{
		logger.log(nombreCliente + ": " + mensaje, RUTA_LOG_SERVIDOR);
	}

	@Override
	public void run() {
		System.out.println("runneo");
		try
		{
			PrintWriter out = new PrintWriter(socketCliente.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			boolean corriendo = true;
			long tInicial = 0;
			long tFinal = 0;
			//Se envía la notificación de conexión exitosa
			out.println(NOMBRE_CLIENTE + nombreCliente);
			while(corriendo)
			{
				String data = in.readLine();
				if(data != null)
				{
					if(data.equals(SALUDO))
					{
						log("Se recibio el saludo del cliente");
						out.println(NOMBRE_CLIENTE + nombreCliente);
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
}
