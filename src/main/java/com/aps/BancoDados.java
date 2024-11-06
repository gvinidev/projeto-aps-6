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

            String sqlCreateUsuario = """
                CREATE TABLE IF NOT EXISTS Usuario (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    senha VARCHAR(255) NOT NULL,
                    permissao INT DEFAULT 1,
                    diretorioImagem VARCHAR(255)
                );
            """;

            stmt.execute(sqlCreateUsuario);
            System.out.println("Tabela Usuario criada ou já existente.");

            criarTabelasInformacoes(stmt);

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
        int nivelPermissao = 1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT permissao FROM Usuario WHERE email = ?";
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

    private static void criarTabelasInformacoes(Statement stmt) throws SQLException {
        String sqlCreateEmpresasFichadas = """
            CREATE TABLE IF NOT EXISTS EmpresasFichadas (
                id_empresa INT PRIMARY KEY AUTO_INCREMENT,
                cidade_empresa VARCHAR(100) NOT NULL,
                estado_empresa VARCHAR(50) NOT NULL
            );
        """;
        stmt.execute(sqlCreateEmpresasFichadas);

        String sqlCreateNivelImpacto = """
            CREATE TABLE IF NOT EXISTS NivelImpacto (
                id_empresa INT NOT NULL,
                nivel_impacto VARCHAR(10),
                PRIMARY KEY (id_empresa),
                FOREIGN KEY (id_empresa) REFERENCES EmpresasFichadas(id_empresa)
            );
        """;
        stmt.execute(sqlCreateNivelImpacto);

        String sqlCreateIdentificacaoEmpresa = """
            CREATE TABLE IF NOT EXISTS IdentificacaoEmpresa (
                id_empresa INT NOT NULL,
                nome_empresa VARCHAR(255) NOT NULL,
                cnpj_empresa VARCHAR(18) NOT NULL,
                PRIMARY KEY (id_empresa),
                FOREIGN KEY (id_empresa) REFERENCES EmpresasFichadas(id_empresa)
            );
        """;
        stmt.execute(sqlCreateIdentificacaoEmpresa);

        System.out.println("Tabelas EmpresasFichadas, NivelImpacto e IdentificacaoEmpresa criadas ou já existentes.");
    }

    // Métodos para consulta das tabelas de informações
    public static List<String[]> obterEmpresasFichadas() {
        List<String[]> empresas = new ArrayList<>();
        String sql = "SELECT id_empresa, cidade_empresa, estado_empresa FROM EmpresasFichadas";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(new String[]{
                        String.valueOf(rs.getInt("id_empresa")),
                        rs.getString("cidade_empresa"),
                        rs.getString("estado_empresa")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empresas;
    }

    public static List<String[]> obterNivelImpacto() {
        List<String[]> impactos = new ArrayList<>();
        String sql = "SELECT id_empresa, nivel_impacto FROM NivelImpacto";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                impactos.add(new String[]{
                        String.valueOf(rs.getInt("id_empresa")),
                        rs.getString("nivel_impacto")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return impactos;
    }

    public static List<String[]> obterIdentificacaoEmpresa() {
        List<String[]> identificacoes = new ArrayList<>();
        String sql = "SELECT id_empresa, nome_empresa, cnpj_empresa FROM IdentificacaoEmpresa";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                identificacoes.add(new String[]{
                        String.valueOf(rs.getInt("id_empresa")),
                        rs.getString("nome_empresa"),
                        rs.getString("cnpj_empresa")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return identificacoes;
    }
}
