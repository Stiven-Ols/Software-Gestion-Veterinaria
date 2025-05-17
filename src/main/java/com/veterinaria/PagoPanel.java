package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date; // Necesario

public class PagoPanel extends JPanel {
    private Main main;
    private Cita citaActual;

    private JLabel lblInfoCita;
    private JComboBox<String> comboMetodoPago;
    private JTextField txtMontoRecibido;
    private JLabel lblCambioADevolver;
    private JButton btnPagar, btnCancelar;

    private DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00"); // Formato para pesos
    private int precioTotalCita = 0;

    public PagoPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(15, 15)); // Espaciado general
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Margen general

        // ----- Título -----
        JLabel lblTituloPanel = new JLabel("Procesar Pago de Cita", SwingConstants.CENTER);
        lblTituloPanel.setFont(new Font("Arial", Font.BOLD, 24));
        lblTituloPanel.setForeground(new Color(25, 55, 100));
        lblTituloPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(lblTituloPanel, BorderLayout.NORTH);

        // ----- Panel Central para Detalles y Formulario de Pago -----
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10); // Espaciado entre componentes
        gbc.weightx = 1.0; // Permitir que los componentes se expandan horizontalmente

        // --- Sección de Información de la Cita ---
        JPanel panelInfoCita = new JPanel(new BorderLayout());
        panelInfoCita.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)), "Detalles de la Cita a Pagar"));
        panelInfoCita.setOpaque(false);
        lblInfoCita = new JLabel("Cargando información...", SwingConstants.LEFT);
        lblInfoCita.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfoCita.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panelInfoCita.add(lblInfoCita, BorderLayout.CENTER);
        gbc.ipady = 20; // Más altura para este panel
        panelCentral.add(panelInfoCita, gbc);
        gbc.ipady = 0; // Resetear altura extra

        // --- Sección de Formulario de Pago ---
        JPanel panelFormularioPago = new JPanel(new GridBagLayout());
        panelFormularioPago.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)), "Formulario de Pago"));
        panelFormularioPago.setOpaque(false);
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.anchor = GridBagConstraints.WEST;
        gbcForm.insets = new Insets(5, 8, 5, 8);

        // Método de Pago
        gbcForm.gridx = 0; gbcForm.gridy = 0;
        panelFormularioPago.add(new JLabel("Método de Pago:"), gbcForm);
        gbcForm.gridx = 1; gbcForm.fill = GridBagConstraints.HORIZONTAL; gbcForm.weightx = 1.0;
        comboMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Crédito/Débito", "Transferencia"});
        comboMetodoPago.setFont(new Font("Arial", Font.PLAIN, 13));
        comboMetodoPago.addActionListener(e -> actualizarCamposSegunMetodo());
        panelFormularioPago.add(comboMetodoPago, gbcForm);
        gbcForm.weightx = 0; gbcForm.fill = GridBagConstraints.NONE; // Resetear

        // Monto Recibido (solo para efectivo)
        gbcForm.gridx = 0; gbcForm.gridy = 1;
        panelFormularioPago.add(new JLabel("Monto Recibido (Efectivo):"), gbcForm);
        gbcForm.gridx = 1; gbcForm.fill = GridBagConstraints.HORIZONTAL; gbcForm.weightx = 1.0;
        txtMontoRecibido = new JTextField(10);
        txtMontoRecibido.setFont(new Font("Arial", Font.PLAIN, 13));
        txtMontoRecibido.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcularYMostrarCambio(); }
            public void removeUpdate(DocumentEvent e) { calcularYMostrarCambio(); }
            public void changedUpdate(DocumentEvent e) { calcularYMostrarCambio(); }
        });
        panelFormularioPago.add(txtMontoRecibido, gbcForm);
        gbcForm.weightx = 0; gbcForm.fill = GridBagConstraints.NONE;

        // Cambio a Devolver
        gbcForm.gridx = 0; gbcForm.gridy = 2;
        panelFormularioPago.add(new JLabel("Cambio a Devolver:"), gbcForm);
        gbcForm.gridx = 1;
        lblCambioADevolver = new JLabel(formatoMoneda.format(0));
        lblCambioADevolver.setFont(new Font("Arial", Font.BOLD, 14));
        lblCambioADevolver.setForeground(new Color(0, 100, 0)); // Verde oscuro
        panelFormularioPago.add(lblCambioADevolver, gbcForm);

        panelCentral.add(panelFormularioPago, gbc);
        add(panelCentral, BorderLayout.CENTER);

        // ----- Panel de Botones Inferior -----
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnPagar = new JButton("Confirmar Pago");
        btnPagar.setFont(new Font("Arial", Font.BOLD, 14));
        btnPagar.setBackground(new Color(0, 128, 0)); // Verde
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setPreferredSize(new Dimension(160, 35));
        btnPagar.addActionListener(e -> procesarPago());

        btnCancelar = new JButton("Cancelar Pago");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCancelar.setPreferredSize(new Dimension(140, 35));
        btnCancelar.addActionListener(e -> main.abrirPanelCitas()); // Volver a citas pendientes

        panelBotones.add(btnCancelar);
        panelBotones.add(btnPagar);
        add(panelBotones, BorderLayout.SOUTH);

        actualizarCamposSegunMetodo(); // Estado inicial de los campos
    }

    private void actualizarCamposSegunMetodo() {
        boolean esEfectivo = "Efectivo".equals(comboMetodoPago.getSelectedItem());
        txtMontoRecibido.setEnabled(esEfectivo);
        if (!esEfectivo) {
            txtMontoRecibido.setText(""); // Limpiar si no es efectivo
            lblCambioADevolver.setText(formatoMoneda.format(0));
            lblCambioADevolver.setForeground(new Color(0, 100, 0));
        }
    }

    private void calcularYMostrarCambio() {
        if (!"Efectivo".equals(comboMetodoPago.getSelectedItem())) return;

        try {
            String montoRecibidoStr = txtMontoRecibido.getText().trim();
            if (montoRecibidoStr.isEmpty()) {
                lblCambioADevolver.setText(formatoMoneda.format(0));
                lblCambioADevolver.setForeground(new Color(0, 100, 0));
                return;
            }
            int montoRecibido = Integer.parseInt(montoRecibidoStr);
            int cambio = montoRecibido - precioTotalCita;
            lblCambioADevolver.setText(formatoMoneda.format(cambio));
            lblCambioADevolver.setForeground(cambio >= 0 ? new Color(0, 100, 0) : Color.RED);
        } catch (NumberFormatException e) {
            lblCambioADevolver.setText("Monto inválido");
            lblCambioADevolver.setForeground(Color.RED);
        }
    }

    public void setCita(Cita cita) {
        this.citaActual = cita;
        if (cita == null) {
            lblInfoCita.setText("<html><b>Error: No se ha seleccionado una cita válida para pagar.</b></html>");
            btnPagar.setEnabled(false);
            txtMontoRecibido.setText("");
            lblCambioADevolver.setText(formatoMoneda.format(0));
            precioTotalCita = 0;
            return;
        }
        btnPagar.setEnabled(true);

        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario());

        String fechaFormateada = (cita.getFechaHora() != null) ? new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(cita.getFechaHora()) : "Fecha no disponible";
        String motivoDesc = (cita.getMotivo() != null) ? cita.getMotivo().getDescripcion() : "Motivo no especificado";
        precioTotalCita = CitasPanel.precioPorTipoServicio(cita.getMotivo()); // Usar el método estático con Enum

        StringBuilder sb = new StringBuilder("<html><body style='width: 300px; font-family: Arial;'>");
        sb.append("<p><b>Mascota:</b> ").append(m != null ? m.getNombre() : "N/A").append("</p>");
        sb.append("<p><b>Propietario:</b> ").append(p != null ? p.getNombre() : "N/A").append("</p>");
        sb.append("<p><b>Veterinario:</b> ").append(v != null ? v.getNombre() : "N/A").append("</p>");
        sb.append("<p><b>Fecha y Hora:</b> ").append(fechaFormateada).append("</p>");
        sb.append("<p><b>Servicio:</b> ").append(motivoDesc).append("</p>");
        sb.append("<hr><p style='font-size: 1.1em;'><b>Total a Pagar: ").append(formatoMoneda.format(precioTotalCita)).append("</b></p>");
        sb.append("</body></html>");
        lblInfoCita.setText(sb.toString());

        // Resetear campos de pago
        comboMetodoPago.setSelectedIndex(0); // Efectivo por defecto
        actualizarCamposSegunMetodo();
        calcularYMostrarCambio();
    }

    private void procesarPago() {
        if (citaActual == null) {
            JOptionPane.showMessageDialog(this, "No hay una cita seleccionada para pagar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String metodoSeleccionado = (String) comboMetodoPago.getSelectedItem();
        if ("Efectivo".equals(metodoSeleccionado)) {
            try {
                String montoRecibidoStr = txtMontoRecibido.getText().trim();
                if (montoRecibidoStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese el monto recibido.", "Monto Requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int montoRecibido = Integer.parseInt(montoRecibidoStr);
                if (montoRecibido < precioTotalCita) {
                    JOptionPane.showMessageDialog(this, "El monto recibido es insuficiente para cubrir el total de la cita.",
                            "Monto Insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El monto recibido debe ser un número válido.",
                        "Monto Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Marcar la cita como pagada y actualizar
        main.marcarCitaComoPagada(citaActual); // Llama al método centralizado en Main

        // Mostrar "factura" de éxito
        mostrarFacturaExito(metodoSeleccionado);

        main.abrirPanelPagos(); // Ir al historial de pagos
    }

    private void mostrarFacturaExito(String metodoPago) {
        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(citaActual.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(citaActual.getIdMascota());
        Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(citaActual.getDocVeterinario());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String fechaHoraStr = (citaActual.getFechaHora() != null) ? sdf.format(citaActual.getFechaHora()) : "N/A";

        StringBuilder facturaHtml = new StringBuilder("<html><body style='width: 380px; font-family: Arial;'>");
        facturaHtml.append("<h2 style='color: #006400; text-align: center;'>¡Pago Confirmado!</h2>");
        facturaHtml.append("<div style='border: 1px solid #D0D0D0; padding: 15px; background-color: #F9F9F9; border-radius: 5px;'>");
        facturaHtml.append("<strong>ID Cita:</strong> ").append(citaActual.getId()).append("<br>");
        facturaHtml.append("<strong>Fecha y Hora:</strong> ").append(fechaHoraStr).append("<br>");
        facturaHtml.append("<strong>Paciente:</strong> ").append(m != null ? m.getNombre() : "N/A").append("<br>");
        facturaHtml.append("<strong>Propietario:</strong> ").append(p != null ? p.getNombre() : "N/A").append("<br>");
        facturaHtml.append("<strong>Veterinario:</strong> ").append(v != null ? v.getNombre() : "N/A").append("<br>");
        facturaHtml.append("<strong>Servicio:</strong> ").append(citaActual.getMotivo().getDescripcion()).append("<br>");
        facturaHtml.append("<strong>Método de Pago:</strong> ").append(metodoPago).append("<br>");
        facturaHtml.append("<hr style='border-top: 1px dashed #B0B0B0;'>");
        facturaHtml.append("<p style='font-size: 1.2em; text-align: right;'><strong>Total Pagado: ").append(formatoMoneda.format(precioTotalCita)).append("</strong></p>");

        if ("Efectivo".equals(metodoPago)) {
            try {
                int montoRecibido = Integer.parseInt(txtMontoRecibido.getText().trim());
                int cambio = montoRecibido - precioTotalCita;
                facturaHtml.append("<p style='text-align: right;'>Monto Recibido: ").append(formatoMoneda.format(montoRecibido)).append("</p>");
                facturaHtml.append("<p style='text-align: right;'>Cambio Devuelto: ").append(formatoMoneda.format(cambio)).append("</p>");
            } catch (NumberFormatException ignored) {}
        }
        facturaHtml.append("</div><p style='text-align: center; font-size: 0.9em; color: #505050;'>Gracias por su preferencia.</p>");
        facturaHtml.append("</body></html>");

        JOptionPane.showMessageDialog(this, facturaHtml.toString(), "Comprobante de Pago", JOptionPane.INFORMATION_MESSAGE);
    }
}
