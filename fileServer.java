package net.incognitas;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class fileServer
{
	private static HttpServer server;
	private static int port = 80;
	private static String fileSystemPath = "files";
	public static void main(String args[])
	{
		if(args.length > 0) port = Integer.parseInt(args[0]);
		if(args.length > 1) fileSystemPath = args[1];
		System.out.println("tips:U can specify port number and base folder in args");
		System.out.println("file server is running on localhost:" + port + " and based on " + fileSystemPath);
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
						File f = new File(fileSystemPath + path);
						String ext = path.substring(path.lastIndexOf(".") + 1);
						if(f.isDirectory())
						{
							Headers header = exchange.getResponseHeaders();
							header.add("Content-Type","text/plain; charset=UTF-8");
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
							Headers header = exchange.getResponseHeaders();
							String type = "text/plain; charset=UTF-8";
							if(ext.equals("html") || ext.equals("htm")) type = "text/html; charset=UTF-8";
							else if(ext.equals("js")) type = "text/javascript; charset=UTF-8";
							else if(ext.equals("css")) type = "text/css; charset=UTF-8";
							else if(ext.equals("svg")) type = "image/svg+xml; charset=UTF-8";
							else if(ext.equals("png")) type = "image/png;";
							else if(ext.equals("jpg")) type = "image/jpg";
							else if(ext.equals("gif")) type = "image/gif";
							else if(ext.equals("json")) type = "text/json; charset=UTF-8";
							header.add("Content-Type", type);
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