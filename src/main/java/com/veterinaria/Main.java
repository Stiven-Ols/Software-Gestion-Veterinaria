package com.veterinaria;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    public CardLayout cardLayout; // public para que LoginPanel/otros puedan cambiar de vista
    public JPanel mainPanel;    // public por la misma razón

    // Usar List para las declaraciones, es mejor práctica. Inicializar para evitar NullPointer.
    public List<Propietario> propietarios = new ArrayList<>();
    public List<Mascota> mascotas = new ArrayList<>();
    public List<Veterinario> veterinarios = new ArrayList<>();
    public List<Cita> citas = new ArrayList<>(); // Citas Pendientes
    public List<Cita> citasPagadas = new ArrayList<>(); // Citas ya Pagadas

    // Paneles
    LoginPanel loginPanel; // Panel de Login como primera vista
    HomePanel homePanel;
    public PropietariosPanel propietariosPanel;
    public MascotasPanel mascotasPanel;
    public VeterinariosPanel veterinariosPanel;
    public CitasPanel citasPanel;
    public PagosPanel pagosPanel;
    public PagoPanel pagoPanel;

    // private boolean esAdmin = false; // Para controlar el acceso de administrador

    public Main() {
        setTitle("Sistema de Gestión Veterinaria Profesional");
        // Tamaño un poco más grande para acomodar mejor los paneles
        setSize(950, 600); // Ajusta según tus preferencias
        setLocationRelativeTo(null); // Centrar en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cerrar la aplicación al cerrar la ventana
        // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Para manejar el cierre manualmente si es necesario (ej. guardar datos)
        // addWindowListener(new java.awt.event.WindowAdapter() {
        //     @Override
        //     public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        //         Database.cerrarConexion(); // Buena práctica cerrar la conexión
        //         System.exit(0);
        //     }
        // });
        setResizable(true); // Permitir redimensionar, o false si prefieres tamaño fijo

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Carga inicial de datos desde la base de datos
        cargarDatosInicialesConManejoDeError();

        // Inicialización de los paneles
        loginPanel        = new LoginPanel(this);
        homePanel           = new HomePanel(this);
        propietariosPanel   = new PropietariosPanel(this);
        mascotasPanel       = new MascotasPanel(this);
        veterinariosPanel   = new VeterinariosPanel(this);
        citasPanel          = new CitasPanel(this);
        pagosPanel          = new PagosPanel(this, this.citasPagadas); // Pasa la lista de citas pagadas
        pagoPanel           = new PagoPanel(this);

        // Añadir paneles al CardLayout con nombres descriptivos
        mainPanel.add(loginPanel, "login");
        mainPanel.add(homePanel, "home");
        mainPanel.add(propietariosPanel, "propietarios");
        mainPanel.add(mascotasPanel, "mascotas");
        mainPanel.add(veterinariosPanel, "veterinarios");
        mainPanel.add(citasPanel, "citas");
        mainPanel.add(pagosPanel, "historialPagos"); // Nombre más descriptivo
        mainPanel.add(pagoPanel, "procesarPago");   // Nombre más descriptivo

        add(mainPanel); // Añadir el panel principal al JFrame
        cardLayout.show(mainPanel, "login"); // Iniciar en la pantalla de login
    }

    private void cargarDatosInicialesConManejoDeError() {
        try {
            Database.conectar(); // Asegura que la conexión está activa y tablas creadas
            propietarios = new ArrayList<>(PropietarioDAO.listarTodos());
            mascotas     = new ArrayList<>(MascotaDAO.listarTodos());
            veterinarios = new ArrayList<>(VeterinarioDAO.listarTodos());
            List<Cita> todasLasCitas = new ArrayList<>(CitaDAO.listarTodos());

            // Limpiar y rellenar listas de citas pendientes y pagadas
            citas.clear();
            citasPagadas.clear();
            for (Cita c : todasLasCitas) {
                if ("PAGADA".equalsIgnoreCase(c.getEstado())) {
                    citasPagadas.add(c);
                } else {
                    citas.add(c); // Asumir PENDIENTE o cualquier otro estado no pagado
                }
            }
            System.out.println("Datos cargados desde la BD. Propietarios: " + propietarios.size() + ", Mascotas: " + mascotas.size() + ", Citas Pendientes: " + citas.size());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error Crítico: No se pudieron cargar los datos iniciales desde la base de datos.\n" + e.getMessage() +
                            "\nLa aplicación podría no funcionar correctamente. Verifique la conexión y el archivo de base de datos.",
                    "Error de Carga de Datos", JOptionPane.ERROR_MESSAGE);
            // Inicializar listas vacías para evitar NullPointerExceptions más adelante
            propietarios = new ArrayList<>();
            mascotas = new ArrayList<>();
            veterinarios = new ArrayList<>();
            citas = new ArrayList<>();
            citasPagadas = new ArrayList<>();
        } catch (Exception e) { // Capturar cualquier otra excepción inesperada
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error inesperado durante la carga de datos iniciales: " + e.getMessage(),
                    "Error Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Métodos de Navegación (públicos para ser llamados desde los paneles) ---
    public void irHome()                 { cardLayout.show(mainPanel, "home"); }
    public void irLogin()                { cardLayout.show(mainPanel, "login"); } // Para cerrar sesión
    public void abrirPanelPropietarios() { if(propietariosPanel!=null) propietariosPanel.actualizarCards(); cardLayout.show(mainPanel, "propietarios"); }
    public void abrirPanelMascotas()     { if(mascotasPanel!=null) mascotasPanel.actualizarCards(); cardLayout.show(mainPanel, "mascotas"); }
    public void abrirPanelVeterinarios() { if(veterinariosPanel!=null) veterinariosPanel.actualizarCards(); cardLayout.show(mainPanel, "veterinarios"); }
    public void abrirPanelCitas()        { if(citasPanel!=null) citasPanel.actualizarCards(); cardLayout.show(mainPanel, "citas"); }
    public void abrirPanelPagos()        { if(pagosPanel!=null) pagosPanel.actualizarCards(this.citasPagadas); cardLayout.show(mainPanel, "historialPagos"); }
    public void abrirPanelPago(Cita cita){ if(pagoPanel!=null) pagoPanel.setCita(cita); cardLayout.show(mainPanel, "procesarPago"); }

    // --- Métodos de Gestión de Datos (interactúan con DAOs y actualizan UI) ---

    public void agregarPropietario(Propietario p) {
        if (p == null || p.getDocumento() == null || p.getDocumento().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El documento del propietario no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Evitar duplicados por documento
        if (propietarios.stream().anyMatch(prop -> prop.getDocumento().equals(p.getDocumento()))) {
            JOptionPane.showMessageDialog(this, "Ya existe un propietario con el documento " + p.getDocumento() + ".", "Propietario Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            PropietarioDAO.guardar(p);
            propietarios.add(p); // Añadir a la lista en memoria DESPUÉS de guardar en BD
            if(propietariosPanel != null) propietariosPanel.actualizarCards(); // Actualizar la vista
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
        // Verificar que el propietario exista
        if (propietarios.stream().noneMatch(p -> p.getDocumento().equals(m.getPropietarioDocumento()))) {
            JOptionPane.showMessageDialog(this, "El propietario asignado a la mascota no existe.", "Error de Propietario", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            MascotaDAO.guardar(m);
            mascotas.add(m);
            if(mascotasPanel != null) mascotasPanel.actualizarCards();
            if(propietariosPanel != null) propietariosPanel.actualizarCards(); // Actualizar contador de mascotas en propietario
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

    public void agregarCita(Cita c) {
        if (c == null || c.getFechaHora() == null || c.getMotivo() == null) {
            JOptionPane.showMessageDialog(this, "La cita tiene datos incompletos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            CitaDAO.guardar(c);
            citas.add(c); // Añadir a la lista de citas pendientes
            if(citasPanel != null) citasPanel.actualizarCards();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cita: " + ex.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // --- Métodos de Eliminación Centralizados ---
    public void eliminarPropietario(Propietario p) throws SQLException { // Propagar SQLException para manejo en panel
        if (p == null) return;
        // Eliminar mascotas asociadas y sus citas
        List<Mascota> mascotasDelPropietario = new ArrayList<>();
        for (Mascota m : new ArrayList<>(mascotas)) { // Iterar sobre una copia para evitar ConcurrentModificationException
            if (m.getPropietarioDocumento().equals(p.getDocumento())) {
                mascotasDelPropietario.add(m);
            }
        }
        for (Mascota m : mascotasDelPropietario) {
            eliminarMascota(m); // Esto ya maneja las citas de la mascota
        }
        PropietarioDAO.eliminarPorDocumento(p.getDocumento());
        propietarios.remove(p);
        if(propietariosPanel != null) propietariosPanel.actualizarCards();
    }

    public void eliminarMascota(Mascota m) throws SQLException {
        if (m == null) return;
        // Eliminar citas asociadas a la mascota (tanto pendientes como pagadas)
        List<Cita> citasDeLaMascota = new ArrayList<>();
        for (Cita c : new ArrayList<>(citas)) { // Citas pendientes
            if (c.getIdMascota().equals(m.getId())) citasDeLaMascota.add(c);
        }
        for (Cita c : new ArrayList<>(citasPagadas)) { // Citas pagadas
            if (c.getIdMascota().equals(m.getId())) citasDeLaMascota.add(c);
        }
        for (Cita c : citasDeLaMascota) {
            eliminarCita(c); // Esto borra de BD y de listas en memoria
        }
        MascotaDAO.eliminarPorId(m.getId());
        mascotas.remove(m);
        if(mascotasPanel != null) mascotasPanel.actualizarCards();
        if(propietariosPanel != null) propietariosPanel.actualizarCards(); // Refrescar contador de mascotas del propietario
    }

    public void eliminarVeterinario(Veterinario v) throws SQLException {
        if (v == null) return;
        // Eliminar citas asociadas al veterinario
        List<Cita> citasDelVeterinario = new ArrayList<>();
        for (Cita c : new ArrayList<>(citas)) {
            if (c.getDocVeterinario().equals(v.getDocumento())) citasDelVeterinario.add(c);
        }
        for (Cita c : new ArrayList<>(citasPagadas)) {
            if (c.getDocVeterinario().equals(v.getDocumento())) citasDelVeterinario.add(c);
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

    // Marcar cita como pagada y moverla de lista
    public void marcarCitaComoPagada(Cita cita) {
        if (cita == null) return;
        cita.setEstado("Pagada");
        try {
            CitaDAO.guardar(cita); // Actualizar estado en BD
            citas.remove(cita); // Quitar de pendientes
            if (!citasPagadas.contains(cita)) { // Añadir a pagadas si no está ya
                citasPagadas.add(cita);
            }
            if(citasPanel != null) citasPanel.actualizarCards();
            if(pagosPanel != null) pagosPanel.actualizarCards(this.citasPagadas);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la cita en la base de datos: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Revertir el cambio en memoria si falla la BD (opcional, depende de la lógica de negocio)
            // cita.setEstado("PENDIENTE");
            // citas.add(cita);
            // citasPagadas.remove(cita);
        }
    }

    // Método main para iniciar la aplicación
    public static void main(String[] args) {
        // Establecer Look and Feel (opcional, para una apariencia más moderna)
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
