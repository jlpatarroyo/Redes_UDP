package Main;

import mundo.ServidorUDP;
import mundo.ServidorUDP.TipoArchivo;

public class MainServidor {
	
	public static void main(String[] args)
	{
		int puerto = Integer.parseInt(args[0]);
		String ip = args[1];
		int numConexiones = Integer.parseInt(args[2]);
		String tipoArchivo = args[3];

		
		ServidorUDP servidor = new ServidorUDP(numConexiones, puerto, ip, tipoArchivo);
	}
}
