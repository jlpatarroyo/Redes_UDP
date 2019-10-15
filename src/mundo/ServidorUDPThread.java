package mundo;

import java.net.Socket;

public class ServidorUDPThread extends Thread
{
	private Socket socketCliente;
	
	public ServidorUDPThread(Socket socketCliente)
	{
		this.socketCliente = socketCliente;
	}
}
