package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends JFrame {
    public CardLayout cardLayout;
    public JPanel mainPanel;

    public List<Propietario> propietarios = new ArrayList<>();
    public List<Mascota> mascotas = new ArrayList<>();
    public List<Veterinario> veterinarios = new ArrayList<>();
    public List<Cita> citas = new ArrayList<>();
    public List<Cita> citasPagadas = new ArrayList<>();

    LoginPanel loginPanel;
    HomePanel homePanel;
    public PropietariosPanel propietariosPanel;
    public MascotasPanel mascotasPanel;
    public VeterinariosPanel veterinariosPanel;
    public CitasPanel citasPanel;
    public PagosPanel pagosPanel;
    public PagoPanel pagoPanel;
    AdminPanel adminPanel;

    private boolean esAdmin = false;
    // private static final SimpleDateFormat DEBUG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Depuración


    public Main() {
        setTitle("Sistema de Gestión Veterinaria Profesional");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        cargarDatosInicialesConManejoDeError();

        loginPanel        = new LoginPanel(this);
        homePanel           = new HomePanel(this);
        propietariosPanel   = new PropietariosPanel(this);
        mascotasPanel       = new MascotasPanel(this);
        veterinariosPanel   = new VeterinariosPanel(this);
        citasPanel          = new CitasPanel(this);
        pagosPanel          = new PagosPanel(this, this.citasPagadas);
        pagoPanel           = new PagoPanel(this);
        adminPanel          = new AdminPanel(this);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(homePanel, "home");
        mainPanel.add(propietariosPanel, "propietarios");
        mainPanel.add(mascotasPanel, "mascotas");
        mainPanel.add(veterinariosPanel, "veterinarios");
        mainPanel.add(citasPanel, "citas");
        mainPanel.add(pagosPanel, "historialPagos");
        mainPanel.add(pagoPanel, "procesarPago");
        mainPanel.add(adminPanel, "adminPanel");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
    public boolean isEsAdmin() {
        return esAdmin;
    }

    private void cargarDatosInicialesConManejoDeError() {
        try {
            Database.conectar();

            Map<String, Integer> preciosConfigurados = ConfiguracionPreciosDAO.cargarPrecios();
            for (TipoServicio servicio : TipoServicio.values()) {
                if (preciosConfigurados.containsKey(servicio.name())) {
                    servicio.setPrecioInterno(preciosConfigurados.get(servicio.name()));
                } else {
                    ConfiguracionPreciosDAO.guardarPrecio(servicio.name(), servicio.getPrecioPorDefecto());
                }
            }
            // System.out.println("Precios de servicios cargados/verificados en BD."); // Depuración

            propietarios = new ArrayList<>(PropietarioDAO.listarTodos());
            mascotas     = new ArrayList<>(MascotaDAO.listarTodos());
            veterinarios = new ArrayList<>(VeterinarioDAO.listarTodos());
            List<Cita> todasLasCitasBD = new ArrayList<>(CitaDAO.listarTodos());

            citas.clear();
            citasPagadas.clear();
            // System.out.println("--- Cargando Citas desde BD en Main ---"); // Depuración
            for (Cita c : todasLasCitasBD) {
                // String fechaStr = (c.getFechaHora() != null) ? DEBUG_SDF.format(c.getFechaHora()) : "FECHA_NULA"; // Depuración
                // String vetDoc = (c.getDocVeterinario() != null && !c.getDocVeterinario().isEmpty()) ? c.getDocVeterinario() : "VET_DOC_NULO_O_VACIO"; // Depuración
                // System.out.println(String.format("  BD Cita ID: %s, Fecha: %s, DocVet: %s, Estado: %s, Motivo: %s", // Depuración quitada
                //         c.getId().substring(0,Math.min(8,c.getId().length())), fechaStr, vetDoc, c.getEstado(), (c.getMotivo() != null ? c.getMotivo().name() : "MOTIVO_NULO")));

                if ("PAGADA".equalsIgnoreCase(c.getEstado())) {
                    citasPagadas.add(c);
                } else {
                    citas.add(c);
                }
            }
            // System.out.println("--- Fin Carga Citas BD ---"); // Depuración
            // System.out.println("Datos cargados desde la BD. Propietarios: " + propietarios.size() + ", Mascotas: " + mascotas.size() + ", Citas Pendientes: " + citas.size() + ", Citas Pagadas: " + citasPagadas.size()); // Depuración
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error Crítico: No se pudieron cargar los datos iniciales.\n" + e.getMessage(),
                    "Error de Carga de Datos", JOptionPane.ERROR_MESSAGE);
            propietarios = new ArrayList<>(); mascotas = new ArrayList<>(); veterinarios = new ArrayList<>();
            citas = new ArrayList<>(); citasPagadas = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error inesperado durante la carga de datos iniciales: " + e.getMessage(),
                    "Error Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void irHome() {
        irHome(this.esAdmin);
    }

    public void irHome(boolean configurarHomeComoAdmin) {
        if (homePanel != null) {
            homePanel.configurarParaVista(configurarHomeComoAdmin);
        }
        cardLayout.show(mainPanel, "home");
    }

    public void irLogin() {
        setEsAdmin(false);
        cardLayout.show(mainPanel, "login");
    }

    public void abrirPanelPropietarios() { if(propietariosPanel!=null) propietariosPanel.actualizarCards(); cardLayout.show(mainPanel, "propietarios"); }
    public void abrirPanelMascotas()     { if(mascotasPanel!=null) mascotasPanel.actualizarCards(); cardLayout.show(mainPanel, "mascotas"); }
    public void abrirPanelVeterinarios() { if(veterinariosPanel!=null) veterinariosPanel.actualizarCards(); cardLayout.show(mainPanel, "veterinarios"); }
    public void abrirPanelCitas()        { if(citasPanel!=null) citasPanel.actualizarCards(); cardLayout.show(mainPanel, "citas"); }
    public void abrirPanelPagos()        { if(pagosPanel!=null) pagosPanel.actualizarCards(this.citasPagadas); cardLayout.show(mainPanel, "historialPagos"); }
    public void abrirPanelPago(Cita cita){ if(pagoPanel!=null) pagoPanel.setCita(cita); cardLayout.show(mainPanel, "procesarPago"); }

    public void abrirAdminPanel() {
        if (this.esAdmin) {
            if (adminPanel != null) adminPanel.cargarDatosDashboard();
            cardLayout.show(mainPanel, "adminPanel");
        } else {
            JOptionPane.showMessageDialog(this, "Acceso denegado. Por favor, inicie sesión como administrador.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            irLogin();
        }
    }

    public void solicitarPasswordParaAdminPanel() {
        JPasswordField passwordField = new JPasswordField(15);
        JPanel panelLoginAdmin = new JPanel(new GridLayout(0,1,5,5));
        panelLoginAdmin.add(new JLabel("Contraseña de Administrador:"));
        panelLoginAdmin.add(passwordField);
        SwingUtilities.invokeLater(passwordField::requestFocusInWindow);

        int option = JOptionPane.showConfirmDialog( this, panelLoginAdmin, "Acceso a Administración",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if ("adminAres".equals(password)) {
                setEsAdmin(true);
                if (adminPanel != null) adminPanel.cargarDatosDashboard();
                cardLayout.show(mainPanel, "adminPanel");
            } else {
                JOptionPane.showMessageDialog( this, "Contraseña incorrecta.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void agregarCita(Cita c) {
        if (c == null || c.getFechaHora() == null || c.getMotivo() == null) {
            JOptionPane.showMessageDialog(this, "La cita tiene datos incompletos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // System.out.println("Main.agregarCita - Recibida para guardar: Cita ID=" + c.getId().substring(0,8) + // Depuración
        //         ", VetDoc=" + ((c.getDocVeterinario() != null) ? c.getDocVeterinario() : "NULO") +
        //         ", Fecha=" + ((c.getFechaHora() != null) ? DEBUG_SDF.format(c.getFechaHora()) : "NULA"));
        try {
            CitaDAO.guardar(c);
            citas.add(c);
            if(citasPanel != null) citasPanel.actualizarCards();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cita: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void marcarCitaComoPagada(Cita cita) {
        if (cita == null) return;

        // System.out.println("Main.marcarCitaComoPagada - Antes de cambiar estado: Cita ID=" + cita.getId().substring(0,8) + // Depuración
        //         ", VetDoc=" + ((cita.getDocVeterinario() != null) ? cita.getDocVeterinario() : "NULO") +
        //         ", Fecha=" + ((cita.getFechaHora() != null) ? DEBUG_SDF.format(cita.getFechaHora()) : "NULA") +
        //         ", Motivo=" + ((cita.getMotivo() != null) ? cita.getMotivo().name() : "NULO") +
        //         ", EstadoActual=" + cita.getEstado());

        cita.setEstado("PAGADA");
        try {
            CitaDAO.guardar(cita);

            // System.out.println("Main.marcarCitaComoPagada - DESPUÉS de guardar en BD: Cita ID=" + cita.getId().substring(0,8) + // Depuración
            //         ", VetDoc=" + ((cita.getDocVeterinario() != null) ? cita.getDocVeterinario() : "NULO") +
            //         ", Estado nuevo=" + cita.getEstado());

            boolean fueRemovida = citas.remove(cita);
            // System.out.println("  Removida de lista 'citas' (pendientes)? " + fueRemovida); // Depuración

            if (!citasPagadas.stream().anyMatch(cp -> cp.getId().equals(cita.getId()))) {
                citasPagadas.add(cita);
                // System.out.println("  Añadida a lista 'citasPagadas'. Tamaño actual: " + citasPagadas.size()); // Depuración
            }
            // else { // Depuración
            //     System.out.println("  La cita con ID " + cita.getId().substring(0,8) + " ya estaba en 'citasPagadas' o se intentó añadir un duplicado. Verificando instancia...");
            // }

            if(citasPanel != null) citasPanel.actualizarCards();
            if(pagosPanel != null) pagosPanel.actualizarCards(this.citasPagadas);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la cita en la BD: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            cita.setEstado("PENDIENTE");
            if(!citas.contains(cita)) citas.add(cita);
            citasPagadas.removeIf(c -> c.getId().equals(cita.getId()));
        }
    }

    public void agregarPropietario(Propietario p) {
        if (p == null || p.getDocumento() == null || p.getDocumento().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El documento del propietario no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (propietarios.stream().anyMatch(prop -> prop.getDocumento().equals(p.getDocumento()))) {
            JOptionPane.showMessageDialog(this, "Ya existe un propietario con el documento " + p.getDocumento() + ".", "Propietario Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            PropietarioDAO.guardar(p);
            propietarios.add(p);
            if(propietariosPanel != null) propietariosPanel.actualizarCards();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el propietario en la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void agregarMascota(Mascota m) {
        if (m == null || m.getPropietarioDocumento() == null || m.getPropietarioDocumento().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La mascota debe estar asociada a un propietario.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (propietarios.stream().noneMatch(p -> p.getDocumento().equals(m.getPropietarioDocumento()))) {
            JOptionPane.showMessageDialog(this, "El propietario asignado a la mascota no existe.", "Error de Propietario", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            MascotaDAO.guardar(m);
            mascotas.add(m);
            if(mascotasPanel != null) mascotasPanel.actualizarCards();
            if(propietariosPanel != null) propietariosPanel.actualizarCards();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la mascota: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void agregarVeterinario(Veterinario v) {
        if (v == null || v.getDocumento() == null || v.getDocumento().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El documento del veterinario no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (veterinarios.stream().anyMatch(vet -> vet.getDocumento().equals(v.getDocumento()))) {
            JOptionPane.showMessageDialog(this, "Ya existe un veterinario con el documento " + v.getDocumento() + ".", "Veterinario Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            VeterinarioDAO.guardar(v);
            veterinarios.add(v);
            if(veterinariosPanel != null) veterinariosPanel.actualizarCards();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el veterinario: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void eliminarPropietario(Propietario p) throws SQLException {
        if (p == null) return;
        List<Mascota> mascotasDelPropietario = new ArrayList<>();
        for (Mascota m : new ArrayList<>(mascotas)) {
            if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                mascotasDelPropietario.add(m);
            }
        }
        for (Mascota m : mascotasDelPropietario) {
            eliminarMascota(m);
        }
        PropietarioDAO.eliminarPorDocumento(p.getDocumento());
        propietarios.remove(p);
        if(propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void eliminarMascota(Mascota m) throws SQLException {
        if (m == null) return;
        List<Cita> citasDeLaMascota = new ArrayList<>();
        for (Cita c : new ArrayList<>(citas)) {
            if (c.getIdMascota().equals(m.getId())) citasDeLaMascota.add(c);
        }
        for (Cita c : new ArrayList<>(citasPagadas)) {
            if (c.getIdMascota().equals(m.getId())) citasDeLaMascota.add(c);
        }
        for (Cita c : citasDeLaMascota) {
            eliminarCita(c);
        }
        MascotaDAO.eliminarPorId(m.getId());
        mascotas.remove(m);
        if(mascotasPanel != null) mascotasPanel.actualizarCards();
        if(propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void eliminarVeterinario(Veterinario v) throws SQLException {
        if (v == null) return;
        List<Cita> citasDelVeterinario = new ArrayList<>();
        for (Cita c : new ArrayList<>(citas)) {
            if (c.getDocVeterinario() != null && c.getDocVeterinario().equals(v.getDocumento())) citasDelVeterinario.add(c);
        }
        for (Cita c : new ArrayList<>(citasPagadas)) {
            if (c.getDocVeterinario() != null && c.getDocVeterinario().equals(v.getDocumento())) citasDelVeterinario.add(c);
        }
        for (Cita c : citasDelVeterinario) {
            eliminarCita(c);
        }
        VeterinarioDAO.eliminarPorDocumento(v.getDocumento());
        veterinarios.remove(v);
        if(veterinariosPanel != null) veterinariosPanel.actualizarCards();
    }

    public void eliminarCita(Cita c) throws SQLException {
        if (c == null) return;
        CitaDAO.eliminarPorId(c.getId());
        boolean removidaDePendientes = citas.remove(c);
        boolean removidaDePagadas = citasPagadas.remove(c);

        if(citasPanel != null && removidaDePendientes) citasPanel.actualizarCards();
        if(pagosPanel != null && removidaDePagadas) pagosPanel.actualizarCards(this.citasPagadas);
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}