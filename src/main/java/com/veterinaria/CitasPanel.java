// CitasPanel.java
package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CitasPanel extends JPanel {
    private Main main;
    private JPanel cardsPanel;
    private JTextField txtBuscar;
    private static final SimpleDateFormat SDF_DISPLAY_CARD = new SimpleDateFormat("dd/MM/yy hh:mm a");
    // private static final SimpleDateFormat DEBUG_FECHA_HORA_FORMAT_CITAS = new SimpleDateFormat("dd/MM/yyyy HH:mm"); // Depuración


    public CitasPanel(Main main) {
        // ... (constructor sin cambios) ...
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Barra superior -----
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Citas", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 100));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        txtBuscar = new JTextField(30); // Ancho de búsqueda
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,200,230)), "Buscar (Mascota, Propietario o Veterinario)"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnAgendar = new JButton("Agendar Nueva Cita");
        btnAgendar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnAgendar.setBackground(new Color(60, 140, 200));
        btnAgendar.setForeground(Color.BLACK);
        btnAgendar.addActionListener(e -> dialogoAgendarCita());

        JPanel panelBusquedaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusquedaBotones.setOpaque(false);
        panelBusquedaBotones.add(txtBuscar);
        panelBusquedaBotones.add(btnAgendar);

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


    public void dialogoAgendarCita() {
        if (main.propietarios == null || main.propietarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar propietarios antes de agendar citas.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (main.veterinarios == null || main.veterinarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe registrar veterinarios antes de agendar citas.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Propietario> comboPropietario = new JComboBox<>(main.propietarios.toArray(new Propietario[0]));
        JComboBox<Mascota> comboMascota = new JComboBox<>();
        JComboBox<Veterinario> comboVeterinario = new JComboBox<>(main.veterinarios.toArray(new Veterinario[0]));
        JDateChooser dateChooser = new JDateChooser();

        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(new Date());
        // System.out.println("CitasPanel - JDateChooser inicializado con: " + (dateChooser.getDate() != null ? DEBUG_FECHA_HORA_FORMAT_CITAS.format(dateChooser.getDate()) : "null")); // Depuración

        String[] horasDisponibles = {
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM",
                "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        };
        JComboBox<String> comboHora = new JComboBox<>(horasDisponibles);
        JComboBox<TipoServicio> comboMotivo = new JComboBox<>(TipoServicio.values());

        comboPropietario.addActionListener(e -> {
            Propietario pSeleccionado = (Propietario) comboPropietario.getSelectedItem();
            comboMascota.removeAllItems();
            if (pSeleccionado != null && main.mascotas != null) {
                for (Mascota m : main.mascotas) {
                    if (m.getPropietarioDocumento().equals(pSeleccionado.getDocumento())) {
                        comboMascota.addItem(m);
                    }
                }
            }
            if (comboMascota.getItemCount() == 0) {
                comboMascota.addItem(null);
                comboMascota.setEnabled(false);
            } else {
                comboMascota.setEnabled(true);
            }
        });

        if (comboPropietario.getItemCount() > 0) {
            comboPropietario.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "No hay propietarios registrados para seleccionar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panelDialogo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;

        panelDialogo.add(new JLabel("Seleccione Propietario:"), gbc); panelDialogo.add(comboPropietario, gbc);
        panelDialogo.add(new JLabel("Seleccione Mascota:"), gbc); panelDialogo.add(comboMascota, gbc);
        panelDialogo.add(new JLabel("Seleccione Veterinario:"), gbc); panelDialogo.add(comboVeterinario, gbc);
        panelDialogo.add(new JLabel("Fecha de la Cita:"), gbc); panelDialogo.add(dateChooser, gbc);
        panelDialogo.add(new JLabel("Hora de la Cita:"), gbc); panelDialogo.add(comboHora, gbc);
        panelDialogo.add(new JLabel("Motivo de la Consulta:"), gbc); panelDialogo.add(comboMotivo, gbc);

        int resultado = JOptionPane.showConfirmDialog(this, panelDialogo, "Agendar Nueva Cita",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            Propietario propSel = (Propietario) comboPropietario.getSelectedItem();
            Mascota mascSel = (Mascota) comboMascota.getSelectedItem();
            Veterinario vetSel = (Veterinario) comboVeterinario.getSelectedItem();
            Date fechaSel = dateChooser.getDate();
            String horaSelStr = (String) comboHora.getSelectedItem();
            TipoServicio motivoSel = (TipoServicio) comboMotivo.getSelectedItem();

            if (propSel == null || mascSel == null || vetSel == null || fechaSel == null || horaSelStr == null || motivoSel == null) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!vetSel.isDisponibilidad()) {
                JOptionPane.showMessageDialog(this, "El veterinario " + vetSel.getNombre() + " no está disponible.", "Veterinario No Disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaSel);
            String[] partesHora = horaSelStr.replace(" AM", "").replace(" PM", "").split(":");
            int hora = Integer.parseInt(partesHora[0]);
            int minuto = Integer.parseInt(partesHora[1]);
            if (horaSelStr.contains("PM") && hora != 12) hora += 12;
            if (horaSelStr.contains("AM") && hora == 12) hora = 0;

            cal.set(Calendar.HOUR_OF_DAY, hora);
            cal.set(Calendar.MINUTE, minuto);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date fechaHoraCita = cal.getTime();

            String docVeterinarioParaCita = vetSel.getDocumento();
            // System.out.println("CitasPanel - Creando cita con DocVeterinario: " + docVeterinarioParaCita + ", FechaHora: " + DEBUG_FECHA_HORA_FORMAT_CITAS.format(fechaHoraCita)); // Depuración


            if (main.citas != null) {
                for (Cita cExistente : main.citas) {
                    if (cExistente.getDocVeterinario() != null && cExistente.getDocVeterinario().equals(vetSel.getDocumento()) &&
                            Math.abs(cExistente.getFechaHora().getTime() - fechaHoraCita.getTime()) < (30 * 60 * 1000)) {
                        JOptionPane.showMessageDialog(this, "El veterinario ya tiene cita cerca de esa hora.", "Conflicto Horario (Veterinario)", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (cExistente.getIdMascota().equals(mascSel.getId()) &&
                            Math.abs(cExistente.getFechaHora().getTime() - fechaHoraCita.getTime()) < (30 * 60 * 1000)) {
                        JOptionPane.showMessageDialog(this, "La mascota ya tiene cita cerca de esa hora.", "Conflicto Horario (Mascota)", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            Cita nuevaCita = new Cita(propSel.getDocumento(), mascSel.getId(), docVeterinarioParaCita, fechaHoraCita, motivoSel, "PENDIENTE");
            main.agregarCita(nuevaCita);
            JOptionPane.showMessageDialog(this, "Cita para '" + mascSel.getNombre() + "' agendada.", "Cita Registrada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarCards() {
        actualizarCards(main.citas);
    }

    public void actualizarCards(List<Cita> listaAMostrar) {
        cardsPanel.removeAll();
        if (listaAMostrar == null || listaAMostrar.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay citas pendientes para mostrar.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(lblVacio, BorderLayout.CENTER);
        } else {
            listaAMostrar.sort(Comparator.comparing(Cita::getFechaHora));
            cardsPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Cita cita : listaAMostrar) {
                if ("PENDIENTE".equalsIgnoreCase(cita.getEstado())) {
                    cardsPanel.add(crearCardCita(cita));
                }
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardCita(Cita cita) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(380, 170));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        try {
            URL imgUrl = getClass().getResource("/Cita.png");
            if (imgUrl != null) lblAvatar.setIcon(new ImageIcon(imgUrl));
            else lblAvatar.setText("Cita");
        } catch (Exception e) { lblAvatar.setText("Err");}
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario());

        String nombreMascota = (m != null) ? m.getNombre() : "Mascota no encontrada";
        String especieMascota = (m != null) ? m.getEspecie() : "N/A";
        String nombrePropietario = (p != null) ? p.getNombre() : "Propietario no encontrado";
        String nombreVeterinario = (v != null) ? v.getNombre() : "Veterinario no asignado";

        infoPanel.add(new JLabel("<html><b>Mascota: " + nombreMascota + "</b> (" + especieMascota + ")</html>")).setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(new JLabel("Propietario: " + nombrePropietario));
        infoPanel.add(new JLabel("Veterinario: " + nombreVeterinario));
        infoPanel.add(new JLabel("Fecha: " + SDF_DISPLAY_CARD.format(cita.getFechaHora())));
        infoPanel.add(new JLabel("Motivo: " + cita.getMotivo().getDescripcion()));
        infoPanel.add(new JLabel("Estado: " + cita.getEstado()));

        JPanel centerAlignInfo = new JPanel(new GridBagLayout());
        centerAlignInfo.setOpaque(false);
        centerAlignInfo.add(infoPanel);
        card.add(centerAlignInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JButton btnCheck = crearBotonIcono("/Check.png", "Marcar como Atendida e Ir a Pagar");
        btnCheck.addActionListener(e -> main.abrirPanelPago(cita));

        JButton btnEliminar = crearBotonIcono("/trash.png", "Eliminar Cita");
        btnEliminar.addActionListener(e -> eliminarCita(cita));

        panelBotones.add(btnCheck);
        panelBotones.add(Box.createVerticalStrut(5));
        panelBotones.add(btnEliminar);

        card.add(panelBotones, BorderLayout.EAST);
        return card;
    }

    private JButton crearBotonIcono(String pathIcono, String tooltip) {
        JButton boton = new JButton();
        try {
            URL imgUrl = getClass().getResource(pathIcono);
            if (imgUrl != null) {
                boton.setIcon(new ImageIcon(imgUrl));
            } else {
                boton.setText(tooltip.substring(0,1));
                System.err.println("Icono no encontrado para CitasPanel: " + pathIcono);
            }
        } catch (Exception e) {
            boton.setText("Err");
            System.err.println("Error cargando icono " + pathIcono + " en CitasPanel: " + e.getMessage());
        }
        boton.setToolTipText(tooltip);
        boton.setPreferredSize(new Dimension(38, 38));
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        return boton;
    }

    private void eliminarCita(Cita cita) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar esta cita?",
                "Confirmar Eliminación de Cita", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                main.eliminarCita(cita);
                JOptionPane.showMessageDialog(this, "Cita eliminada correctamente.",
                        "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cita: " + ex.getMessage(),
                        "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Cita> filtradas = new ArrayList<>();

        if (main.citas != null) {
            for (Cita c : main.citas) {
                if (!"PENDIENTE".equalsIgnoreCase(c.getEstado())) continue;

                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(c.getDocPropietario());
                Mascota m = main.mascotasPanel.encontrarMascotaPorId(c.getIdMascota());
                Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(c.getDocVeterinario());

                boolean matchMascota = (m != null && m.getNombre().toLowerCase().contains(filtro));
                boolean matchPropietario = (p != null && p.getNombre().toLowerCase().contains(filtro));
                boolean matchVeterinario = (v != null && v.getNombre().toLowerCase().contains(filtro));
                boolean matchMotivo = (c.getMotivo()!=null && c.getMotivo().getDescripcion().toLowerCase().contains(filtro));


                if (filtro.isEmpty() || matchMascota || matchPropietario || matchVeterinario || matchMotivo) {
                    filtradas.add(c);
                }
            }
        }
        actualizarCards(filtradas);
    }

    public static int precioPorTipoServicio(TipoServicio motivo) {
        if (motivo == null) return 0;
        return motivo.getPrecio();
    }
    public static int precioPorTipoServicio(String motivoStr) {
        if (motivoStr == null || motivoStr.trim().isEmpty()) return 0;
        TipoServicio motivoEnum = TipoServicio.fromString(motivoStr);
        return (motivoEnum != null) ? motivoEnum.getPrecio() : 0;
    }
}