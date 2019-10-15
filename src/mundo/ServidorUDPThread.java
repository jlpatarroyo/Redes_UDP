package mundo;

import java.net.Socket;

public class ServidorUDPThread extends Thread
{
	private Socket socketCliente;
	private String nombreCliente;
	
	public ServidorUDPThread(Socket socketCliente, String nombreCliente)
	{
		this.socketCliente = socketCliente;
		this.nombreCliente = nombreCliente;
	}
	
	@Override
	public void run() {
		
	}
}
