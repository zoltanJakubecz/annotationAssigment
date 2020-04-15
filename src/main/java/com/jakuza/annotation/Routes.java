package com.jakuza.annotation;

public class Routes {

    @WebRoute("/")
    public String home(){
        return "Home route";
    }
    @WebRoute("/test")
    public String test(){
        return "Test route";
    }

}
