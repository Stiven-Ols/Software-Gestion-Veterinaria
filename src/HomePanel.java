import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel(Main main) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 28, 20));
        cardsPanel.setBackground(getBackground());

        // Cards de navegación
        cardsPanel.add(PanelUtil.crearCard("Mascotas", "/Mascotas.png", main::abrirPanelMascotas));
        cardsPanel.add(PanelUtil.crearCard("Propietarios", "/Propietarios.png", main::abrirPanelPropietarios));
        cardsPanel.add(PanelUtil.crearCard("Veterinarios", "/Veterinario.png", main::abrirPanelVeterinarios));
        cardsPanel.add(PanelUtil.crearCard("Citas", "/Citas.png", main::abrirPanelCitas));
        cardsPanel.add(PanelUtil.crearCard("Pagos/Reportes", "/Pago.png", main::abrirPanelPagos));

        JLabel titulo = new JLabel("Sistema Gestión Veterinaria", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(new Color(36, 46, 120));
        titulo.setIcon(new ImageIcon(getClass().getResource("user.png")));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(getBackground());
        top.add(titulo, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(getBackground());
        center.add(cardsPanel);

        add(center, BorderLayout.CENTER);
    }
}
