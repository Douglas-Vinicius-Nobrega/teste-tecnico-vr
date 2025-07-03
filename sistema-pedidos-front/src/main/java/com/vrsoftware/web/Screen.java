package com.vrsoftware.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vrsoftware.web.model.Pedido;
import okhttp3.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Screen extends JFrame {
    private JTextField produtoField;
    private JTextField quantidadeField;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private final Map<UUID, String> pedidos = new LinkedHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final OkHttpClient client = new OkHttpClient();
    private final String backendUrl = "http://localhost:8080/api/pedidos";

    public Screen() {
        setTitle("Cliente de Pedidos");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        produtoField = new JTextField(15);
        quantidadeField = new JTextField(5);
        JButton enviarBtn = new JButton("Enviar Pedido");

        tableModel = new DefaultTableModel(new String[]{"ID", "Status"}, 0);
        tabela = new JTable(tableModel);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Produto:"));
        topPanel.add(produtoField);
        topPanel.add(new JLabel("Quantidade:"));
        topPanel.add(quantidadeField);
        topPanel.add(enviarBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        enviarBtn.addActionListener(e -> enviarPedido());

        iniciarPolling();
    }

    private void enviarPedido() {
        String produto = produtoField.getText();
        String qtdText = quantidadeField.getText();

        if (produto.isEmpty() || qtdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(qtdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            return;
        }

        UUID id = UUID.randomUUID();
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setProduto(produto);
        pedido.setQuantidade(quantidade);
        pedido.setDataCriacao(LocalDateTime.now());

        try {
            String json = objectMapper.writeValueAsString(pedido);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(backendUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Screen.this, "Erro ao enviar pedido."));
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        pedidos.put(id, "ENVIADO, AGUARDANDO PROCESSO");
                        SwingUtilities.invokeLater(() -> tableModel.addRow(new Object[]{id, "ENVIADO, AGUARDANDO PROCESSO"}));
                    } else {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Screen.this, "Erro na requisição: " + response.code()));
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao criar JSON.");
        }
    }

    private void iniciarPolling() {
        Timer timer = new Timer(4000, e -> {
            for (UUID id : pedidos.keySet()) {
                String statusAtual = pedidos.get(id);
                if (statusAtual.contains("AGUARDANDO") || statusAtual.contains("PROCESSANDO")) {
                    Request request = new Request.Builder()
                            .url(backendUrl + "/status/" + id)
                            .get()
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {}

                        @Override public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String novoStatus = response.body().string().replaceAll("\"", "");
                                pedidos.put(id, novoStatus);
                                SwingUtilities.invokeLater(() -> atualizarTabela(id, novoStatus));
                            }
                        }
                    });
                }
            }
        });
        timer.start();
    }

    private void atualizarTabela(UUID id, String status) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(id.toString())) {
                tableModel.setValueAt(status, i, 1);
                break;
            }
        }
    }
}
