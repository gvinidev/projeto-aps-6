package com.aps;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {
    private int nivelPermissao;

    public TelaPrincipal(int nivelPermissao) {
        this.nivelPermissao = nivelPermissao;

        setTitle("Painel Principal - Sistema de Autenticação");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        // Painel de Navegação Lateral
        JPanel painelNavegacao = new JPanel();
        painelNavegacao.setLayout(new BoxLayout(painelNavegacao, BoxLayout.Y_AXIS));
        painelNavegacao.setBackground(new Color(50, 50, 50));
        painelNavegacao.setPreferredSize(new Dimension(200, 0));

        JLabel lblTitulo = new JLabel("Menu");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelNavegacao.add(lblTitulo);
        painelNavegacao.add(Box.createVerticalStrut(20));

        JButton btnInformacoes = new JButton("Informações Gerais");
        JButton btnInformacoesDetalhadas = new JButton("Informações Detalhadas");
        JButton btnAcessoCompleto = new JButton("Acesso Completo");

        styleButton(btnInformacoes);
        styleButton(btnInformacoesDetalhadas);
        styleButton(btnAcessoCompleto);

        painelNavegacao.add(btnInformacoes);
        if (nivelPermissao >= 2) {
            painelNavegacao.add(Box.createVerticalStrut(10));
            painelNavegacao.add(btnInformacoesDetalhadas);
        }

        if (nivelPermissao == 3) {
            JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
            styleButton(btnGerenciarUsuarios);
            btnGerenciarUsuarios.addActionListener(e -> abrirTelaGerenciarUsuarios());
            painelNavegacao.add(Box.createVerticalStrut(10));
            painelNavegacao.add(btnGerenciarUsuarios);
        }

        // Painel Central
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(240, 240, 240));

        JLabel lblBemVindo = new JLabel("Bem-vindo, Usuário Nível " + nivelPermissao);
        lblBemVindo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblBemVindo.setHorizontalAlignment(SwingConstants.CENTER);
        lblBemVindo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        painelCentral.add(lblBemVindo, BorderLayout.NORTH);

        // Painel de informações
        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setText("Informações iniciais do sistema...\n");
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtInfo);
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        // Adicionando Ações para os Botões
        btnInformacoes.addActionListener(e -> txtInfo.setText("Exibindo informações gerais do sistema..."));

        if (nivelPermissao >= 2) {
            btnInformacoesDetalhadas.addActionListener(e -> txtInfo.setText("Exibindo informações detalhadas..."));
        }

        if (nivelPermissao >= 3) {
            btnAcessoCompleto.addActionListener(e -> txtInfo.setText("Exibindo acesso completo aos dados confidenciais..."));
        }

        // Adicionando Paineis à Tela Principal
        add(painelNavegacao, BorderLayout.WEST);
        add(painelCentral, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void abrirTelaGerenciarUsuarios() {
        TelaGerenciarUsuarios telaGerenciarUsuarios = new TelaGerenciarUsuarios();
        telaGerenciarUsuarios.setVisible(true);
    }

}
