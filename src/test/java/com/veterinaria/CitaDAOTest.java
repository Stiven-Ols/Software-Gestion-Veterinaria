package com.veterinaria;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CitaDAOTest {

    @BeforeAll
    static void setUpDatabase() {
        try (Connection conn = Database.conectar()) {
            assertNotNull(conn);
        } catch (SQLException e) {
            fail("Error al conectar a la BD para CitaDAOTest: " + e.getMessage());
        }
    }

    @BeforeEach
    @AfterEach
    void limpiarTablas() { // Limpiar todas las tablas relevantes
        try (Connection conn = Database.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Citas");
            stmt.execute("DELETE FROM Mascotas");
            stmt.execute("DELETE FROM Propietarios");
            stmt.execute("DELETE FROM Veterinarios");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error al limpiar tablas para CitaDAOTest: " + e.getMessage());
        }
    }

    // Métodos auxiliares para crear entidades de prueba
    private Propietario crearPropietarioPrueba(String doc) throws SQLException {
        Propietario p = new Propietario(doc, "Prop Cita " + doc, "TelCita", "DirCita", "cita@test.com");
        PropietarioDAO.guardar(p);
        return p;
    }

    private Mascota crearMascotaPrueba(String nombre, String docProp) throws SQLException {
        Mascota m = new Mascota(nombre, "Perro", "Mixto", 1, docProp);
        MascotaDAO.guardar(m);
        return m;
    }

    private Veterinario crearVeterinarioPrueba(String doc) throws SQLException {
        Veterinario v = new Veterinario(doc, "Vet Cita " + doc, "General", "TelVetCita", true);
        VeterinarioDAO.guardar(v);
        return v;
    }

    @Test
    void testGuardarYListarCita() throws SQLException {
        Propietario prop = crearPropietarioPrueba("P001");
        Mascota masc = crearMascotaPrueba("Bobby", prop.getDocumento());
        Veterinario vet = crearVeterinarioPrueba("V001");

        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.DECEMBER, 24, 10, 30, 0); // Año, Mes (0-11), Día, Hora, Min, Seg
        Date fechaCita = cal.getTime();

        Cita c1 = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), fechaCita, TipoServicio.CONSULTA, "PENDIENTE");
        CitaDAO.guardar(c1);

        List<Cita> citas = CitaDAO.listarTodos();
        assertNotNull(citas);
        assertEquals(1, citas.size());
        Cita citaRecuperada = citas.get(0);
        assertEquals(c1.getId(), citaRecuperada.getId());
        assertEquals(masc.getId(), citaRecuperada.getIdMascota());
        assertEquals(TipoServicio.CONSULTA, citaRecuperada.getMotivo());
        assertEquals("PENDIENTE", citaRecuperada.getEstado());

        // Comparar fechas con un margen de error pequeño si es necesario, o comparar sus .getTime()
        assertEquals(fechaCita.getTime() / 1000, citaRecuperada.getFechaHora().getTime() / 1000); // Compara segundos
    }

    @Test
    void testEliminarCita() throws SQLException {
        Propietario prop = crearPropietarioPrueba("P002");
        Mascota masc = crearMascotaPrueba("Rocky", prop.getDocumento());
        Veterinario vet = crearVeterinarioPrueba("V002");
        Cita c1 = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(), TipoServicio.VACUNACION, "PAGADA");
        CitaDAO.guardar(c1);
        String idCitaGuardada = c1.getId();

        CitaDAO.eliminarPorId(idCitaGuardada);

        List<Cita> citas = CitaDAO.listarTodos();
        assertTrue(citas.isEmpty());
    }

    @Test
    void testListarCitasPorEstado() throws SQLException {
        Propietario prop = crearPropietarioPrueba("P003");
        Mascota masc = crearMascotaPrueba("Maxi", prop.getDocumento());
        Veterinario vet = crearVeterinarioPrueba("V003");

        Cita cPendiente1 = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(), TipoServicio.CONSULTA, "PENDIENTE");
        Cita cPendiente2 = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(System.currentTimeMillis() + 3600000), TipoServicio.CIRUGIA, "PENDIENTE");
        Cita cPagada = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(System.currentTimeMillis() - 86400000), TipoServicio.URGENCIA, "PAGADA");

        CitaDAO.guardar(cPendiente1);
        CitaDAO.guardar(cPendiente2);
        CitaDAO.guardar(cPagada);

        List<Cita> citasPendientes = CitaDAO.listarPorEstado("PENDIENTE");
        assertEquals(2, citasPendientes.size());
        assertTrue(citasPendientes.stream().allMatch(c -> "PENDIENTE".equals(c.getEstado())));

        List<Cita> citasPagadas = CitaDAO.listarPorEstado("PAGADA");
        assertEquals(1, citasPagadas.size());
        assertEquals("PAGADA", citasPagadas.get(0).getEstado());
    }

    @Test
    void testActualizarEstadoCita() throws SQLException {
        Propietario prop = crearPropietarioPrueba("P004");
        Mascota masc = crearMascotaPrueba("Lassie", prop.getDocumento());
        Veterinario vet = crearVeterinarioPrueba("V004");
        Cita c1 = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(), TipoServicio.CONSULTA, "PENDIENTE");
        CitaDAO.guardar(c1); // Se guarda como PENDIENTE

        // Ahora actualizamos el estado y guardamos de nuevo (INSERT OR REPLACE)
        c1.setEstado("PAGADA");
        CitaDAO.guardar(c1);

        List<Cita> citas = CitaDAO.listarTodos();
        assertEquals(1, citas.size());
        assertEquals("PAGADA", citas.get(0).getEstado(), "El estado de la cita debería ser PAGADA.");
    }

    @Test
    void testMostrarInfoCita() throws SQLException {
        Propietario prop = crearPropietarioPrueba("PCITAINFO");
        Mascota masc = crearMascotaPrueba("MascInfo", prop.getDocumento());
        Veterinario vet = crearVeterinarioPrueba("VCITAINFO");
        Cita c = new Cita(prop.getDocumento(), masc.getId(), vet.getDocumento(), new Date(), TipoServicio.CONSULTA, "PENDIENTE");
        CitaDAO.guardar(c);

        String info = c.mostrarInfo();
        assertNotNull(info);
        assertTrue(info.contains(c.getId()));
        assertTrue(info.contains(masc.getId()));
        assertTrue(info.contains(prop.getDocumento()));
        assertTrue(info.contains(TipoServicio.CONSULTA.getDescripcion()));
    }
}
