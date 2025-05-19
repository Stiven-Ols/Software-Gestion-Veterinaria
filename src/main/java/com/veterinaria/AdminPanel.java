package com.veterinaria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;


public class AdminPanel extends JPanel {
    private Main main;
    private static final DecimalFormat MONEDA_FORMAT = new DecimalFormat("$#,##0");
    private static final SimpleDateFormat FECHA_HORA_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat MES_ANIO_FORMAT = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    private JLabel lblCitasHoyVal;
    private JLabel lblCitasMesVal;
    private JLabel lblIngresosHoyVal;
    private JLabel lblIngresosMesVal;
    private JPanel panelServiciosPopularesContenido;
    private JPanel panelVeterinariosActivosContenido;
    private JComboBox<String> comboAnioReporteCitasFiltro;
    private JComboBox<String> comboMesReporteCitasFiltro;

    public AdminPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(220, 225, 230));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTituloDashboard = new JLabel("Dashboard de Administración", SwingConstants.CENTER);
        lblTituloDashboard.setFont(new Font("Arial", Font.BOLD, 28));
        lblTituloDashboard.setForeground(new Color(40, 60, 90));
        lblTituloDashboard.setBorder(new EmptyBorder(0,0,15,0));
        add(lblTituloDashboard, BorderLayout.NORTH);

        JPanel widgetsPanel = new JPanel(new GridBagLayout());
        widgetsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        widgetsPanel.add(crearWidgetCitasHoy(), gbc);
        gbc.gridx = 1;
        widgetsPanel.add(crearWidgetCitasMesActual(), gbc);
        gbc.gridx = 2;
        widgetsPanel.add(crearWidgetIngresosHoy(), gbc);
        gbc.gridx = 3;
        widgetsPanel.add(crearWidgetIngresosMesActual(), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.weighty = 1.5;
        widgetsPanel.add(crearWidgetServiciosPopulares(), gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        widgetsPanel.add(crearWidgetVeterinariosActivos(), gbc);
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        widgetsPanel.add(crearPanelAccionesAdministrativas(), gbc);

        JScrollPane scrollPane = new JScrollPane(widgetsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(getBackground());
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton btnIrVistaEmpleado = new JButton("Ir a Vista Empleado");
        btnIrVistaEmpleado.setFont(new Font("Arial", Font.BOLD, 13));
        btnIrVistaEmpleado.addActionListener(e -> main.irHome(false));

        JButton btnCerrarSesion = new JButton("Cerrar Sesión Admin");
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 13));
        btnCerrarSesion.addActionListener(e -> main.irLogin());

        bottomPanel.add(btnIrVistaEmpleado);
        bottomPanel.add(btnCerrarSesion);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel crearWidgetKPI(String titulo, String valorInicial, Color colorTitulo, Color colorValor) {
        JPanel widget = new JPanel(new BorderLayout(5,5));
        widget.setBackground(Color.WHITE);
        widget.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,200,200)),
                new EmptyBorder(15,15,15,15)
        ));
        widget.setPreferredSize(new Dimension(220, 130));

        JLabel lblTituloWidget = new JLabel(titulo.toUpperCase(), SwingConstants.LEFT);
        lblTituloWidget.setFont(new Font("Arial", Font.BOLD, 13));
        lblTituloWidget.setForeground(colorTitulo);
        widget.add(lblTituloWidget, BorderLayout.NORTH);

        JLabel lblValorWidget = new JLabel(valorInicial, SwingConstants.LEFT);
        lblValorWidget.setFont(new Font("Arial", Font.BOLD, 32));
        lblValorWidget.setForeground(colorValor);
        widget.add(lblValorWidget, BorderLayout.CENTER);

        if (titulo.contains("Citas Hoy")) lblCitasHoyVal = lblValorWidget;
        else if (titulo.contains("Citas Mes")) lblCitasMesVal = lblValorWidget;
        else if (titulo.contains("Ingresos Hoy")) lblIngresosHoyVal = lblValorWidget;
        else if (titulo.contains("Ingresos Mes")) lblIngresosMesVal = lblValorWidget;

        return widget;
    }

    private JPanel crearWidgetCitasHoy() {
        return crearWidgetKPI("Citas Hoy", "0", new Color(0, 120, 215), new Color(0, 100, 190));
    }
    private JPanel crearWidgetCitasMesActual() {
        return crearWidgetKPI("Citas Mes Actual", "0", new Color(0, 150, 136),new Color(0, 130, 116));
    }
    private JPanel crearWidgetIngresosHoy() {
        return crearWidgetKPI("Ingresos Hoy", MONEDA_FORMAT.format(0), new Color(255, 140, 0), new Color(230, 120, 0));
    }
    private JPanel crearWidgetIngresosMesActual() {
        return crearWidgetKPI("Ingresos Mes Actual", MONEDA_FORMAT.format(0), new Color(216, 67, 21), new Color(190, 50, 10));
    }

    private JPanel crearWidgetLista(String titulo) {
        JPanel widget = new JPanel(new BorderLayout(5,8));
        widget.setBackground(Color.WHITE);
        widget.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,200,200)),
                new EmptyBorder(15,15,15,15)
        ));

        JLabel lblTituloWidget = new JLabel(titulo.toUpperCase(), SwingConstants.LEFT);
        lblTituloWidget.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloWidget.setForeground(new Color(70,80,100));
        lblTituloWidget.setBorder(new EmptyBorder(0,0,5,0));
        widget.add(lblTituloWidget, BorderLayout.NORTH);

        JPanel contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new BoxLayout(contenidoPanel, BoxLayout.Y_AXIS));
        contenidoPanel.setOpaque(false);

        if ("Servicios Más Populares (Mes Actual)".equalsIgnoreCase(titulo)) {
            panelServiciosPopularesContenido = contenidoPanel;
        } else if ("Veterinarios con Más Citas (Mes Actual)".equalsIgnoreCase(titulo)) {
            panelVeterinariosActivosContenido = contenidoPanel;
        }

        JScrollPane scrollPane = new JScrollPane(contenidoPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        widget.add(scrollPane, BorderLayout.CENTER);

        return widget;
    }

    private JPanel crearWidgetServiciosPopulares() {
        return crearWidgetLista("Servicios Más Populares (Mes Actual)");
    }
    private JPanel crearWidgetVeterinariosActivos() {
        return crearWidgetLista("Veterinarios con Más Citas (Mes Actual)");
    }

    private JPanel crearPanelAccionesAdministrativas() {
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180,190,200)), "Acciones Administrativas",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 16), new Color(50,70,100)
        ));

        JButton btnConfigPrecios = crearBotonAccion("Configurar Precios", this::accionMostrarDialogoConfigPrecios);
        JButton btnReporteCitas = crearBotonAccion("Reporte Mensual Citas", this::accionExportarReporteMensualCitasPDF);
        JButton btnReporteIngresos = crearBotonAccion("Resumen Ingresos", this::accionExportarResumenIngresosPDF);
        JButton btnReporteServicios = crearBotonAccion("Detalle Servicios", this::accionExportarDetalleServiciosPDF);

        panelAcciones.add(btnConfigPrecios);
        panelAcciones.add(btnReporteCitas);
        panelAcciones.add(btnReporteIngresos);
        panelAcciones.add(btnReporteServicios);

        return panelAcciones;
    }

    private JButton crearBotonAccion(String texto, java.util.function.Consumer<ActionEvent> accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setFocusPainted(false);
        boton.addActionListener(accion::accept);
        return boton;
    }

    public void cargarDatosDashboard() {
        if (!main.isEsAdmin()) {
            main.irLogin();
            return;
        }

        Calendar calHoy = Calendar.getInstance();
        Calendar calMesInicio = Calendar.getInstance();
        calMesInicio.set(Calendar.DAY_OF_MONTH, 1);
        calMesInicio.set(Calendar.HOUR_OF_DAY, 0); calMesInicio.set(Calendar.MINUTE, 0); calMesInicio.set(Calendar.SECOND, 0); calMesInicio.set(Calendar.MILLISECOND, 0);
        Calendar calMesFin = Calendar.getInstance();
        calMesFin.set(Calendar.DAY_OF_MONTH, calMesFin.getActualMaximum(Calendar.DAY_OF_MONTH));
        calMesFin.set(Calendar.HOUR_OF_DAY, 23); calMesFin.set(Calendar.MINUTE, 59); calMesFin.set(Calendar.SECOND, 59); calMesFin.set(Calendar.MILLISECOND, 999);

        List<Cita> todasLasCitas = new ArrayList<>(main.citas);
        todasLasCitas.addAll(main.citasPagadas);

        long citasHoy = todasLasCitas.stream().filter(c -> c.getFechaHora() != null &&
                isMismoDia(calHoy.getTime(), c.getFechaHora())).count();
        if (lblCitasHoyVal != null) lblCitasHoyVal.setText(String.valueOf(citasHoy));

        long citasMes = todasLasCitas.stream().filter(c -> c.getFechaHora() != null &&
                !c.getFechaHora().before(calMesInicio.getTime()) && !c.getFechaHora().after(calMesFin.getTime())).count();
        if (lblCitasMesVal != null) lblCitasMesVal.setText(String.valueOf(citasMes));

        long ingresosHoy = main.citasPagadas.stream().filter(c -> c.getFechaHora() != null &&
                        isMismoDia(calHoy.getTime(), c.getFechaHora()))
                .mapToLong(c -> (c.getMotivo() != null) ? c.getMotivo().getPrecio() : 0).sum();
        if (lblIngresosHoyVal != null) lblIngresosHoyVal.setText(MONEDA_FORMAT.format(ingresosHoy));

        long ingresosMes = main.citasPagadas.stream().filter(c -> c.getFechaHora() != null &&
                        !c.getFechaHora().before(calMesInicio.getTime()) && !c.getFechaHora().after(calMesFin.getTime()))
                .mapToLong(c -> (c.getMotivo() != null) ? c.getMotivo().getPrecio() : 0).sum();
        if (lblIngresosMesVal != null) lblIngresosMesVal.setText(MONEDA_FORMAT.format(ingresosMes));

        if (panelServiciosPopularesContenido != null) {
            panelServiciosPopularesContenido.removeAll();
            Map<TipoServicio, Long> conteoServiciosMes = todasLasCitas.stream()
                    .filter(c -> c.getMotivo() != null && c.getFechaHora() != null &&
                            !c.getFechaHora().before(calMesInicio.getTime()) && !c.getFechaHora().after(calMesFin.getTime()))
                    .collect(Collectors.groupingBy(Cita::getMotivo, Collectors.counting()));

            conteoServiciosMes.entrySet().stream()
                    .sorted(Map.Entry.<TipoServicio, Long>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> {
                        JLabel lblServ = new JLabel(String.format("• %s (%d)", entry.getKey().getDescripcion(), entry.getValue()));
                        lblServ.setFont(new Font("Arial", Font.PLAIN, 13));
                        panelServiciosPopularesContenido.add(lblServ);
                    });
            if (conteoServiciosMes.isEmpty()) panelServiciosPopularesContenido.add(new JLabel("(Sin datos este mes)"));
            panelServiciosPopularesContenido.revalidate();
            panelServiciosPopularesContenido.repaint();
        }

        if (panelVeterinariosActivosContenido != null) {
            panelVeterinariosActivosContenido.removeAll();
            Map<String, Long> conteoCitasVetMes = todasLasCitas.stream()
                    .filter(c -> {
                        boolean tieneDocVet = c.getDocVeterinario() != null && !c.getDocVeterinario().isEmpty();
                        boolean tieneFecha = c.getFechaHora() != null;
                        boolean enMesActual = false;

                        if (tieneFecha) {
                            enMesActual = (c.getFechaHora().equals(calMesInicio.getTime()) || c.getFechaHora().after(calMesInicio.getTime())) &&
                                    (c.getFechaHora().equals(calMesFin.getTime()) || c.getFechaHora().before(calMesFin.getTime()));
                        }

                        return tieneDocVet && tieneFecha && enMesActual;
                    })
                    .collect(Collectors.groupingBy(Cita::getDocVeterinario, Collectors.counting()));

            if (conteoCitasVetMes.isEmpty()) {
                panelVeterinariosActivosContenido.add(new JLabel("(Sin datos este mes)"));
            } else {
                conteoCitasVetMes.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .forEach(entry -> {
                            Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(entry.getKey());
                            String nombreVet = (v != null) ? v.getNombre() : "ID " + entry.getKey().substring(0, Math.min(6, entry.getKey().length())) + "...";
                            JLabel lblVet = new JLabel(String.format("• %s (%d citas)", nombreVet, entry.getValue()));
                            lblVet.setFont(new Font("Arial", Font.PLAIN, 13));
                            panelVeterinariosActivosContenido.add(lblVet);
                        });
            }
            panelVeterinariosActivosContenido.revalidate();
            panelVeterinariosActivosContenido.repaint();
        }
    }

    private boolean isMismoDia(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance(); cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void accionMostrarDialogoConfigPrecios(ActionEvent e) {
        JPanel panelDialogo = new JPanel(new GridBagLayout());
        panelDialogo.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbcDialog = new GridBagConstraints();
        gbcDialog.anchor = GridBagConstraints.WEST;
        gbcDialog.insets = new Insets(4,4,4,4);

        Map<TipoServicio, JTextField> camposPreciosDialogo = new HashMap<>();

        for(TipoServicio servicio : TipoServicio.values()) {
            gbcDialog.gridx = 0; gbcDialog.gridy++;
            panelDialogo.add(new JLabel(servicio.getDescripcion() + ":"), gbcDialog);
            gbcDialog.gridx = 1;
            JTextField txtPrecio = new JTextField(String.valueOf(servicio.getPrecio()), 8);
            txtPrecio.setFont(new Font("Arial", Font.PLAIN, 13));
            camposPreciosDialogo.put(servicio, txtPrecio);
            panelDialogo.add(txtPrecio, gbcDialog);
        }

        int resultado = JOptionPane.showConfirmDialog(main, panelDialogo, "Configurar Precios de Servicios",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            boolean cambiosRealizados = false;
            StringBuilder errores = new StringBuilder();
            for (Map.Entry<TipoServicio, JTextField> entry : camposPreciosDialogo.entrySet()) {
                TipoServicio servicio = entry.getKey();
                String valorInput = entry.getValue().getText().trim();
                try {
                    int nuevoPrecio = Integer.parseInt(valorInput);
                    if (nuevoPrecio < 0) {
                        errores.append("El precio para ").append(servicio.getDescripcion()).append(" no puede ser negativo.\n");
                        continue;
                    }
                    if (nuevoPrecio != servicio.getPrecio()) {
                        servicio.setPrecioConfigurado(nuevoPrecio);
                        cambiosRealizados = true;
                    }
                } catch (NumberFormatException ex) {
                    errores.append("Valor inválido para ").append(servicio.getDescripcion()).append(": '").append(entry.getValue().getText()).append("'.\n");
                } catch (SQLException ex) {
                    errores.append("Error BD al guardar ").append(servicio.getDescripcion()).append(": ").append(ex.getMessage()).append("\n");
                }
            }
            if (errores.length() > 0) {
                JOptionPane.showMessageDialog(main, "Errores:\n" + errores.toString(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
            } else if (cambiosRealizados) {
                JOptionPane.showMessageDialog(main, "Precios actualizados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatosDashboard();
            }
        }
    }

    private void prepararFiltrosReporteCitas() {
        if (comboAnioReporteCitasFiltro == null) {
            comboAnioReporteCitasFiltro = new JComboBox<>();
            int anioActual = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 0; i < 5; i++) comboAnioReporteCitasFiltro.addItem(String.valueOf(anioActual - i));
        }
        if (comboMesReporteCitasFiltro == null) {
            comboMesReporteCitasFiltro = new JComboBox<>(new String[]{
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            });
            comboMesReporteCitasFiltro.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        }
    }

    private void accionExportarReporteMensualCitasPDF(ActionEvent e) {
        prepararFiltrosReporteCitas();

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.add(new JLabel("Año:")); panelFiltro.add(comboAnioReporteCitasFiltro);
        panelFiltro.add(new JLabel("Mes:")); panelFiltro.add(comboMesReporteCitasFiltro);

        int opt = JOptionPane.showConfirmDialog(main, panelFiltro, "Seleccionar Mes y Año para Reporte", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        int anioSeleccionado = Integer.parseInt((String) comboAnioReporteCitasFiltro.getSelectedItem());
        int mesSeleccionado = comboMesReporteCitasFiltro.getSelectedIndex();

        List<Cita> citasDelMes = main.citasPagadas.stream()
                .filter(c -> c.getFechaHora() != null)
                .filter(c -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(c.getFechaHora());
                    return cal.get(Calendar.YEAR) == anioSeleccionado && cal.get(Calendar.MONTH) == mesSeleccionado;
                })
                .sorted(Comparator.comparing(Cita::getFechaHora))
                .collect(Collectors.toList());

        if (citasDelMes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay citas (pagadas) para el mes y año seleccionados.", "Sin Datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nombreArchivoSugerido = "Reporte_Citas_" + comboMesReporteCitasFiltro.getSelectedItem() + "_" + anioSeleccionado + ".pdf";
        exportarPDF("Guardar Reporte Mensual de Citas", nombreArchivoSugerido, documento -> {
            agregarTituloPDF(documento, "Reporte Mensual de Citas - " + comboMesReporteCitasFiltro.getSelectedItem() + " " + anioSeleccionado);

            PdfPTable tabla = new PdfPTable(new float[]{0.7f, 1.3f, 1.3f, 1.3f, 1.5f, 1f, 0.7f});
            tabla.setWidthPercentage(100);
            addEncabezadosTablaPDF(tabla, new String[]{"ID Cita", "Fecha", "Mascota", "Propietario", "Veterinario", "Motivo", "Precio"}, new Color(70, 130, 180));
            com.lowagie.text.Font fontCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 8);
            long totalMes = 0;

            for (Cita cita : citasDelMes) {
                Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
                Veterinario v = main.veterinariosPanel.encontrarVeterinarioPorDoc(cita.getDocVeterinario());
                int precio = cita.getMotivo().getPrecio();
                totalMes += precio;

                tabla.addCell(crearCeldaPDF(cita.getId().substring(0,Math.min(8, cita.getId().length()))+"...", fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(FECHA_HORA_FORMAT.format(cita.getFechaHora()), fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(m != null ? m.getNombre() : "N/A", fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(p != null ? p.getNombre() : "N/A", fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(v != null ? v.getNombre() : "N/A", fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(cita.getMotivo().getDescripcion(), fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(MONEDA_FORMAT.format(precio), fontCuerpo, Element.ALIGN_RIGHT));
            }
            documento.add(tabla);
            agregarParrafoPDF(documento, "Total Ingresos del Mes: " + MONEDA_FORMAT.format(totalMes), FontFactory.HELVETICA_BOLD, 12, Element.ALIGN_RIGHT, 15);
        }, PageSize.A4.rotate());
    }

    private void accionExportarResumenIngresosPDF(ActionEvent e) {
        exportarPDF("Guardar Resumen de Ingresos", "Reporte_Resumen_Ingresos.pdf", documento -> {
            agregarTituloPDF(documento, "Resumen de Ingresos por Citas Pagadas");
            agregarParrafoPDF(documento, "Fecha de Generación: " + FECHA_HORA_FORMAT.format(new Date()), FontFactory.TIMES_ROMAN, 10, com.lowagie.text.Font.ITALIC, Element.ALIGN_LEFT, 0);
            documento.add(Chunk.NEWLINE);

            PdfPTable tablaServicios = new PdfPTable(3);
            tablaServicios.setWidthPercentage(90);
            tablaServicios.setWidths(new float[]{3f, 1f, 2f});
            addEncabezadosTablaPDF(tablaServicios, new String[]{"Tipo de Servicio", "Cantidad", "Ingresos Generados"}, new Color(0, 102, 102));

            Map<TipoServicio, List<Cita>> citasPorServicio = main.citasPagadas.stream()
                    .filter(c -> c.getMotivo() != null)
                    .collect(Collectors.groupingBy(Cita::getMotivo));
            long granTotalIngresos = 0;
            com.lowagie.text.Font fontCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10);

            for(Map.Entry<TipoServicio, List<Cita>> entry : citasPorServicio.entrySet()){
                TipoServicio servicio = entry.getKey();
                List<Cita> citasDeEsteServicio = entry.getValue();
                long ingresosEsteServicio = citasDeEsteServicio.stream().mapToLong(c -> servicio.getPrecio()).sum();
                granTotalIngresos += ingresosEsteServicio;
                tablaServicios.addCell(crearCeldaPDF(servicio.getDescripcion(), fontCuerpo, Element.ALIGN_LEFT));
                tablaServicios.addCell(crearCeldaPDF(String.valueOf(citasDeEsteServicio.size()), fontCuerpo, Element.ALIGN_CENTER));
                tablaServicios.addCell(crearCeldaPDF(MONEDA_FORMAT.format(ingresosEsteServicio), fontCuerpo, Element.ALIGN_RIGHT));
            }
            documento.add(tablaServicios);
            documento.add(Chunk.NEWLINE);
            agregarParrafoPDF(documento, "Ingresos Totales Generales: " + MONEDA_FORMAT.format(granTotalIngresos), FontFactory.HELVETICA_BOLD, 14, Element.ALIGN_RIGHT, 0);
        }, PageSize.A4);
    }

    private void accionExportarDetalleServiciosPDF(ActionEvent e) {
        exportarPDF("Guardar Detalle de Servicios Solicitados", "Reporte_Detalle_Servicios.pdf", documento -> {
            agregarTituloPDF(documento, "Detalle de Servicios Solicitados (Todas las Citas)");
            agregarParrafoPDF(documento, "Fecha de Generación: " + FECHA_HORA_FORMAT.format(new Date()), FontFactory.TIMES_ROMAN, 10, com.lowagie.text.Font.ITALIC, Element.ALIGN_LEFT, 0);
            documento.add(Chunk.NEWLINE);

            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(90);
            tabla.setWidths(new float[]{3f, 1f, 1.5f});
            addEncabezadosTablaPDF(tabla, new String[]{"Tipo de Servicio", "Cantidad Total Solicitada", "Precio Unitario Actual"}, new Color(102, 0, 102));

            List<Cita> todasLasCitasAdmin = new ArrayList<>(main.citas);
            todasLasCitasAdmin.addAll(main.citasPagadas);
            Map<TipoServicio, Long> conteoServiciosAdmin = todasLasCitasAdmin.stream()
                    .filter(c -> c.getMotivo() != null)
                    .collect(Collectors.groupingBy(Cita::getMotivo, Collectors.counting()));
            com.lowagie.text.Font fontCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10);
            long totalServicios = 0;

            for(Map.Entry<TipoServicio, Long> entry : conteoServiciosAdmin.entrySet()){
                TipoServicio servicio = entry.getKey();
                long cantidad = entry.getValue();
                totalServicios += cantidad;
                tabla.addCell(crearCeldaPDF(servicio.getDescripcion(), fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaPDF(String.valueOf(cantidad), fontCuerpo, Element.ALIGN_CENTER));
                tabla.addCell(crearCeldaPDF(MONEDA_FORMAT.format(servicio.getPrecio()), fontCuerpo, Element.ALIGN_RIGHT));
            }
            documento.add(tabla);
            agregarParrafoPDF(documento, "Cantidad Total de Servicios Registrados: " + totalServicios, FontFactory.HELVETICA_BOLD, 12, Element.ALIGN_RIGHT, 15);
        }, PageSize.A4);
    }

    @FunctionalInterface
    interface PdfContentBuilder { void build(Document document) throws DocumentException; }

    private void exportarPDF(String dialogoTitulo, String nombreArchivoSugerido, PdfContentBuilder contentBuilder, com.lowagie.text.Rectangle pageSize) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogoTitulo);
        fileChooser.setSelectedFile(new java.io.File(nombreArchivoSugerido));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File archivoParaGuardar = fileChooser.getSelectedFile();
            if (!archivoParaGuardar.getName().toLowerCase().endsWith(".pdf")) {
                archivoParaGuardar = new java.io.File(archivoParaGuardar.getParentFile(), archivoParaGuardar.getName() + ".pdf");
            }
            try {
                Document documento = new Document(pageSize, 36, 36, 54, 36);
                PdfWriter.getInstance(documento, new FileOutputStream(archivoParaGuardar));
                documento.open();
                contentBuilder.build(documento);
                documento.close();
                JOptionPane.showMessageDialog(this, "Reporte PDF generado exitosamente en:\n" + archivoParaGuardar.getAbsolutePath(), "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(archivoParaGuardar);
                    } catch (IOException ex) {
                        System.err.println("No se pudo abrir el PDF automáticamente: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void agregarTituloPDF(Document document, String textoTitulo) throws DocumentException {
        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(25, 55, 100));
        Paragraph titulo = new Paragraph(textoTitulo, font);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);
    }

    private void agregarParrafoPDF(Document document, String texto, String nombreFuente, int tamanoFuente, int estiloFuente, int alineacion, float espaciadoAntes) throws DocumentException {
        com.lowagie.text.Font font = FontFactory.getFont(nombreFuente, tamanoFuente, estiloFuente);
        Paragraph parrafo = new Paragraph(texto, font);
        parrafo.setAlignment(alineacion);
        if (espaciadoAntes > 0) parrafo.setSpacingBefore(espaciadoAntes);
        document.add(parrafo);
    }
    private void agregarParrafoPDF(Document document, String texto, String nombreFuente, int tamanoFuente, int alineacion, float espaciadoAntes) throws DocumentException {
        agregarParrafoPDF(document, texto, nombreFuente, tamanoFuente, com.lowagie.text.Font.NORMAL, alineacion, espaciadoAntes);
    }

    private void addEncabezadosTablaPDF(PdfPTable tabla, String[] encabezados, Color colorFondo) {
        com.lowagie.text.Font fontEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String encabezado : encabezados) {
            PdfPCell cell = new PdfPCell(new Phrase(encabezado, fontEncabezado));
            cell.setBackgroundColor(colorFondo);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(6);
            tabla.addCell(cell);
        }
        tabla.setHeaderRows(1);
    }

    private PdfPCell crearCeldaPDF(String texto, com.lowagie.text.Font fuente, int alineacionHorizontal) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(alineacionHorizontal);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(5);
        celda.setBorderWidth(0.5f);
        celda.setBorderColor(new Color(190, 190, 190));
        return celda;
    }

}