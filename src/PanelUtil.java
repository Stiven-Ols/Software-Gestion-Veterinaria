// PanelUtil.java
import javax.swing.*;
import java.awt.*;

public class PanelUtil {
    /**
     * Crea un "card" de sección con imagen, título y botón.
     * @param titulo Título de la sección
     * @param recursoImg Ruta en resources, ej: "/Mascotas.png"
     * @param accion Runnable, acción al hacer clic en el botón "Ir"
     * @return JPanel listo para usarse en el Home
     */
    public static JPanel crearCard(String titulo, String recursoImg, Runnable accion) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 190));
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100,140,255,140), 2, true));
        panel.setLayout(new BorderLayout(0,8));

        // Imagen
        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            img.setIcon(new ImageIcon(PanelUtil.class.getResource(recursoImg)));
        } catch (Exception e) {
            img.setText("Sin imagen");
        }
        panel.add(img, BorderLayout.CENTER);

        // Título
        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 17));
        lbl.setForeground(new Color(30, 40, 85));
        panel.add(lbl, BorderLayout.NORTH);

        // Botón
        JButton btn = new JButton("Ir");
        btn.setBackground(new Color(255, 255, 255));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.addActionListener(e -> accion.run());
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }
}
