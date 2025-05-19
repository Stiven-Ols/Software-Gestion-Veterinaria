package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropietariosPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public PropietariosPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 245, 250)); // Color de fondo general
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen

        // ----- Barra superior: Título, búsqueda y botón agregar -----
        JPanel topPanel = new JPanel(new BorderLayout(15, 0)); // Espacio entre componentes
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Propietarios", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 100));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0)); // Espacio inferior

        txtBuscar = new JTextField(25); // Ancho del campo de búsqueda
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,200,230)), "Buscar (Nombre o Documento)"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgregar = new JButton("Nuevo Propietario");
        btnAgregar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnAgregar.setBackground(new Color(60, 140, 200, 255));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.addActionListener(e -> dialogoAgregarPropietario());

        JPanel panelBusquedaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Alineación y espaciado
        panelBusquedaBotones.setOpaque(false);
        panelBusquedaBotones.add(txtBuscar);
        panelBusquedaBotones.add(btnAgregar);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(panelBusquedaBotones, BorderLayout.CENTER);

        // ----- Panel central con scroll para las cards -----
        // Usar GridLayout para las cards, 0 filas significa que se ajusta, 2 columnas
        cardsPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // gap horizontal y vertical
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240))); // Borde suave para el scroll
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave

        // ----- Panel inferior: Botón volver -----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton btnVolver = new JButton("Volver al Inicio");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 13));
        btnVolver.addActionListener(e -> main.irHome());
        bottomPanel.add(btnVolver);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        actualizarCards(); // Carga inicial
    }

    private void dialogoAgregarPropietario() {
        JTextField txtDocumento = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JTextField txtTelefono = new JTextField(20);
        JTextField txtDireccion = new JTextField(20);
        JTextField txtCorreo = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Documento (*):"), gbc); panel.add(txtDocumento, gbc);
        panel.add(new JLabel("Nombre Completo (*):"), gbc); panel.add(txtNombre, gbc);
        panel.add(new JLabel("Teléfono:"), gbc); panel.add(txtTelefono, gbc);
        panel.add(new JLabel("Dirección:"), gbc); panel.add(txtDireccion, gbc);
        panel.add(new JLabel("Correo Electrónico:"), gbc); panel.add(txtCorreo, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Nuevo Propietario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String doc = txtDocumento.getText().trim();
            String nom = txtNombre.getText().trim();
            String tel = txtTelefono.getText().trim();
            String dir = txtDireccion.getText().trim();
            String cor = txtCorreo.getText().trim();

            if (doc.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El Documento y el Nombre son campos obligatorios.",
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Propietario existente : main.propietarios) {
                if (existente.getDocumento().equals(doc)) {
                    JOptionPane.showMessageDialog(this, "Ya existe un propietario con el documento: " + doc,
                            "Documento Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Propietario nuevoPropietario = new Propietario(doc, nom, tel, dir, cor);
            main.agregarPropietario(nuevoPropietario); // Llama al metodo de Main para agregar y persistir
            JOptionPane.showMessageDialog(this, "Propietario '" + nom + "' agregado exitosamente.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() {
        actualizarCards(main.propietarios); // Siempre usa la lista de Main
    }

    public void actualizarCards(List<Propietario> listaAMostrar) {
        cardsPanel.removeAll();
        if (listaAMostrar == null || listaAMostrar.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay propietarios para mostrar.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            cardsPanel.setLayout(new BorderLayout()); // Cambiar layout para centrar el mensaje
            cardsPanel.add(lblVacio, BorderLayout.CENTER);
        } else {
            cardsPanel.setLayout(new GridLayout(0, 2, 15, 15)); // Restaurar GridLayout
            for (Propietario p : listaAMostrar) {
                cardsPanel.add(crearCardPropietario(p));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardPropietario(Propietario p) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(350, 160)); // Altura para más info
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1), // Borde más suave
                BorderFactory.createEmptyBorder(12, 15, 12, 15)) // Padding interno
        );

        // Avatar
        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        try {
            URL imgUrl = getClass().getResource("/user.png");
            if (imgUrl != null) lblAvatar.setIcon(new ImageIcon(imgUrl));
            else lblAvatar.setText(":)");
        } catch (Exception e) { lblAvatar.setText("Err");}
        card.add(lblAvatar, BorderLayout.WEST);

        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // Vertical para la info

        int totalMascotas = 0;
        if (main.mascotas != null) { // Asegurar que la lista de mascotas no sea null
            for (Mascota m : main.mascotas) {
                if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                    totalMascotas++;
                }
            }
        }

        infoPanel.add(new JLabel("<html><b>" + p.getNombre() + "</b></html>")).setFont(new Font("Arial", Font.BOLD, 15));
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(new JLabel("Doc: " + p.getDocumento()));
        infoPanel.add(new JLabel("Tel: " + (p.getTelefono().isEmpty() ? "N/A" : p.getTelefono())));
        infoPanel.add(new JLabel("Dir: " + (p.getDireccion().isEmpty() ? "N/A" : p.getDireccion())));
        infoPanel.add(new JLabel("Correo: " + (p.getCorreo().isEmpty() ? "N/A" : p.getCorreo())));
        infoPanel.add(new JLabel("Mascotas: " + totalMascotas));

        // Alinear al centro verticalmente usando un panel intermedio con GridBagLayout
        JPanel centerAlignInfo = new JPanel(new GridBagLayout());
        centerAlignInfo.setOpaque(false);
        centerAlignInfo.add(infoPanel); // Agrega el panel de info al panel de centrado

        card.add(centerAlignInfo, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS)); // Botones en columna
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));


        JButton btnVer = crearBotonIcono("/eye.png", "Ver Detalles");
        btnVer.addActionListener(e -> verDetallePropietario(p));

        JButton btnEliminar = crearBotonIcono("/trash.png", "Eliminar Propietario");
        btnEliminar.addActionListener(e -> eliminarPropietario(p));

        panelBotones.add(btnVer);
        panelBotones.add(Box.createVerticalStrut(5)); // Espacio entre botones
        panelBotones.add(btnEliminar);

        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    // Metodo para crear botones con icono
    private JButton crearBotonIcono(String pathIcono, String tooltip) {
        JButton boton = new JButton();
        try {
            URL imgUrl = getClass().getResource(pathIcono);
            if (imgUrl != null) {
                boton.setIcon(new ImageIcon(imgUrl));
            } else {
                boton.setText(tooltip.substring(0,1)); // Inicial si no hay icono
                System.err.println("Icono no encontrado: " + pathIcono);
            }
        } catch (Exception e) {
            boton.setText("Err");
            System.err.println("Error cargando icono " + pathIcono + ": " + e.getMessage());
        }
        boton.setToolTipText(tooltip);
        boton.setPreferredSize(new Dimension(38, 38)); // Tamaño fijo para botones de icono
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return boton;
    }

    private void verDetallePropietario(Propietario p) {
        StringBuilder mascotasStr = new StringBuilder();
        int count = 0;
        if (main.mascotas != null) {
            for (Mascota m : main.mascotas) {
                if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                    mascotasStr.append("  • ").append(m.getNombre()).append(" (").append(m.getEspecie()).append(" - ").append(m.getRaza()).append(")<br>");
                    count++;
                }
            }
        }
        if (count == 0) mascotasStr.append("  (No tiene mascotas registradas)");

        String info = "<html><body style='width: 350px; font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #193764;'>" + p.getNombre() + "</h2>" +
                "<p><b>Documento:</b> " + p.getDocumento() + "</p>" +
                "<p><b>Teléfono:</b> " + (p.getTelefono().isEmpty() ? "No registrado" : p.getTelefono()) + "</p>" +
                "<p><b>Dirección:</b> " + (p.getDireccion().isEmpty() ? "No registrada" : p.getDireccion()) + "</p>" +
                "<p><b>Correo:</b> " + (p.getCorreo().isEmpty() ? "No registrado" : p.getCorreo()) + "</p>" +
                "<hr><p><b>Mascotas Registradas ("+count+"):</b></p>" +
                "<div>" + mascotasStr.toString() + "</div>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, info, "Detalles del Propietario", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarPropietario(Propietario p) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al propietario '" + p.getNombre() + "'?\n" +
                        "Esta acción también eliminará TODAS sus mascotas y las citas asociadas.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                main.eliminarPropietario(p); // Llama al metodo centralizado en Main
                JOptionPane.showMessageDialog(this, "Propietario y sus datos asociados eliminados correctamente.",
                        "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el propietario: " + ex.getMessage(),
                        "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public Propietario encontrarPropietarioPorDoc(String documento) {
        if (main.propietarios != null) {
            for (Propietario p : main.propietarios) {
                if (p.getDocumento().equals(documento)) return p;
            }
        }
        return null;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Propietario> filtrados = new ArrayList<>();

        if (main.propietarios != null) {
            for (Propietario p : main.propietarios) {
                if (filtro.isEmpty() ||
                        p.getNombre().toLowerCase().contains(filtro) ||
                        p.getDocumento().contains(filtro)) {
                    filtrados.add(p);
                }
            }
        }
        actualizarCards(filtrados);
    }
}
