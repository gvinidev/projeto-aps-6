package com.aps.telas;

import com.aps.model.Usuario;
import com.aps.database.BancoDados;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TelaGerenciarUsuarios extends JFrame {

    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;

    public TelaGerenciarUsuarios() {
        setTitle("Gerenciar Usuários");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Cabeçalho
        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setBackground(new Color(50, 50, 50));
        painelCabecalho.setPreferredSize(new Dimension(600, 60));

        JLabel lblTitulo = new JLabel("Gerenciar Usuários");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        painelCabecalho.add(lblTitulo);

        // Tabela de Usuários
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Email", "Permissão"}, 0);
        tabelaUsuarios = new JTable(modeloTabela);
        tabelaUsuarios.setFillsViewportHeight(true);
        tabelaUsuarios.setRowHeight(25);
        tabelaUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Estilizar cabeçalho da tabela
        JTableHeader header = tabelaUsuarios.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255));
        header.setForeground(Color.WHITE);

        // Centralizar células na tabela
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabelaUsuarios.getColumnCount(); i++) {
            tabelaUsuarios.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);

        // Botão de Atualizar Permissão
        JButton btnAtualizarPermissao = new JButton("Atualizar Permissão");
        styleButton(btnAtualizarPermissao);
        btnAtualizarPermissao.addActionListener(e -> atualizarPermissaoUsuario());

        // Painel de Botão
        JPanel painelBotoes = new JPanel();
        painelBotoes.setBackground(new Color(240, 240, 240));
        painelBotoes.add(btnAtualizarPermissao);

        // Adicionando componentes ao JFrame
        add(painelCabecalho, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        List<Usuario> usuarios = BancoDados.obterUsuarios(); // Método no BancoDados
        for (Usuario usuario : usuarios) {
            modeloTabela.addRow(new Object[]{usuario.getId(), usuario.getEmail(), usuario.getPermissao()});
        }
    }

    private void atualizarPermissaoUsuario() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        if (linhaSelecionada != -1) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            int novaPermissao;
            try {
                novaPermissao = Integer.parseInt(JOptionPane.showInputDialog(this, "Nova permissão:"));
                BancoDados.atualizarPermissaoUsuario(id, novaPermissao);
                modeloTabela.setValueAt(novaPermissao, linhaSelecionada, 2);
                JOptionPane.showMessageDialog(this, "Permissão atualizada com sucesso.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Permissão inválida. Insira um número inteiro.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para atualizar.");
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
