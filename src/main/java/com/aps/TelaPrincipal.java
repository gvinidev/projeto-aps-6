package com.aps;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TelaPrincipal extends JFrame {
    private int nivelPermissao;
    private String emailUsuario;
    private JPanel painelCentral;

    public TelaPrincipal(int nivelPermissao, String emailUsuario) {
        this.nivelPermissao = nivelPermissao;
        this.emailUsuario = emailUsuario;

        setTitle("Painel Principal - Sistema de Autenticação");
        setSize(1000, 600);
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

        // Botão para voltar ao Menu Principal
        JButton btnVoltarMenu = new JButton("Menu Principal");
        styleButton(btnVoltarMenu);
        btnVoltarMenu.addActionListener(e -> mostrarTelaInicial());
        painelNavegacao.add(btnVoltarMenu);
        painelNavegacao.add(Box.createVerticalStrut(15));

        // Adicionando botões de acordo com o nível de permissão
        if (nivelPermissao >= 1) {
            JButton btnEmpresasFichadas = new JButton("Empresas Fichadas");
            styleButton(btnEmpresasFichadas);
            btnEmpresasFichadas.addActionListener(e -> carregarTabelaEmpresasFichadas());
            painelNavegacao.add(btnEmpresasFichadas);
            painelNavegacao.add(Box.createVerticalStrut(15));
        }

        if (nivelPermissao >= 2) {
            JButton btnNivelImpacto = new JButton("Nível de Impacto");
            styleButton(btnNivelImpacto);
            btnNivelImpacto.addActionListener(e -> carregarTabelaNivelImpacto());
            painelNavegacao.add(btnNivelImpacto);
            painelNavegacao.add(Box.createVerticalStrut(15));
        }

        if (nivelPermissao >= 3) {
            JButton btnIdentificacaoEmpresa = new JButton("Identificação da Empresa");
            styleButton(btnIdentificacaoEmpresa);
            btnIdentificacaoEmpresa.addActionListener(e -> carregarTabelaIdentificacaoEmpresa());
            painelNavegacao.add(btnIdentificacaoEmpresa);
            painelNavegacao.add(Box.createVerticalStrut(15));

            // Novo botão de Gerenciar Usuários, disponível apenas para nível 3
            JButton btnGerenciarUsuarios = new JButton("Gerenciar Usuários");
            styleButton(btnGerenciarUsuarios);
            btnGerenciarUsuarios.addActionListener(e -> mostrarTelaGerenciarUsuarios());
            painelNavegacao.add(btnGerenciarUsuarios);
            painelNavegacao.add(Box.createVerticalStrut(15));
        }

        // Painel Central
        painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(240, 240, 240));

        mostrarTelaInicial();

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

    private void mostrarTelaInicial() {
        painelCentral.removeAll();

        JPanel painelInformacoes = new JPanel();
        painelInformacoes.setLayout(new BoxLayout(painelInformacoes, BoxLayout.Y_AXIS));
        painelInformacoes.setBackground(new Color(240, 240, 240));
        painelInformacoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblEmail = new JLabel("Email: " + emailUsuario);
        lblEmail.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblEmail.setForeground(new Color(60, 60, 60));
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNivel = new JLabel("Nível de Usuário: " + nivelPermissao);
        lblNivel.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNivel.setForeground(new Color(60, 60, 60));
        lblNivel.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelInformacoes.add(lblEmail);
        painelInformacoes.add(Box.createVerticalStrut(10));
        painelInformacoes.add(lblNivel);

        painelCentral.add(painelInformacoes, BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
    }

    private void carregarTabelaEmpresasFichadas() {
        List<String[]> dados = BancoDados.obterEmpresasFichadas();
        String[] colunas = {"ID da Empresa", "Cidade da Empresa", "Estado da Empresa"};
        atualizarTabela(dados, colunas);
    }

    private void carregarTabelaNivelImpacto() {
        List<String[]> dados = BancoDados.obterNivelImpacto();
        String[] colunas = {"ID da Empresa", "Nível de Impacto"};
        atualizarTabela(dados, colunas);
    }

    private void carregarTabelaIdentificacaoEmpresa() {
        List<String[]> dados = BancoDados.obterIdentificacaoEmpresa();
        String[] colunas = {"ID da Empresa", "Nome da Empresa", "CNPJ da Empresa"};
        atualizarTabela(dados, colunas);
    }

    private void atualizarTabela(List<String[]> dados, String[] colunas) {
        // Definindo o modelo de dados da tabela
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        for (String[] linha : dados) {
            modelo.addRow(linha);
        }

        // Criando a tabela com o modelo de dados
        JTable tabela = new JTable(modelo);

        // Estilizando a tabela
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(new Color(200, 200, 200)); // Cor das linhas da grade
        tabela.setRowHeight(30); // Altura das linhas
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Fonte para as células
        tabela.setForeground(new Color(60, 60, 60)); // Cor do texto das células
        tabela.setBackground(new Color(255, 255, 255)); // Cor de fundo das células
        tabela.setIntercellSpacing(new Dimension(0, 0)); // Remove o espaço entre as células

        // Estilizando a tabela (cabeçalho)
        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14)); // Fonte do cabeçalho
        header.setBackground(new Color(30, 144, 255)); // Cor de fundo do cabeçalho
        header.setForeground(Color.WHITE); // Cor do texto do cabeçalho
        header.setReorderingAllowed(false); // Desabilita a reorganização das colunas

        // Adicionando a tabela com rolagem no painel
        painelCentral.removeAll();
        painelCentral.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
    }


    private void mostrarTelaGerenciarUsuarios() {
        TelaGerenciarUsuarios telaGerenciarUsuarios = new TelaGerenciarUsuarios();
        telaGerenciarUsuarios.setVisible(true);
    }
}
