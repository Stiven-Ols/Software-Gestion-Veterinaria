import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List; // Importa List

public class VeterinariosPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public VeterinariosPanel(Main main) { // Constructor solo recibe Main
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());

        JLabel lblTitulo = new JLabel("Veterinarios", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por nombre o documento"));
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Agregar Veterinario");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.addActionListener(e -> dialogoAgregarVeterinario());

        JPanel panelBusquedaBotones = new JPanel(new BorderLayout());
        panelBusquedaBotones.setBackground(getBackground());
        panelBusquedaBotones.add(txtBuscar, BorderLayout.CENTER);
        panelBusquedaBotones.add(btnAgregar, BorderLayout.EAST);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(panelBusquedaBotones, BorderLayout.SOUTH);

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(0, 2, 15, 15)); // 2 columnas, espaciado horizontal y vertical
        cardsPanel.setBackground(getBackground());
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(null);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome()); // Llama al método de Main

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(getBackground());
        bottomPanel.add(btnVolver);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        actualizarCards(); // Carga inicial usando la lista de Main
    }

    private void dialogoAgregarVeterinario() {
        JTextField txtDocumento = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtEspecialidad = new JTextField();
        JTextField txtTelefono = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Documento:")); panel.add(txtDocumento);
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Especialidad:")); panel.add(txtEspecialidad);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Veterinario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (txtDocumento.getText().isBlank() || txtNombre.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Documento y Nombre son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verificar si ya existe
            for(Veterinario existente : main.veterinarios) {
                if(existente.getDocumento().equals(txtDocumento.getText())) {
                    JOptionPane.showMessageDialog(this, "Ya existe un veterinario con ese documento.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            Veterinario nuevoVeterinario = new Veterinario(
                    txtDocumento.getText(), txtNombre.getText(), txtEspecialidad.getText(), txtTelefono.getText(), true
            );
            main.agregarVeterinario(nuevoVeterinario); // Usa el método de Main
            JOptionPane.showMessageDialog(this, "Veterinario agregado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() { // Actualiza usando la lista de main
        actualizarCards(main.veterinarios);
    }

    public void actualizarCards(List<Veterinario> lista) {
        cardsPanel.removeAll();
        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("No hay veterinarios registrados."));
        } else {
            for (Veterinario v : lista) {
                cardsPanel.add(crearCardVeterinario(v));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardVeterinario(Veterinario v) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(300, 130));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(245, 250, 255), 1));
        JLabel lblAvatar = new JLabel(new ImageIcon(getClass().getResource("/Veterinary.png")), SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(70, 70));
        card.add(lblAvatar, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("<html><div style='text-align:center'><b>" + v.getNombre() + "</b><br>"
                + v.getDocumento() + "<br>"
                + v.getEspecialidad() + "<br>"
                + v.getTelefono() + "</div></html>", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        card.add(lblInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton btnEliminar = new JButton(new ImageIcon(getClass().getResource("/trash.png")));
        btnEliminar.setToolTipText("Eliminar veterinario");
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar veterinario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                main.eliminarVeterinario(v); // Usa el metodo de Main
                JOptionPane.showMessageDialog(this, "Veterinario eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelBotones.add(btnEliminar);
        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    public Veterinario encontrarVeterinarioPorDoc(String documento) { // Metodo público
        for (Veterinario v : main.veterinarios) {
            if (v.getDocumento().equals(documento)) return v;
        }
        return null;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().toLowerCase();
        List<Veterinario> filtrados = new ArrayList<>();
        for (Veterinario v : main.veterinarios) { // Usa lista de Main
            if (v.getNombre().toLowerCase().contains(filtro) ||
                    v.getDocumento().contains(filtro) ||
                    v.getEspecialidad().toLowerCase().contains(filtro))
                filtrados.add(v);
        }
        actualizarCards(filtrados);
    }
}
