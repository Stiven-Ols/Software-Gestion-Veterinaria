package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class HomePanel extends JPanel {
    public HomePanel(Main main) {
        setLayout(new BorderLayout(20, 20)); // Añadir espaciado general
        setBackground(new Color(235, 245, 255)); // Fondo claro y agradable
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margen general

        // Título principal del Home
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setOpaque(false);
        JLabel lblTituloPrincipal = new JLabel("Sistema de Gestión Veterinaria", SwingConstants.CENTER);
        lblTituloPrincipal.setFont(new Font("Arial", Font.BOLD, 28)); // Fuente más grande
        lblTituloPrincipal.setForeground(new Color(30, 40, 85)); // Color oscuro y profesional

        // Intentar cargar un icono para el título (opcional)
        try {
            URL iconUrl = getClass().getResource("/logo_vet.png"); // Cambia "logo_vet.png" por el nombre de tu logo si tienes
            if (iconUrl != null) {
                lblTituloPrincipal.setIcon(new ImageIcon(iconUrl));
                lblTituloPrincipal.setIconTextGap(15); // Espacio entre icono y texto
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono para el título del HomePanel.");
        }
        panelTitulo.add(lblTituloPrincipal);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central para las cards de navegación
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 30, 30)); // Más espaciado entre cards
        cardsPanel.setOpaque(false); // Hacer transparente para ver el fondo del HomePanel
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Espacio arriba y abajo de las cards

        // Cards de navegación (usando PanelUtil)
        cardsPanel.add(PanelUtil.crearCard("Propietarios", "/Propietarios.png", main::abrirPanelPropietarios));
        cardsPanel.add(PanelUtil.crearCard("Mascotas", "/Mascotas.png", main::abrirPanelMascotas));
        cardsPanel.add(PanelUtil.crearCard("Veterinarios", "/Veterinario.png", main::abrirPanelVeterinarios));
        cardsPanel.add(PanelUtil.crearCard("Agendar Citas", "/Citas.png", main::abrirPanelCitas));
        cardsPanel.add(PanelUtil.crearCard("Pagos e Historial", "/Pago.png", main::abrirPanelPagos));
        // Podrías añadir una card para "Configuración" o "Salir" si es necesario

        // Contenedor para centrar el panel de cards
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(cardsPanel); // Añade el panel de cards al contenedor GridBagLayout para centrarlo

        add(centerContainer, BorderLayout.CENTER);
    }
}
