import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MascotasPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public MascotasPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // Barra superior: Título, barra de búsqueda y botón agregar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());

        JLabel lblTitulo = new JLabel("Mascotas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por nombre o propietario"));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Agregar Mascota");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.addActionListener(e -> dialogoAgregarMascota());

        JPanel searchAndBtn = new JPanel(new BorderLayout());
        searchAndBtn.setBackground(getBackground());
        searchAndBtn.add(txtBuscar, BorderLayout.CENTER);
        searchAndBtn.add(btnAgregar, BorderLayout.EAST);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(searchAndBtn, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Panel de cards
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(0, 2, 22, 16)); // Grid para cards en 2 columnas
        cardsPanel.setBackground(getBackground());
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        // Botón volver
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottom.setOpaque(false);
        bottom.add(btnVolver);
        add(bottom, BorderLayout.SOUTH);

        actualizarCards();
    }

    public void filtrarCards() {
        String texto = txtBuscar.getText().toLowerCase();
        if (texto.isBlank()) {
            actualizarCards();
            return;
        }

        List<Mascota> filtradas = new ArrayList<>();
        for (Mascota m : main.mascotas) {
            if (m.getNombre().toLowerCase().contains(texto)) {
                filtradas.add(m);
                continue;
            }

            // Buscar por propietario si no se encontró por nombre
            for (Propietario p : main.propietarios) {
                if (p.getDocumento().equals(m.getPropietarioDocumento()) && p.getNombre().toLowerCase().contains(texto)) {
                    filtradas.add(m);
                    break;
                }
            }
        }
        actualizarCards(filtradas);
    }

    private void dialogoAgregarMascota() {
        JTextField txtNombre = new JTextField();
        JTextField txtEspecie = new JTextField();
        JTextField txtRaza = new JTextField();
        JTextField txtEdad = new JTextField();
        JComboBox<Propietario> comboPropietario = new JComboBox<>(main.propietarios.toArray(new Propietario[0]));

        if (comboPropietario.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debes registrar al menos un propietario primero.", "Atención", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Especie:"));
        panel.add(txtEspecie);
        panel.add(new JLabel("Raza:"));
        panel.add(txtRaza);
        panel.add(new JLabel("Edad:"));
        panel.add(txtEdad);
        panel.add(new JLabel("Propietario:"));
        panel.add(comboPropietario);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Mascota", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText().trim();
                String especie = txtEspecie.getText().trim();
                String raza = txtRaza.getText().trim();
                int edad = Integer.parseInt(txtEdad.getText().trim());
                Propietario propietario = (Propietario) comboPropietario.getSelectedItem();

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Mascota mascota = new Mascota(nombre, especie, raza, edad, propietario.getDocumento());
                try {
                    main.agregarMascota(mascota);
                    JOptionPane.showMessageDialog(this, "Mascota agregada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al agregar mascota: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al agregar mascota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualizarCards() {
        actualizarCards(main.mascotas);
    }

    public void actualizarCards(List<Mascota> lista) {
        cardsPanel.removeAll();
        if (lista.isEmpty()) {
            cardsPanel.add(new JLabel("No hay mascotas registradas."));
        } else {
            for (Mascota m : lista) {
                cardsPanel.add(crearCardMascota(m));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardMascota(Mascota m) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setPreferredSize(new Dimension(320, 140));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Avatar/imagen
        java.net.URL imgUrl = getClass().getResource("/Mascotas.png");
        ImageIcon icon = null;
        if (imgUrl != null) {
            icon = new ImageIcon(imgUrl);
        } else {
            icon = new ImageIcon(); // Icono vacío si no encuentra la imagen
        }
        JLabel lblAvatar = new JLabel(icon, SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        card.add(lblAvatar, BorderLayout.WEST);

        // Info
        String propietarioNombre = "Sin propietario";
        for (Propietario p : main.propietarios) {
            if (p.getDocumento().equals(m.getPropietarioDocumento())) {
                propietarioNombre = p.getNombre();
                break;
            }
        }

        // Panel de info centralizado
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel("<html><b>" + m.getNombre() + "</b></html>");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblDetalles = new JLabel("<html>" +
                "Especie: " + m.getEspecie() + "<br>" +
                "Raza: " + m.getRaza() + "<br>" +
                "Edad: " + m.getEdad() + " años<br>" +
                "Propietario: " + propietarioNombre +
                "</html>");

        info.add(Box.createVerticalGlue());
        info.add(lblNombre);
        info.add(Box.createVerticalStrut(5));
        info.add(lblDetalles);
        info.add(Box.createVerticalGlue());

        card.add(info, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel();
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setOpaque(false);

        // Botón eliminar
        JButton btnDel = new JButton(new ImageIcon(getClass().getResource("/trash.png")));
        btnDel.setPreferredSize(new Dimension(35, 35));
        btnDel.setMaximumSize(new Dimension(35, 35));
        btnDel.setToolTipText("Eliminar mascota");
        btnDel.addActionListener(e -> eliminarMascota(m));

        // NUEVO: Botón para ver historial médico
        JButton btnHistorial = new JButton(new ImageIcon(getClass().getResource("/Historial.png")));
        btnHistorial.setPreferredSize(new Dimension(35, 35));
        btnHistorial.setMaximumSize(new Dimension(35, 35));
        btnHistorial.setToolTipText("Ver historial médico");
        btnHistorial.addActionListener(e -> mostrarHistorialMascota(m));

        botones.add(btnHistorial);
        botones.add(Box.createVerticalStrut(10));
        botones.add(btnDel);
        botones.add(Box.createVerticalGlue());

        card.add(botones, BorderLayout.EAST);
        return card;
    }

    private void eliminarMascota(Mascota m) {
        // Verificar si la mascota tiene citas
        boolean tieneCitas = false;
        for (Cita c : main.citas) {
            if (c.getIdMascota().equals(m.getId())) {
                tieneCitas = true;
                break;
            }
        }
        for (Cita c : main.citasPagadas) {
            if (c.getIdMascota().equals(m.getId())) {
                tieneCitas = true;
                break;
            }
        }

        // Si tiene citas, advertir al usuario
        if (tieneCitas) {
            JOptionPane.showMessageDialog(this,
                    "Esta mascota tiene citas asignadas. No se puede eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la mascota " + m.getNombre() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                main.eliminarMascota(m);
                JOptionPane.showMessageDialog(this, "Mascota eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar la mascota: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // NUEVO - Método para mostrar historial médico de una mascota
    private void mostrarHistorialMascota(Mascota mascota) {
        // Filtrar citas pagadas de esta mascota
        List<Cita> citasDeMascota = new ArrayList<>();
        for (Cita c : main.citasPagadas) {
            if (c.getIdMascota().equals(mascota.getId())) {
                citasDeMascota.add(c);
            }
        }

        // Si no hay historial, mostrar mensaje
        if (citasDeMascota.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay historial médico para esta mascota.",
                    "Sin historial",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Crear panel para el historial médico
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setPreferredSize(new Dimension(600, 400));

        // Título con info de la mascota
        JLabel lblTitulo = new JLabel("Historial Médico: " + mascota.getNombre());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        historialPanel.add(lblTitulo, BorderLayout.NORTH);

        // Tabla para mostrar el historial
        String[] columnNames = {"Fecha", "Veterinario", "Servicio", "Precio"};
        Object[][] data = new Object[citasDeMascota.size()][4];

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");

        for (int i = 0; i < citasDeMascota.size(); i++) {
            Cita cita = citasDeMascota.get(i);

            // 1. Fecha
            data[i][0] = sdf.format(cita.getFechaHora());

            // 2. Veterinario
            String nombreVeterinario = "Desconocido";
            for (Veterinario v : main.veterinarios) {
                if (v.getDocumento().equals(cita.getDocVeterinario())) {
                    nombreVeterinario = v.getNombre();
                    break;
                }
            }
            data[i][1] = nombreVeterinario;

            // 3. Servicio/Motivo
            data[i][2] = cita.getMotivo();

            // 4. Precio
            data[i][3] = "$" + CitasPanel.precioPorTipoServicio(cita.getMotivo());
        }

        JTable tabla = new JTable(data, columnNames);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollTabla = new JScrollPane(tabla);
        historialPanel.add(scrollTabla, BorderLayout.CENTER);

        // Mostrar el panel en un JDialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historial Médico", true);
        dialog.setContentPane(historialPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public Mascota encontrarMascotaPorId(String idMascota) {
        for (Mascota m : main.mascotas) {
            if (m.getId().equals(idMascota)) return m;
        }
        return null;
    }
}
