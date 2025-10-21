package at.fhtw.restserver.server;

import at.fhtw.mrp.http.MediaHandler;
import at.fhtw.sampleapp.service.echo.EchoHandler;
import at.fhtw.sampleapp.service.weather.WeatherHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import at.fhtw.mrp.http.RegisterHandler;
import at.fhtw.mrp.http.LoginHandler;


public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 10);

        server.createContext("/api/users/register", new RegisterHandler());
        server.createContext("/api/users/login", new LoginHandler());
        server.createContext("/api/media", new MediaHandler());

        server.createContext("/echo", new EchoHandler());

        server.start();
    }
}
