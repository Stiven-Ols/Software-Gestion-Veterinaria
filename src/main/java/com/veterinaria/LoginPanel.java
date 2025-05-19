// LoginPanel.java
package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginPanel extends JPanel {
    private Main main;

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(235, 245, 255));

        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        cardsPanel.setOpaque(false);

        JPanel cardAdmin = crearCardLogin("Administrador", "/Administrador.png", true);
        JPanel cardEmpleado = crearCardLogin("Empleado", "/Empleados.png", false);

        cardsPanel.add(cardAdmin);
        cardsPanel.add(cardEmpleado);

        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Gestión Veterinaria", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        lblBienvenida.setForeground(new Color(30, 40, 85));
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        add(lblBienvenida, BorderLayout.NORTH);
        cardContainer.add(cardsPanel, gbc);
        add(cardContainer, BorderLayout.CENTER);
    }

    private JPanel crearCardLogin(String texto, String imagenPath, boolean esAdminLogin) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setPreferredSize(new Dimension(280, 320));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(220, 220));
        try {
            URL imgUrl = getClass().getResource(imagenPath);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No se encontró: " + imagenPath);
                System.err.println("Error: Icono no encontrado en LoginPanel: " + imagenPath);
            }
        } catch (Exception e) {
            imageLabel.setText("Error al cargar imagen");
            e.printStackTrace();
        }
        card.add(imageLabel, BorderLayout.CENTER);

        JLabel txtLabel = new JLabel(texto, SwingConstants.CENTER);
        txtLabel.setFont(new Font("Arial", Font.BOLD, 20));
        txtLabel.setForeground(new Color(40, 60, 110));
        card.add(txtLabel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (esAdminLogin) {
                    JPasswordField passwordField = new JPasswordField(15);
                    JPanel panelLoginAdmin = new JPanel(new GridLayout(0,1,5,5));
                    panelLoginAdmin.add(new JLabel("Contraseña de Administrador:"));
                    panelLoginAdmin.add(passwordField);

                    SwingUtilities.invokeLater(passwordField::requestFocusInWindow);

                    int option = JOptionPane.showConfirmDialog(
                            main,
                            panelLoginAdmin,
                            "Login Administrador",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (option == JOptionPane.OK_OPTION) {
                        String password = new String(passwordField.getPassword());
                        if ("adminAres".equals(password)) {
                            main.setEsAdmin(true);
                            JOptionPane.showMessageDialog(
                                    main,
                                    "Acceso como administrador concedido.",
                                    "Login Exitoso",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            // CAMBIO IMPORTANTE: Ir directamente al AdminPanel (dashboard)
                            main.abrirAdminPanel();
                        } else {
                            main.setEsAdmin(false);
                            JOptionPane.showMessageDialog(
                                    main,
                                    "Contraseña incorrecta.",
                                    "Error de Login",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } else { // Es Empleado
                    main.setEsAdmin(false);
                    // Para el empleado, ir al HomePanel configurado para vista de empleado
                    main.irHome(false);
                }
            }
        });
        return card;
    }
}