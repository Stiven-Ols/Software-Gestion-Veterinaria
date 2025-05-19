package com.veterinaria;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelUtil {
    public static JPanel crearCard(String titulo, String recursoImg, Runnable accion) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 190)); // Ajuste dimensiones
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100,140,255,140), 2, true));
        panel.setLayout(new BorderLayout(0,8));

        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        if (recursoImg != null && !recursoImg.isEmpty()) {
            try {
                URL imgUrl = PanelUtil.class.getResource(recursoImg);
                if (imgUrl != null) {
                    // Escalar la imagen si es necesario, por ejemplo a 64x64 para estas cards
                    ImageIcon originalIcon = new ImageIcon(imgUrl);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    img.setIcon(new ImageIcon(scaledImage));
                } else {
                    img.setText("Sin imagen");
                    System.err.println("Error: Icono no encontrado en PanelUtil: " + recursoImg);
                }
            } catch (Exception e) {
                img.setText("Err img");
                System.err.println("Excepción cargando icono en PanelUtil " + recursoImg + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            img.setText("No img path");
        }
        panel.add(img, BorderLayout.CENTER);

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 17));
        lbl.setForeground(new Color(30, 40, 85));
        panel.add(lbl, BorderLayout.NORTH);

        JButton btn = new JButton("Ir");
        btn.setBackground(new Color(255, 255, 255));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.addActionListener(e -> accion.run());
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }
}