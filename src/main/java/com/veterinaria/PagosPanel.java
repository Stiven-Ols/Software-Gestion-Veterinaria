package com.veterinaria;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.Font;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
// Imports para iText (PDF)
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream; // Para guardar el PDF

public class PagosPanel extends JPanel {
    private Main main;
    private JTextField txtBuscar;
    private JPanel cardsPanel;
    private static final SimpleDateFormat SDF_DISPLAY_CARD_PAGOS = new SimpleDateFormat("dd/MM/yy hh:mm a");

    public PagosPanel(Main main, List<Cita> citasPagadasIniciales) { // El constructor recibe la lista de Main
        this.main = main;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Barra superior -----
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Historial de Pagos y Reportes", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 100));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        txtBuscar = new JTextField(30);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,200,230)), "Buscar (Mascota, Propietario o Motivo)"),
                BorderFactory.createEmptyBorder(3,3,3,3)
        ));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnExportarPdf = new JButton("Exportar a PDF");
        btnExportarPdf.setFont(new Font("Arial", Font.PLAIN, 13));
        btnExportarPdf.setBackground(new Color(60, 140, 200));
        btnExportarPdf.setForeground(Color.BLACK);
        btnExportarPdf.addActionListener(e -> exportarCitasAPdf());

        JPanel panelBusquedaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusquedaBotones.setOpaque(false);
        panelBusquedaBotones.add(txtBuscar);
        panelBusquedaBotones.add(btnExportarPdf);

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

        actualizarCards(main.citasPagadas); // Mostrar todas las citas pagadas al inicio
    }

    public void actualizarCards(List<Cita> listaAMostrar) {
        cardsPanel.removeAll();
        if (listaAMostrar == null || listaAMostrar.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay pagos registrados en el historial.", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(lblVacio, BorderLayout.CENTER);
        } else {
            // Ordenar por fecha, las más recientes primero
            listaAMostrar.sort(Comparator.comparing(Cita::getFechaHora).reversed());
            cardsPanel.setLayout(new GridLayout(0, 2, 15, 15));
            for (Cita cita : listaAMostrar) {
                cardsPanel.add(crearCardPago(cita));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardPago(Cita cita) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(380, 150)); // Altura de la card
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        try {
            URL imgUrl = getClass().getResource("/Pago.png"); // Icono para pago
            if (imgUrl != null) lblAvatar.setIcon(new ImageIcon(imgUrl));
            else lblAvatar.setText("Pago");
        } catch (Exception e) { lblAvatar.setText("Err"); }
        card.add(lblAvatar, BorderLayout.WEST);

        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        String nombrePropietario = (p != null) ? p.getNombre() : "N/A";
        String nombreMascota = (m != null) ? m.getNombre() : "N/A";
        String motivoDesc = (cita.getMotivo() != null) ? cita.getMotivo().getDescripcion() : "N/A";
        int precio = CitasPanel.precioPorTipoServicio(cita.getMotivo()); // Usamos un metodo estático

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        infoPanel.add(new JLabel("<html><b>Mascota: " + nombreMascota + "</b></html>")).setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(new JLabel("Propietario: " + nombrePropietario));
        infoPanel.add(new JLabel("Servicio: " + motivoDesc));
        infoPanel.add(new JLabel("Fecha: " + SDF_DISPLAY_CARD_PAGOS.format(cita.getFechaHora())));
        infoPanel.add(new JLabel("Precio: $" + precio));
        infoPanel.add(new JLabel("Estado: " + cita.getEstado()));


        JPanel centerAlignInfo = new JPanel(new GridBagLayout());
        centerAlignInfo.setOpaque(false);
        centerAlignInfo.add(infoPanel);
        card.add(centerAlignInfo, BorderLayout.CENTER);

        // una posible futura funcion para añadir un botón para ver detalle del pago o reimprimir factura si se necesita

        return card;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        List<Cita> filtradas = new ArrayList<>();

        if (main.citasPagadas != null) {
            for (Cita c : main.citasPagadas) {
                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(c.getDocPropietario());
                Mascota m = main.mascotasPanel.encontrarMascotaPorId(c.getIdMascota());
                String nombrePropietario = (p != null) ? p.getNombre().toLowerCase() : "";
                String nombreMascota = (m != null) ? m.getNombre().toLowerCase() : "";
                String motivoDesc = (c.getMotivo() != null) ? c.getMotivo().getDescripcion().toLowerCase() : "";

                if (filtro.isEmpty() || nombreMascota.contains(filtro) || nombrePropietario.contains(filtro) || motivoDesc.contains(filtro)) {
                    filtradas.add(c);
                }
            }
        }
        actualizarCards(filtradas);
    }

    private void exportarCitasAPdf() {
        if (main.citasPagadas == null || main.citasPagadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay citas pagadas para exportar.", "Sin Datos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new java.io.File("Reporte_Pagos_Veterinaria.pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivoParaGuardar = fileChooser.getSelectedFile();
        // Asegurar que el archivo tenga extensión .pdf
        if (!archivoParaGuardar.getName().toLowerCase().endsWith(".pdf")) {
            archivoParaGuardar = new java.io.File(archivoParaGuardar.getParentFile(), archivoParaGuardar.getName() + ".pdf");
        }


        Document documento = new Document(PageSize.A4, 36, 36, 54, 36); // Márgenes
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivoParaGuardar));
            documento.open();

            // Título del Documento
            com.lowagie.text.Font fontTituloDoc = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 20, com.lowagie.text.Font.BOLD, new Color(25, 55, 100));
            Paragraph tituloDoc = new Paragraph("Reporte de Citas Pagadas - Veterinaria", fontTituloDoc);
            tituloDoc.setAlignment(Element.ALIGN_CENTER);
            tituloDoc.setSpacingAfter(20);
            documento.add(tituloDoc);

            // Información General del Reporte (fecha de generación, etc.)
            Paragraph infoReporte = new Paragraph("Fecha de Generación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()),
                    new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10, com.lowagie.text.Font.ITALIC));
            infoReporte.setAlignment(Element.ALIGN_RIGHT);
            infoReporte.setSpacingAfter(15);
            documento.add(infoReporte);


            // Tabla
            PdfPTable tabla = new PdfPTable(5); // 5 columnas
            tabla.setWidthPercentage(100); // Ancho total de la página
            float[] anchosColumnas = {2.5f, 2f, 2f, 2.5f, 1.5f}; // Proporciones de ancho
            tabla.setWidths(anchosColumnas);

            com.lowagie.text.Font fontCabecera = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, Color.WHITE);
            String[] cabeceras = {"Fecha y Hora", "Mascota", "Propietario", "Servicio", "Precio"};
            for (String cabecera : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(cabecera, fontCabecera));
                cell.setBackgroundColor(new Color(70, 130, 180)); // Azul
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                tabla.addCell(cell);
            }
            tabla.setHeaderRows(1); // Repetir cabecera en cada página

            com.lowagie.text.Font fontCuerpo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10);
            int totalGeneral = 0;

            // Ordenar por fecha antes de generar el PDF
            List<Cita> citasOrdenadas = new ArrayList<>(main.citasPagadas);
            citasOrdenadas.sort(Comparator.comparing(Cita::getFechaHora));


            for (Cita cita : citasOrdenadas) { // Usar la lista ordenada
                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
                Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
                String nombreMascota = (m != null) ? m.getNombre() : "N/A";
                String nombrePropietario = (p != null) ? p.getNombre() : "N/A";
                String motivoDesc = (cita.getMotivo() != null) ? cita.getMotivo().getDescripcion() : "N/A";
                int precio = CitasPanel.precioPorTipoServicio(cita.getMotivo());
                totalGeneral += precio;

                tabla.addCell(crearCeldaTabla(SDF_DISPLAY_CARD_PAGOS.format(cita.getFechaHora()), fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaTabla(nombreMascota, fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaTabla(nombrePropietario, fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaTabla(motivoDesc, fontCuerpo, Element.ALIGN_LEFT));
                tabla.addCell(crearCeldaTabla("$" + String.format("%,d", precio), fontCuerpo, Element.ALIGN_RIGHT)); // Formato moneda
            }

            documento.add(tabla);

            // Pie de página con total general
            Paragraph totalPar = new Paragraph("Total General Pagado: $" + String.format("%,d", totalGeneral),
                    new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD));
            totalPar.setAlignment(Element.ALIGN_RIGHT);
            totalPar.setSpacingBefore(20);
            documento.add(totalPar);


            documento.close();
            JOptionPane.showMessageDialog(this, "Reporte PDF generado exitosamente en:\n" + archivoParaGuardar.getAbsolutePath(),
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            // Abrir el PDF generado (opcional)
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(archivoParaGuardar);
                } catch (java.io.IOException ex) {
                    System.err.println("No se pudo abrir el PDF automáticamente: " + ex.getMessage());
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte PDF: " + ex.getMessage(),
                    "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Metodo funcional para crear celdas de tabla con estilo
    private PdfPCell crearCeldaTabla(String texto, com.lowagie.text.Font fuente, int alineacionHorizontal) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(alineacionHorizontal);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(6);
        celda.setBorderWidth(0.5f);
        celda.setBorderColor(new Color(200, 200, 200));
        return celda;
    }
}
