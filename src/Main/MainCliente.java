package Main;

import mundo.ClienteUDP;

public class MainCliente{
	
	public static void main(String[] args)
	{
		int puerto = Integer.parseInt(args[0]);
		String ip = args[1];
		int numConexiones = Integer.parseInt(args[2]);
		
		for(int i=0; i < numConexiones; i++)
		{
			ClienteUDP cliente = new ClienteUDP(puerto, ip);
			cliente.start();
		}
	}

}
