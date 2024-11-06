package com.aps;

public class Usuario {
    private int id;
    private String email;
    private int permissao;

    public Usuario(int id, String email, int permissao) {
        this.id = id;
        this.email = email;
        this.permissao = permissao;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getPermissao() {
        return permissao;
    }
}
