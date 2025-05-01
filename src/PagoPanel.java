import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class PagoPanel extends JPanel {
    private Main main;
    private Cita citaActual;
    private JLabel infoLabel;
    private JComboBox<String> comboMetodo;
    private JButton btnPagar, btnCancelar;

    public PagoPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JLabel lblTitulo = new JLabel("Registro de Pago", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        infoLabel = new JLabel("", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelPago = new JPanel(new FlowLayout());
        panelPago.setBackground(getBackground());

        comboMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta"});
        comboMetodo.setFont(new Font("Arial", Font.PLAIN, 15));
        comboMetodo.setPreferredSize(new Dimension(130, 28));
        panelPago.add(new JLabel("Forma de pago:"));
        panelPago.add(comboMetodo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPagar = new JButton("Pagar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnPagar);
        panelBotones.add(btnCancelar);

        add(lblTitulo, BorderLayout.NORTH);
        add(infoLabel, BorderLayout.CENTER);
        add(panelPago, BorderLayout.WEST);
        add(panelBotones, BorderLayout.SOUTH);

        btnPagar.addActionListener(e -> {
            if (citaActual != null) {
                citaActual.setEstado("Pagada");
                main.citas.remove(citaActual);
                if (!main.citasPagadas.contains(citaActual))
                    main.citasPagadas.add(citaActual);
                try { // Guardar el estado actualizado en la base
                    CitaDAO.guardar(citaActual);
                } catch (Exception ex) { ex.printStackTrace(); }

                if (main.pagosPanel != null) main.pagosPanel.actualizarCards(main.citasPagadas);
                JOptionPane.showMessageDialog(this, "¡Pago realizado! Cita registrada como pagada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                main.citasPanel.actualizarCards(main.citas); // Para que desaparezca de CitasPanel
                main.cardLayout.show(main.mainPanel, "pagos");
            }
        });

        btnCancelar.addActionListener(e -> main.cardLayout.show(main.mainPanel, "citas"));
    }

    public void setCita(Cita cita) {
        this.citaActual = cita;
        if (cita == null) {
            infoLabel.setText("<html><b>Seleccione una cita válida.</b></html>");
            btnPagar.setEnabled(false);
            return;
        }
        btnPagar.setEnabled(true);

        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario()); // Usa el método público

        String fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(cita.getFechaHora());
        String motivo = cita.getMotivo();
        int precio = CitasPanel.precioPorTipoServicio(motivo);


        StringBuilder texto = new StringBuilder("<html>");
        texto.append("<b>Mascota:</b> ").append(m != null ? m.getNombre() : "-").append("<br>");
        texto.append("<b>Propietario:</b> ").append(p != null ? p.getNombre() : "-").append("<br>");
        texto.append("<b>Veterinario:</b> ").append(v != null ? v.getNombre() : "-").append("<br>");
        texto.append("<b>Fecha:</b> ").append(fecha).append("<br>");
        texto.append("<b>Motivo:</b> ").append(motivo).append("<br>");
        texto.append("<b>Total a Pagar:</b> $").append(precio).append("<br>");
        texto.append("</html>");

        infoLabel.setText(texto.toString());
    }
}
