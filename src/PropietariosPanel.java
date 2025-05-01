import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PropietariosPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public PropietariosPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // ----- Barra superior: Título y barra de búsqueda
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());

        JLabel lblTitulo = new JLabel("Propietarios", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por nombre o documento"));
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Agregar Propietario");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.addActionListener(e -> dialogoAgregarPropietario());

        JPanel panelBusquedaBotones = new JPanel(new BorderLayout());
        panelBusquedaBotones.setBackground(getBackground());
        panelBusquedaBotones.add(txtBuscar, BorderLayout.CENTER);
        panelBusquedaBotones.add(btnAgregar, BorderLayout.EAST);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(panelBusquedaBotones, BorderLayout.CENTER);

        // ----- Panel central con scroll para las cards
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(getBackground());

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(getBackground());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ----- Panel inferior: Botón volver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(getBackground());
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome());
        bottomPanel.add(btnVolver);

        // Agregar paneles al layout principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        actualizarCards(main.propietarios);
    }

    private void dialogoAgregarPropietario() {
        JTextField txtDocumento = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JTextField txtTelefono = new JTextField(20);
        JTextField txtDireccion = new JTextField(20);
        JTextField txtCorreo = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Documento (*):"));
        panel.add(txtDocumento);
        panel.add(new JLabel("Nombre (*):"));
        panel.add(txtNombre);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Dirección:"));
        panel.add(txtDireccion);
        panel.add(new JLabel("Correo:"));
        panel.add(txtCorreo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Propietario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String doc = txtDocumento.getText().trim();
            String nom = txtNombre.getText().trim();

            if (doc.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Documento y Nombre son obligatorios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si ya existe un propietario con ese documento
            for (Propietario existente : main.propietarios) {
                if (existente.getDocumento().equals(doc)) {
                    JOptionPane.showMessageDialog(this, "Documento ya registrado.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Propietario nuevoPropietario = new Propietario(doc, nom,
                    txtTelefono.getText(), txtDireccion.getText(), txtCorreo.getText());
            main.agregarPropietario(nuevoPropietario);
            JOptionPane.showMessageDialog(this, "Propietario agregado con éxito.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() {
        actualizarCards(main.propietarios);
    }

    public void actualizarCards(List<Propietario> lista) {
        cardsPanel.removeAll();
        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("No hay propietarios registrados."));
        } else {
            for (Propietario p : lista) {
                cardsPanel.add(crearCardPropietario(p));
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardPropietario(Propietario p) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        // Aumentamos la altura para que quepa toda la info
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15))
        );

        JLabel lblAvatar = new JLabel(new ImageIcon(getClass().getResource("/user.png")), SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        card.add(lblAvatar, BorderLayout.WEST);

        // Contamos las mascotas del propietario
        int totalMascotas = 0;
        for (Mascota m : main.mascotas) {
            if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                totalMascotas++;
            }
        }

        // Panel con la información centrada
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);

        JLabel lblInfo = new JLabel("<html><div style='text-align: center;'>" +
                "<b>" + p.getNombre() + "</b><br>" +
                "Documento: " + p.getDocumento() + "<br>" +
                "Teléfono: " + p.getTelefono() + "<br>" +
                "Dirección: " + p.getDireccion() + "<br>" +
                "Correo: " + p.getCorreo() + "<br>" +
                "Mascotas: " + totalMascotas +
                "</div></html>", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(lblInfo);
        card.add(infoPanel, BorderLayout.CENTER);

        // Panel de botones a la derecha
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 0, 5));
        panelBotones.setOpaque(false);

        JButton btnVer = new JButton(new ImageIcon(getClass().getResource("/eye.png")));
        btnVer.setContentAreaFilled(false);
        btnVer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btnVer.addActionListener(e -> verDetallePropietario(p));

        JButton btnEliminar = new JButton(new ImageIcon(getClass().getResource("/trash.png")));
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btnEliminar.addActionListener(e -> eliminarPropietario(p));

        panelBotones.add(btnVer);
        panelBotones.add(btnEliminar);

        card.add(panelBotones, BorderLayout.EAST);

        return card;
    }

    // Método para mostrar detalles del propietario (mascotas incluidas)
    private void verDetallePropietario(Propietario p) {
        StringBuilder mascotas = new StringBuilder();
        for (Mascota m : main.mascotas) {
            if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                mascotas.append("• ").append(m.getNombre()).append(" (").append(m.getEspecie()).append(")<br>");
            }
        }

        String info = "<html><div style='text-align: center; width: 300px;'>" +
                "<h2>" + p.getNombre() + "</h2>" +
                "<br><b>Documento:</b> " + p.getDocumento() +
                "<br><b>Teléfono:</b> " + p.getTelefono() +
                "<br><b>Dirección:</b> " + p.getDireccion() +
                "<br><b>Correo:</b> " + p.getCorreo();

        if (mascotas.length() > 0) {
            info += "<br><br><b>Mascotas:</b><br>" + mascotas.toString();
        } else {
            info += "<br><br>No tiene mascotas registradas.";
        }

        info += "</div></html>";

        JOptionPane.showMessageDialog(this, info, "Información de Propietario", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para eliminar un propietario
    private void eliminarPropietario(Propietario p) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Se eliminará el propietario y TODAS sus mascotas.\n¿Está seguro?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            main.eliminarPropietario(p);
            JOptionPane.showMessageDialog(this, "Propietario eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método público para buscar propietarios (usado desde otros paneles)
    public Propietario encontrarPropietarioPorDoc(String documento) {
        for (Propietario p : main.propietarios) {
            if (p.getDocumento().equals(documento)) return p;
        }
        return null;
    }

    // Método para filtrar las cards según el texto de búsqueda
    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Propietario> filtrados = new ArrayList<>();

        for (Propietario p : main.propietarios) {
            if (filtro.isEmpty() ||
                    p.getNombre().toLowerCase().contains(filtro) ||
                    p.getDocumento().contains(filtro)) {
                filtrados.add(p);
            }
        }

        actualizarCards(filtrados);
    }


}

