package com.aps.telas;

import com.aps.database.BancoDados;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuario extends JFrame {

    // Campos de entrada e botão para o cadastro do usuário
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnCadastrar;

    public TelaCadastroUsuario() {
        setTitle("Cadastro de Usuário");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        // Painel principal usando GridBagLayout para organização flexível dos componentes
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 45));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Título principal da tela de cadastro
        JLabel lblTitulo = new JLabel("Cadastro de Usuário");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(JLabel.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(lblTitulo, constraints);

        // Label e campo de entrada para o email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblEmail.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(lblEmail, constraints);

        txtEmail = new JTextField(20);
        txtEmail.setBackground(new Color(60, 63, 65));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        constraints.gridx = 1;
        panel.add(txtEmail, constraints);

        // Label e campo de entrada para a senha
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSenha.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(lblSenha, constraints);

        txtSenha = new JPasswordField(20);
        txtSenha.setBackground(new Color(60, 63, 65));
        txtSenha.setForeground(Color.WHITE);
        txtSenha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        constraints.gridx = 1;
        panel.add(txtSenha, constraints);

        // Botão de cadastro
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(30, 144, 255));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCadastrar.addActionListener(e -> {

            String email = txtEmail.getText();
            String senha = new String(txtSenha.getPassword());
            String diretorioImagem = String.format("imagens/%s", email);

            BancoDados.adicionarUsuario(email, senha, email.contains("admin") ? 3 : 1, diretorioImagem);
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new TelaLogin().setVisible(true);
        });

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(btnCadastrar, constraints);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario();
            telaCadastro.setVisible(true);
        });
    }
}
