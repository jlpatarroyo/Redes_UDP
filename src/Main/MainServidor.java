package Main;

import mundo.ServidorUDP;

public class MainServidor {
	
	public static void main(String[] args)
	{
		int puerto = Integer.parseInt(args[0]);
		String ip = args[1];
		int numConexiones = Integer.parseInt(args[2]);
		
		ServidorUDP servidor = new ServidorUDP(numConexiones, puerto, ip);
	}
}
