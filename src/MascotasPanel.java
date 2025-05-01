import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class MascotasPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public MascotasPanel(Main main) { // Constructor solo recibe Main
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // --- Barra superior ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblTitulo = new JLabel("Mascotas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        // ... (configuración del título) ...
        topPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setBackground(getBackground());
        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por nombre o propietario"));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });
        searchBarPanel.add(txtBuscar, BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar Mascota");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.addActionListener(e -> dialogoAgregarMascota());
        searchBarPanel.add(btnAgregar, BorderLayout.EAST);
        topPanel.add(searchBarPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- Panel de Cards ---
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(new Color(245, 250, 255));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scrollCards = new JScrollPane(cardsPanel);
        scrollCards.setBorder(BorderFactory.createEmptyBorder());
        add(scrollCards, BorderLayout.CENTER);

        // --- Botón Volver ---
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome()); // Llama a Main
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(getBackground());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
        panelInferior.add(btnVolver);
        add(panelInferior, BorderLayout.SOUTH);

        actualizarCards(); // Carga inicial
    }

    private void dialogoAgregarMascota() {
        if (main.propietarios.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Primero debe registrar un propietario.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField txtNombre = new JTextField();
        JTextField txtEspecie = new JTextField();
        JTextField txtRaza = new JTextField();
        JTextField txtEdad = new JTextField();
        JComboBox<Propietario> comboPropietario = new JComboBox<>(main.propietarios.toArray(new Propietario[0])); // Usa lista de Main

        JPanel p = new JPanel(new GridLayout(0, 2, 15, 4)); // GridLayout para mejor alineación
        p.add(new JLabel("Nombre:"));   p.add(txtNombre);
        p.add(new JLabel("Especie:"));  p.add(txtEspecie);
        p.add(new JLabel("Raza:"));     p.add(txtRaza);
        p.add(new JLabel("Edad:"));     p.add(txtEdad);
        p.add(new JLabel("Propietario:")); p.add(comboPropietario);

        int r = JOptionPane.showConfirmDialog(this, p, "Agregar Mascota", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            try {
                int edad = Integer.parseInt(txtEdad.getText().trim());
                Propietario propSel = (Propietario) comboPropietario.getSelectedItem();
                if (propSel == null) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar un propietario.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Usar constructor SIN ID, ya que es autogenerado en el modelo
                Mascota m = new Mascota(
                        txtNombre.getText().trim(), txtEspecie.getText().trim(), txtRaza.getText().trim(), edad, propSel.getDocumento()
                );
                main.agregarMascota(m); // Usa el método de Main para persistir
                JOptionPane.showMessageDialog(this, "Mascota agregada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Edad inválida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualizarCards() { // Público, usa lista de main
        actualizarCards(main.mascotas);
    }

    public void actualizarCards(List<Mascota> list) {
        cardsPanel.removeAll();
        if(list.isEmpty()){
            cardsPanel.add(new JLabel("No hay mascotas registradas."));
        } else {
            for (Mascota m : list) {
                cardsPanel.add(crearCardMascota(m));
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardMascota(Mascota m) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        JLabel lblAvatar = new JLabel(new ImageIcon(getClass().getResource("/huella.png")), SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(70, 70));
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(Box.createVerticalGlue());
        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(m.getPropietarioDocumento());
        String nombreProp = (p != null) ? p.getNombre() : "-";
        infoPanel.add(new JLabel("<html><b>" + m.getNombre() + "</b></html>"));
        infoPanel.add(new JLabel("Especie: " + m.getEspecie()));
        infoPanel.add(new JLabel("Raza: " + m.getRaza()));
        infoPanel.add(new JLabel("Edad: " + m.getEdad()));
        infoPanel.add(new JLabel("Propietario: " + nombreProp));
        infoPanel.add(Box.createVerticalGlue());
        card.add(infoPanel, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.add(Box.createVerticalGlue());
        JButton btnEliminar = new JButton(new ImageIcon(getClass().getResource("/trash.png")));
        btnEliminar.setToolTipText("Eliminar mascota");
        btnEliminar.setMargin(new Insets(2,2,2,2));
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar mascota y sus citas asociadas?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                main.eliminarMascota(m); // Llama al método de Main
                JOptionPane.showMessageDialog(this, "Mascota eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelBotones.add(btnEliminar);
        panelBotones.add(Box.createVerticalGlue());
        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    public Mascota encontrarMascotaPorId(String idMascota) { // Público
        for (Mascota m : main.mascotas) {
            if (m.getId().equals(idMascota)) return m;
        }
        return null;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Mascota> filtrados = new ArrayList<>();
        for (Mascota m : main.mascotas) { // Usa lista de Main
            Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(m.getPropietarioDocumento());
            String nombreProp = (p != null) ? p.getNombre().toLowerCase() : "";
            if (filtro.isEmpty() || m.getNombre().toLowerCase().contains(filtro) || nombreProp.contains(filtro)) {
                filtrados.add(m);
            }
        }
        actualizarCards(filtrados);
    }
}
