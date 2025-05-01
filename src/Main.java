import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;

    public List<Propietario> propietarios;
    public List<Mascota> mascotas;
    public List<Veterinario> veterinarios;
    public List<Cita> citas;
    public List<Cita> citasPagadas = new ArrayList<>();

    HomePanel homePanel;
    public PropietariosPanel propietariosPanel;
    public MascotasPanel mascotasPanel;
    public VeterinariosPanel veterinariosPanel;
    public CitasPanel citasPanel;
    public PagosPanel pagosPanel;
    public PagoPanel pagoPanel;

    public Main() {
        setTitle("Sistema Gestión Veterinaria");
        setSize(900, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        cargarDatosIniciales();

        homePanel = new HomePanel(this);
        propietariosPanel = new PropietariosPanel(this);
        mascotasPanel = new MascotasPanel(this);
        veterinariosPanel = new VeterinariosPanel(this);
        citasPanel = new CitasPanel(this);
        pagosPanel = new PagosPanel(this, citasPagadas);
        pagoPanel = new PagoPanel(this);

        mainPanel.add(homePanel, "home");
        mainPanel.add(propietariosPanel, "propietarios");
        mainPanel.add(mascotasPanel, "mascotas");
        mainPanel.add(veterinariosPanel, "veterinarios");
        mainPanel.add(citasPanel, "citas");
        mainPanel.add(pagosPanel, "pagos");
        mainPanel.add(pagoPanel, "pago");

        add(mainPanel);
        cardLayout.show(mainPanel, "home");
    }

    private void cargarDatosIniciales() {
        try {
            Database.conectar();
            propietarios = new ArrayList<>(PropietarioDAO.listarTodos());
            mascotas = new ArrayList<>(MascotaDAO.listarTodos());
            veterinarios = new ArrayList<>(VeterinarioDAO.listarTodos());
            List<Cita> todasLasCitas = new ArrayList<>(CitaDAO.listarTodos());

            citas = new ArrayList<>();
            citasPagadas = new ArrayList<>();
            for (Cita c : todasLasCitas) {
                if ("PAGADA".equalsIgnoreCase(c.getEstado())) {
                    citasPagadas.add(c);
                } else {
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos iniciales: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            propietarios = new ArrayList<>();
            mascotas = new ArrayList<>();
            veterinarios = new ArrayList<>();
            citas = new ArrayList<>();
        }
    }

    public void irHome() {
        cardLayout.show(mainPanel, "home");
    }
    public void abrirPanelPropietarios() {
        if (propietariosPanel != null) propietariosPanel.actualizarCards();
        cardLayout.show(mainPanel, "propietarios");
    }
    public void abrirPanelMascotas() {
        if (mascotasPanel != null) mascotasPanel.actualizarCards();
        cardLayout.show(mainPanel, "mascotas");
    }
    public void abrirPanelVeterinarios() {
        if (veterinariosPanel != null) veterinariosPanel.actualizarCards();
        cardLayout.show(mainPanel, "veterinarios");
    }
    public void abrirPanelCitas() {
        if (citasPanel != null) citasPanel.actualizarCards();
        cardLayout.show(mainPanel, "citas");
    }
    public void abrirPanelPagos() {
        if (pagosPanel != null) pagosPanel.actualizarCards(citasPagadas);
        cardLayout.show(mainPanel, "pagos");
    }
    public void abrirPanelPago(Cita cita) {
        if (pagoPanel != null) pagoPanel.setCita(cita);
        cardLayout.show(mainPanel, "pago");
    }

    public void agregarPropietario(Propietario p) {
        for (Propietario existente : propietarios) {
            if (existente.getDocumento().equals(p.getDocumento())) {
                JOptionPane.showMessageDialog(this, "El propietario con documento " + p.getDocumento() + " ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        propietarios.add(p);
        try { PropietarioDAO.guardar(p); } catch (SQLException ex) { ex.printStackTrace(); }
        if (propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void agregarMascota(Mascota m) {
        mascotas.add(m);
        try { MascotaDAO.guardar(m); } catch (SQLException ex) { ex.printStackTrace(); }
        if (mascotasPanel != null) mascotasPanel.actualizarCards();
        if (propietariosPanel != null) propietariosPanel.actualizarCards();
    }
    public void agregarVeterinario(Veterinario v) {
        for (Veterinario existente : veterinarios) {
            if (existente.getDocumento().equals(v.getDocumento())) {
                JOptionPane.showMessageDialog(this, "El veterinario con documento " + v.getDocumento() + " ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        veterinarios.add(v);
        try { VeterinarioDAO.guardar(v); } catch (SQLException ex) { ex.printStackTrace(); }
        if (veterinariosPanel != null) veterinariosPanel.actualizarCards();
    }
    public void agregarCita(Cita c) {
        citas.add(c);
        try { CitaDAO.guardar(c); } catch (SQLException ex) { ex.printStackTrace(); }
        if (citasPanel != null) citasPanel.actualizarCards();
    }
    public void eliminarPropietario(Propietario p) {
        List<Mascota> mascotasAEliminar = new ArrayList<>();
        for (Mascota m : mascotas) {
            if (m.getPropietarioDocumento().equals(p.getDocumento())) mascotasAEliminar.add(m);
        }
        for (Mascota m : mascotasAEliminar) eliminarMascota(m);
        List<Cita> citasAEliminar = new ArrayList<>();
        for (Cita c : citas) if (c.getDocPropietario().equals(p.getDocumento())) citasAEliminar.add(c);
        for (Cita c : citasPagadas) if (c.getDocPropietario().equals(p.getDocumento())) citasAEliminar.add(c);
        for (Cita c : citasAEliminar) eliminarCita(c);

        propietarios.remove(p);
        try { PropietarioDAO.eliminarPorDocumento(p.getDocumento()); } catch (SQLException ex) { ex.printStackTrace(); }
        if (propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void eliminarMascota(Mascota m) {
        List<Cita> citasAEliminar = new ArrayList<>();
        for (Cita c : citas) if (c.getIdMascota().equals(m.getId())) citasAEliminar.add(c);
        for (Cita c : citasPagadas) if (c.getIdMascota().equals(m.getId())) citasAEliminar.add(c);
        for (Cita c : citasAEliminar) eliminarCita(c);

        mascotas.remove(m);
        try { MascotaDAO.eliminarPorId(m.getId()); } catch (SQLException ex) { ex.printStackTrace(); }
        if (mascotasPanel != null) mascotasPanel.actualizarCards();
        if (propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void eliminarVeterinario(Veterinario v) {
        List<Cita> citasAEliminar = new ArrayList<>();
        for (Cita c : citas) if (c.getDocVeterinario().equals(v.getDocumento())) citasAEliminar.add(c);
        for (Cita c : citasPagadas) if (c.getDocVeterinario().equals(v.getDocumento())) citasAEliminar.add(c);
        for (Cita c : citasAEliminar) eliminarCita(c);

        veterinarios.remove(v);
        try { VeterinarioDAO.eliminarPorDocumento(v.getDocumento()); } catch (SQLException ex) { ex.printStackTrace(); }
        if (veterinariosPanel != null) veterinariosPanel.actualizarCards();
    }

    public void eliminarCita(Cita c) {
        boolean removed = citas.remove(c);
        if (!removed) citasPagadas.remove(c);
        try { CitaDAO.eliminarPorId(c.getId()); } catch (SQLException ex) { ex.printStackTrace(); }
        if (citasPanel != null) citasPanel.actualizarCards();
        if (pagosPanel != null) pagosPanel.actualizarCards(citasPagadas);
    }

    public void marcarCitaComoPagada(Cita cita) {
        cita.setEstado("Pagada");
        citas.remove(cita);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
