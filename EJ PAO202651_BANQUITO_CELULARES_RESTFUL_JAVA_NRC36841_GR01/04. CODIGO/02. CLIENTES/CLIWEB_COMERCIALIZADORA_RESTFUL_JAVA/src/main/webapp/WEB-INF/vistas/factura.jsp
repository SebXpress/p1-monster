<%-- Vista: detalle de una factura (y su tabla de amortización si fue a crédito) --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Comercializadora Monster - Factura</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main>
<c:if test="${not empty factura}">
    <section class="tarjeta forma-${factura.formaPago}" style="border-left:4px solid var(--c)">
        <div class="factura-cab">
            <img src="img/monster.png" alt="Monster">
            <div style="flex:1">
                <h2>FACTURA N.º ${factura.numero}</h2>
                <dl style="margin-top:8px">
                    <dt>Fecha</dt><dd>${factura.fecha}</dd>
                    <dt>Cliente</dt><dd><c:out value="${factura.cliente}"/> · C.I. ${factura.cedula}</dd>
                    <dt>Forma de pago</dt>
                    <dd><span class="chip">${factura.formaPago == 'CREDITO' ? 'CRÉDITO DIRECTO BANQUITO' : 'EFECTIVO (42 % DESC.)'}</span>
                        <c:if test="${factura.formaPago == 'CREDITO'}"> Crédito N.º ${factura.codigoCredito} · ${factura.numeroCuotas} cuotas de
                            $ <fmt:formatNumber value="${factura.valorCuota}" pattern="#,##0.00"/></c:if></dd>
                </dl>
            </div>
            <a class="btn sec" href="facturas">← Facturas</a>
            <button class="btn" onclick="window.print()">Imprimir</button>
        </div>
        <table style="margin-top:18px">
            <thead><tr><th>Línea</th><th>Celular</th><th class="der">Cantidad</th><th class="der">P. Unitario</th><th class="der">Subtotal</th></tr></thead>
            <tbody>
            <c:forEach items="${factura.detalles}" var="d">
                <tr><td>${d.linea}</td><td><c:out value="${d.celular}"/></td><td class="der">${d.cantidad}</td>
                    <td class="der"><fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00"/></td>
                    <td class="der"><fmt:formatNumber value="${d.subtotal}" pattern="#,##0.00"/></td></tr>
            </c:forEach>
            </tbody>
        </table>
        <dl class="totales">
            <dt>Subtotal</dt><dd>$ <fmt:formatNumber value="${factura.subtotal}" pattern="#,##0.00"/></dd>
            <dt>Descuento</dt><dd class="ing">- $ <fmt:formatNumber value="${factura.descuento}" pattern="#,##0.00"/></dd>
            <dt style="align-self:center">TOTAL</dt><dd class="gran">$ <span class="contar" data-valor="${factura.total}"><fmt:formatNumber value="${factura.total}" pattern="#,##0.00"/></span></dd>
        </dl>
    </section>
    <c:if test="${not empty credito}">
        <section class="tarjeta"><%@include file="tabla_amortizacion.jspf"%></section>
    </c:if>
</c:if>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
