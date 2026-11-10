package co.edu.uniquindio.panaderia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite completa de 9 pruebas unitarias JUnit 5 para Panadería El Trigal.
 * Cumple estrictamente la especificación y tabla de diseño de pruebas del Parcial 3.
 */
public class PanaderiaTest {

    private Panaderia panaderia;

    @BeforeEach
    void setUp() {
        // Cada prueba inicializa una panadería limpia sin datos compartidos
        panaderia = new Panaderia();
    }

    // =========================================================================
    // 10.1 FUNCIONALIDAD: REGISTRAR (3 PRUEBAS)
    // =========================================================================

    @Test
    void registrarPedido_conDatosValidos_quedaPendienteYNoReservaMinutos() {
        // Arrange
        PanaderoArtesanal ana = new PanaderoArtesanal("Ana", "1001", "E01", 100);
        panaderia.registrarEmpleado(ana);

        // Act
        Pedido p = panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);

        // Assert
        assertEquals(EstadoPedido.PENDIENTE, p.getEstado(), "El pedido debe iniciar en estado PENDIENTE");
        assertEquals(25, p.getCantidadUnidades(), "La cantidad de unidades debe coincidir con la registrada");
        assertEquals(100, ana.getMinutosDisponibles(), "El registro no debe descontar minutos del empleado");
        assertNull(p.getCodigoEmpleadoAsignado(), "No debe tener empleado asignado al registrarse");
        assertEquals(0, p.getMinutosReservados(), "Los minutos reservados deben ser 0 al registrarse");
        assertEquals(1, panaderia.getPedidos().size(), "El historial debe contener exactamente un pedido");
    }

    @Test
    void registrarPedido_conCodigoDePedidoCancelado_lanzaIllegalArgumentException() {
        // Arrange
        panaderia.registrarPedido("P01", "Cafe Central", TipoPan.TRADICIONAL, 25);
        panaderia.cancelarPedido("P01");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            panaderia.registrarPedido("P01", "Otro", TipoPan.INTEGRAL, 10);
        }, "Debe rechazar el registro de un código ya existente en el historial");

        // Assert
        assertEquals(1, panaderia.getPedidos().size(), "El historial debe conservar únicamente el pedido original");
        Pedido original = panaderia.buscarPedido("P01");
        assertNotNull(original, "El pedido original debe existir en el historial");
        assertEquals(EstadoPedido.CANCELADO, original.getEstado(), "El pedido original debe permanecer CANCELADO");
        assertEquals("Cafe Central", original.getNombreCliente(), "El cliente original debe permanecer intacto");
        assertEquals(25, original.getCantidadUnidades(), "La cantidad original debe permanecer intacta");
    }

    @Test
    void registrarPedido_conCeroPanes_lanzaIllegalArgumentExceptionYHistorialVacio() {
        // Arrange: historial vacío

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            panaderia.registrarPedido("P01", "Cafe", TipoPan.TRADICIONAL, 0);
        }, "La cantidad <= 0 debe arrojar IllegalArgumentException");

        // Assert
        assertTrue(panaderia.getPedidos().isEmpty(), "El historial debe continuar vacío tras un intento fallido");
    }

    // =========================================================================
    // 10.2 FUNCIONALIDAD: ASIGNAR (3 PRUEBAS)
    // =========================================================================

    @Test
    void asignarPedido_artesanalYIndustrial_seleccionaAlPanaderoMasRapido() {
        // Arrange
        PanaderoArtesanal ana = new PanaderoArtesanal("Ana", "1001", "E01", 100);
        PanaderoIndustrial carlos = new PanaderoIndustrial("Carlos", "1002", "E02", 45);
        panaderia.registrarEmpleado(ana);
        panaderia.registrarEmpleado(carlos);

        Pedido pedido = panaderia.registrarPedido("P01", "Cafe", TipoPan.TRADICIONAL, 25);

        // Verificar tiempos teóricos sin consumir
        assertEquals(90, ana.calcularTiempoProduccion(pedido), "Ana debe tomar 90 min (3 bandejas * 30)");
        assertEquals(45, carlos.calcularTiempoProduccion(pedido), "Carlos debe tomar 45 min (20 + 25)");

        // Act
        Pedido p = panaderia.asignarPedido("P01");

        // Assert
        assertEquals(EstadoPedido.ASIGNADO, p.getEstado(), "El estado debe cambiar a ASIGNADO");
        assertEquals("E02", p.getCodigoEmpleadoAsignado(), "Debe asignarse a Carlos (E02) por ser el más rápido");
        assertEquals(45, p.getMinutosReservados(), "Se deben reservar 45 minutos");
        assertEquals(0, carlos.getMinutosDisponibles(), "Carlos debe quedar con 0 minutos");
        assertEquals(100, ana.getMinutosDisponibles(), "Ana debe conservar sus 100 minutos intactos");
    }

    @Test
    void asignarPedido_conEmpateDeTiempo_seleccionaAlPrimeroRegistrado() {
        // Arrange
        PanaderoArtesanal ana = new PanaderoArtesanal("Ana", "1001", "E01", 80);
        PanaderoArtesanal diana = new PanaderoArtesanal("Diana", "1002", "E02", 80);
        PanaderoIndustrial carlos = new PanaderoIndustrial("Carlos", "1003", "E03", 54);

        panaderia.registrarEmpleado(ana);
        panaderia.registrarEmpleado(diana);
        panaderia.registrarEmpleado(carlos);

        panaderia.registrarPedido("P01", "Cafe", TipoPan.INTEGRAL, 13);

        // Act
        Pedido p = panaderia.asignarPedido("P01");

        // Assert
        assertEquals("E01", p.getCodigoEmpleadoAsignado(), "Debe desempatar a favor de Ana (primera registrada)");
        assertEquals(80, p.getMinutosReservados(), "Deben reservarse 80 minutos");
        assertEquals(0, ana.getMinutosDisponibles(), "Ana consume sus 80 minutos");
        assertEquals(80, diana.getMinutosDisponibles(), "Diana conserva sus 80 minutos");
        assertEquals(54, carlos.getMinutosDisponibles(), "Carlos no fue seleccionado y conserva sus 54 minutos");
    }

    @Test
    void asignarPedido_sinPanaderoConTiempoSuficiente_lanzaIllegalStateException() {
        // Arrange
        PanaderoIndustrial carlos = new PanaderoIndustrial("Carlos", "1001", "E01", 44);
        Cajero lucia = new Cajero("Lucia", "1002", "E02", 120, "Caja 1");
        panaderia.registrarEmpleado(carlos);
        panaderia.registrarEmpleado(lucia);

        panaderia.registrarPedido("P01", "Cafe", TipoPan.TRADICIONAL, 25);

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> {
            panaderia.asignarPedido("P01");
        }, "Debe lanzar IllegalStateException si no hay panadero disponible");

        // Assert (Atomicidad y estado intacto)
        Pedido pedido = panaderia.buscarPedido("P01");
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado(), "El pedido debe seguir PENDIENTE");
        assertNull(pedido.getCodigoEmpleadoAsignado(), "No debe haber empleado asignado");
        assertEquals(0, pedido.getMinutosReservados(), "No debe haber minutos reservados");
        assertEquals(44, carlos.getMinutosDisponibles(), "Carlos conserva sus 44 minutos");
        assertEquals(120, lucia.getMinutosDisponibles(), "Lucía conserva sus 120 minutos");
    }

    // =========================================================================
    // 10.3 FUNCIONALIDAD: CANCELAR (3 PRUEBAS)
    // =========================================================================

    @Test
    void cancelarPedido_asignadoConDosPedidos_devuelveMinutosSoloDelCancelado() {
        // Arrange
        PanaderoIndustrial carlos = new PanaderoIndustrial("Carlos", "1001", "E01", 120);
        panaderia.registrarEmpleado(carlos);

        panaderia.registrarPedido("P01", "Cafe A", TipoPan.TRADICIONAL, 25);
        panaderia.registrarPedido("P02", "Cafe B", TipoPan.TRADICIONAL, 25);

        panaderia.asignarPedido("P01");
        panaderia.asignarPedido("P02");

        assertEquals(30, carlos.getMinutosDisponibles(), "Carlos debe tener 30 minutos disponibles tras 2 asignaciones");

        // Act
        panaderia.cancelarPedido("P01");

        // Assert
        assertEquals(75, carlos.getMinutosDisponibles(), "Carlos debe recuperar exactamente los 45 min de P01 (30 + 45 = 75)");
        assertEquals(EstadoPedido.CANCELADO, panaderia.buscarPedido("P01").getEstado(), "P01 debe quedar CANCELADO");
        assertEquals(EstadoPedido.ASIGNADO, panaderia.buscarPedido("P02").getEstado(), "P02 debe continuar ASIGNADO");
    }

    @Test
    void cancelarPedido_pendiente_conservaMinutosYQuedaEnHistorial() {
        // Arrange
        PanaderoArtesanal ana = new PanaderoArtesanal("Ana", "1001", "E01", 100);
        panaderia.registrarEmpleado(ana);

        panaderia.registrarPedido("P01", "Cafe", TipoPan.TRADICIONAL, 25);

        // Act
        panaderia.cancelarPedido("P01");

        // Assert
        assertEquals(EstadoPedido.CANCELADO, panaderia.buscarPedido("P01").getEstado(), "El pedido debe quedar CANCELADO");
        assertEquals(1, panaderia.getPedidos().size(), "El pedido cancelado debe permanecer en el historial");
        assertEquals(100, ana.getMinutosDisponibles(), "Los minutos del empleado deben permanecer intactos");
    }

    @Test
    void cancelarPedido_dosVecesElMismoPedido_lanzaIllegalStateExceptionYDevuelveUnaVez() {
        // Arrange
        PanaderoIndustrial carlos = new PanaderoIndustrial("Carlos", "1001", "E01", 100);
        panaderia.registrarEmpleado(carlos);

        panaderia.registrarPedido("P01", "Cafe", TipoPan.TRADICIONAL, 25);
        panaderia.asignarPedido("P01"); // Carlos queda en 55 min
        panaderia.cancelarPedido("P01"); // Carlos vuelve a 100 min

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> {
            panaderia.cancelarPedido("P01");
        }, "Una segunda cancelación debe lanzar IllegalStateException");

        // Assert
        assertEquals(100, carlos.getMinutosDisponibles(), "Los minutos no deben devolverse dos veces (debe seguir en 100, no 145)");
        assertEquals(EstadoPedido.CANCELADO, panaderia.buscarPedido("P01").getEstado(), "El pedido debe continuar CANCELADO");
    }
}