package net.den3.hackathonWeb.View;

import io.javalin.Javalin;

public class RegisterPage {
    public RegisterPage(Javalin javalin){
        javalin.get("/register",ctx->{
            ctx.render("/WEB-INF/templates/register.html");
        });
    }
}
