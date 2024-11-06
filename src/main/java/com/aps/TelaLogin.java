package com.aps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaLogin extends JFrame {

    // Campos de entrada de dados e botões para o formulário de login
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCadastrar;

    // Construtor da tela de login
    public TelaLogin() {
        setTitle("Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        // Configuração do painel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 50));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Título principal da tela
        JLabel lblTitulo = new JLabel("Sistema de Autenticação");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(lblTitulo, constraints);

        // Label e campo de entrada para o email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setForeground(Color.LIGHT_GRAY);
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(lblEmail, constraints);

        txtEmail = new JTextField(20);
        txtEmail.setBackground(new Color(70, 70, 70));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        constraints.gridx = 1;
        panel.add(txtEmail, constraints);

        // Label e campo de entrada para a senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.LIGHT_GRAY);
        lblSenha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(lblSenha, constraints);

        txtSenha = new JPasswordField(20);
        txtSenha.setBackground(new Color(70, 70, 70));
        txtSenha.setForeground(Color.WHITE);
        txtSenha.setCaretColor(Color.WHITE);
        txtSenha.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        constraints.gridx = 1;
        panel.add(txtSenha, constraints);

        // Botão de login
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(30, 144, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String senha = new String(txtSenha.getPassword());
                autenticarUsuario(email);
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(btnLogin, constraints);

        // Botão de cadastro
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(34, 139, 34));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario();
                telaCadastro.setVisible(true);
                dispose();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(btnCadastrar, constraints);

        add(panel);
    }

    // Método responsável por autenticar o usuário, neste caso direcionando para a tela de biometria
    private void autenticarUsuario(String email) {
        List<Usuario> usuarios = BancoDados.obterUsuarios();

        if (!usuarios.stream().anyMatch(u -> u.getEmail().equals(email))) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TelaAutenticacaoBiometria telaBiometria = new TelaAutenticacaoBiometria(email);
        telaBiometria.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
        });
    }
}
