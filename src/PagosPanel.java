import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;

public class PagosPanel extends JPanel {
    private Main main;
    private List<Cita> citasPagadas;
    private JTextField txtBuscar;
    private JPanel cardsPanel;

    public PagosPanel(Main main, List<Cita> citasPagadas) {
        this.main = main;
        this.citasPagadas = citasPagadas;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setBackground(getBackground());

        JLabel lblTitulo = new JLabel("Historial de Pagos", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(36, 46, 120));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por mascota, propietario o motivo"));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarCards(); }
            public void removeUpdate(DocumentEvent e) { filtrarCards(); }
            public void changedUpdate(DocumentEvent e) { filtrarCards(); }
        });

        JButton btnExportarPdf = new JButton("Exportar a PDF");
        btnExportarPdf.setFont(new Font("Arial", Font.BOLD, 14));
        btnExportarPdf.addActionListener(e -> exportarCitasAPdf());

        JPanel searchBtnPanel = new JPanel(new BorderLayout());
        searchBtnPanel.setOpaque(false);
        searchBtnPanel.add(txtBuscar, BorderLayout.CENTER);
        searchBtnPanel.add(btnExportarPdf, BorderLayout.EAST);

        topPanel.add(lblTitulo, BorderLayout.NORTH);
        topPanel.add(searchBtnPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 2, 26, 16));
        cardsPanel.setBackground(getBackground());
        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.addActionListener(e -> main.irHome());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottom.setOpaque(false);
        bottom.add(btnVolver);
        add(bottom, BorderLayout.SOUTH);

        actualizarCards(citasPagadas);
    }

    public void actualizarCards(List<Cita> lista) {
        cardsPanel.removeAll();
        for (Cita cita : lista) {
            cardsPanel.add(crearCardPago(cita));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel crearCardPago(Cita cita) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setPreferredSize(new Dimension(350, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(18, 15, 18, 17)
        ));

        JLabel lblAvatar = new JLabel(new ImageIcon(getClass().getResource("/Pago.png")));
        lblAvatar.setPreferredSize(new Dimension(72, 72));
        card.add(lblAvatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
        Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
        String nombreProp = (p != null) ? p.getNombre() : "-";
        String nombreMasc = (m != null) ? m.getNombre() : "-";
        String motivo = cita.getMotivo();
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String fecha = f.format(cita.getFechaHora());
        int precio = CitasPanel.precioPorTipoServicio(motivo);


        JLabel lblInfo = new JLabel("<html>"
                + "<b>Mascota:</b> " + nombreMasc
                + "<br><b>Propietario:</b> " + nombreProp
                + "<br><b>Motivo:</b> " + motivo
                + "<br><b>Fecha y hora:</b> " + fecha
                + "<br><b>Precio:</b> $" + precio
                + "</html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 15));
        info.add(lblInfo);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    public void filtrarCards() {
        String filtro = txtBuscar.getText().toLowerCase();
        List<Cita> filtradas = new ArrayList<>();
        for (Cita c : citasPagadas) {
            Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(c.getDocPropietario());
            Mascota m = main.mascotasPanel.encontrarMascotaPorId(c.getIdMascota());
            if ( (m != null && m.getNombre().toLowerCase().contains(filtro)) ||
                    (p != null && p.getNombre().toLowerCase().contains(filtro)) ||
                    (c.getMotivo().toLowerCase().contains(filtro)) ||
                    filtro.isEmpty()
            ) {
                filtradas.add(c);
            }
        }
        actualizarCards(filtradas);
    }

    private void exportarCitasAPdf() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("reporte_citas_pagadas.pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) return;

            Document documento = new Document();
            PdfWriter.getInstance(documento, new java.io.FileOutputStream(fileChooser.getSelectedFile()));
            documento.open();

            com.lowagie.text.Font fontTitulo = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontHeader = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font fontNormal = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11);

            documento.add(new Paragraph("Reporte de Citas Pagadas", fontTitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell(new PdfPCell(new Phrase("Fecha y Hora", fontHeader)));
            tabla.addCell(new PdfPCell(new Phrase("Mascota", fontHeader)));
            tabla.addCell(new PdfPCell(new Phrase("Propietario", fontHeader)));
            tabla.addCell(new PdfPCell(new Phrase("Motivo", fontHeader)));
            tabla.addCell(new PdfPCell(new Phrase("Precio", fontHeader)));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

            for (Cita cita : citasPagadas) {
                Propietario p = main.propietariosPanel.encontrarPropietarioPorDoc(cita.getDocPropietario());
                Mascota m = main.mascotasPanel.encontrarMascotaPorId(cita.getIdMascota());
                String nombreMascota = (m != null) ? m.getNombre() : "-";
                String nombreProp = (p != null) ? p.getNombre() : "-";
                String motivo = cita.getMotivo();
                int precio = CitasPanel.precioPorTipoServicio(motivo);

                tabla.addCell(new PdfPCell(new Phrase(sdf.format(cita.getFechaHora()), fontNormal)));
                tabla.addCell(new PdfPCell(new Phrase(nombreMascota, fontNormal)));
                tabla.addCell(new PdfPCell(new Phrase(nombreProp, fontNormal)));
                tabla.addCell(new PdfPCell(new Phrase(motivo, fontNormal)));
                tabla.addCell(new PdfPCell(new Phrase("$" + precio, fontNormal)));
            }

            documento.add(tabla);
            documento.close();

            JOptionPane.showMessageDialog(this, "Exportado correctamente.", "PDF exitoso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falló la exportación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
