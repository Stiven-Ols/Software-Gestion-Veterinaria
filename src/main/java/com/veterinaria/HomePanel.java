// HomePanel.java
package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class HomePanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JPanel cardAdminPlaceholder;
    private boolean mostrarComoAdminViewGlobal;

    public HomePanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(235, 245, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setOpaque(false);
        JLabel lblTituloPrincipal = new JLabel("Sistema de Gestión Veterinaria", SwingConstants.CENTER);
        lblTituloPrincipal.setFont(new Font("Arial", Font.BOLD, 28));
        lblTituloPrincipal.setForeground(new Color(30, 40, 85));
//        try {
//            URL iconUrl = getClass().getResource("/logo_vet.png");
//            if (iconUrl != null) {
//                lblTituloPrincipal.setIcon(new ImageIcon(iconUrl));
//                lblTituloPrincipal.setIconTextGap(15);
//            } else {
//                System.err.println("Error: Icono no encontrado en HomePanel: /logo_vet.png");
//            }
//        } catch (Exception e) {
//            System.err.println("Excepción cargando logo_vet.png: " + e.getMessage());
//        }
        panelTitulo.add(lblTituloPrincipal);
        add(panelTitulo, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 3, 25, 25));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        cardsPanel.add(PanelUtil.crearCard("Propietarios", "/Propietarios.png", main::abrirPanelPropietarios));
        cardsPanel.add(PanelUtil.crearCard("Mascotas", "/Mascotas.png", main::abrirPanelMascotas));
        cardsPanel.add(PanelUtil.crearCard("Veterinarios", "/Veterinario.png", main::abrirPanelVeterinarios));
        cardsPanel.add(PanelUtil.crearCard("Agendar Citas", "/Citas.png", main::abrirPanelCitas));
        cardsPanel.add(PanelUtil.crearCard("Pagos e Historial", "/Pago.png", main::abrirPanelPagos));

        cardAdminPlaceholder = new JPanel(new BorderLayout());
        cardAdminPlaceholder.setOpaque(false);
        cardsPanel.add(cardAdminPlaceholder);

        configurarParaVista(false);

        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(cardsPanel);
        add(centerContainer, BorderLayout.CENTER);
    }

    public void configurarParaVista(boolean esVistaAdminGlobalmente) {
        this.mostrarComoAdminViewGlobal = esVistaAdminGlobalmente;
        cardAdminPlaceholder.removeAll();

        if (this.mostrarComoAdminViewGlobal && main.isEsAdmin()) {
            JPanel adminCard = PanelUtil.crearCard("Panel Admin", "/Administrador.png", () -> { // USA EL NOMBRE EXACTO DE TU ARCHIVO
                main.abrirAdminPanel();
            });
            cardAdminPlaceholder.add(adminCard, BorderLayout.CENTER);
        } else {
            JPanel accederAdminCard = PanelUtil.crearCard("Panel Admin", "/Administrador.png", () -> { // USA EL NOMBRE EXACTO DE TU ARCHIVO
                main.solicitarPasswordParaAdminPanel();
            });
            cardAdminPlaceholder.add(accederAdminCard, BorderLayout.CENTER);
        }

        cardAdminPlaceholder.revalidate();
        cardAdminPlaceholder.repaint();
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}