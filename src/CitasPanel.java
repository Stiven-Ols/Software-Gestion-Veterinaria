import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class CitasPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;

    public CitasPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        // --- Barra superior
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(getBackground());

        JLabel lblTitulo = new JLabel("Citas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por mascota, propietario o veterinario"));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgendar = new JButton("Agendar cita");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgendar.addActionListener(e -> dialogoAgendarCita());

        JPanel searchAndBtn = new JPanel(new BorderLayout(10,0));
        searchAndBtn.setOpaque(false);
        searchAndBtn.add(txtBuscar, BorderLayout.CENTER);
        searchAndBtn.add(btnAgendar, BorderLayout.EAST);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(searchAndBtn, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // --- Panel de cards
        cardsPanel = new JPanel(new GridLayout(0, 2, 22, 16));
        cardsPanel.setBackground(getBackground());
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        // --- Botón volver
        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottom.setOpaque(false);
        bottom.add(btnVolver);
        add(bottom, BorderLayout.SOUTH);

        actualizarCards();
    }

    // Mostrar todas las cards de citas pendientes
    public void actualizarCards() {
        actualizarCards(main.citas);
    }

    public void actualizarCards(List<Cita> lista) {
        cardsPanel.removeAll();
        for (Cita cita : lista) {
            if ("PENDIENTE".equalsIgnoreCase(cita.getEstado())) {
                cardsPanel.add(crearCardCita(cita));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardCita(Cita cita) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setPreferredSize(new Dimension(350, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(18, 15, 18, 17)
        ));

        JLabel lblAvatar = new JLabel(new ImageIcon(getClass().getResource("/Cita.png")));
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        Mascota m = encontrarMascotaPorId(cita.getIdMascota());
        Propietario p = encontrarPropietarioPorDoc(cita.getDocPropietario());
        Veterinario v = encontrarVeterinarioPorDoc(cita.getDocVeterinario());

        String nombreMascota = (m != null) ? m.getNombre() : "-";
        String especieMascota = (m != null) ? m.getEspecie() : "-";
        String nombreProp = (p != null) ? p.getNombre() : "-";
        String nombreVet = (v != null) ? v.getNombre() : "-";
        String estado = cita.getEstado();

        JLabel lblMascota = new JLabel("<html><b>Mascota:</b> " + nombreMascota +
                "<br><b>Especie:</b> " + especieMascota +
                "<br><b>Propietario:</b> " + nombreProp +
                "<br><b>Veterinario:</b> " + nombreVet +
                "<br><b>Fecha:</b> " + formatearFechaHoraCita(cita.getFechaHora()) +
                "<br><b>Motivo:</b> " + cita.getMotivo() +
                "<br><b>Estado:</b> " + estado + "</html>", SwingConstants.CENTER);
        lblMascota.setFont(new Font("Arial", Font.PLAIN, 15));
        info.add(lblMascota);

        card.add(info, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setBorder(BorderFactory.createEmptyBorder(0,0,0,3));

        JButton btnCheck = new JButton(new ImageIcon(getClass().getResource("/Check.png")));
        btnCheck.setPreferredSize(new Dimension(35,35));
        btnCheck.setToolTipText("Marcar cita como pagada y pasar a pago");
        btnCheck.addActionListener(e -> main.abrirPanelPago(cita));

        JButton btnDel = new JButton(new ImageIcon(getClass().getResource("/trash.png")));
        btnDel.setPreferredSize(new Dimension(35,35));
        btnDel.setToolTipText("Eliminar cita");
        btnDel.addActionListener(e -> eliminarCita(cita));
        botones.add(btnCheck);
        botones.add(Box.createVerticalStrut(6));
        botones.add(btnDel);

        card.add(botones, BorderLayout.EAST);
        return card;
    }

    // Elimina una cita, muestra confirmación y mensaje de éxito
    private void eliminarCita(Cita cita) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta cita?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            main.eliminarCita(cita);
            JOptionPane.showMessageDialog(this, "Cita eliminada correctamente.", "Eliminada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Modal completo para agendar cita
    private void dialogoAgendarCita() {
        if (main.propietarios.isEmpty() || main.mascotas.isEmpty() || main.veterinarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes registrar al menos un propietario, una mascota y un veterinario primero.", "Atención", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Propietario> comboPropietario = new JComboBox<>(main.propietarios.toArray(new Propietario[0]));
        JComboBox<Mascota> comboMascota = new JComboBox<>();
        JComboBox<Veterinario> comboVeterinario = new JComboBox<>(main.veterinarios.toArray(new Veterinario[0]));
        JDateChooser dateChooser = new JDateChooser();
        String[] horas = {"9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM"};
        JComboBox<String> comboHora = new JComboBox<>(horas);
        JComboBox<TipoServicio> comboMotivo = new JComboBox<>(TipoServicio.values());

        comboPropietario.addActionListener(e -> {
            comboMascota.removeAllItems();
            Propietario sel = (Propietario) comboPropietario.getSelectedItem();
            if (sel != null) {
                for (Mascota m : main.mascotas) {
                    if (m.getPropietarioDocumento().equals(sel.getDocumento()))
                        comboMascota.addItem(m);
                }
                if (comboMascota.getItemCount() > 0) comboMascota.setSelectedIndex(0);
            }
        });
        if (comboPropietario.getItemCount() > 0)
            comboPropietario.setSelectedIndex(0);

        JPanel panel = new JPanel(new GridLayout(0,1, 0,7));
        panel.add(new JLabel("Propietario:"));   panel.add(comboPropietario);
        panel.add(new JLabel("Mascota:"));       panel.add(comboMascota);
        panel.add(new JLabel("Veterinario:"));   panel.add(comboVeterinario);
        panel.add(new JLabel("Fecha:"));         panel.add(dateChooser);
        panel.add(new JLabel("Hora:"));          panel.add(comboHora);
        panel.add(new JLabel("Motivo:"));        panel.add(comboMotivo);

        int res = JOptionPane.showConfirmDialog(this, panel, "Agendar Cita", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            Propietario propietario = (Propietario) comboPropietario.getSelectedItem();
            Mascota mascota = (Mascota) comboMascota.getSelectedItem();
            Veterinario veterinario = (Veterinario) comboVeterinario.getSelectedItem();
            Date fecha = dateChooser.getDate();
            String horaStr = (String)comboHora.getSelectedItem();
            TipoServicio motivo = (TipoServicio) comboMotivo.getSelectedItem();

            if (propietario == null || mascota == null || veterinario == null || fecha == null || horaStr == null || motivo == null) {
                JOptionPane.showMessageDialog(this, "¡Completa todos los campos!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            int hour = Integer.parseInt(horaStr.split(":")[0]);
            int minute = Integer.parseInt(horaStr.split(":")[1].substring(0,2));
            boolean pm = horaStr.toLowerCase().contains("pm");
            if (hour == 12 && !pm) hour = 0;
            if (pm && hour < 12) hour += 12;
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date fechaCita = calendar.getTime();

            for (Cita c : main.citas) {
                if (c.getDocVeterinario().equals(veterinario.getDocumento()) && c.getFechaHora().equals(fechaCita) && c.getEstado().equalsIgnoreCase("Pendiente")) {
                    JOptionPane.showMessageDialog(this, "El veterinario ya tiene cita a esa hora.", "Conflicto", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (c.getIdMascota().equals(mascota.getId()) && c.getFechaHora().equals(fechaCita) && c.getEstado().equalsIgnoreCase("Pendiente")) {
                    JOptionPane.showMessageDialog(this, "La mascota ya tiene una cita a esa hora.", "Conflicto", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String id = UUID.randomUUID().toString();
            Cita nueva = new Cita(id, propietario.getDocumento(), mascota.getId(), veterinario.getDocumento(), fechaCita, motivo.name(), "Pendiente");
            main.agregarCita(nueva);
            JOptionPane.showMessageDialog(this, "Cita registrada correctamente.");
        }
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().toLowerCase();
        List<Cita> filtradas = new ArrayList<>();
        for (Cita c : main.citas) {
            Propietario p = encontrarPropietarioPorDoc(c.getDocPropietario());
            Mascota m = encontrarMascotaPorId(c.getIdMascota());
            Veterinario v = encontrarVeterinarioPorDoc(c.getDocVeterinario());
            if ( (m != null && m.getNombre().toLowerCase().contains(filtro)) ||
                    (p != null && p.getNombre().toLowerCase().contains(filtro)) ||
                    (v != null && v.getNombre().toLowerCase().contains(filtro)) ||
                    filtro.isEmpty() )
                if("PENDIENTE".equalsIgnoreCase(c.getEstado())) filtradas.add(c);
        }
        actualizarCards(filtradas);
    }

    private Mascota encontrarMascotaPorId(String id) {
        for (Mascota m : main.mascotas) if (m.getId().equals(id)) return m;
        return null;
    }
    private Propietario encontrarPropietarioPorDoc(String doc) {
        for (Propietario p : main.propietarios) if (p.getDocumento().equals(doc)) return p;
        return null;
    }
    public Veterinario encontrarVeterinarioPorDoc(String doc) {
        for (Veterinario v : main.veterinarios) if (v.getDocumento().equals(doc)) return v;
        return null;
    }

    private String formatearFechaHoraCita(Date fechaHora) {
        try { return new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(fechaHora); }
        catch (Exception e) { return "-"; }
    }

    // --- ¡MÉTODO público y ESTÁTICO requerido! ---
    public static int precioPorTipoServicio(String motivo) {
        if (motivo == null) return 0;
        switch (motivo.toUpperCase()) {
            case "CONSULTA": return 60000;
            case "VACUNACION": return 40000;
            case "CIRUGIA": return 200000;
            case "URGENCIA": return 100000;
            default: return 0;
        }
    }
}
