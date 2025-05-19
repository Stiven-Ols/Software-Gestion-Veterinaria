# 🐾 Sistema de Gestión Veterinaria 🏥

## ✨ Características Principales

Este sistema integral ofrece una amplia gama de funcionalidades para cubrir todas tus necesidades:

*   👤 **Gestión de Perfiles:**
    *   👨‍⚕️ **Administradores:** Acceso completo al sistema, configuración de precios y estadísticas avanzadas.
    *   👥 **Empleados:** Operaciones diarias de gestión de clientes y citas.
       
*   🙋‍♂️ **Gestión de Propietarios:** Registra, busca y visualiza información detallada de los dueños de las mascotas.
*   🐕 **Gestión de Mascotas:**
    *   Completo registro de pacientes (nombre, especie, raza, edad).
    *   📋 Historial médico detallado por mascota.
*   👨‍⚕️ **Gestión de Veterinarios:** Administra perfiles de veterinarios, especialidades y disponibilidad.
*   📅 **Agendamiento de Citas Inteligente:**
    *   Programación de citas por tipo de servicio (Consulta, Vacunación, Cirugía, Urgencia).
    *   Selección de propietario, mascota y veterinario.
    *   Selector de fecha y hora con validación de disponibilidad.
    *   Evita conflictos de horario para veterinarios y mascotas.
*   💳 **Procesamiento de Pagos:**
    *   Interfaz clara para procesar pagos de citas atendidas.
    *   Soporte para múltiples métodos de pago (Efectivo, Tarjeta, Transferencia).
    *   Cálculo automático de cambio.
    *   Generación de comprobante de pago.
*   📊 **Panel de Administración (Dashboard):** *¡Nuevo y Mejorado!*
    *   📈 **KPIs en Tiempo Real:** Visualiza citas de hoy/mes, ingresos de hoy/mes.
    *   ⭐ **Servicios Populares:** Identifica los servicios más solicitados del mes.
    *   🩺 **Veterinarios Destacados:** Observa qué veterinarios tienen más citas en el mes.
    *   ⚙️ **Configuración de Precios:** El administrador puede ajustar dinámicamente los precios de los servicios.
    *   📄 **Generación de Reportes PDF:**
        *   Reporte mensual de citas.
        *   Resumen de ingresos.
        *   Detalle de servicios solicitados.
*   🔍 **Búsqueda Avanzada:** Filtra rápidamente propietarios, mascotas, veterinarios y citas.
*   🎨 **Interfaz Gráfica Intuitiva:** Diseño amigable y fácil de navegar construido con Java Swing.
*   💾 **Persistencia de Datos:** Utiliza SQLite para un almacenamiento de datos local, confiable y eficiente.

---

## 🛠️ Tecnologías Utilizadas

*   ☕ **Lenguaje:** Java
*   🖼️ **Interfaz Gráfica:** Swing (con componentes como `JDateChooser` de Toedter)
*   🧱 **Gestor de Dependencias y Build:** Gradle
*   🗄️ **Base de Datos:** SQLite
*   📄 **Reportes PDF:** OpenPDF / iText (versión antigua)
*   🧪 **Pruebas Unitarias:** JUnit 5

---

## 🚀 Cómo Empezar (Desarrollo)

1.  **Clona el Repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/tu-repositorio.git
    cd tu-repositorio
    ```
2.  **Abre el Proyecto en IntelliJ IDEA:**
    *   Asegúrate de tener IntelliJ IDEA instalado.
    *   Importa el proyecto como un proyecto Gradle. IntelliJ debería detectar el archivo `build.gradle` y configurar todo automáticamente.
3.  **Construye el Proyecto:**
    *   Puedes usar la interfaz de IntelliJ (`Build > Build Project`) o el wrapper de Gradle:
        ```bash
        ./gradlew build
        ```
4.  **Ejecuta la Aplicación:**
    *   Localiza la clase `Main.java` (`com.veterinaria.Main`) y ejecútala.
5.  **Acceso de Administrador:**
    *   Usuario: Administrador
    *   Contraseña: `adminAres`

---

## 📸 Vistazos del Sistema

*   **Dashboard de Administración:**
    `(Docs/Screenshots/Panel_Admin.png)`
    ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Admin.png)
*   **Home de Empleado:**
    `(Docs/Screenshots/Panel_Empleados.png)`
    ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Empleados.png)
*   **Gestión de Citas:**
    `(Docs/Screenshots/Panel_Citas.png)`
     ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Citas.png)
*   **Panel Mascotas:**
    `(Docs/Screenshots/Panel_Mascotas.png)`
     ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Mascotas.png)
*   **Panel Propietarios:**
    `(Docs/Screenshots/Panel_Propietarios.png)`
    ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Propietarios.png)

*   **Panel Veterinarios:**
    `(Docs/Screenshots/Panel_Veterinarios.png)`
    ![alt text](https://github.com/Stiven-Ols/Software-Gestion-Veterinaria/blob/56f6afe5044c9c4ad8f21e4a7a4f7e3849028e92/Docs/Screenshots/Panel_Veterinarios.png)


---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si tienes ideas para mejorar el sistema, por favor:
1.  Haz un Fork del repositorio.
2.  Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3.  Haz tus cambios y haz commit (`git commit -am 'Añadir nueva funcionalidad X'`).
4.  Sube tus cambios a la rama (`git push origin feature/nueva-funcionalidad`).
5.  Abre un Pull Request.

---

Desarrollado con ❤️ por Stiven-Ols
