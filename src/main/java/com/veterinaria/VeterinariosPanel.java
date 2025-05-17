package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeterinariosPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public VeterinariosPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Barra superior -----
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Veterinarios", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 100));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        txtBuscar = new JTextField(25);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,200,230)), "Buscar (Nombre, Documento o Especialidad)"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Nuevo Veterinario");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAgregar.setBackground(new Color(60, 140, 200));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> dialogoAgregarVeterinario());

        JPanel panelBusquedaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusquedaBotones.setOpaque(false);
        panelBusquedaBotones.add(txtBuscar);
        panelBusquedaBotones.add(btnAgregar);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(panelBusquedaBotones, BorderLayout.CENTER);

        // ----- Panel central para cards -----
        cardsPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 2 columnas
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

    private void dialogoAgregarVeterinario() {
        JTextField txtDocumento = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JTextField txtEspecialidad = new JTextField(20);
        JTextField txtTelefono = new JTextField(20);
        JCheckBox chkDisponible = new JCheckBox("Disponible para citas", true); // Por defecto disponible

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Documento (*):"), gbc); panel.add(txtDocumento, gbc);
        panel.add(new JLabel("Nombre Completo (*):"), gbc); panel.add(txtNombre, gbc);
        panel.add(new JLabel("Especialidad:"), gbc); panel.add(txtEspecialidad, gbc);
        panel.add(new JLabel("Teléfono:"), gbc); panel.add(txtTelefono, gbc);
        gbc.fill = GridBagConstraints.NONE; // Para que el checkbox no se estire
        panel.add(chkDisponible, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Nuevo Veterinario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String doc = txtDocumento.getText().trim();
            String nom = txtNombre.getText().trim();
            String esp = txtEspecialidad.getText().trim();
            String tel = txtTelefono.getText().trim();
            boolean disp = chkDisponible.isSelected();

            if (doc.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El Documento y el Nombre son campos obligatorios.",
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (main.veterinarios != null) {
                for (Veterinario existente : main.veterinarios) {
                    if (existente.getDocumento().equals(doc)) {
                        JOptionPane.showMessageDialog(this, "Ya existe un veterinario con el documento: " + doc,
                                "Documento Duplicado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            Veterinario nuevoVeterinario = new Veterinario(doc, nom, esp, tel, disp);
            main.agregarVeterinario(nuevoVeterinario);
            JOptionPane.showMessageDialog(this, "Veterinario '" + nom + "' agregado exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() {
        actualizarCards(main.veterinarios);
    }

    public void actualizarCards(List<Veterinario> listaAMostrar) {
        cardsPanel.removeAll();
        if (listaAMostrar == null || listaAMostrar.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay veterinarios para mostrar.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(lblVacio, BorderLayout.CENTER);
        } else {
            cardsPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Veterinario v : listaAMostrar) {
                cardsPanel.add(crearCardVeterinario(v));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardVeterinario(Veterinario v) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(350, 140)); // Tamaño de card
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        try {
            URL imgUrl = getClass().getResource("/Veterinario.png"); // Usar imagen correcta
            if (imgUrl != null) lblAvatar.setIcon(new ImageIcon(imgUrl));
            else lblAvatar.setText("Vet");
        } catch (Exception e) { lblAvatar.setText("Err"); }
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        infoPanel.add(new JLabel("<html><b>" + v.getNombre() + "</b></html>")).setFont(new Font("Arial", Font.BOLD, 15));
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(new JLabel("Doc: " + v.getDocumento()));
        infoPanel.add(new JLabel("Especialidad: " + (v.getEspecialidad().isEmpty() ? "General" : v.getEspecialidad())));
        infoPanel.add(new JLabel("Tel: " + (v.getTelefono().isEmpty() ? "N/A" : v.getTelefono())));
        infoPanel.add(new JLabel("Disponible: " + (v.isDisponibilidad() ? "Sí" : "No")));

        JPanel centerAlignInfo = new JPanel(new GridBagLayout());
        centerAlignInfo.setOpaque(false);
        centerAlignInfo.add(infoPanel);
        card.add(centerAlignInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JButton btnEliminar = crearBotonIcono("/trash.png", "Eliminar Veterinario");
        btnEliminar.addActionListener(e -> eliminarVeterinario(v));

        panelBotones.add(btnEliminar);

        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    // Método utilitario para crear botones con icono
    private JButton crearBotonIcono(String pathIcono, String tooltip) {
        JButton boton = new JButton();
        try {
            URL imgUrl = getClass().getResource(pathIcono);
            if (imgUrl != null) {
                boton.setIcon(new ImageIcon(imgUrl));
            } else {
                boton.setText(tooltip.substring(0,1));
                System.err.println("Icono no encontrado para VeterinariosPanel: " + pathIcono);
            }
        } catch (Exception e) {
            boton.setText("Err");
            System.err.println("Error cargando icono " + pathIcono + " en VeterinariosPanel: " + e.getMessage());
        }
        boton.setToolTipText(tooltip);
        boton.setPreferredSize(new Dimension(38, 38));
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return boton;
    }

    private void eliminarVeterinario(Veterinario v) {
        // Verificar si el veterinario tiene citas pendientes
        boolean tieneCitasPendientes = false;
        if (main.citas != null) {
            for (Cita c : main.citas) {
                if (c.getDocVeterinario().equals(v.getDocumento()) && "PENDIENTE".equalsIgnoreCase(c.getEstado())) {
                    tieneCitasPendientes = true;
                    break;
                }
            }
        }

        if (tieneCitasPendientes) {
            JOptionPane.showMessageDialog(this,
                    "El veterinario '" + v.getNombre() + "' tiene citas PENDIENTES.\n" +
                            "No se puede eliminar hasta que las citas se completen o cancelen.",
                    "Eliminación Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al veterinario '" + v.getNombre() + "'?\n" +
                        "Se eliminarán también sus citas PAGADAS del historial.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                main.eliminarVeterinario(v);
                JOptionPane.showMessageDialog(this, "Veterinario y su historial eliminados correctamente.",
                        "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el veterinario: " + ex.getMessage(),
                        "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public Veterinario encontrarVeterinarioPorDoc(String documento) {
        if (main.veterinarios != null) {
            for (Veterinario v : main.veterinarios) {
                if (v.getDocumento().equals(documento)) return v;
            }
        }
        return null;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Veterinario> filtrados = new ArrayList<>();

        if (main.veterinarios != null) {
            for (Veterinario v : main.veterinarios) {
                if (filtro.isEmpty() ||
                        v.getNombre().toLowerCase().contains(filtro) ||
                        v.getDocumento().contains(filtro) ||
                        v.getEspecialidad().toLowerCase().contains(filtro)) {
                    filtrados.add(v);
                }
            }
        }
        actualizarCards(filtrados);
    }
}
