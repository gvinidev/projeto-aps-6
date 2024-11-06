package com.aps;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BancoDados {

    private static final String URL = "jdbc:h2:~/faculdadeApp";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // Inicializa o banco de dados e cria a tabela Usuario
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String sqlCreateTable = """
                CREATE TABLE IF NOT EXISTS Usuario (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    senha VARCHAR(255) NOT NULL,
                    permissao INT DEFAULT 1, -- 1 para padrão
                    diretorioImagem VARCHAR(255)
                );
            """;

            stmt.execute(sqlCreateTable);
            System.out.println("Tabela Usuario criada ou já existente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void adicionarUsuario(String email, String senha, int permissao, String diretorioImagem) {
        String sqlInsert = "INSERT INTO Usuario (email, senha, permissao, diretorioImagem) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);
            pstmt.setInt(3, permissao);
            pstmt.setString(4, diretorioImagem);
            pstmt.executeUpdate();

            System.out.println("Usuário cadastrado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void atualizarDiretorioImagem(String email, String diretorioImagem) {
        String sqlUpdate = "UPDATE Usuario SET diretorioImagem = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setString(1, diretorioImagem);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

            System.out.println("Diretório da imagem atualizado com sucesso para o usuário " + email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int obterNivelPermissao(String email) {
        int nivelPermissao = 1; // Nível padrão, caso não encontre
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/faculdadeApp", "sa", "")) {
            String sql = "SELECT permissao FROM usuario WHERE email = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    nivelPermissao = rs.getInt("permissao");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nivelPermissao;
    }
    public static List<Usuario> obterUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, email, permissao FROM Usuario";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                int permissao = rs.getInt("permissao");
                usuarios.add(new Usuario(id, email, permissao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public static void atualizarPermissaoUsuario(int id, int novaPermissao) {
        String sqlUpdate = "UPDATE Usuario SET permissao = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, novaPermissao);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Permissão do usuário atualizada com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}