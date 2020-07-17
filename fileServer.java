package net.incognitas;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class fileServer
{
	private static HttpServer server;
	private static int port = 80;
	private static final String fileSystemPath = "files";
	public static void main(String args[])
	{
		if(args.length > 0) port = Integer.parseInt(args[0]);
		System.out.println("tips:U can add a port number in args");
		System.out.println("file server running on localhost:" + port);
		try
		{
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.setExecutor(null);
			server.createContext("/",exchange ->
			{
				URI uri = exchange.getRequestURI();
				String path = uri.getPath();
				try
				{
					OutputStream os = exchange.getResponseBody();
					byte[] buffer = new byte[1024];
					System.out.println(path);
					InputStream is = fileServer.class.getResourceAsStream("/" + fileSystemPath + path);
					if(is == null)
					{
						Headers header = exchange.getResponseHeaders();
						header.add("Content-Type","text/plain; charset=UTF-8");
						File f = new File(fileSystemPath + path);
						if(f.isDirectory())
						{
							exchange.sendResponseHeaders(200,0);
							String[] filenames = f.list();
							for(String filename : filenames)
							{
								os.write((filename + "\n").getBytes("UTF-8"));
							}
							os.close();
							return;
						}
						else
						{
							is = new FileInputStream(f);
						}
					}
					/*else
					{
						Headers header = exchange.getResponseHeaders();
						header.add("Content-Type","text/plain; charset=big5");
					}*/
					exchange.sendResponseHeaders(200,0);
					int hasRead;
					while((hasRead = is.read(buffer)) > 0)
					{
						os.write(buffer,0,hasRead);
					}
					is.close();
					os.close();
				}
				catch(Exception e)
				{
					exchange.sendResponseHeaders(404,0);
				}
				finally
				{
					exchange.close();
				}
			});
			server.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}