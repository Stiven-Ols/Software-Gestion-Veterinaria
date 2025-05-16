import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private Main main;

    public LoginPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255));

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        cardPanel.setBackground(getBackground());

        // Card Administrador
        JPanel cardAdmin = crearCard("Administrador", "/Administrador.png", true);

        // Card Empleado
        JPanel cardEmpleado = crearCard("Empleado", "/Empleados.png", false);

        cardPanel.add(cardAdmin);
        cardPanel.add(cardEmpleado);

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel crearCard(String texto, String imagenPath, boolean esAdmin) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 300));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Imagen
        java.net.URL imgUrl = getClass().getResource(imagenPath);
        ImageIcon icon = (imgUrl != null) ? new ImageIcon(imgUrl) : null;
        JLabel imageLabel = new JLabel(icon, SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        card.add(imageLabel, BorderLayout.CENTER);

        // Texto
        JLabel txtLabel = new JLabel(texto, SwingConstants.CENTER);
        txtLabel.setFont(new Font("Arial", Font.BOLD, 18));
        card.add(txtLabel, BorderLayout.SOUTH);

        // Evento click
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (esAdmin) {
                    // Modal de login para administrador
                    String password = JOptionPane.showInputDialog(
                            main,
                            "Ingrese contraseña de administrador:",
                            "Login Administrador",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if ("adminAres".equals(password)) {
                        // Implementa aquí vista de administrador cuando la tengas
                        JOptionPane.showMessageDialog(
                                main,
                                "Acceso como administrador",
                                "Login Exitoso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else if (password != null) {
                        JOptionPane.showMessageDialog(
                                main,
                                "Contraseña incorrecta",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    // Ir a la vista normal del sistema
                    main.cardLayout.show(main.mainPanel, "home");
                }
            }
        });

        return card;
    }
}
