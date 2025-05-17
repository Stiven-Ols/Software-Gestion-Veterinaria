package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginPanel extends JPanel {
    private Main main;

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(235, 245, 255)); // Un fondo general claro

        JPanel cardContainer = new JPanel(new GridBagLayout()); // Para centrar las cards
        cardContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0)); // Espacio entre cards
        cardsPanel.setOpaque(false);

        // Card Administrador
        JPanel cardAdmin = crearCardLogin("Administrador", "/Administrador.png", true);
        // Card Empleado
        JPanel cardEmpleado = crearCardLogin("Empleado", "/Empleados.png", false);

        cardsPanel.add(cardAdmin);
        cardsPanel.add(cardEmpleado);

        // Título principal del Login
        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Gestión Veterinaria", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        lblBienvenida.setForeground(new Color(30, 40, 85));
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        add(lblBienvenida, BorderLayout.NORTH);
        cardContainer.add(cardsPanel, gbc); // Añadir el panel de cards al contenedor centrado
        add(cardContainer, BorderLayout.CENTER);
    }

    private JPanel crearCardLogin(String texto, String imagenPath, boolean esAdmin) {
        JPanel card = new JPanel(new BorderLayout(0, 15)); // Espacio entre imagen y texto
        card.setPreferredSize(new Dimension(280, 320)); // Tamaño de la card
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Imagen
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER); // Placeholder si no carga
        imageLabel.setPreferredSize(new Dimension(220, 220)); // Tamaño para la imagen
        try {
            URL imgUrl = getClass().getResource(imagenPath);
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                // Escalar la imagen para que se ajuste mejor si es necesario
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No se encontró: " + imagenPath);
            }
        } catch (Exception e) {
            imageLabel.setText("Error al cargar imagen");
            e.printStackTrace();
        }
        card.add(imageLabel, BorderLayout.CENTER);

        // Texto
        JLabel txtLabel = new JLabel(texto, SwingConstants.CENTER);
        txtLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Fuente más grande para el título de la card
        txtLabel.setForeground(new Color(40, 60, 110));
        card.add(txtLabel, BorderLayout.SOUTH);

        // Evento click
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (esAdmin) {
                    JPasswordField passwordField = new JPasswordField(15);
                    JPanel panelLoginAdmin = new JPanel(new GridLayout(0,1,5,5));
                    panelLoginAdmin.add(new JLabel("Contraseña de Administrador:"));
                    panelLoginAdmin.add(passwordField);

                    // Solicitar foco para el campo de contraseña
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
                            // Aquí se podría establecer un flag en Main para indicar que es admin
                            // y luego mostrar un panel de administración o habilitar opciones admin.
                            // Por ahora, solo un mensaje y luego al home.
                            JOptionPane.showMessageDialog(
                                    main,
                                    "Acceso como administrador concedido.",
                                    "Login Exitoso",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            // main.setEsAdmin(true); // Suponiendo un método en Main
                            main.irHome(); // O a un panel de admin si lo tienes
                        } else {
                            JOptionPane.showMessageDialog(
                                    main,
                                    "Contraseña incorrecta.",
                                    "Error de Login",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                } else {
                    // main.setEsAdmin(false); // Suponiendo un método en Main
                    main.irHome(); // Ir a la vista normal del sistema para empleados
                }
            }
        });
        return card;
    }
}
