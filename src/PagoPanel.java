import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class PagoPanel extends JPanel {
    private Main main;
    private Cita citaActual;
    private JLabel infoLabel;
    private JComboBox<String> comboMetodo;
    private JTextField txtDineroRecibido;
    private JLabel lblCambio;
    private JButton btnPagar, btnCancelar;
    private DecimalFormat formatoPesos = new DecimalFormat("$#,###");
    private int precio = 0;

    public PagoPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // TÍTULO DE LA SECCIÓN
        JLabel lblTitulo = new JLabel("Registro de Pago", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // PANEL PRINCIPAL (usa GridBagLayout para mayor flexibilidad)
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Panel para información de la cita
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(Color.WHITE);
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        infoLabel = new JLabel("", SwingConstants.LEFT);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInfo.add(infoLabel, BorderLayout.CENTER);
        gbc.insets = new Insets(10, 10, 20, 10);
        panelCentral.add(panelInfo, gbc);

        // PANEL DE PAGO
        JPanel panelPago = new JPanel(new GridBagLayout());
        panelPago.setBackground(Color.WHITE);
        panelPago.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbcPago = new GridBagConstraints();
        gbcPago.anchor = GridBagConstraints.WEST;
        gbcPago.insets = new Insets(5, 5, 5, 5);

        // Método de pago
        JLabel lblMetodo = new JLabel("Método de pago:");
        lblMetodo.setFont(new Font("Arial", Font.BOLD, 14));
        panelPago.add(lblMetodo, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.weightx = 1.0;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        comboMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        comboMetodo.setPreferredSize(new Dimension(150, 30));
        comboMetodo.addActionListener(e -> {
            boolean esEfectivo = "Efectivo".equals(comboMetodo.getSelectedItem());
            txtDineroRecibido.setEnabled(esEfectivo);
            if (!esEfectivo) {
                lblCambio.setText(formatoPesos.format(0));
                txtDineroRecibido.setText("");
            }
        });
        panelPago.add(comboMetodo, gbcPago);

        // Dinero recibido (solo visible si es efectivo)
        gbcPago.gridx = 0;
        gbcPago.gridy = 1;
        gbcPago.weightx = 0;
        gbcPago.fill = GridBagConstraints.NONE;
        JLabel lblDineroRecibido = new JLabel("Dinero recibido:");
        lblDineroRecibido.setFont(new Font("Arial", Font.BOLD, 14));
        panelPago.add(lblDineroRecibido, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.weightx = 1.0;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        txtDineroRecibido = new JTextField();
        txtDineroRecibido.setPreferredSize(new Dimension(150, 30));
        txtDineroRecibido.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
        });
        panelPago.add(txtDineroRecibido, gbcPago);

        // Cambio a devolver
        gbcPago.gridx = 0;
        gbcPago.gridy = 2;
        gbcPago.weightx = 0;
        gbcPago.fill = GridBagConstraints.NONE;
        JLabel lblCambioTitulo = new JLabel("Cambio a devolver:");
        lblCambioTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelPago.add(lblCambioTitulo, gbcPago);

        gbcPago.gridx = 1;
        gbcPago.weightx = 1.0;
        gbcPago.fill = GridBagConstraints.HORIZONTAL;
        lblCambio = new JLabel("$0");
        lblCambio.setFont(new Font("Arial", Font.BOLD, 16));
        lblCambio.setForeground(new Color(0, 128, 0));
        panelPago.add(lblCambio, gbcPago);

        gbc.insets = new Insets(0, 10, 20, 10);
        panelCentral.add(panelPago, gbc);

        // BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        btnPagar = new JButton("Realizar Pago");
        btnPagar.setFont(new Font("Arial", Font.BOLD, 14));
        btnPagar.setBackground(new Color(0, 128, 0));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setPreferredSize(new Dimension(150, 40));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(100, 40));

        panelBotones.add(btnCancelar);
        panelBotones.add(btnPagar);

        // AÑADE COMPONENTES AL PANEL PRINCIPAL
        add(lblTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // Event Listeners
        btnPagar.addActionListener(e -> {
            if (citaActual != null) {
                // Validación si es pago en efectivo
                if ("Efectivo".equals(comboMetodo.getSelectedItem())) {
                    try {
                        int dineroRecibido = Integer.parseInt(txtDineroRecibido.getText().trim());
                        if (dineroRecibido < precio) {
                            JOptionPane.showMessageDialog(this,
                                    "El dinero recibido es insuficiente para cubrir el costo.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Por favor, ingrese un valor numérico válido en el campo de dinero recibido.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Procede con el pago (lógica original)
                citaActual.setEstado("Pagada");
                main.citas.remove(citaActual);
                if (!main.citasPagadas.contains(citaActual))
                    main.citasPagadas.add(citaActual);
                try {
                    CitaDAO.guardar(citaActual);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (main.pagosPanel != null) main.pagosPanel.actualizarCards(main.citasPagadas);

                // Muestra factura de éxito con formato
                mostrarFacturaExito();

                main.citasPanel.actualizarCards(main.citas);
                main.cardLayout.show(main.mainPanel, "pagos");
            }
        });

        btnCancelar.addActionListener(e -> main.cardLayout.show(main.mainPanel, "citas"));
    }

    // Método para mostrar una factura con formato después del pago exitoso
    private void mostrarFacturaExito() {
        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(citaActual.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(citaActual.getIdMascota());

        StringBuilder factura = new StringBuilder("<html><div style='text-align:center; font-family:Arial;'>");
        factura.append("<h2 style='color:#2E4C96;'>¡Pago Realizado con Éxito!</h2>");
        factura.append("<div style='border:1px solid #ccc; padding:15px; margin:10px; background-color:#f8f8f8;'>");
        factura.append("<b>Cliente:</b> ").append(p != null ? p.getNombre() : "-").append("<br>");
        factura.append("<b>Mascota:</b> ").append(m != null ? m.getNombre() : "-").append("<br>");
        factura.append("<b>Servicio:</b> ").append(citaActual.getMotivo()).append("<br>");
        factura.append("<b>Fecha:</b> ").append(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(citaActual.getFechaHora())).append("<br>");
        factura.append("<b>Método de pago:</b> ").append(comboMetodo.getSelectedItem()).append("<br>");
        factura.append("<hr>");
        factura.append("<h3>Total pagado: ").append(formatoPesos.format(precio)).append("</h3>");

        // Si fue efectivo, muestra también el dinero recibido y el cambio
        if ("Efectivo".equals(comboMetodo.getSelectedItem())) {
            try {
                int dineroRecibido = Integer.parseInt(txtDineroRecibido.getText().trim());
                factura.append("Recibido: ").append(formatoPesos.format(dineroRecibido)).append("<br>");
                factura.append("Cambio: ").append(formatoPesos.format(dineroRecibido - precio)).append("<br>");
            } catch (NumberFormatException ignored) {}
        }

        factura.append("</div></div></html>");

        JOptionPane.showMessageDialog(
                this,
                factura.toString(),
                "Factura - Pago Realizado",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Método para calcular el cambio (cuando se ingresa dinero en efectivo)
    private void calcularCambio() {
        if ("Efectivo".equals(comboMetodo.getSelectedItem())) {
            try {
                int dineroRecibido = Integer.parseInt(txtDineroRecibido.getText().trim());
                int cambio = dineroRecibido - precio;
                lblCambio.setText(formatoPesos.format(cambio));
                lblCambio.setForeground(cambio >= 0 ? new Color(0, 128, 0) : Color.RED);
            } catch (NumberFormatException e) {
                lblCambio.setText("$0");
                lblCambio.setForeground(new Color(0, 128, 0));
            }
        }
    }

    public void setCita(Cita cita) {
        this.citaActual = cita;
        if (cita == null) {
            infoLabel.setText("<html><b>Seleccione una cita válida.</b></html>");
            btnPagar.setEnabled(false);
            txtDineroRecibido.setText("");
            lblCambio.setText("$0");
            return;
        }
        btnPagar.setEnabled(true);

        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario());

        String fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(cita.getFechaHora());
        String motivo = cita.getMotivo();
        precio = CitasPanel.precioPorTipoServicio(motivo);

        StringBuilder texto = new StringBuilder("<html>");
        texto.append("<div style='font-size: 16px;'><b style='color:#2E4C96;'>DETALLE DEL SERVICIO</b></div><br>");
        texto.append("<b>Mascota:</b> ").append(m != null ? m.getNombre() : "-").append("<br>");
        texto.append("<b>Propietario:</b> ").append(p != null ? p.getNombre() : "-").append("<br>");
        texto.append("<b>Veterinario:</b> ").append(v != null ? v.getNombre() : "-").append("<br>");
        texto.append("<b>Fecha y hora:</b> ").append(fecha).append("<br>");
        texto.append("<b>Motivo de consulta:</b> ").append(motivo).append("<br><br>");
        texto.append("<div style='font-size: 18px; color:#2E4C96;'><b>TOTAL A PAGAR: ").append(formatoPesos.format(precio)).append("</b></div>");
        texto.append("</html>");

        infoLabel.setText(texto.toString());

        // Reset campos de pago
        comboMetodo.setSelectedItem("Efectivo");
        txtDineroRecibido.setText("");
        lblCambio.setText("$0");
        txtDineroRecibido.setEnabled(true);
    }
}
