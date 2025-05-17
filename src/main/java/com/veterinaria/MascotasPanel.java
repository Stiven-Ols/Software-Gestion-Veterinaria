package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // Necesario para el historial
import java.util.List;

public class MascotasPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public MascotasPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Barra superior -----
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Mascotas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 100));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        txtBuscar = new JTextField(25);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,200,230)), "Buscar (Nombre Mascota o Nombre Propietario)"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Nueva Mascota");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAgregar.setBackground(new Color(60, 140, 200));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> dialogoAgregarMascota());

        JPanel panelBusquedaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusquedaBotones.setOpaque(false);
        panelBusquedaBotones.add(txtBuscar);
        panelBusquedaBotones.add(btnAgregar);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(panelBusquedaBotones, BorderLayout.CENTER);

        // ----- Panel central para cards -----
        cardsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ----- Panel inferior -----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton btnVolver = new JButton("Volver al Inicio");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 13));
        btnVolver.addActionListener(e -> main.irHome());
        bottomPanel.add(btnVolver);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        actualizarCards();
    }

    private void dialogoAgregarMascota() {
        if (main.propietarios == null || main.propietarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar al menos un propietario antes de agregar mascotas.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtNombre = new JTextField(20);
        JTextField txtEspecie = new JTextField(20);
        JTextField txtRaza = new JTextField(20);
        JTextField txtEdad = new JTextField(5); // Más corto para edad
        JComboBox<Propietario> comboPropietario = new JComboBox<>(main.propietarios.toArray(new Propietario[0]));
        comboPropietario.setRenderer(new DefaultListCellRenderer() { // Para mostrar el nombre del propietario
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Propietario) {
                    setText(((Propietario) value).getNombre());
                }
                return this;
            }
        });


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Nombre Mascota (*):"), gbc); panel.add(txtNombre, gbc);
        panel.add(new JLabel("Especie:"), gbc); panel.add(txtEspecie, gbc);
        panel.add(new JLabel("Raza:"), gbc); panel.add(txtRaza, gbc);
        panel.add(new JLabel("Edad (años):"), gbc); panel.add(txtEdad, gbc);
        panel.add(new JLabel("Propietario (*):"), gbc); panel.add(comboPropietario, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Nueva Mascota",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String especie = txtEspecie.getText().trim();
            String raza = txtRaza.getText().trim();
            String edadStr = txtEdad.getText().trim();
            Propietario propietarioSeleccionado = (Propietario) comboPropietario.getSelectedItem();

            if (nombre.isEmpty() || propietarioSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "El Nombre de la mascota y la selección del Propietario son obligatorios.",
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int edad;
            try {
                edad = edadStr.isEmpty() ? 0 : Integer.parseInt(edadStr); // Edad 0 si está vacío
                if (edad < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La Edad debe ser un número entero no negativo.",
                        "Error en Edad", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Mascota nuevaMascota = new Mascota(nombre, especie, raza, edad, propietarioSeleccionado.getDocumento());
            main.agregarMascota(nuevaMascota);
            JOptionPane.showMessageDialog(this, "Mascota '" + nombre + "' agregada exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() {
        actualizarCards(main.mascotas);
    }

    public void actualizarCards(List<Mascota> listaAMostrar) {
        cardsPanel.removeAll();
        if (listaAMostrar == null || listaAMostrar.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay mascotas para mostrar.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(lblVacio, BorderLayout.CENTER);
        } else {
            cardsPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Mascota m : listaAMostrar) {
                cardsPanel.add(crearCardMascota(m));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardMascota(Mascota m) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(350, 150)); // Tamaño de card
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        try {
            URL imgUrl = getClass().getResource("/Mascotas.png"); // Usar la imagen correcta
            if (imgUrl != null) lblAvatar.setIcon(new ImageIcon(imgUrl));
            else lblAvatar.setText("Pet");
        } catch (Exception e) { lblAvatar.setText("Err"); }
        card.add(lblAvatar, BorderLayout.WEST);

        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(m.getPropietarioDocumento());
        String nombrePropietario = (p != null) ? p.getNombre() : "No asignado";

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        infoPanel.add(new JLabel("<html><b>" + m.getNombre() + "</b></html>")).setFont(new Font("Arial", Font.BOLD, 15));
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(new JLabel("Especie: " + (m.getEspecie().isEmpty() ? "N/A" : m.getEspecie())));
        infoPanel.add(new JLabel("Raza: " + (m.getRaza().isEmpty() ? "N/A" : m.getRaza())));
        infoPanel.add(new JLabel("Edad: " + m.getEdad() + " años"));
        infoPanel.add(new JLabel("Propietario: " + nombrePropietario));

        JPanel centerAlignInfo = new JPanel(new GridBagLayout());
        centerAlignInfo.setOpaque(false);
        centerAlignInfo.add(infoPanel);
        card.add(centerAlignInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JButton btnHistorial = crearBotonIcono("/Historial.png", "Ver Historial Médico");
        btnHistorial.addActionListener(e -> mostrarHistorialMascota(m));

        JButton btnEliminar = crearBotonIcono("/trash.png", "Eliminar Mascota");
        btnEliminar.addActionListener(e -> eliminarMascota(m));

        panelBotones.add(btnHistorial);
        panelBotones.add(Box.createVerticalStrut(5));
        panelBotones.add(btnEliminar);

        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    // Método utilitario para crear botones con icono (igual que en PropietariosPanel)
    private JButton crearBotonIcono(String pathIcono, String tooltip) {
        JButton boton = new JButton();
        try {
            URL imgUrl = getClass().getResource(pathIcono);
            if (imgUrl != null) {
                boton.setIcon(new ImageIcon(imgUrl));
            } else {
                boton.setText(tooltip.substring(0,1));
                System.err.println("Icono no encontrado para MascotasPanel: " + pathIcono);
            }
        } catch (Exception e) {
            boton.setText("Err");
            System.err.println("Error cargando icono " + pathIcono + " en MascotasPanel: " + e.getMessage());
        }
        boton.setToolTipText(tooltip);
        boton.setPreferredSize(new Dimension(38, 38));
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return boton;
    }


    private void eliminarMascota(Mascota m) {
        // Verificar si la mascota tiene citas pendientes
        boolean tieneCitasPendientes = false;
        if(main.citas != null) {
            for (Cita c : main.citas) {
                if (c.getIdMascota().equals(m.getId()) && "PENDIENTE".equalsIgnoreCase(c.getEstado())) {
                    tieneCitasPendientes = true;
                    break;
                }
            }
        }

        if (tieneCitasPendientes) {
            JOptionPane.showMessageDialog(this,
                    "La mascota '" + m.getNombre() + "' tiene citas PENDIENTES.\n" +
                            "No se puede eliminar hasta que las citas se completen o cancelen.",
                    "Eliminación Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la mascota '" + m.getNombre() + "'?\n" +
                        "Se eliminarán también sus citas PAGADAS del historial.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                main.eliminarMascota(m); // Llama al método centralizado en Main
                JOptionPane.showMessageDialog(this, "Mascota y su historial eliminados correctamente.",
                        "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la mascota: " + ex.getMessage(),
                        "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void mostrarHistorialMascota(Mascota mascota) {
        List<Cita> historial = new ArrayList<>();
        if (main.citasPagadas != null) { // Asegurar que citasPagadas no sea null
            for (Cita c : main.citasPagadas) {
                if (c.getIdMascota().equals(mascota.getId())) {
                    historial.add(c);
                }
            }
        }

        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La mascota '" + mascota.getNombre() + "' no tiene historial médico registrado.",
                    "Sin Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Ordenar historial por fecha (más reciente primero)
        historial.sort((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()));

        JPanel panelHistorial = new JPanel(new BorderLayout(10,10));
        panelHistorial.setPreferredSize(new Dimension(650, 450)); // Tamaño del diálogo
        panelHistorial.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel lblTituloHistorial = new JLabel("Historial Médico de: " + mascota.getNombre(), SwingConstants.CENTER);
        lblTituloHistorial.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloHistorial.setForeground(new Color(25, 55, 100));
        panelHistorial.add(lblTituloHistorial, BorderLayout.NORTH);

        String[] columnas = {"Fecha y Hora", "Veterinario", "Motivo/Servicio", "Precio"};
        Object[][] data = new Object[historial.size()][4];
        SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        SimpleDateFormat sdfHora = new SimpleDateFormat("hh:mm a");

        for (int i = 0; i < historial.size(); i++) {
            Cita cita = historial.get(i);
            data[i][0] = sdfDisplay.format(cita.getFechaHora());
            Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario());
            data[i][1] = (v != null) ? v.getNombre() : "N/A";
            data[i][2] = cita.getMotivo().getDescripcion();
            data[i][3] = "$" + CitasPanel.precioPorTipoServicio(cita.getMotivo().name()); // Usar el método estático
        }

        JTable tablaHistorial = new JTable(data, columnas);
        tablaHistorial.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaHistorial.setRowHeight(28);
        tablaHistorial.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tablaHistorial.getTableHeader().setBackground(new Color(220, 230, 240));

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        panelHistorial.add(scrollTabla, BorderLayout.CENTER);

        JDialog dialogoHistorial = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historial Médico", true);
        dialogoHistorial.setContentPane(panelHistorial);
        dialogoHistorial.pack();
        dialogoHistorial.setLocationRelativeTo(this);
        dialogoHistorial.setVisible(true);
    }

    public Mascota encontrarMascotaPorId(String idMascota) {
        if (main.mascotas != null) {
            for (Mascota m : main.mascotas) {
                if (m.getId().equals(idMascota)) return m;
            }
        }
        return null;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Mascota> filtradas = new ArrayList<>();

        if (main.mascotas != null) {
            for (Mascota m : main.mascotas) {
                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(m.getPropietarioDocumento());
                String nombrePropietario = (p != null) ? p.getNombre().toLowerCase() : "";

                if (filtro.isEmpty() ||
                        m.getNombre().toLowerCase().contains(filtro) ||
                        nombrePropietario.contains(filtro)) {
                    filtradas.add(m);
                }
            }
        }
        actualizarCards(filtradas);
    }
}
