package com.jakuza.annotation;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {
    private static Map<String, Method> routes = new HashMap<String, Method>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        for(Method m: Routes.class.getMethods()) {
            if(m.isAnnotationPresent(WebRoute.class)) {
                WebRoute webRouteAnnotation = m.getAnnotation(WebRoute.class);
                routes.put(webRouteAnnotation.value(),m);
                server.createContext(webRouteAnnotation.value(), new MyHandler());
            }
        }
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI requestURI = t.getRequestURI();
            String response = "";
            Class routeClass = Routes.class;
            Set<Map.Entry<String, Method>> entrySet = routes.entrySet();
            for (Map.Entry<String, Method> entry: entrySet) {
                if(requestURI.toString().equals(entry.getKey())){
                    try {
                        response = (String) entry.getValue().invoke(new Routes(),null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}